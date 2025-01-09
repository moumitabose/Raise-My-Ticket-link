package com.example.support_ticket_system.service;

import com.example.support_ticket_system.model.Agent;
import com.example.support_ticket_system.model.Ticket;
import com.example.support_ticket_system.repository.AgentRepository;
import com.example.support_ticket_system.repository.TicketRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.lexruntimev2.LexRuntimeV2Client;
import software.amazon.awssdk.services.lexruntimev2.model.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final SnsClient snsClient;
    private final SqsClient sqsClient;
    private final TicketRepository ticketRepository;
    private final AgentRepository agentRepository;
    private final ComprehendService comprehendService;
    private final LexRuntimeV2Client lexClient; // Lex client for runtime interactions

    private final String snsTopicArn = "arn:aws:sns:us-east-2:412381741682:SupportTickets";
    private final String sqsQueueUrl = "https://sqs.us-east-2.amazonaws.com/412381741682/SupportTicketQueue";

    public TicketService(SnsClient snsClient, SqsClient sqsClient,TicketRepository ticketRepository,
                         AgentRepository agentRepository,ComprehendService comprehendService,LexRuntimeV2Client lexClient) {
        this.snsClient = snsClient;
        this.sqsClient = sqsClient;
        this.ticketRepository = ticketRepository;
        this.agentRepository = agentRepository;
        this.comprehendService = comprehendService;
        this.lexClient = lexClient;
    }


    public List<Ticket> getAllTickets() {
        return ticketRepository.getAllTickets();  // Retrieves all tickets from the repository
    }

    public void createTicketFromLex(String userInput, String sessionId) {
        // Step 1: Send input to Lex bot for processing
        RecognizeTextRequest request = RecognizeTextRequest.builder()
                .botId("RWBKOCOQBN")
                .botAliasId("UDR7FFCORC")  // ID of your bot
                .localeId("en_US")  // Locale (language) for the bot (example: en_US)
                .sessionId(sessionId)  // Use the same session ID for the entire conversation
                .text(userInput)  // The input text from the user
                .build();

        RecognizeTextResponse response = lexClient.recognizeText(request);

        // Step 2: Log the entire response for debugging
        System.out.println("Lex Response: " + response);

        // Step 3: Extract the interpretations (possible outputs from Lex)
        if (response.interpretations() != null && !response.interpretations().isEmpty()) {
            // Extract the first interpretation
            Interpretation interpretation = response.interpretations().get(0);

            // Step 4: Extract the intent and its slots from the interpretation
            Intent intent = interpretation.intent();
            Map<String, Slot> slots = intent.slots();

            System.out.println("Intent: " + intent.name());
            System.out.println("Slots: " + slots);

            // Step 5: Check if Lex is asking for a slot (PriorityLevel in this case)
            if (response.sessionState().dialogAction() != null &&
                    response.sessionState().dialogAction().type() == DialogActionType.ELICIT_SLOT) {

                String slotToElicit = response.sessionState().dialogAction().slotToElicit();
                System.out.println("Lex is asking for slot: " + slotToElicit);


                if ("IssueDescription".equals(slotToElicit)) {
                    System.out.println("Please provide a description of the issue.");
                    return;  // Stop here and wait for the user's response with the missing slot
                }

                // Respond to the user with a message prompting for the missing slot (PriorityLevel)
                if ("PriorityLevel".equals(slotToElicit)) {
                    // You can customize the message as needed
                    System.out.println("Please provide the priority level for the ticket (e.g., High, Medium, Low).");
                }
                return; // Stop here and wait for the user's response with the missing slot
            }

            // Step 6: Extract individual slot values (ensure the slot exists)
            String name = slots.containsKey("Name") ? String.valueOf(slots.get("Name").value()) : null;
            String email = slots.containsKey("Email") ? String.valueOf(slots.get("Email").value()) : null;
            String issueType = slots.containsKey("IssueType") ? String.valueOf(slots.get("IssueType").value()) : null;
            String description = slots.containsKey("IssueDescription") ? String.valueOf(slots.get("IssueDescription").value()) : null;
            String priorityLevel = slots.containsKey("PriorityLevel") ? String.valueOf(slots.get("PriorityLevel").value()) : null;

            // Step 7: Validate that required fields are not null or empty
            if (name == null || email == null || issueType == null || description == null || priorityLevel == null) {
                throw new IllegalArgumentException("Missing required information from Lex response");
            }

            // Step 8: Create a Ticket object and populate it
            Ticket ticket = new Ticket();
            System.out.println("Setting Ticket Name: " + name);
            ticket.setName(name);

            System.out.println("Setting Ticket Email: " + email);
            ticket.setEmail(email);

            System.out.println("Setting Ticket Issue Type: " + issueType);
            ticket.setIssueType(issueType);

            System.out.println("Setting Ticket Description: " + description);
            ticket.setDescription(description);

// If PriorityLevel is also extracted, you can add debugging for it as well
            if (priorityLevel != null) {
                System.out.println("Setting Ticket Priority Level: " + priorityLevel);
                ticket.setPriority(priorityLevel);
            }
           // ticket.setPriorityLevel(priorityLevel);

            // Step 9: Call the existing createTicket method to save it to DynamoDB
       //     createTicket(ticket);
        } else {
            // Handle the case where no valid interpretation is found
            throw new IllegalArgumentException("No valid interpretation found in Lex response");
        }
    }


    public void createTicket(Ticket ticket) {

        String ticketNumber = "T-" + LocalDate.now().getYear() +
                String.format("%06d", new Random().nextInt(900000) + 100000) +
                "-" + System.currentTimeMillis();
        ticket.setTicketNumber(ticketNumber);
        // Step 1: Set default values for status and agent
        ticket.setStatus("Pending");
        ticket.setAgent("Unassigned");

        // Step 2: Perform AWS Comprehend analysis on the ticket description
        String sentiment = comprehendService.detectSentiment(ticket.getDescription());
        String entities = comprehendService.detectEntities(ticket.getDescription());
        String keyPhrases = comprehendService.detectKeyPhrases(ticket.getDescription());

        // Step 3: Set the analysis results in the ticket object
        ticket.setSentiment(sentiment);
        ticket.setEntities(List.of(entities.split("\n"))); // Split entities into a list
        ticket.setKeyPhrases(List.of(keyPhrases.split("\n"))); // Split key phrases into a list

        if (ticket.getSentiment().equals("NEGATIVE")) {
            ticket.setPriority("High");
        } else {
            ticket.setPriority("Normal");
        }



        // Step 4: Save the ticket to the repository
        ticketRepository.createTicket(ticket);

        // Step 5: Attempt to auto-assign the ticket
        try {
            autoAssignTicket(ticket.getEmail(), ticket.getIssueType(), ticket.getCategory(), ticket.getRegion());
            ticket.setStatus("In Progress"); // Update status after successful assignment
        } catch (RuntimeException e) {
            // Log the error, but allow the process to continue
            System.err.println("Auto-assignment failed: " + e.getMessage());
        }

        // Step 6: Prepare message to send to SNS
        String snsMessage = "New ticket submitted:\n" +
                "Name: " + ticket.getName() + "\n" +
                "Email: " + ticket.getEmail() + "\n" +
                "Category: " + ticket.getCategory() + "\n" +
                "Description: " + ticket.getDescription() + "\n" +
                "Priority: "+ ticket.getPriority()+ "\n" +
                "Sentiment: " + ticket.getSentiment() + "\n" +
                "Entities: " + ticket.getEntities() + "\n" +
                "Key Phrases: " + ticket.getKeyPhrases() + "\n" +
                "Status: " + ticket.getStatus() + "\n" +
                "Agent: " + ticket.getAgent();

        // Step 7: Publish the message to SNS
        PublishRequest snsRequest = PublishRequest.builder()
                .topicArn(snsTopicArn)
                .message(snsMessage) // Send the ticket details with analysis results
                .subject("Support Ticket from " + ticket.getName())
                .build();
        snsClient.publish(snsRequest);

        // Step 8: Prepare message for SQS
        String sqsMessageBody = "Name: " + ticket.getName() +
                ", Email: " + ticket.getEmail() +
                ", IssueType: " + ticket.getIssueType() +
                ", Description: " + ticket.getDescription() +
                ", Priority: "+ ticket.getPriority()+
                ", Sentiment: " + ticket.getSentiment() +
                ", Entities: " + ticket.getEntities() +
                ", Key Phrases: " + ticket.getKeyPhrases() +
                ", Status: " + ticket.getStatus() +
                ", Agent: " + ticket.getAgent();

        // Step 9: Push the ticket details to SQS
        SendMessageRequest sqsRequest = SendMessageRequest.builder()
                .queueUrl(sqsQueueUrl)
                .messageBody(sqsMessageBody) // Send the ticket details with analysis results
                .build();
        sqsClient.sendMessage(sqsRequest);
    }


    public void assignTicketToAgent(String email, String issueType, String agent) {
        ticketRepository.assignTicketToAgent(email, issueType, agent);
    }

    // Change the status of a ticket
    public void changeTicketStatus(String email, String issueType, String newStatus) {
        ticketRepository.changeTicketStatus(email, issueType, newStatus);
    }

    // Mark ticket as resolved
    public void markTicketAsResolved(String email, String issueType) {
        changeTicketStatus(email, issueType, "Resolved");
    }


    public void autoAssignTicket(String email, String issueType, String category, String region) {
        List<Agent> agents = agentRepository.getAllAgents();

        // Filter agents based on category and region
        List<Agent> filteredAgents = agents.stream()
                .filter(agent -> agent.getExpertise().contains(category)) // Match expertise
                .filter(agent -> agent.getRegion().equalsIgnoreCase(region)) // Match region
                .collect(Collectors.toList());

        if (filteredAgents.isEmpty()) {
            throw new RuntimeException("No agents available for the given category and region");
        }

        // Find the agent with the least workload
        Optional<Agent> bestAgent = filteredAgents.stream()
                .min(Comparator.comparingInt(Agent::getActiveTickets));

        if (bestAgent.isPresent()) {
            Agent agent = bestAgent.get();

            // Assign the ticket to the selected agent
            ticketRepository.assignTicketToAgent(email, issueType, agent.getAgentId());

            // Update the agent's workload
            agent.setActiveTickets(agent.getActiveTickets() + 1);
            agentRepository.updateAgent(agent);
        } else {
            throw new RuntimeException("No agents available for assignment");
        }
    }

    // Fetch tickets by email
    public List<Ticket> getTicketsByEmail(String email) {
        return ticketRepository.findByEmail(email);
    }


    public List<Agent> getAllAgents() {
        return agentRepository.getAllAgents();
    }

    // Add a new agent
    public void addAgent(Agent agent) {
        agentRepository.saveAgent(agent);
    }


    public List<Ticket> getTicketsByAgentId(String agentId) {
        return ticketRepository.findTicketsByAgentId(agentId);
    }
}

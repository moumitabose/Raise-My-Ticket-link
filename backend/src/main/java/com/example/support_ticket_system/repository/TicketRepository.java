package com.example.support_ticket_system.repository;

import com.example.support_ticket_system.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TicketRepository {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbClient dynamoDbClient;  // Add DynamoDbClient for scan operations
    private final DynamoDbTable<Ticket> ticketTable;

    public TicketRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient,DynamoDbClient dynamoDbClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.dynamoDbClient = dynamoDbClient;
        this.ticketTable = dynamoDbEnhancedClient.table("SupportTickets", TableSchema.fromBean(Ticket.class));
    }

    // Create ticket
    public void createTicket(Ticket ticket) {
        ticketTable.putItem(ticket);
    }

    // Get ticket by email and issue type
    public Ticket getTicket(String email, String issueType) {
        return ticketTable.getItem(r -> r.key(k -> k.partitionValue(email).sortValue(issueType)));
    }

    // Get all tickets
    public List<Ticket> getAllTickets() {
        return ticketTable.scan().items().stream().collect(Collectors.toList());
    }

    // Update ticket (you can add specific update logic based on your needs)
    public void updateTicket(Ticket ticket) {
        ticketTable.putItem(ticket);
    }

    // Delete ticket by email and issue type
    public void deleteTicket(String email, String issueType) {
        ticketTable.deleteItem(r -> r.key(k -> k.partitionValue(email).sortValue(issueType)));
    }


    public void assignTicketToAgent(String email, String issueType, String agent) {
        Ticket ticket = getTicket(email, issueType);
        if (ticket != null) {
            ticket.setAgent(agent);
            ticket.setStatus("In Progress"); // Optionally change the status when assigned
            updateTicket(ticket);  // Update the ticket in DynamoDB
        }
    }

    // Change the status of a ticket
    public void changeTicketStatus(String email, String issueType, String newStatus) {
        Ticket ticket = getTicket(email, issueType);
        if (ticket != null) {
            ticket.setStatus(newStatus);
            updateTicket(ticket);  // Update the ticket status in DynamoDB
        }
    }


    public Ticket findTicketByEmailAndIssueType(String email, String issueType) {
        try {
            return ticketTable.getItem(r -> r.key(k -> k.partitionValue(email).sortValue(issueType)));
        } catch (ResourceNotFoundException e) {
            return null; // Return null if the ticket is not found
        }
    }





    public List<Ticket> findTicketsByAgentId(String agent) {
        System.out.println("Searching for tickets assigned to agent: " + agent);

        // Build the query request
        QueryRequest queryRequest = QueryRequest.builder()
                .tableName("SupportTickets")
                .indexName("agent-index") // Use the GSI name
                .keyConditionExpression("#agent = :agent") // Use alias for reserved keyword
                .expressionAttributeNames(Map.of("#agent", "agent")) // Alias "agent"
                .expressionAttributeValues(Map.of(":agent", AttributeValue.builder().s(agent).build()))
                .build();

        // Execute the query
        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        // Parse the response
        List<Ticket> tickets = new ArrayList<>();
        for (Map<String, AttributeValue> item : queryResponse.items()) {
            Ticket ticket = new Ticket();
            ticket.setName(item.getOrDefault("name", AttributeValue.builder().s("").build()).s());
            ticket.setEmail(item.getOrDefault("email", AttributeValue.builder().s("").build()).s());
            ticket.setIssueType(item.getOrDefault("issueType", AttributeValue.builder().s("").build()).s());
            ticket.setDescription(item.getOrDefault("description", AttributeValue.builder().s("").build()).s());
            ticket.setCategory(item.getOrDefault("category", AttributeValue.builder().s("").build()).s());
            ticket.setStatus(item.getOrDefault("status", AttributeValue.builder().s("").build()).s());
            ticket.setAgent(item.getOrDefault("agent", AttributeValue.builder().s("").build()).s());
            ticket.setRegion(item.getOrDefault("region", AttributeValue.builder().s("").build()).s());
            tickets.add(ticket);
        }

        System.out.println("Found " + tickets.size() + " tickets for agent: " + agent);
        return tickets;
    }


    public List<Ticket> findByEmail(String email) {
        return ticketTable.query(r -> r.queryConditional(
                QueryConditional.keyEqualTo(Key.builder().partitionValue(email).build())
        )).items().stream().collect(Collectors.toList());
    }


}

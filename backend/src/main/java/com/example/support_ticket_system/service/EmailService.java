package com.example.support_ticket_system.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {


    private final SesClient sesClient;

    public EmailService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public EmailService() {
        // Initialize the SesClient with the specified region
        this.sesClient = SesClient.builder()
                .region(Region.US_EAST_2) // Set region explicitly
                .build();
    }

    public void sendEmail(String toAddress, String subject, String body) {
        try {
            // Build the SendEmailRequest
            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(Destination.builder()
                            .toAddresses(toAddress)
                            .build())
                    .message(Message.builder()
                            .subject(Content.builder()
                                    .data(subject)
                                    .build())
                            .body(Body.builder()
                                    .text(Content.builder()
                                            .data(body)
                                            .build())
                                    .build())
                            .build())
                    .source("support@moumitatech.com") // Verified SES email
                    .build();

            // Send the email using SesClient
            sesClient.sendEmail(request);
            System.out.println("Email sent successfully to: " + toAddress);

        } catch (SesException e) {
            System.err.println("Failed to send email: " + e.awsErrorDetails().errorMessage());
        }
    }

}

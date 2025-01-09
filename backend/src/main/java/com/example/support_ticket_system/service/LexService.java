package com.example.support_ticket_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.lexruntimev2.LexRuntimeV2Client;
import software.amazon.awssdk.services.lexruntimev2.model.Message;
import software.amazon.awssdk.services.lexruntimev2.model.RecognizeTextRequest;
import software.amazon.awssdk.services.lexruntimev2.model.RecognizeTextResponse;

import java.util.List;

@Service
public class LexService {


    private final LexRuntimeV2Client lexRuntimeV2Client;

    @Autowired
    public LexService(LexRuntimeV2Client lexRuntimeV2Client) {
        this.lexRuntimeV2Client = lexRuntimeV2Client;
    }

    public String processUserInput(String userInput) {
        // Create a request to interact with the Lex bot
        RecognizeTextRequest request = RecognizeTextRequest.builder()
                .botId("RWBKOCOQBN").botAliasId("UDR7FFCORC") // ID of your bot
                .localeId("en_US")  // Locale (language) for the bot (example: en_US)
                .sessionId("user-" + System.currentTimeMillis())  // Use a dynamic session ID
                .text(userInput)  // The input text from the user
                .build();

        RecognizeTextResponse response = lexRuntimeV2Client.recognizeText(request);

        // Check if the response contains any messages
        if (response.messages().isEmpty()) {
            return "No response from Lex bot";
        }

        // Return the content of the first message from the bot
        return response.messages().get(0).content();
    }
}

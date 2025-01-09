package com.example.support_ticket_system.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.*;

@Service
public class ComprehendService {
    private final ComprehendClient comprehendClient;

    // Constructor
    public ComprehendService() {
        this.comprehendClient = ComprehendClient.builder()
                .region(Region.US_EAST_2) // Choose appropriate region
                .build();
    }

    public String detectSentiment(String text) {
        DetectSentimentRequest request = DetectSentimentRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        DetectSentimentResponse response = comprehendClient.detectSentiment(request);
        return response.sentimentAsString();
    }

    // Method to detect entities (e.g., person, date, location, etc.)
    public String detectEntities(String text) {
        DetectEntitiesRequest request = DetectEntitiesRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        DetectEntitiesResponse response = comprehendClient.detectEntities(request);
        StringBuilder entities = new StringBuilder();
        for (Entity entity : response.entities()) {
            entities.append("Entity: ").append(entity.text()).append(", Type: ").append(entity.type()).append("\n");
        }
        return entities.toString();
    }

    // Method to detect key phrases
    public String detectKeyPhrases(String text) {
        DetectKeyPhrasesRequest request = DetectKeyPhrasesRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        DetectKeyPhrasesResponse response = comprehendClient.detectKeyPhrases(request);
        StringBuilder keyPhrases = new StringBuilder();
        for (KeyPhrase keyPhrase : response.keyPhrases()) {
            keyPhrases.append("Key Phrase: ").append(keyPhrase.text()).append("\n");
        }
        return keyPhrases.toString();
    }

    // Method to detect syntax (e.g., part of speech)
    // Method to detect syntax (e.g., part of speech)
    public String detectSyntax(String text) {
        DetectSyntaxRequest request = DetectSyntaxRequest.builder()
                .text(text)
                .languageCode("en")
                .build();

        DetectSyntaxResponse response = comprehendClient.detectSyntax(request);
        StringBuilder syntax = new StringBuilder();
        for (SyntaxToken token : response.syntaxTokens()) {
            syntax.append("Token: ").append(token.text())
                    .append(", Part of Speech: ").append(token.partOfSpeech().tagAsString())
                    .append("\n");
        }
        return syntax.toString();
    }








}

package com.example.support_ticket_system.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.polly.PollyClient;
import software.amazon.awssdk.services.polly.model.OutputFormat;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechRequest;
import software.amazon.awssdk.services.polly.model.SynthesizeSpeechResponse;
import software.amazon.awssdk.services.polly.model.VoiceId;
import software.amazon.awssdk.services.translate.TranslateClient;
import software.amazon.awssdk.services.translate.model.TranslateTextRequest;
import software.amazon.awssdk.services.translate.model.TranslateTextResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
public class PollyService {


    private final PollyClient pollyClient;
    private final TranslateClient translateClient;

    // Inject the Polly client using dependency injection
    public PollyService(PollyClient pollyClient,TranslateClient translateClient) {
        this.pollyClient = pollyClient;
        this.translateClient = translateClient;
    }


//    public CompletableFuture<InputStream> convertTextToSpeech(String text) {
//        SynthesizeSpeechRequest synthesizeSpeechRequest = SynthesizeSpeechRequest.builder()
//                .text(text)
//                .outputFormat(OutputFormat.MP3)
//                .voiceId(VoiceId.JOANNA) // Specify the voice
//                .build();
//
//        CompletableFuture<ResponseInputStream<SynthesizeSpeechResponse>> future =
//                CompletableFuture.supplyAsync(() -> pollyClient.synthesizeSpeech(synthesizeSpeechRequest));
//
//        return future.thenApply(responseInputStream -> {
//            try {
//                byte[] audioBytes = responseInputStream.readAllBytes(); // Read audio bytes
//                return new ByteArrayInputStream(audioBytes); // Wrap bytes in InputStream
//            } catch (Exception e) {
//                throw new RuntimeException("Failed to read audio stream from Polly", e);
//            }
//        });
//    }

    private VoiceId getVoiceIdByLanguage(String language) {
        switch (language) {
            case "es-ES": return VoiceId.MIGUEL;
            case "fr-FR": return VoiceId.LISA;
            case "de-DE": return VoiceId.VICKI;
            case "it-IT": return VoiceId.GIORGIO;
            case "ja-JP": return VoiceId.MIZUKI;
            case "hi-IN": return VoiceId.ADITI;
            case "en-US":
            default: return VoiceId.JOANNA;
        }
    }

    // Method to translate text using AWS Translate
    private String translateText(String text, String targetLanguage) {
        TranslateTextRequest translateRequest = TranslateTextRequest.builder()
                .text(text)
                .sourceLanguageCode("en") // Assuming the input text is in English
                .targetLanguageCode(targetLanguage.split("-")[0]) // Extracting language code (e.g., 'hi' from 'hi-IN')
                .build();

        TranslateTextResponse translateResponse = translateClient.translateText(translateRequest);
        return translateResponse.translatedText();
    }

    // Method to synthesize speech based on translated text and language
    public byte[] synthesizeSpeech(String text, String language) throws IOException {
        String translatedText = translateText(text, language);
        VoiceId voiceId = getVoiceIdByLanguage(language);

        SynthesizeSpeechRequest request = SynthesizeSpeechRequest.builder()
                .text(translatedText)
                .voiceId(voiceId)
                .outputFormat(OutputFormat.MP3)
                .build();

        ResponseInputStream<SynthesizeSpeechResponse> response = pollyClient.synthesizeSpeech(request);

        try (InputStream inputStream = response) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }




}

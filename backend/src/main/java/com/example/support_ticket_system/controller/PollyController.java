package com.example.support_ticket_system.controller;

import com.example.support_ticket_system.model.PollyRequest;
import com.example.support_ticket_system.service.PollyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/polly")
public class PollyController {

    @Autowired
    private PollyService pollyService;

//    @GetMapping("/convertTextToSpeech")
//    public ResponseEntity<byte[]> convertTextToSpeech(@RequestParam String text) throws ExecutionException, InterruptedException, IOException {
//        // Convert text to speech (async)
//        InputStream audioStream = pollyService.convertTextToSpeech(text).get();
//
//        // Convert InputStream to byte array to send in response
//        byte[] audioBytes = audioStream.readAllBytes();
//
//        // Return audio as an MP3 response
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
//                .body(audioBytes);
//    }


    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesizeSpeech(@RequestBody PollyRequest request) {
        try {
            // Call the Polly service to synthesize speech after translation
            byte[] audioContent = pollyService.synthesizeSpeech(request.getText(), request.getLanguage());

            // Set the response headers for audio content
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "audio/mpeg");
            headers.add("Content-Disposition", "attachment; filename=\"speech.mp3\"");

            // Return the audio content as a byte array with response headers
            return new ResponseEntity<>(audioContent, headers, HttpStatus.OK);
        } catch (IOException e) {
            // Handle the error if speech synthesis fails
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

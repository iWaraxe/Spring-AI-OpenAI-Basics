package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.MistralAiService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/mistral")
public class MistralAiController {

    private final MistralAiService mistralAiService;

    public MistralAiController(MistralAiService mistralAiService) {
        this.mistralAiService = mistralAiService;
    }

    @GetMapping("/generate")
    public Map<String, String> generate(@RequestParam(value = "message", defaultValue = "Tell me about Mistral AI") String message) {
        ChatResponse response = mistralAiService.generateResponse(message);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "Tell me about Mistral AI") String message) {
        return mistralAiService.generateStreamResponse(message)
                .map(response -> response.getResult().getOutput().getContent());
    }

    @PostMapping("/temperature")
    public Map<String, String> generateWithTemperature(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Double temperature = ((Number) request.get("temperature")).doubleValue();
        
        ChatResponse response = mistralAiService.generateWithTemperature(message, temperature);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/tokens")
    public Map<String, String> generateWithMaxTokens(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Integer maxTokens = ((Number) request.get("maxTokens")).intValue();
        
        ChatResponse response = mistralAiService.generateWithMaxTokens(message, maxTokens);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/safe")
    public Map<String, String> generateWithSafePrompt(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Boolean safePrompt = (Boolean) request.get("safePrompt");
        
        ChatResponse response = mistralAiService.generateWithSafePrompt(message, safePrompt);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/multimodal")
    public Map<String, String> generateMultiModal(
            @RequestBody Map<String, String> request) {
        String message = request.get("message");
        String imageUrl = request.get("imageUrl");
        
        ChatResponse response = mistralAiService.generateMultiModal(message, imageUrl);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/seed")
    public Map<String, String> generateWithRandomSeed(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Integer randomSeed = ((Number) request.get("randomSeed")).intValue();
        
        ChatResponse response = mistralAiService.generateWithRandomSeed(message, randomSeed);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/json")
    public Map<String, String> generateJsonResponse(
            @RequestBody Map<String, String> request) {
        String message = request.get("message");
        
        ChatResponse response = mistralAiService.generateJsonResponse(message);
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/stop")
    public Map<String, String> generateWithStopSequences(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        java.util.List<String> stopSequences = (java.util.List<String>) request.get("stopSequences");
        
        ChatResponse response = mistralAiService.generateWithStopSequences(
                message, stopSequences.toArray(new String[0]));
        return Map.of("response", response.getResult().getOutput().getContent());
    }

    @PostMapping("/topP")
    public Map<String, String> generateWithTopP(
            @RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Double topP = ((Number) request.get("topP")).doubleValue();
        
        ChatResponse response = mistralAiService.generateWithTopP(message, topP);
        return Map.of("response", response.getResult().getOutput().getContent());
    }
}
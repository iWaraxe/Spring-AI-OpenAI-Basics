package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface MistralAiService {
    
    ChatResponse generateResponse(String message);
    
    Flux<ChatResponse> generateStreamResponse(String message);
    
    ChatResponse generateWithTemperature(String message, Double temperature);
    
    ChatResponse generateWithMaxTokens(String message, Integer maxTokens);
    
    ChatResponse generateWithSafePrompt(String message, Boolean safePrompt);
    
    ChatResponse generateMultiModal(String message, String imageUrl);
    
    ChatResponse generateWithRandomSeed(String message, Integer randomSeed);
    
    ChatResponse generateJsonResponse(String message);
    
    ChatResponse generateWithStopSequences(String message, String... stopSequences);
    
    ChatResponse generateWithTopP(String message, Double topP);
}
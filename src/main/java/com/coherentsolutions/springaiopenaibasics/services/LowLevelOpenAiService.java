package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.Role;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Service interface for demonstrating low-level OpenAI API usage.
 * 
 * This service showcases when and why you might use the low-level OpenAI API
 * instead of the high-level ChatModel interface:
 * 
 * 1. Fine-grained control over request/response handling
 * 2. Access to raw HTTP response metadata
 * 3. Custom error handling and retry logic
 * 4. Direct access to OpenAI-specific features not abstracted by Spring AI
 * 5. Performance optimization for specific use cases
 */
public interface LowLevelOpenAiService {
    
    /**
     * Basic chat completion using low-level API
     * Benefits: Direct access to HTTP response metadata
     */
    ResponseEntity<ChatCompletion> basicChatCompletion(String message);
    
    /**
     * Streaming chat completion using low-level API
     * Benefits: Raw streaming response handling
     */
    Flux<ChatCompletionChunk> streamingChatCompletion(String message);
    
    /**
     * Chat completion with custom request parameters
     * Benefits: Full control over OpenAI-specific parameters
     */
    ResponseEntity<ChatCompletion> customParameterChat(String message, String model, 
                                                      Double temperature, Integer maxTokens);
    
    /**
     * Batch processing multiple messages efficiently
     * Benefits: Optimized for bulk operations
     */
    List<ResponseEntity<ChatCompletion>> batchProcessMessages(List<String> messages);
    
    /**
     * Chat with system and user messages
     * Benefits: Direct control over message roles and structure
     */
    ResponseEntity<ChatCompletion> systemUserChat(String systemPrompt, String userMessage);
    
    /**
     * Extract detailed response metadata
     * Benefits: Access to usage statistics, model info, timestamps
     */
    Map<String, Object> getDetailedResponseMetadata(String message);
    
    /**
     * Chat with logit bias for token control
     * Benefits: Fine-grained control over token generation
     */
    ResponseEntity<ChatCompletion> chatWithLogitBias(String message, Map<String, Integer> logitBias);
    
    /**
     * Chat with custom HTTP headers
     * Benefits: Full control over HTTP request configuration
     */
    ResponseEntity<ChatCompletion> chatWithCustomHeaders(String message, Map<String, String> headers);
}
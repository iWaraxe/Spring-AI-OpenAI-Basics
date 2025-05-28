package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionResponse;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionRequest;
import org.springframework.ai.anthropic.api.AnthropicApi.StreamResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Service interface for demonstrating low-level Anthropic API usage.
 * 
 * This service showcases when and why you might use the low-level Anthropic API
 * instead of the high-level ChatModel interface:
 * 
 * 1. Direct access to Anthropic-specific features and parameters
 * 2. Fine-grained control over message structure and roles
 * 3. Access to raw HTTP response metadata and error handling
 * 4. Custom streaming response processing
 * 5. Advanced Anthropic features like system prompts and stop sequences
 * 6. Performance optimization for specific use cases
 * 7. Enterprise integration requirements
 */
public interface LowLevelAnthropicService {
    
    /**
     * Basic chat completion using low-level Anthropic API
     * Benefits: Direct access to HTTP response metadata and Anthropic-specific response structure
     */
    ResponseEntity<ChatCompletionResponse> basicChatCompletion(String message);
    
    /**
     * Streaming chat completion using low-level Anthropic API
     * Benefits: Raw streaming response handling with Anthropic's event model
     */
    Flux<StreamResponse> streamingChatCompletion(String message);
    
    /**
     * Chat completion with custom Anthropic parameters
     * Benefits: Full control over Claude-specific parameters like stop sequences, top-k, top-p
     */
    ResponseEntity<ChatCompletionResponse> customParameterChat(String message, String model, 
                                                              Double temperature, Integer maxTokens,
                                                              List<String> stopSequences);
    
    /**
     * Chat with system prompt and user message (Anthropic's preferred structure)
     * Benefits: Direct control over Anthropic's message format and system prompts
     */
    ResponseEntity<ChatCompletionResponse> systemPromptChat(String systemPrompt, String userMessage);
    
    /**
     * Batch processing multiple messages efficiently
     * Benefits: Optimized for bulk operations with Anthropic's API
     */
    List<ResponseEntity<ChatCompletionResponse>> batchProcessMessages(List<String> messages);
    
    /**
     * Extract detailed Anthropic-specific response metadata
     * Benefits: Access to usage statistics, model info, stop reasons, Claude-specific data
     */
    Map<String, Object> getDetailedResponseMetadata(String message);
    
    /**
     * Chat with advanced Anthropic parameters (top-k, top-p)
     * Benefits: Fine-grained control over Claude's generation behavior
     */
    ResponseEntity<ChatCompletionResponse> advancedParameterChat(String message, 
                                                               Integer topK, Double topP);
    
    /**
     * Multi-turn conversation with conversation history
     * Benefits: Direct control over conversation structure and message roles
     */
    ResponseEntity<ChatCompletionResponse> multiTurnConversation(List<Map<String, String>> conversationHistory);
    
    /**
     * Chat with custom HTTP headers for enterprise use
     * Benefits: Full control over HTTP request configuration
     */
    ResponseEntity<ChatCompletionResponse> chatWithCustomHeaders(String message, 
                                                               Map<String, String> headers);
    
    /**
     * Get Anthropic API capabilities and model information
     * Benefits: Access to Anthropic-specific model capabilities and limits
     */
    Map<String, Object> getAnthropicApiInfo();
}
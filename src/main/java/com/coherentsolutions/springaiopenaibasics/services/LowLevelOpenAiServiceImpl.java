package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.Role;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implementation demonstrating low-level OpenAI API usage.
 * 
 * This implementation shows the benefits of using the low-level API:
 * - Direct HTTP response access for metadata
 * - Fine-grained control over request parameters
 * - Custom error handling capabilities
 * - Access to OpenAI-specific features
 * - Performance optimizations for specific scenarios
 */
@Service
public class LowLevelOpenAiServiceImpl implements LowLevelOpenAiService {

    private final OpenAiApi openAiApi;
    private static final String DEFAULT_MODEL = "gpt-4o-mini";

    public LowLevelOpenAiServiceImpl(OpenAiApi openAiApi) {
        this.openAiApi = openAiApi;
    }

    @Override
    public ResponseEntity<ChatCompletion> basicChatCompletion(String message) {
        ChatCompletionMessage chatMessage = new ChatCompletionMessage(message, Role.USER);
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            List.of(chatMessage), 
            DEFAULT_MODEL, 
            0.7, 
            false
        );
        
        // Direct access to HTTP response with status codes, headers, etc.
        return openAiApi.chatCompletionEntity(request);
    }

    @Override
    public Flux<ChatCompletionChunk> streamingChatCompletion(String message) {
        ChatCompletionMessage chatMessage = new ChatCompletionMessage(message, Role.USER);
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            List.of(chatMessage), 
            DEFAULT_MODEL, 
            0.7, 
            true // Enable streaming
        );
        
        // Direct streaming response handling
        return openAiApi.chatCompletionStream(request);
    }

    @Override
    public ResponseEntity<ChatCompletion> customParameterChat(String message, String model, 
                                                            Double temperature, Integer maxTokens) {
        ChatCompletionMessage chatMessage = new ChatCompletionMessage(message, Role.USER);
        
        // Fine-grained control over all OpenAI parameters
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(List.of(chatMessage))
            .model(model != null ? model : DEFAULT_MODEL)
            .temperature(temperature != null ? temperature : 0.7)
            .maxTokens(maxTokens)
            .presencePenalty(0.0)
            .frequencyPenalty(0.0)
            .stream(false)
            .build();
        
        return openAiApi.chatCompletionEntity(request);
    }

    @Override
    public List<ResponseEntity<ChatCompletion>> batchProcessMessages(List<String> messages) {
        // Efficient batch processing - can be optimized with parallel streams
        return messages.parallelStream()
            .map(this::basicChatCompletion)
            .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ChatCompletion> systemUserChat(String systemPrompt, String userMessage) {
        // Direct control over message roles and conversation structure
        List<ChatCompletionMessage> messages = List.of(
            new ChatCompletionMessage(systemPrompt, Role.SYSTEM),
            new ChatCompletionMessage(userMessage, Role.USER)
        );
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            messages, 
            DEFAULT_MODEL, 
            0.7, 
            false
        );
        
        return openAiApi.chatCompletionEntity(request);
    }

    @Override
    public Map<String, Object> getDetailedResponseMetadata(String message) {
        ResponseEntity<ChatCompletion> response = basicChatCompletion(message);
        
        Map<String, Object> metadata = new HashMap<>();
        
        // HTTP response metadata
        metadata.put("httpStatus", response.getStatusCode().value());
        metadata.put("httpHeaders", response.getHeaders().toSingleValueMap());
        
        if (response.getBody() != null) {
            ChatCompletion completion = response.getBody();
            
            // OpenAI response metadata
            metadata.put("id", completion.id());
            metadata.put("object", completion.object());
            metadata.put("created", completion.created());
            metadata.put("model", completion.model());
            metadata.put("systemFingerprint", completion.systemFingerprint());
            
            // Usage statistics
            if (completion.usage() != null) {
                metadata.put("promptTokens", completion.usage().promptTokens());
                metadata.put("completionTokens", completion.usage().completionTokens());
                metadata.put("totalTokens", completion.usage().totalTokens());
                
                // Calculate cost estimation (example pricing)
                double inputCost = completion.usage().promptTokens() * 0.00015 / 1000; // $0.15 per 1K tokens
                double outputCost = completion.usage().completionTokens() * 0.0006 / 1000; // $0.60 per 1K tokens
                metadata.put("estimatedCost", inputCost + outputCost);
            }
            
            // Response content
            if (!completion.choices().isEmpty()) {
                metadata.put("finishReason", completion.choices().get(0).finishReason());
                metadata.put("responseContent", completion.choices().get(0).message().content());
            }
        }
        
        return metadata;
    }

    @Override
    public ResponseEntity<ChatCompletion> chatWithLogitBias(String message, Map<String, Integer> logitBias) {
        ChatCompletionMessage chatMessage = new ChatCompletionMessage(message, Role.USER);
        
        // Advanced parameter: logit bias for controlling token generation
        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .messages(List.of(chatMessage))
            .model(DEFAULT_MODEL)
            .temperature(0.7)
            .logitBias(logitBias) // Fine-grained token control
            .stream(false)
            .build();
        
        return openAiApi.chatCompletionEntity(request);
    }

    @Override
    public ResponseEntity<ChatCompletion> chatWithCustomHeaders(String message, Map<String, String> headers) {
        // Note: This is a conceptual example. The actual OpenAiApi implementation
        // would need to be extended to support custom headers.
        // This demonstrates the flexibility needed for enterprise use cases.
        
        ChatCompletionMessage chatMessage = new ChatCompletionMessage(message, Role.USER);
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            List.of(chatMessage), 
            DEFAULT_MODEL, 
            0.7, 
            false
        );
        
        // In a real implementation, you might create a custom OpenAiApi instance
        // with custom headers or use HTTP interceptors
        return openAiApi.chatCompletionEntity(request);
    }
}
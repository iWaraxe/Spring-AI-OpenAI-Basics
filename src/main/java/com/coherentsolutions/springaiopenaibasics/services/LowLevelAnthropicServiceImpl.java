package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionResponse;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionRequest;
import org.springframework.ai.anthropic.api.AnthropicApi.AnthropicMessage;
import org.springframework.ai.anthropic.api.AnthropicApi.ContentBlock;
import org.springframework.ai.anthropic.api.AnthropicApi.Role;
import org.springframework.ai.anthropic.api.AnthropicApi.StreamResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Implementation demonstrating low-level Anthropic API usage.
 * 
 * This implementation shows the benefits of using the low-level Anthropic API:
 * - Direct access to Anthropic's message structure and event model
 * - Fine-grained control over Claude-specific parameters
 * - Custom error handling capabilities
 * - Access to Anthropic-specific features and metadata
 * - Performance optimizations for specific scenarios
 */
@Service
public class LowLevelAnthropicServiceImpl implements LowLevelAnthropicService {

    private final AnthropicApi anthropicApi;
    private static final String DEFAULT_MODEL = "claude-3-5-sonnet-latest";

    public LowLevelAnthropicServiceImpl(AnthropicApi anthropicApi) {
        this.anthropicApi = anthropicApi;
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> basicChatCompletion(String message) {
        AnthropicMessage anthropicMessage = new AnthropicMessage(
            List.of(new ContentBlock(message)), 
            Role.USER
        );
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            DEFAULT_MODEL,
            List.of(anthropicMessage),
            null, // system prompt
            500,  // max tokens
            0.7,  // temperature
            false // stream
        );
        
        // Direct access to HTTP response with status codes, headers, etc.
        return anthropicApi.chatCompletionEntity(request);
    }

    @Override
    public Flux<StreamResponse> streamingChatCompletion(String message) {
        AnthropicMessage anthropicMessage = new AnthropicMessage(
            List.of(new ContentBlock(message)), 
            Role.USER
        );
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            DEFAULT_MODEL,
            List.of(anthropicMessage),
            null, // system prompt
            500,  // max tokens
            0.7,  // temperature
            true  // Enable streaming
        );
        
        // Direct streaming response handling with Anthropic's event model
        return anthropicApi.chatCompletionStream(request);
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> customParameterChat(String message, String model, 
                                                                    Double temperature, Integer maxTokens,
                                                                    List<String> stopSequences) {
        AnthropicMessage anthropicMessage = new AnthropicMessage(
            List.of(new ContentBlock(message)), 
            Role.USER
        );
        
        // Fine-grained control over all Anthropic parameters
        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
            .model(model != null ? model : DEFAULT_MODEL)
            .messages(List.of(anthropicMessage))
            .maxTokens(maxTokens != null ? maxTokens : 500)
            .temperature(temperature != null ? temperature : 0.7)
            .stream(false);
        
        // Anthropic-specific: stop sequences
        if (stopSequences != null && !stopSequences.isEmpty()) {
            requestBuilder.stopSequences(stopSequences);
        }
        
        return anthropicApi.chatCompletionEntity(requestBuilder.build());
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> systemPromptChat(String systemPrompt, String userMessage) {
        AnthropicMessage userMsg = new AnthropicMessage(
            List.of(new ContentBlock(userMessage)), 
            Role.USER
        );
        
        // Anthropic's preferred way to handle system prompts
        ChatCompletionRequest request = new ChatCompletionRequest(
            DEFAULT_MODEL,
            List.of(userMsg),
            systemPrompt, // System prompt as separate parameter
            500,
            0.7,
            false
        );
        
        return anthropicApi.chatCompletionEntity(request);
    }

    @Override
    public List<ResponseEntity<ChatCompletionResponse>> batchProcessMessages(List<String> messages) {
        // Efficient batch processing - can be optimized with parallel streams
        return messages.parallelStream()
            .map(this::basicChatCompletion)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getDetailedResponseMetadata(String message) {
        ResponseEntity<ChatCompletionResponse> response = basicChatCompletion(message);
        
        Map<String, Object> metadata = new HashMap<>();
        
        // HTTP response metadata
        metadata.put("httpStatus", response.getStatusCode().value());
        metadata.put("httpHeaders", response.getHeaders().toSingleValueMap());
        
        if (response.getBody() != null) {
            ChatCompletionResponse completion = response.getBody();
            
            // Anthropic response metadata
            metadata.put("id", completion.id());
            metadata.put("type", completion.type());
            metadata.put("role", completion.role());
            metadata.put("model", completion.model());
            
            // Usage statistics (Anthropic-specific structure)
            if (completion.usage() != null) {
                metadata.put("inputTokens", completion.usage().inputTokens());
                metadata.put("outputTokens", completion.usage().outputTokens());
                
                // Calculate cost estimation for Claude (example pricing)
                double inputCost = completion.usage().inputTokens() * 0.000015; // $15 per 1M tokens
                double outputCost = completion.usage().outputTokens() * 0.000075; // $75 per 1M tokens
                metadata.put("estimatedCost", inputCost + outputCost);
            }
            
            // Response content and stop reason
            if (completion.content() != null && !completion.content().isEmpty()) {
                metadata.put("responseContent", completion.content().get(0).text());
                metadata.put("stopReason", completion.stopReason());
                metadata.put("stopSequence", completion.stopSequence());
            }
        }
        
        return metadata;
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> advancedParameterChat(String message, 
                                                                       Integer topK, Double topP) {
        AnthropicMessage anthropicMessage = new AnthropicMessage(
            List.of(new ContentBlock(message)), 
            Role.USER
        );
        
        // Advanced Anthropic parameters for fine-tuned generation control
        ChatCompletionRequest.Builder requestBuilder = ChatCompletionRequest.builder()
            .model(DEFAULT_MODEL)
            .messages(List.of(anthropicMessage))
            .maxTokens(500)
            .temperature(0.7)
            .stream(false);
        
        // Claude-specific advanced parameters
        if (topK != null) {
            requestBuilder.topK(topK);
        }
        
        if (topP != null) {
            requestBuilder.topP(topP);
        }
        
        return anthropicApi.chatCompletionEntity(requestBuilder.build());
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> multiTurnConversation(List<Map<String, String>> conversationHistory) {
        // Convert conversation history to Anthropic message format
        List<AnthropicMessage> messages = conversationHistory.stream()
            .map(turn -> {
                String role = turn.get("role");
                String content = turn.get("content");
                Role anthropicRole = "user".equals(role) ? Role.USER : Role.ASSISTANT;
                return new AnthropicMessage(List.of(new ContentBlock(content)), anthropicRole);
            })
            .collect(Collectors.toList());
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            DEFAULT_MODEL,
            messages,
            null, // system prompt
            500,
            0.7,
            false
        );
        
        return anthropicApi.chatCompletionEntity(request);
    }

    @Override
    public ResponseEntity<ChatCompletionResponse> chatWithCustomHeaders(String message, 
                                                                       Map<String, String> headers) {
        // Note: This is a conceptual example. The actual AnthropicApi implementation
        // would need to be extended to support custom headers.
        // This demonstrates the flexibility needed for enterprise use cases.
        
        AnthropicMessage anthropicMessage = new AnthropicMessage(
            List.of(new ContentBlock(message)), 
            Role.USER
        );
        
        ChatCompletionRequest request = new ChatCompletionRequest(
            DEFAULT_MODEL,
            List.of(anthropicMessage),
            null,
            500,
            0.7,
            false
        );
        
        // In a real implementation, you might create a custom AnthropicApi instance
        // with custom headers or use HTTP interceptors
        return anthropicApi.chatCompletionEntity(request);
    }

    @Override
    public Map<String, Object> getAnthropicApiInfo() {
        return Map.of(
            "supportedModels", List.of(
                "claude-3-5-sonnet-latest",
                "claude-3-opus-20240229", 
                "claude-3-sonnet-20240229",
                "claude-3-haiku-20240307"
            ),
            "maxTokensLimits", Map.of(
                "claude-3-5-sonnet-latest", 8192,
                "claude-3-opus-20240229", 4096,
                "claude-3-sonnet-20240229", 4096,
                "claude-3-haiku-20240307", 4096
            ),
            "contextWindows", Map.of(
                "claude-3-5-sonnet-latest", 200000,
                "claude-3-opus-20240229", 200000,
                "claude-3-sonnet-20240229", 200000,
                "claude-3-haiku-20240307", 200000
            ),
            "supportedFeatures", List.of(
                "System prompts",
                "Multi-turn conversations", 
                "Custom stop sequences",
                "Advanced sampling (top-k, top-p)",
                "Vision capabilities (image analysis)",
                "PDF processing (Claude 3.5 Sonnet)",
                "Tool/Function calling",
                "Streaming responses"
            ),
            "anthropicSpecificFeatures", List.of(
                "Constitutional AI training",
                "Helpful, Harmless, Honest principles",
                "Advanced reasoning capabilities",
                "Code generation and analysis",
                "Mathematical problem solving",
                "Creative writing assistance",
                "Document analysis and summarization"
            )
        );
    }
}
package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelAnthropicService;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionResponse;
import org.springframework.ai.anthropic.api.AnthropicApi.StreamResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * REST controller demonstrating low-level Anthropic API usage.
 * 
 * This controller showcases scenarios where the low-level Anthropic API provides
 * advantages over the high-level ChatModel interface:
 * 
 * 1. Direct access to Anthropic's message structure and event model
 * 2. Fine-grained control over Claude-specific parameters
 * 3. Access to HTTP metadata and Anthropic-specific response data
 * 4. Custom streaming response processing
 * 5. Advanced Claude features like system prompts and stop sequences
 * 6. Enterprise integration requirements
 */
@RestController
@RequestMapping("/api/anthropic-lowlevel")
public class LowLevelAnthropicController {

    private final LowLevelAnthropicService lowLevelService;

    public LowLevelAnthropicController(LowLevelAnthropicService lowLevelService) {
        this.lowLevelService = lowLevelService;
    }

    /**
     * Basic chat with HTTP response metadata access
     * 
     * Benefits over high-level API:
     * - Access to HTTP status codes and headers
     * - Anthropic-specific response structure
     * - Direct error handling capabilities
     */
    @PostMapping("/basic-chat")
    public ResponseEntity<ChatCompletionResponse> basicChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.basicChatCompletion(message);
    }

    /**
     * Streaming chat completion with Anthropic's event model
     * 
     * Benefits over high-level API:
     * - Direct access to raw streaming events
     * - Anthropic-specific streaming metadata
     * - Custom streaming processing logic
     */
    @PostMapping(value = "/stream-chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<StreamResponse> streamChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.streamingChatCompletion(message);
    }

    /**
     * Chat with custom Anthropic parameters
     * 
     * Benefits over high-level API:
     * - Fine-grained parameter control
     * - Claude-specific options (stop sequences, advanced sampling)
     * - Model-specific configurations
     */
    @PostMapping("/custom-params")
    public ResponseEntity<ChatCompletionResponse> customParamsChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.get("model");
        Double temperature = request.containsKey("temperature") ? 
            ((Number) request.get("temperature")).doubleValue() : null;
        Integer maxTokens = request.containsKey("maxTokens") ? 
            ((Number) request.get("maxTokens")).intValue() : null;
        
        @SuppressWarnings("unchecked")
        List<String> stopSequences = (List<String>) request.get("stopSequences");
        
        return lowLevelService.customParameterChat(message, model, temperature, maxTokens, stopSequences);
    }

    /**
     * System prompt + User message (Anthropic's preferred structure)
     * 
     * Benefits over high-level API:
     * - Direct control over Anthropic's message format
     * - System prompt as separate parameter (Claude's preferred way)
     * - Fine-grained conversation structure management
     */
    @PostMapping("/system-prompt-chat")
    public ResponseEntity<ChatCompletionResponse> systemPromptChat(@RequestBody Map<String, String> request) {
        String systemPrompt = request.get("systemPrompt");
        String userMessage = request.get("userMessage");
        return lowLevelService.systemPromptChat(systemPrompt, userMessage);
    }

    /**
     * Batch processing multiple messages
     * 
     * Benefits over high-level API:
     * - Optimized for bulk operations
     * - Parallel processing control
     * - Batch error handling with individual response metadata
     */
    @PostMapping("/batch-process")
    public List<ResponseEntity<ChatCompletionResponse>> batchProcess(@RequestBody Map<String, List<String>> request) {
        List<String> messages = request.get("messages");
        return lowLevelService.batchProcessMessages(messages);
    }

    /**
     * Detailed Anthropic-specific response metadata
     * 
     * Benefits over high-level API:
     * - Complete usage statistics
     * - Claude-specific cost calculation
     * - Stop reasons and sequences
     * - Performance metrics and debug information
     */
    @PostMapping("/metadata")
    public Map<String, Object> getMetadata(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.getDetailedResponseMetadata(message);
    }

    /**
     * Advanced Claude parameters (top-k, top-p)
     * 
     * Benefits over high-level API:
     * - Fine-grained generation control
     * - Advanced sampling parameters
     * - Claude-specific behavior tuning
     */
    @PostMapping("/advanced-params")
    public ResponseEntity<ChatCompletionResponse> advancedParamsChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        Integer topK = request.containsKey("topK") ? 
            ((Number) request.get("topK")).intValue() : null;
        Double topP = request.containsKey("topP") ? 
            ((Number) request.get("topP")).doubleValue() : null;
        return lowLevelService.advancedParameterChat(message, topK, topP);
    }

    /**
     * Multi-turn conversation with conversation history
     * 
     * Benefits over high-level API:
     * - Direct control over message roles and structure
     * - Conversation context management
     * - Custom conversation flow handling
     */
    @PostMapping("/multi-turn")
    public ResponseEntity<ChatCompletionResponse> multiTurnConversation(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> conversationHistory = 
            (List<Map<String, String>>) request.get("conversationHistory");
        return lowLevelService.multiTurnConversation(conversationHistory);
    }

    /**
     * Get Anthropic API capabilities and model information
     * 
     * Benefits over high-level API:
     * - Access to Claude-specific capabilities
     * - Model limitations and features
     * - Anthropic-specific information
     */
    @GetMapping("/api-info")
    public Map<String, Object> getApiInfo() {
        return lowLevelService.getAnthropicApiInfo();
    }

    /**
     * Demonstration endpoint comparing high-level vs low-level Anthropic API benefits
     */
    @GetMapping("/comparison-info")
    public Map<String, Object> getComparisonInfo() {
        return Map.of(
            "highLevelBenefits", List.of(
                "Simplified API usage with ChatModel interface",
                "Spring AI abstractions and utilities",
                "Portable across different AI providers",
                "Built-in Spring Boot integration",
                "Structured output converters (BeanOutputConverter)",
                "Automatic retry and error handling"
            ),
            "lowLevelBenefits", List.of(
                "Direct access to Anthropic's message structure",
                "Fine-grained control over Claude-specific parameters",
                "Access to HTTP response metadata and headers",
                "Raw streaming capabilities with event model",
                "Advanced Claude features (stop sequences, top-k, top-p)",
                "Custom error handling and retry logic",
                "Performance optimizations for specific scenarios",
                "Enterprise integration requirements"
            ),
            "anthropicSpecificUseCases", List.of(
                "System prompts with Claude's preferred structure",
                "Advanced sampling control (top-k, top-p)",
                "Custom stop sequences for controlled generation",
                "Multi-turn conversations with precise role management",
                "Cost tracking with Anthropic's token pricing",
                "Constitutional AI behavior customization",
                "Claude-specific error handling and response codes",
                "Vision and PDF processing capabilities"
            ),
            "whenToUseLowLevel", List.of(
                "Need direct access to Claude's response metadata",
                "Require Anthropic-specific parameters not in ChatModel",
                "Building enterprise applications with custom error handling",
                "Performance-critical applications requiring optimization",
                "Research and experimentation with Claude's capabilities",
                "Custom streaming processing requirements",
                "Integration with existing Anthropic-based systems",
                "Need for detailed cost and usage tracking"
            ),
            "whenToUseHighLevel", List.of(
                "Rapid application development",
                "Multi-provider compatibility (switching between AI providers)",
                "Standard business applications with simple requirements",
                "Spring Boot integration and auto-configuration",
                "Type-safe response handling with Java objects",
                "Built-in prompt templates and structured outputs",
                "Simple chat applications without advanced features"
            )
        );
    }
}
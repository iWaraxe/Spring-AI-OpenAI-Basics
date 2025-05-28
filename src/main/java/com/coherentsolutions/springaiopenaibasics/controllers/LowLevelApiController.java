package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOpenAiService;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * REST controller demonstrating low-level OpenAI API usage.
 * 
 * This controller showcases scenarios where the low-level API provides
 * advantages over the high-level ChatModel interface:
 * 
 * 1. HTTP metadata access
 * 2. Custom parameter control
 * 3. Streaming responses
 * 4. Batch processing
 * 5. Advanced OpenAI features
 */
@RestController
@RequestMapping("/api/lowlevel")
public class LowLevelApiController {

    private final LowLevelOpenAiService lowLevelService;

    public LowLevelApiController(LowLevelOpenAiService lowLevelService) {
        this.lowLevelService = lowLevelService;
    }

    /**
     * Basic chat with HTTP response metadata access
     * 
     * Benefits over high-level API:
     * - Access to HTTP status codes
     * - Response headers inspection
     * - Full response entity handling
     */
    @PostMapping("/basic-chat")
    public ResponseEntity<ChatCompletion> basicChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.basicChatCompletion(message);
    }

    /**
     * Streaming chat completion
     * 
     * Benefits over high-level API:
     * - Direct access to raw streaming chunks
     * - Custom chunk processing logic
     * - Streaming metadata access
     */
    @PostMapping(value = "/stream-chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatCompletionChunk> streamChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.streamingChatCompletion(message);
    }

    /**
     * Chat with custom OpenAI parameters
     * 
     * Benefits over high-level API:
     * - Fine-grained parameter control
     * - Model-specific options
     * - Advanced configurations
     */
    @PostMapping("/custom-params")
    public ResponseEntity<ChatCompletion> customParamsChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.get("model");
        Double temperature = request.containsKey("temperature") ? 
            ((Number) request.get("temperature")).doubleValue() : null;
        Integer maxTokens = request.containsKey("maxTokens") ? 
            ((Number) request.get("maxTokens")).intValue() : null;
        
        return lowLevelService.customParameterChat(message, model, temperature, maxTokens);
    }

    /**
     * Batch processing multiple messages
     * 
     * Benefits over high-level API:
     * - Optimized for bulk operations
     * - Parallel processing control
     * - Batch error handling
     */
    @PostMapping("/batch-process")
    public List<ResponseEntity<ChatCompletion>> batchProcess(@RequestBody Map<String, List<String>> request) {
        List<String> messages = request.get("messages");
        return lowLevelService.batchProcessMessages(messages);
    }

    /**
     * System + User message conversation
     * 
     * Benefits over high-level API:
     * - Direct message role control
     * - Conversation structure management
     * - Multi-turn conversation setup
     */
    @PostMapping("/system-user-chat")
    public ResponseEntity<ChatCompletion> systemUserChat(@RequestBody Map<String, String> request) {
        String systemPrompt = request.get("systemPrompt");
        String userMessage = request.get("userMessage");
        return lowLevelService.systemUserChat(systemPrompt, userMessage);
    }

    /**
     * Detailed response metadata extraction
     * 
     * Benefits over high-level API:
     * - Complete usage statistics
     * - Cost calculation
     * - Performance metrics
     * - Debug information
     */
    @PostMapping("/metadata")
    public Map<String, Object> getMetadata(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return lowLevelService.getDetailedResponseMetadata(message);
    }

    /**
     * Chat with logit bias (advanced feature)
     * 
     * Benefits over high-level API:
     * - Token-level generation control
     * - Advanced AI behavior tuning
     * - Research and experimentation
     */
    @PostMapping("/logit-bias")
    public ResponseEntity<ChatCompletion> logitBiasChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        Map<String, Integer> logitBias = (Map<String, Integer>) request.get("logitBias");
        return lowLevelService.chatWithLogitBias(message, logitBias);
    }

    /**
     * Demonstration endpoint comparing high-level vs low-level API benefits
     */
    @GetMapping("/comparison-info")
    public Map<String, Object> getComparisonInfo() {
        return Map.of(
            "highLevelBenefits", List.of(
                "Simplified API usage",
                "Spring AI abstractions",
                "Portable across different AI providers",
                "Built-in Spring Boot integration",
                "Structured output converters"
            ),
            "lowLevelBenefits", List.of(
                "Direct HTTP response access",
                "Fine-grained parameter control",
                "OpenAI-specific features",
                "Custom error handling",
                "Performance optimizations",
                "Raw streaming capabilities",
                "Advanced configuration options"
            ),
            "useCasesForLowLevel", List.of(
                "Custom retry and error handling logic",
                "Detailed usage and cost tracking",
                "Advanced OpenAI features (logit bias, etc.)",
                "Performance-critical applications",
                "Research and experimentation",
                "Enterprise integration requirements",
                "Custom HTTP configuration needs"
            ),
            "useCasesForHighLevel", List.of(
                "Rapid application development",
                "Multi-provider compatibility",
                "Standard business applications",
                "Spring Boot integration",
                "Type-safe response handling",
                "Built-in prompt templates",
                "Structured output conversion"
            )
        );
    }
}
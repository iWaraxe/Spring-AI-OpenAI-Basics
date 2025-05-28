package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOllamaService;
import org.springframework.ai.ollama.api.OllamaApi.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * REST controller demonstrating low-level Ollama API usage.
 * 
 * This controller showcases scenarios where the low-level Ollama API provides
 * advantages over the high-level ChatModel interface:
 * 
 * 1. Model management (pull, list, show)
 * 2. Direct control over local model parameters
 * 3. Performance optimization for edge computing
 * 4. Model comparison and benchmarking
 * 5. Custom context and memory management
 * 6. Direct access to model capabilities
 */
@RestController
@RequestMapping("/api/ollama-lowlevel")
public class LowLevelOllamaController {

    private final LowLevelOllamaService lowLevelService;

    public LowLevelOllamaController(LowLevelOllamaService lowLevelService) {
        this.lowLevelService = lowLevelService;
    }

    /**
     * Basic chat completion
     * 
     * Benefits over high-level API:
     * - Direct control over request format
     * - Access to raw response data
     * - Model-specific configurations
     */
    @PostMapping("/basic-chat")
    public ChatResponse basicChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String model = request.getOrDefault("model", "llama3.2");
        return lowLevelService.basicChatCompletion(message, model);
    }

    /**
     * Streaming chat completion
     * 
     * Benefits over high-level API:
     * - Real-time token generation
     * - Progress tracking
     * - Low latency for local models
     */
    @PostMapping(value = "/stream-chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponse> streamChat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String model = request.getOrDefault("model", "llama3.2");
        return lowLevelService.streamingChatCompletion(message, model);
    }

    /**
     * Chat with advanced Ollama-specific parameters
     * 
     * Benefits over high-level API:
     * - Full control over hardware optimization (NUMA, GPU, threads)
     * - Memory management (mmap, mlock)
     * - Model-specific tuning
     */
    @PostMapping("/advanced-chat")
    public ChatResponse advancedChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", "llama3.2");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> options = (Map<String, Object>) request.getOrDefault("options", new HashMap<>());
        
        return lowLevelService.advancedParameterChat(message, model, options);
    }

    /**
     * Multi-turn conversation
     * 
     * Benefits over high-level API:
     * - Direct context management
     * - Conversation history control
     * - Context window optimization
     */
    @PostMapping("/multi-turn")
    public ChatResponse multiTurnConversation(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, String>> conversationHistory = 
            (List<Map<String, String>>) request.get("conversationHistory");
        String model = (String) request.getOrDefault("model", "llama3.2");
        
        return lowLevelService.multiTurnConversation(conversationHistory, model);
    }

    /**
     * Generate text (non-chat format)
     * 
     * Benefits over high-level API:
     * - Direct text generation without chat structure
     * - Better for completion tasks
     * - Lower overhead
     */
    @PostMapping("/generate")
    public GenerateResponse generate(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String model = request.getOrDefault("model", "llama3.2");
        return lowLevelService.generateText(prompt, model);
    }

    /**
     * Streaming text generation
     * 
     * Benefits over high-level API:
     * - Real-time generation progress
     * - Token-by-token output
     * - Progress statistics
     */
    @PostMapping(value = "/stream-generate", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<GenerateResponse> streamGenerate(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String model = request.getOrDefault("model", "llama3.2");
        return lowLevelService.streamingGenerateText(prompt, model);
    }

    /**
     * List available models
     * 
     * Benefits over high-level API:
     * - Direct access to model inventory
     * - Model metadata (size, digest, modified date)
     * - Dynamic model discovery
     */
    @GetMapping("/models")
    public ModelListResponse listModels() {
        return lowLevelService.listAvailableModels();
    }

    /**
     * Get detailed model information
     * 
     * Benefits over high-level API:
     * - Model architecture details
     * - Parameter counts
     * - Capabilities and limitations
     */
    @GetMapping("/models/{modelName}")
    public ModelDetailsResponse getModelDetails(@PathVariable String modelName) {
        return lowLevelService.getModelDetails(modelName);
    }

    /**
     * Pull a model from Ollama library or Hugging Face
     * 
     * Benefits over high-level API:
     * - Dynamic model management
     * - Progress tracking
     * - Access to thousands of GGUF models
     */
    @PostMapping(value = "/pull-model", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PullModelResponse> pullModel(@RequestBody Map<String, String> request) {
        String modelName = request.get("modelName");
        return lowLevelService.pullModel(modelName);
    }

    /**
     * Optimized chat with model-specific settings
     * 
     * Benefits over high-level API:
     * - Model-specific optimizations
     * - Custom system prompts
     * - Hardware-aware configurations
     */
    @PostMapping("/optimized-chat")
    public ChatResponse optimizedChat(@RequestBody Map<String, Object> request) {
        String systemPrompt = (String) request.get("systemPrompt");
        String userMessage = (String) request.get("userMessage");
        String model = (String) request.getOrDefault("model", "llama3.2");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> modelOptions = (Map<String, Object>) request.getOrDefault("modelOptions", new HashMap<>());
        
        return lowLevelService.optimizedChat(systemPrompt, userMessage, model, modelOptions);
    }

    /**
     * Batch process multiple messages
     * 
     * Benefits over high-level API:
     * - Model warm-up optimization
     * - Bulk processing efficiency
     * - Consistent settings across batch
     */
    @PostMapping("/batch-process")
    public List<ChatResponse> batchProcess(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.get("messages");
        String model = (String) request.getOrDefault("model", "llama3.2");
        
        return lowLevelService.batchProcessMessages(messages, model);
    }

    /**
     * Get model performance metrics
     * 
     * Benefits over high-level API:
     * - Token generation speed
     * - Resource usage
     * - Benchmark results
     */
    @PostMapping("/model-performance")
    public Map<String, Object> getModelPerformance(@RequestBody Map<String, String> request) {
        String model = request.getOrDefault("model", "llama3.2");
        return lowLevelService.getModelPerformanceMetrics(model);
    }

    /**
     * Chat with context optimization
     * 
     * Benefits over high-level API:
     * - Custom context window sizes
     * - Memory mapping control
     * - Hardware-specific optimizations
     */
    @PostMapping("/context-optimized-chat")
    public ChatResponse contextOptimizedChat(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        String model = (String) request.getOrDefault("model", "llama3.2");
        Integer contextSize = (Integer) request.getOrDefault("contextSize", 2048);
        Boolean useMemoryMapping = (Boolean) request.getOrDefault("useMemoryMapping", true);
        
        return lowLevelService.chatWithContextOptimization(message, model, contextSize, useMemoryMapping);
    }

    /**
     * Compare multiple models
     * 
     * Benefits over high-level API:
     * - A/B testing capabilities
     * - Model selection assistance
     * - Performance comparison
     */
    @PostMapping("/compare-models")
    public Map<String, ChatResponse> compareModels(@RequestBody Map<String, Object> request) {
        String message = (String) request.get("message");
        @SuppressWarnings("unchecked")
        List<String> models = (List<String>) request.getOrDefault("models", 
            List.of("llama3.2", "mistral", "phi3"));
        
        return lowLevelService.compareModels(message, models);
    }

    /**
     * Get Ollama server information
     * 
     * Benefits over high-level API:
     * - Server capabilities
     * - Available models
     * - Recommended configurations
     */
    @GetMapping("/server-info")
    public Map<String, Object> getServerInfo() {
        return lowLevelService.getOllamaServerInfo();
    }

    /**
     * Demonstration endpoint comparing high-level vs low-level API benefits
     */
    @GetMapping("/comparison-info")
    public Map<String, Object> getComparisonInfo() {
        return Map.of(
            "highLevelBenefits", List.of(
                "Simplified API usage with ChatModel interface",
                "Spring AI abstractions and utilities",
                "Portable across different AI providers",
                "Built-in Spring Boot integration",
                "Structured output converters",
                "Automatic retry and error handling"
            ),
            "lowLevelBenefits", List.of(
                "Direct model management (pull, list, show, delete)",
                "Hardware optimization controls (GPU, NUMA, threads)",
                "Memory management (mmap, mlock)",
                "Model-specific parameter tuning",
                "Direct access to generation statistics",
                "Custom context window management",
                "Raw generate endpoint for non-chat use cases",
                "Model benchmarking and comparison",
                "Dynamic model loading from Hugging Face",
                "Progress tracking for model downloads"
            ),
            "ollamaSpecificUseCases", List.of(
                "Edge computing and offline deployments",
                "Privacy-sensitive applications",
                "Custom model fine-tuning and deployment",
                "Resource-constrained environments",
                "Multi-model comparison and selection",
                "Hardware-specific optimizations",
                "Real-time applications with streaming",
                "Batch processing with model warm-up"
            ),
            "supportedModelTypes", Map.of(
                "general", List.of("llama3.2", "llama3.1", "mistral", "mixtral"),
                "coding", List.of("codellama", "deepseek-coder", "codegemma"),
                "small", List.of("phi3", "tinyllama", "gemma2:2b"),
                "multimodal", List.of("llava", "bakllava"),
                "specialized", List.of("nomic-embed-text", "all-minilm")
            ),
            "hardwareOptimizations", Map.of(
                "gpu", "Control GPU layers with numGPU parameter",
                "cpu", "Optimize CPU threads with numThread",
                "memory", "Control memory usage with useMMap and useMLock",
                "context", "Adjust context window with numCtx",
                "batch", "Optimize batch processing with numBatch"
            )
        );
    }
}
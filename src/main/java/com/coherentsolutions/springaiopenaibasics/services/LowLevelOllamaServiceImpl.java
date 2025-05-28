package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.*;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation demonstrating low-level Ollama API usage.
 * 
 * This implementation shows the benefits of using the low-level Ollama API:
 * - Direct model management (pull, list, show, delete)
 * - Fine-grained control over local model parameters
 * - Performance optimization for edge computing
 * - Model-specific configurations for different architectures
 * - Direct access to model details and capabilities
 * - Custom context and memory management
 */
@Service
public class LowLevelOllamaServiceImpl implements LowLevelOllamaService {

    private final OllamaApi ollamaApi;

    public LowLevelOllamaServiceImpl(OllamaApi ollamaApi) {
        this.ollamaApi = ollamaApi;
    }

    @Override
    public ChatResponse basicChatCompletion(String message, String model) {
        ChatRequest request = ChatRequest.builder(model)
            .stream(false)
            .messages(List.of(
                Message.builder(Role.USER)
                    .content(message)
                    .build()
            ))
            .build();
        
        return ollamaApi.chat(request);
    }

    @Override
    public Flux<ChatResponse> streamingChatCompletion(String message, String model) {
        ChatRequest request = ChatRequest.builder(model)
            .stream(true)
            .messages(List.of(
                Message.builder(Role.USER)
                    .content(message)
                    .build()
            ))
            .build();
        
        return ollamaApi.streamingChat(request);
    }

    @Override
    public ChatResponse advancedParameterChat(String message, String model, Map<String, Object> options) {
        // Build options with Ollama-specific parameters
        OllamaOptions.Builder optionsBuilder = OllamaOptions.builder();
        
        // Set common parameters
        if (options.containsKey("temperature")) {
            optionsBuilder.temperature(((Number) options.get("temperature")).doubleValue());
        }
        if (options.containsKey("numCtx")) {
            optionsBuilder.numCtx((Integer) options.get("numCtx"));
        }
        if (options.containsKey("numGPU")) {
            optionsBuilder.numGPU((Integer) options.get("numGPU"));
        }
        if (options.containsKey("numThread")) {
            optionsBuilder.numThread((Integer) options.get("numThread"));
        }
        if (options.containsKey("topK")) {
            optionsBuilder.topK((Integer) options.get("topK"));
        }
        if (options.containsKey("topP")) {
            optionsBuilder.topP(((Number) options.get("topP")).doubleValue());
        }
        if (options.containsKey("repeatPenalty")) {
            optionsBuilder.repeatPenalty(((Number) options.get("repeatPenalty")).doubleValue());
        }
        if (options.containsKey("seed")) {
            optionsBuilder.seed((Integer) options.get("seed"));
        }
        
        // Ollama-specific: NUMA optimization
        if (options.containsKey("numa")) {
            optionsBuilder.numa((Boolean) options.get("numa"));
        }
        
        // Ollama-specific: Memory mapping
        if (options.containsKey("useMMap")) {
            optionsBuilder.useMMap((Boolean) options.get("useMMap"));
        }
        
        // Ollama-specific: Memory locking
        if (options.containsKey("useMLock")) {
            optionsBuilder.useMLock((Boolean) options.get("useMLock"));
        }
        
        ChatRequest request = ChatRequest.builder(model)
            .stream(false)
            .messages(List.of(
                Message.builder(Role.USER)
                    .content(message)
                    .build()
            ))
            .options(optionsBuilder.build())
            .build();
        
        return ollamaApi.chat(request);
    }

    @Override
    public ChatResponse multiTurnConversation(List<Map<String, String>> conversationHistory, String model) {
        List<Message> messages = conversationHistory.stream()
            .map(turn -> {
                String role = turn.get("role");
                String content = turn.get("content");
                Role ollamaRole = "user".equals(role) ? Role.USER : 
                                 "system".equals(role) ? Role.SYSTEM : Role.ASSISTANT;
                return Message.builder(ollamaRole)
                    .content(content)
                    .build();
            })
            .collect(Collectors.toList());
        
        ChatRequest request = ChatRequest.builder(model)
            .stream(false)
            .messages(messages)
            .options(OllamaOptions.builder()
                .numCtx(4096) // Larger context for conversation
                .build())
            .build();
        
        return ollamaApi.chat(request);
    }

    @Override
    public GenerateResponse generateText(String prompt, String model) {
        GenerateRequest request = GenerateRequest.builder(model)
            .prompt(prompt)
            .stream(false)
            .build();
        
        return ollamaApi.generate(request);
    }

    @Override
    public Flux<GenerateResponse> streamingGenerateText(String prompt, String model) {
        GenerateRequest request = GenerateRequest.builder(model)
            .prompt(prompt)
            .stream(true)
            .build();
        
        return ollamaApi.streamingGenerate(request);
    }

    @Override
    public ModelListResponse listAvailableModels() {
        return ollamaApi.listModels();
    }

    @Override
    public ModelDetailsResponse getModelDetails(String modelName) {
        ModelDetailsRequest request = ModelDetailsRequest.builder(modelName).build();
        return ollamaApi.showModelDetails(request);
    }

    @Override
    public Flux<PullModelResponse> pullModel(String modelName) {
        PullModelRequest request = PullModelRequest.builder()
            .name(modelName)
            .stream(true)
            .build();
        
        return ollamaApi.streamingPullModel(request);
    }

    @Override
    public ChatResponse optimizedChat(String systemPrompt, String userMessage, String model, 
                                    Map<String, Object> modelOptions) {
        OllamaOptions.Builder optionsBuilder = OllamaOptions.builder();
        
        // Apply model-specific optimizations
        if (model.contains("llama")) {
            // Llama models benefit from specific settings
            optionsBuilder.numCtx(4096)
                         .repeatPenalty(1.1)
                         .temperature(0.7);
        } else if (model.contains("mistral")) {
            // Mistral models have different optimal settings
            optionsBuilder.numCtx(8192)
                         .repeatPenalty(1.0)
                         .temperature(0.8);
        } else if (model.contains("phi")) {
            // Microsoft Phi models are smaller and faster
            optionsBuilder.numCtx(2048)
                         .temperature(0.75);
        }
        
        // Apply additional custom options
        modelOptions.forEach((key, value) -> {
            if ("numGPU".equals(key)) {
                optionsBuilder.numGPU((Integer) value);
            }
            // Add other options as needed
        });
        
        List<Message> messages = List.of(
            Message.builder(Role.SYSTEM)
                .content(systemPrompt)
                .build(),
            Message.builder(Role.USER)
                .content(userMessage)
                .build()
        );
        
        ChatRequest request = ChatRequest.builder(model)
            .stream(false)
            .messages(messages)
            .options(optionsBuilder.build())
            .build();
        
        return ollamaApi.chat(request);
    }

    @Override
    public List<ChatResponse> batchProcessMessages(List<String> messages, String model) {
        // Pre-warm the model with a simple request
        basicChatCompletion("Hello", model);
        
        // Process messages with optimized settings for batch
        OllamaOptions options = OllamaOptions.builder()
            .numCtx(2048) // Smaller context for faster processing
            .numBatch(512) // Larger batch size
            .build();
        
        return messages.stream()
            .map(message -> {
                ChatRequest request = ChatRequest.builder(model)
                    .stream(false)
                    .messages(List.of(
                        Message.builder(Role.USER)
                            .content(message)
                            .build()
                    ))
                    .options(options)
                    .build();
                return ollamaApi.chat(request);
            })
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getModelPerformanceMetrics(String model) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Get model details for size and architecture info
        ModelDetailsResponse details = getModelDetails(model);
        metrics.put("modelName", model);
        metrics.put("modelDetails", details);
        
        // Run a benchmark
        String testPrompt = "The quick brown fox jumps over the lazy dog.";
        long startTime = System.currentTimeMillis();
        
        GenerateResponse response = generateText(testPrompt, model);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        metrics.put("responseTimeMs", duration);
        metrics.put("promptEvalCount", response.promptEvalCount());
        metrics.put("evalCount", response.evalCount());
        
        if (response.promptEvalDuration() != null && response.promptEvalDuration() > 0) {
            double promptTokensPerSecond = (response.promptEvalCount() * 1_000_000_000.0) / response.promptEvalDuration();
            metrics.put("promptTokensPerSecond", promptTokensPerSecond);
        }
        
        if (response.evalDuration() != null && response.evalDuration() > 0) {
            double generateTokensPerSecond = (response.evalCount() * 1_000_000_000.0) / response.evalDuration();
            metrics.put("generateTokensPerSecond", generateTokensPerSecond);
        }
        
        metrics.put("totalDuration", response.totalDuration());
        metrics.put("loadDuration", response.loadDuration());
        
        return metrics;
    }

    @Override
    public ChatResponse chatWithContextOptimization(String message, String model, 
                                                  int contextSize, boolean useMemoryMapping) {
        OllamaOptions options = OllamaOptions.builder()
            .numCtx(contextSize)
            .useMMap(useMemoryMapping)
            .numThread(Runtime.getRuntime().availableProcessors()) // Use all CPU cores
            .build();
        
        ChatRequest request = ChatRequest.builder(model)
            .stream(false)
            .messages(List.of(
                Message.builder(Role.USER)
                    .content(message)
                    .build()
            ))
            .options(options)
            .build();
        
        return ollamaApi.chat(request);
    }

    @Override
    public Map<String, ChatResponse> compareModels(String message, List<String> models) {
        Map<String, ChatResponse> responses = new HashMap<>();
        
        for (String model : models) {
            try {
                ChatResponse response = basicChatCompletion(message, model);
                responses.put(model, response);
            } catch (Exception e) {
                // Model might not be available
                System.err.println("Model " + model + " failed: " + e.getMessage());
            }
        }
        
        return responses;
    }

    @Override
    public Map<String, Object> getOllamaServerInfo() {
        Map<String, Object> serverInfo = new HashMap<>();
        
        // Get list of available models
        ModelListResponse models = listAvailableModels();
        serverInfo.put("availableModels", models.models().stream()
            .map(model -> Map.of(
                "name", model.name(),
                "size", model.size(),
                "digest", model.digest(),
                "modifiedAt", model.modifiedAt()
            ))
            .collect(Collectors.toList()));
        
        // Server capabilities
        serverInfo.put("capabilities", Map.of(
            "chat", true,
            "generate", true,
            "embeddings", true,
            "multimodal", true,
            "functionCalling", true,
            "streaming", true,
            "modelManagement", true
        ));
        
        // Recommended models for different use cases
        serverInfo.put("recommendedModels", Map.of(
            "general", "llama3.2",
            "coding", "codellama",
            "fast", "phi3",
            "multimodal", "llava",
            "large", "mixtral",
            "tiny", "tinyllama"
        ));
        
        // Model sources
        serverInfo.put("modelSources", List.of(
            "Ollama Library (ollama pull <model>)",
            "Hugging Face GGUF (ollama pull hf.co/<user>/<model>)",
            "Custom models (ollama create)"
        ));
        
        return serverInfo;
    }
}
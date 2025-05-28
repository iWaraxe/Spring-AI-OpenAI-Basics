package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.ChatRequest;
import org.springframework.ai.ollama.api.OllamaApi.ChatResponse;
import org.springframework.ai.ollama.api.OllamaApi.ModelListResponse;
import org.springframework.ai.ollama.api.OllamaApi.ModelDetailsResponse;
import org.springframework.ai.ollama.api.OllamaApi.PullModelRequest;
import org.springframework.ai.ollama.api.OllamaApi.PullModelResponse;
import org.springframework.ai.ollama.api.OllamaApi.GenerateRequest;
import org.springframework.ai.ollama.api.OllamaApi.GenerateResponse;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Service interface for demonstrating low-level Ollama API usage.
 * 
 * This service showcases when and why you might use the low-level Ollama API
 * instead of the high-level ChatModel interface:
 * 
 * 1. Direct model management (pull, list, show, delete)
 * 2. Fine-grained control over local model parameters
 * 3. Custom context and memory management
 * 4. Performance optimization for edge computing
 * 5. Model-specific configurations for different architectures
 * 6. Direct access to model details and capabilities
 * 7. Custom streaming and response handling
 * 8. Integration with model repositories (Hugging Face GGUF)
 */
public interface LowLevelOllamaService {
    
    /**
     * Basic chat completion using low-level Ollama API
     * Benefits: Direct control over request/response format
     */
    ChatResponse basicChatCompletion(String message, String model);
    
    /**
     * Streaming chat completion using low-level Ollama API
     * Benefits: Real-time token generation for local models
     */
    Flux<ChatResponse> streamingChatCompletion(String message, String model);
    
    /**
     * Chat with advanced Ollama-specific parameters
     * Benefits: Full control over model-specific settings (NUMA, GPU layers, etc.)
     */
    ChatResponse advancedParameterChat(String message, String model, Map<String, Object> options);
    
    /**
     * Multi-turn conversation with context management
     * Benefits: Direct control over conversation history and context window
     */
    ChatResponse multiTurnConversation(List<Map<String, String>> conversationHistory, String model);
    
    /**
     * Generate text using the raw generate endpoint (non-chat format)
     * Benefits: Direct text generation without chat structure
     */
    GenerateResponse generateText(String prompt, String model);
    
    /**
     * Streaming text generation
     * Benefits: Real-time text generation with progress tracking
     */
    Flux<GenerateResponse> streamingGenerateText(String prompt, String model);
    
    /**
     * List all available models
     * Benefits: Direct access to model inventory and capabilities
     */
    ModelListResponse listAvailableModels();
    
    /**
     * Get detailed information about a specific model
     * Benefits: Access to model architecture, parameters, and capabilities
     */
    ModelDetailsResponse getModelDetails(String modelName);
    
    /**
     * Pull a model from Ollama library or Hugging Face
     * Benefits: Dynamic model management at runtime
     */
    Flux<PullModelResponse> pullModel(String modelName);
    
    /**
     * Chat with custom system prompt and model-specific optimizations
     * Benefits: Fine-tuned behavior for specific use cases
     */
    ChatResponse optimizedChat(String systemPrompt, String userMessage, String model, 
                              Map<String, Object> modelOptions);
    
    /**
     * Batch processing multiple messages with the same model
     * Benefits: Optimized for bulk operations with model warm-up
     */
    List<ChatResponse> batchProcessMessages(List<String> messages, String model);
    
    /**
     * Get model performance metrics and resource usage
     * Benefits: Monitor local resource consumption
     */
    Map<String, Object> getModelPerformanceMetrics(String model);
    
    /**
     * Chat with context window optimization
     * Benefits: Manage large contexts efficiently on local hardware
     */
    ChatResponse chatWithContextOptimization(String message, String model, 
                                           int contextSize, boolean useMemoryMapping);
    
    /**
     * Compare responses from multiple models
     * Benefits: A/B testing and model selection
     */
    Map<String, ChatResponse> compareModels(String message, List<String> models);
    
    /**
     * Get Ollama server information and capabilities
     * Benefits: Understand local deployment capabilities
     */
    Map<String, Object> getOllamaServerInfo();
}
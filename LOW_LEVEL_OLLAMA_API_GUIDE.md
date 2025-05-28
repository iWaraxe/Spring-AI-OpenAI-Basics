# Low-Level Ollama API Usage Guide

This branch demonstrates when and how to use Spring AI's low-level Ollama API instead of the high-level `ChatModel` interface.

## Overview

Spring AI provides two main approaches for interacting with Ollama:

1. **High-Level ChatModel Interface** - Simplified, abstracted API
2. **Low-Level Ollama API Client** - Direct access to Ollama's REST API

## What Ollama Can Help You Do

Ollama enables you to:
- **Run LLMs Locally** - Complete privacy and control over your AI models
- **Deploy Models on Edge Devices** - No internet required after model download
- **Access Thousands of Models** - From Ollama library and Hugging Face GGUF models
- **Optimize for Hardware** - Fine-tune for specific CPU/GPU configurations
- **Build Offline AI Applications** - Perfect for secure environments

## Extra Features with Low-Level Ollama API

### 1. **Model Management**
```java
// List all available models
ModelListResponse models = lowLevelService.listAvailableModels();

// Get detailed model information
ModelDetailsResponse details = lowLevelService.getModelDetails("llama3.2");

// Pull models dynamically (including from Hugging Face)
Flux<PullModelResponse> pullProgress = lowLevelService.pullModel("hf.co/bartowski/gemma-2-2b-it-GGUF");
```

### 2. **Hardware Optimization Controls**
```java
Map<String, Object> options = Map.of(
    "numGPU", 1,          // GPU layers to offload
    "numThread", 8,       // CPU threads to use
    "numa", true,         // NUMA optimization
    "useMMap", true,      // Memory mapping
    "useMLock", false,    // Memory locking
    "numCtx", 4096,       // Context window size
    "numBatch", 512       // Batch size for processing
);

ChatResponse response = lowLevelService.advancedParameterChat(message, model, options);
```

### 3. **Direct Generate Endpoint (Non-Chat Format)**
```java
// Better for completion tasks, lower overhead
GenerateResponse response = lowLevelService.generateText("The capital of France is", "llama3.2");

// Access generation statistics
int promptTokens = response.promptEvalCount();
int generatedTokens = response.evalCount();
long totalDuration = response.totalDuration();
```

### 4. **Streaming with Progress Tracking**
```java
Flux<GenerateResponse> stream = lowLevelService.streamingGenerateText(prompt, model);
stream.subscribe(chunk -> {
    System.out.println("Generated: " + chunk.response());
    System.out.println("Progress: " + chunk.evalCount() + " tokens");
});
```

### 5. **Model Performance Benchmarking**
```java
Map<String, Object> metrics = lowLevelService.getModelPerformanceMetrics("llama3.2");
// Returns:
// - Token generation speed
// - Prompt evaluation speed
// - Total response time
// - Load duration
```

### 6. **Multi-Model Comparison**
```java
Map<String, ChatResponse> comparison = lowLevelService.compareModels(
    "Explain quantum computing",
    List.of("llama3.2", "mistral", "phi3")
);
```

## When to Use Low-Level Ollama API

### 1. **Edge Deployment and Offline Applications**
```java
// Optimize for resource-constrained devices
ChatResponse response = lowLevelService.chatWithContextOptimization(
    message, 
    "phi3",           // Small, fast model
    1024,             // Smaller context for memory savings
    true              // Use memory mapping
);
```

### 2. **Privacy-Sensitive Applications**
```java
// All processing happens locally
ChatResponse response = lowLevelService.optimizedChat(
    "Analyze this confidential data: [SENSITIVE_INFO]",
    "Process this without sending to external servers",
    "llama3.2",
    Map.of("seed", 42) // Reproducible results
);
```

### 3. **Custom Model Deployment**
```java
// Pull custom GGUF models from Hugging Face
Flux<PullModelResponse> progress = lowLevelService.pullModel(
    "hf.co/TheBloke/CodeLlama-13B-Python-GGUF"
);

progress.subscribe(update -> {
    System.out.println("Status: " + update.status());
    System.out.println("Progress: " + update.completed() + "/" + update.total());
});
```

### 4. **Hardware-Specific Optimization**
```java
// For GPU-enabled systems
Map<String, Object> gpuOptions = Map.of(
    "numGPU", -1,     // Use all available GPU layers
    "mainGPU", 0,     // Primary GPU for small tensors
    "lowVram", false  // Full VRAM usage
);

// For CPU-only systems
Map<String, Object> cpuOptions = Map.of(
    "numGPU", 0,      // No GPU usage
    "numThread", Runtime.getRuntime().availableProcessors(),
    "numa", true,     // NUMA optimization for multi-socket systems
    "f16KV", false    // Use FP32 for better CPU performance
);
```

### 5. **Batch Processing with Model Warm-up**
```java
// Pre-warm model for better batch performance
List<ChatResponse> responses = lowLevelService.batchProcessMessages(
    List.of(
        "Translate: Hello",
        "Translate: Goodbye",
        "Translate: Thank you"
    ),
    "llama3.2"
);
```

## High-Level vs Low-Level Comparison for Ollama

| Feature | High-Level ChatModel | Low-Level Ollama API |
|---------|---------------------|---------------------|
| **Ease of Use** | ✅ Simple | ❌ More Complex |
| **Provider Portability** | ✅ Multi-provider | ❌ Ollama-specific |
| **Model Management** | ❌ Limited | ✅ Full Control |
| **Hardware Optimization** | ❌ Basic | ✅ Fine-grained |
| **Generate Endpoint** | ❌ No | ✅ Direct Access |
| **Performance Metrics** | ❌ No | ✅ Detailed Stats |
| **Streaming Progress** | ❌ Basic | ✅ Token-level |
| **Context Management** | ❌ Limited | ✅ Full Control |
| **Model Comparison** | ❌ Manual | ✅ Built-in |
| **Dynamic Model Loading** | ❌ No | ✅ Yes |

## Code Examples

### Basic Chat Comparison
```java
// High-level approach
String response = chatModel.call("What is AI?");

// Low-level approach
ChatResponse response = lowLevelService.basicChatCompletion("What is AI?", "llama3.2");
String content = response.message().content();
boolean done = response.done();
long evalDuration = response.evalDuration();
```

### Streaming Comparison
```java
// High-level approach
Flux<ChatResponse> stream = chatModel.stream(new Prompt("Tell me a story"));

// Low-level approach (with progress)
Flux<ChatResponse> stream = lowLevelService.streamingChatCompletion("Tell me a story", "llama3.2");
stream.subscribe(chunk -> {
    System.out.println("Content: " + chunk.message().content());
    System.out.println("Model: " + chunk.model());
    System.out.println("Done: " + chunk.done());
});
```

### Model-Specific Optimization
```java
// Llama models optimization
ChatResponse llamaResponse = lowLevelService.optimizedChat(
    "You are a helpful assistant",
    "Explain quantum physics",
    "llama3.2",
    Map.of(
        "numCtx", 4096,
        "repeatPenalty", 1.1,
        "temperature", 0.7
    )
);

// Code-specific models
ChatResponse codeResponse = lowLevelService.optimizedChat(
    "You are a code expert",
    "Write a Python function for sorting",
    "codellama",
    Map.of(
        "temperature", 0.1,  // Lower for code generation
        "topP", 0.95
    )
);

// Small, fast models for edge
ChatResponse edgeResponse = lowLevelService.optimizedChat(
    "Be concise",
    "What is 2+2?",
    "phi3",
    Map.of(
        "numCtx", 512,      // Small context
        "temperature", 0.1   // Deterministic
    )
);
```

### Multi-Modal Support (LLaVA)
```java
// Note: Requires multimodal model like llava
ChatRequest request = ChatRequest.builder("llava")
    .messages(List.of(
        Message.builder(Role.USER)
            .content("What's in this image?")
            .images(List.of(imageBase64))
            .build()
    ))
    .build();
```

## Supported Model Types

### General Purpose
- **llama3.2** - Latest Llama model, good balance
- **llama3.1** - Larger context window
- **mistral** - Fast and efficient
- **mixtral** - Mixture of experts for better quality

### Coding
- **codellama** - Specialized for code generation
- **deepseek-coder** - Advanced code understanding
- **codegemma** - Google's code model

### Small/Fast Models
- **phi3** - Microsoft's efficient model
- **tinyllama** - Tiny but capable
- **gemma2:2b** - Google's small model

### Multimodal
- **llava** - Vision and language
- **bakllava** - Alternative vision model

### Specialized
- **nomic-embed-text** - Text embeddings
- **all-minilm** - Sentence embeddings

## Performance Optimization Tips

### 1. **Context Window Management**
```java
// Smaller context = faster processing
ChatResponse response = lowLevelService.chatWithContextOptimization(
    message, model, 
    2048,    // Reduced context
    true     // Memory mapping
);
```

### 2. **Batch Processing**
```java
// Process multiple messages efficiently
List<ChatResponse> responses = lowLevelService.batchProcessMessages(
    messages, 
    "llama3.2"  // Model stays loaded
);
```

### 3. **Model Selection**
```java
// Choose model based on task
String model = taskType.equals("code") ? "codellama" :
               taskType.equals("fast") ? "phi3" :
               taskType.equals("quality") ? "llama3.2" :
               "mistral";
```

## API Endpoints

This branch provides the following REST endpoints:

- `POST /api/ollama-lowlevel/basic-chat` - Basic chat completion
- `POST /api/ollama-lowlevel/stream-chat` - Streaming chat
- `POST /api/ollama-lowlevel/advanced-chat` - Advanced parameters
- `POST /api/ollama-lowlevel/multi-turn` - Multi-turn conversations
- `POST /api/ollama-lowlevel/generate` - Direct text generation
- `POST /api/ollama-lowlevel/stream-generate` - Streaming generation
- `GET /api/ollama-lowlevel/models` - List available models
- `GET /api/ollama-lowlevel/models/{name}` - Model details
- `POST /api/ollama-lowlevel/pull-model` - Pull new models
- `POST /api/ollama-lowlevel/optimized-chat` - Model-specific optimization
- `POST /api/ollama-lowlevel/batch-process` - Batch processing
- `POST /api/ollama-lowlevel/model-performance` - Performance metrics
- `POST /api/ollama-lowlevel/context-optimized-chat` - Context optimization
- `POST /api/ollama-lowlevel/compare-models` - Model comparison
- `GET /api/ollama-lowlevel/server-info` - Server capabilities
- `GET /api/ollama-lowlevel/comparison-info` - API comparison

## Testing

### Unit Tests
- `LowLevelOllamaServiceMockTest` - Service layer tests
- `LowLevelOllamaControllerTest` - Controller layer tests

### Integration Tests
- `LowLevelOllamaIntegrationTest` - Full integration tests

### Running Tests
```bash
# Start Ollama
ollama serve

# Pull a model
ollama pull llama3.2

# Run tests
mvn test -Dollama.integration.tests=true
```

## Best Practices

1. **Model Selection** - Choose the right model for your use case
2. **Hardware Optimization** - Configure based on your hardware
3. **Context Management** - Use appropriate context sizes
4. **Batch Processing** - Group similar requests
5. **Model Warm-up** - Pre-load models for production
6. **Resource Monitoring** - Track memory and CPU usage
7. **Fallback Strategies** - Handle model unavailability

## Conclusion

The low-level Ollama API provides powerful capabilities for local AI deployment, including:
- Complete privacy and data control
- Hardware-specific optimizations
- Dynamic model management
- Detailed performance metrics
- Edge deployment capabilities
- Access to thousands of models

While the high-level ChatModel interface is suitable for most applications, the low-level API is essential for production deployments requiring fine-grained control, performance optimization, and local model management.
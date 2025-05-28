# Low-Level Anthropic API Usage Guide

This branch demonstrates when and how to use Spring AI's low-level Anthropic API instead of the high-level `ChatModel` interface.

## Overview

Spring AI provides two main approaches for interacting with Anthropic Claude:

1. **High-Level ChatModel Interface** - Simplified, abstracted API
2. **Low-Level Anthropic API Client** - Direct access to Anthropic's REST API

## When to Use Low-Level Anthropic API

### 1. **Direct Access to Anthropic's Message Structure**
Use when you need:
- Fine-grained control over message roles and content blocks
- Direct access to Anthropic's event streaming model
- Custom message formatting specific to Claude

```java
AnthropicMessage message = new AnthropicMessage(
    List.of(new ContentBlock("Hello Claude")), 
    Role.USER
);
```

### 2. **Advanced Claude-Specific Parameters**
Use when you need:
- Stop sequences for controlled generation
- Advanced sampling parameters (top-k, top-p)
- Claude-specific model configurations

```java
ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
    message, 
    "claude-3-opus-20240229",
    0.3,     // temperature
    1000,    // maxTokens
    List.of("STOP", "END") // stopSequences
);
```

### 3. **System Prompts with Claude's Preferred Structure**
Use when you need:
- System prompts as separate parameters (Claude's preferred way)
- Fine-grained control over conversation structure
- Constitutional AI behavior customization

```java
ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(
    "You are a helpful assistant specialized in Spring AI.", 
    "What is Spring AI?"
);
```

### 4. **Detailed Anthropic-Specific Metadata**
Use when you need:
- Token usage with Anthropic's pricing model
- Stop reasons and stop sequences
- Claude-specific response metadata
- Cost estimation for Claude models

```java
Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(message);
Integer inputTokens = (Integer) metadata.get("inputTokens");
Integer outputTokens = (Integer) metadata.get("outputTokens");
String stopReason = (String) metadata.get("stopReason");
Double estimatedCost = (Double) metadata.get("estimatedCost");
```

### 5. **Raw Streaming with Anthropic's Event Model**
Use when you need:
- Direct access to Claude's streaming events
- Custom event processing logic
- Real-time response handling with event types

```java
Flux<StreamResponse> stream = lowLevelService.streamingChatCompletion(message);
stream.subscribe(event -> {
    System.out.println("Event type: " + event.type());
    System.out.println("Event data: " + event.data());
});
```

### 6. **Multi-Turn Conversations with Precise Control**
Use when you need:
- Direct control over conversation history
- Custom role management
- Context window optimization

```java
List<Map<String, String>> conversation = List.of(
    Map.of("role", "user", "content", "Hello"),
    Map.of("role", "assistant", "content", "Hi there!"),
    Map.of("role", "user", "content", "How are you?")
);
ResponseEntity<ChatCompletionResponse> response = 
    lowLevelService.multiTurnConversation(conversation);
```

## High-Level vs Low-Level Comparison for Anthropic

| Feature | High-Level ChatModel | Low-Level Anthropic API |
|---------|---------------------|------------------------|
| **Ease of Use** | ✅ Simple | ❌ More Complex |
| **Provider Portability** | ✅ Multi-provider | ❌ Anthropic-specific |
| **Message Structure Control** | ❌ Limited | ✅ Full Control |
| **System Prompt Handling** | ❌ Basic | ✅ Claude's Preferred Way |
| **Stop Sequences** | ❌ No | ✅ Full Support |
| **Advanced Sampling** | ❌ Limited | ✅ Top-k, Top-p |
| **Streaming Events** | ❌ Basic | ✅ Raw Event Access |
| **Usage Metadata** | ❌ Basic | ✅ Detailed Anthropic Stats |
| **Constitutional AI** | ❌ No Access | ✅ Advanced Control |
| **Cost Tracking** | ❌ Limited | ✅ Precise Claude Pricing |

## Anthropic-Specific Features

### Constitutional AI and Safety
Claude is trained using Constitutional AI, which provides unique safety and helpfulness characteristics:

```java
// System prompt leveraging Claude's training
ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(
    "You are Claude, an AI assistant created by Anthropic to be helpful, harmless, and honest.",
    "How should I approach this ethical dilemma?"
);
```

### Advanced Reasoning Capabilities
Claude excels at complex reasoning tasks:

```java
// Leveraging Claude's reasoning with controlled generation
ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
    "Solve this step-by-step: If a train travels 60 mph for 2 hours, then 80 mph for 1.5 hours, what's the average speed?",
    "claude-3-opus-20240229",
    0.1, // Low temperature for consistent reasoning
    1000,
    List.of("FINAL ANSWER:") // Stop when reaching conclusion
);
```

### Code Analysis and Generation
Claude is particularly strong at code-related tasks:

```java
// Using Claude for code review with specific instructions
ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(
    "You are a senior Spring developer. Review code for best practices, security, and performance.",
    "Review this Spring Boot controller: [code here]"
);
```

## Code Examples

### Basic Usage Comparison
```java
// High-level approach
String response = chatModel.call("Explain machine learning");

// Low-level approach
ResponseEntity<ChatCompletionResponse> response = 
    lowLevelService.basicChatCompletion("Explain machine learning");
String content = response.getBody().content().get(0).text();
HttpStatus status = response.getStatusCode();
```

### System Prompts
```java
// High-level approach
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a helpful assistant"),
    new UserMessage("What is AI?")
));
ChatResponse response = chatModel.call(prompt);

// Low-level approach (Claude's preferred way)
ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(
    "You are a helpful assistant", // System prompt as separate parameter
    "What is AI?"
);
```

### Advanced Parameters
```java
// High-level approach (limited parameters)
ChatResponse response = chatModel.call(
    new Prompt("Creative writing task",
        AnthropicChatOptions.builder()
            .model("claude-3-opus-20240229")
            .temperature(0.8)
            .maxTokens(500)
            .build())
);

// Low-level approach (full parameter control)
ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
    "Creative writing task",
    "claude-3-opus-20240229",
    0.8,                        // temperature
    500,                        // maxTokens
    List.of("THE END", "---")   // stopSequences (not available in high-level)
);
```

### Streaming
```java
// High-level approach
Flux<ChatResponse> stream = chatModel.stream(new Prompt("Tell me a story"));

// Low-level approach (raw events)
Flux<StreamResponse> stream = lowLevelService.streamingChatCompletion("Tell me a story");
stream.subscribe(event -> {
    switch (event.type()) {
        case "message_start":
            System.out.println("Message started");
            break;
        case "content_block_delta":
            System.out.println("Content: " + event.data());
            break;
        case "message_stop":
            System.out.println("Message completed");
            break;
    }
});
```

## API Endpoints

This branch provides the following REST endpoints for testing:

- `POST /api/anthropic-lowlevel/basic-chat` - Basic chat completion
- `POST /api/anthropic-lowlevel/stream-chat` - Streaming responses
- `POST /api/anthropic-lowlevel/custom-params` - Custom parameters with stop sequences
- `POST /api/anthropic-lowlevel/system-prompt-chat` - System + user messages
- `POST /api/anthropic-lowlevel/batch-process` - Batch processing
- `POST /api/anthropic-lowlevel/metadata` - Detailed metadata extraction
- `POST /api/anthropic-lowlevel/advanced-params` - Advanced sampling (top-k, top-p)
- `POST /api/anthropic-lowlevel/multi-turn` - Multi-turn conversations
- `GET /api/anthropic-lowlevel/api-info` - Anthropic API capabilities
- `GET /api/anthropic-lowlevel/comparison-info` - API comparison info

## Testing

### Unit Tests
- `LowLevelAnthropicServiceMockTest` - Service layer tests with mocked Anthropic API
- `LowLevelAnthropicControllerTest` - Controller layer tests

### Integration Tests
- `LowLevelAnthropicIntegrationTest` - Full integration tests (requires ANTHROPIC_API_KEY)

### Running Tests
```bash
# Unit tests only
mvn test

# Integration tests (requires API key)
ANTHROPIC_API_KEY=your_key mvn test -Dtest=LowLevelAnthropicIntegrationTest
```

## Configuration

The low-level Anthropic API is configured through:

```java
@Bean
public AnthropicApi anthropicApi() {
    return AnthropicApi.builder()
        .apiKey(apiKey)
        .baseUrl(baseUrl)
        .version(version)
        .betaVersion(betaVersion)
        .build();
}
```

## Best Practices

1. **Use High-Level API by Default** - Start with `ChatModel` for most use cases
2. **Switch to Low-Level When Needed** - Only use low-level API for Claude-specific features
3. **Leverage System Prompts** - Use Claude's preferred system prompt structure
4. **Monitor Usage and Costs** - Leverage detailed metadata for cost tracking
5. **Handle Stop Sequences** - Use stop sequences for controlled generation
6. **Optimize Context Windows** - Use multi-turn conversations efficiently
7. **Constitutional AI Considerations** - Design prompts that work with Claude's training

## Anthropic-Specific Use Cases

### 1. **Research and Analysis**
```java
// Using Claude for academic research with controlled output
ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
    "Analyze the implications of quantum computing on cryptography",
    "claude-3-opus-20240229",
    0.2, // Low temperature for analytical consistency
    2000,
    List.of("CONCLUSION:", "REFERENCES:")
);
```

### 2. **Code Review and Security Analysis**
```java
// Leveraging Claude's code analysis capabilities
ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(
    "You are a security expert. Identify potential vulnerabilities and suggest improvements.",
    "Review this authentication code: [code snippet]"
);
```

### 3. **Creative Writing with Structure**
```java
// Using stop sequences for structured creative output
ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
    "Write a short story about AI",
    "claude-3-5-sonnet-latest",
    0.9, // High temperature for creativity
    1500,
    List.of("THE END", "---END---")
);
```

### 4. **Mathematical Problem Solving**
```java
// Claude's step-by-step reasoning with controlled output
ResponseEntity<ChatCompletionResponse> response = lowLevelService.advancedParameterChat(
    "Solve this calculus problem step by step: ∫(x² + 3x + 2)dx",
    40,  // topK for focused mathematical vocabulary
    0.95 // topP for comprehensive explanations
);
```

## Conclusion

The low-level Anthropic API provides access to Claude's unique capabilities and Anthropic-specific features that aren't available through the generic ChatModel interface. While the high-level API is suitable for most applications, understanding when and how to use the low-level API gives you access to Claude's full potential, including Constitutional AI features, advanced reasoning capabilities, and fine-grained control over generation behavior.

Key advantages of the low-level Anthropic API:
- Direct access to Claude's message structure and event model
- System prompts using Claude's preferred format
- Advanced sampling parameters (top-k, top-p)
- Stop sequences for controlled generation
- Detailed usage statistics and cost tracking
- Constitutional AI behavior customization
- Raw streaming capabilities with event types
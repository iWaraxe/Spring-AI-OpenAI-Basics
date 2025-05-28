# Low-Level OpenAI API Usage Guide

This branch demonstrates when and how to use Spring AI's low-level OpenAI API instead of the high-level `ChatModel` interface.

## Overview

Spring AI provides two main approaches for interacting with OpenAI:

1. **High-Level ChatModel Interface** - Simplified, abstracted API
2. **Low-Level OpenAI API Client** - Direct access to OpenAI's REST API

## When to Use Low-Level API

### 1. **HTTP Response Metadata Access**
Use when you need:
- HTTP status codes and headers
- Response timing information
- Custom error handling based on HTTP details

```java
ResponseEntity<ChatCompletion> response = lowLevelService.basicChatCompletion("Hello");
HttpStatus status = response.getStatusCode();
HttpHeaders headers = response.getHeaders();
```

### 2. **Fine-Grained Parameter Control**
Use when you need:
- OpenAI-specific parameters not exposed by ChatModel
- Dynamic parameter adjustment
- Advanced model configurations

```java
ResponseEntity<ChatCompletion> response = lowLevelService.customParameterChat(
    message, 
    "gpt-4", 
    0.3,     // temperature
    100      // maxTokens
);
```

### 3. **Detailed Usage Statistics and Cost Tracking**
Use when you need:
- Token usage breakdown
- Cost estimation
- Performance monitoring
- Billing analytics

```java
Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(message);
Integer promptTokens = (Integer) metadata.get("promptTokens");
Integer completionTokens = (Integer) metadata.get("completionTokens");
Double estimatedCost = (Double) metadata.get("estimatedCost");
```

### 4. **Raw Streaming Capabilities**
Use when you need:
- Direct access to streaming chunks
- Custom streaming processing
- Real-time response handling

```java
Flux<ChatCompletionChunk> stream = lowLevelService.streamingChatCompletion(message);
stream.subscribe(chunk -> {
    // Custom chunk processing
    System.out.println("Received chunk: " + chunk.id());
});
```

### 5. **Advanced OpenAI Features**
Use when you need:
- Logit bias for token control
- Function calling with raw responses
- Custom system/user message structures

```java
Map<String, Integer> logitBias = Map.of("50256", -100); // Suppress end-of-text
ResponseEntity<ChatCompletion> response = lowLevelService.chatWithLogitBias(message, logitBias);
```

### 6. **Batch Processing Optimization**
Use when you need:
- Parallel request processing
- Bulk operations
- Custom retry logic

```java
List<ResponseEntity<ChatCompletion>> responses = 
    lowLevelService.batchProcessMessages(messages);
```

### 7. **Enterprise Integration Requirements**
Use when you need:
- Custom HTTP headers
- Proxy configurations
- Corporate security requirements
- Custom authentication flows

## High-Level vs Low-Level Comparison

| Feature | High-Level ChatModel | Low-Level OpenAI API |
|---------|---------------------|---------------------|
| **Ease of Use** | ✅ Simple | ❌ More Complex |
| **Provider Portability** | ✅ Multi-provider | ❌ OpenAI-specific |
| **HTTP Metadata** | ❌ Limited | ✅ Full Access |
| **Parameter Control** | ❌ Limited | ✅ Complete |
| **Usage Statistics** | ❌ Basic | ✅ Detailed |
| **Custom Headers** | ❌ No | ✅ Yes |
| **Streaming Control** | ❌ Limited | ✅ Raw Access |
| **Error Handling** | ❌ Abstracted | ✅ Fine-grained |
| **Performance Tuning** | ❌ Limited | ✅ Full Control |

## Code Examples

### Basic Usage
```java
// High-level approach
String response = chatModel.call("Hello, AI!");

// Low-level approach
ResponseEntity<ChatCompletion> response = lowLevelService.basicChatCompletion("Hello, AI!");
String content = response.getBody().choices().get(0).message().content();
HttpStatus status = response.getStatusCode();
```

### System + User Messages
```java
// High-level approach
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a helpful assistant"),
    new UserMessage("What is AI?")
));
ChatResponse response = chatModel.call(prompt);

// Low-level approach
ResponseEntity<ChatCompletion> response = lowLevelService.systemUserChat(
    "You are a helpful assistant", 
    "What is AI?"
);
```

### Streaming
```java
// High-level approach
Flux<ChatResponse> stream = chatModel.stream(new Prompt("Tell me a story"));

// Low-level approach
Flux<ChatCompletionChunk> stream = lowLevelService.streamingChatCompletion("Tell me a story");
```

## API Endpoints

This branch provides the following REST endpoints for testing:

- `POST /api/lowlevel/basic-chat` - Basic chat completion
- `POST /api/lowlevel/stream-chat` - Streaming responses
- `POST /api/lowlevel/custom-params` - Custom parameters
- `POST /api/lowlevel/batch-process` - Batch processing
- `POST /api/lowlevel/system-user-chat` - System + user messages
- `POST /api/lowlevel/metadata` - Detailed metadata
- `POST /api/lowlevel/logit-bias` - Advanced logit bias
- `GET /api/lowlevel/comparison-info` - API comparison info

## Testing

### Unit Tests
- `LowLevelOpenAiServiceMockTest` - Service layer tests with mocked OpenAI API
- `LowLevelApiControllerTest` - Controller layer tests

### Integration Tests
- `LowLevelApiIntegrationTest` - Full integration tests (requires OPENAI_API_KEY)

### Running Tests
```bash
# Unit tests only
mvn test

# Integration tests (requires API key)
OPENAI_API_KEY=your_key mvn test -Dtest=LowLevelApiIntegrationTest
```

## Configuration

The low-level API is configured through:

```java
@Bean
public OpenAiApi openAiApi() {
    return OpenAiApi.builder()
        .apiKey(apiKey)
        .baseUrl(baseUrl)
        .organizationId(organizationId)
        .projectId(projectId)
        .build();
}
```

## Best Practices

1. **Use High-Level API by Default** - Start with `ChatModel` for most use cases
2. **Switch to Low-Level When Needed** - Only use low-level API when you need specific features
3. **Monitor Usage and Costs** - Leverage detailed metadata for cost tracking
4. **Handle Errors Gracefully** - Use HTTP status codes for robust error handling
5. **Optimize for Performance** - Use batch processing for multiple requests
6. **Security Considerations** - Properly handle API keys and custom headers

## Conclusion

The low-level OpenAI API provides powerful capabilities for advanced use cases, enterprise requirements, and performance optimization. While the high-level ChatModel interface is suitable for most applications, understanding when and how to use the low-level API gives you complete control over your AI interactions.
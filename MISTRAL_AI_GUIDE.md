# Mistral AI Integration Guide

## Overview

Mistral AI is a European AI company that develops efficient, instruction-following language models. Their models excel at multilingual tasks, code generation, and mathematical reasoning while being more cost-effective than many alternatives.

## When to Use Mistral AI

### 1. Multilingual Applications
```java
// Excellent for international applications
ChatResponse response = mistralAiService.generateResponse(
    "Respond in French: Explain machine learning"
);
```

**Use Case**: International customer support, content localization, translation tasks

### 2. Code Generation and Programming
```java
// Strong coding capabilities across languages
ChatResponse response = mistralAiService.generateResponse(
    "Write a REST API endpoint in Spring Boot for user management"
);
```

**Use Case**: Code assistance, documentation generation, API development

### 3. Mathematical and Logical Reasoning
```java
// Excellent at solving complex problems
ChatResponse response = mistralAiService.generateResponse(
    "Solve this optimization problem step by step..."
);
```

**Use Case**: Educational applications, data analysis, scientific computing

### 4. Cost-Effective AI Solutions
```java
// High performance at lower cost
var options = MistralAiChatOptions.builder()
    .model("mistral-small-latest")  // Cost-effective option
    .temperature(0.7)
    .build();
```

**Use Case**: Startups, high-volume applications, budget-conscious projects

### 5. European Data Compliance
```java
// EU-based AI provider for data sovereignty
@Value("${mistral.ai.base.url:https://api.mistral.ai}")
private String baseUrl;  // European infrastructure
```

**Use Case**: GDPR compliance, European data residency requirements

## Key Features and Capabilities

### 1. Temperature Control for Creativity
```java
// Low temperature for focused responses
ChatResponse focused = mistralAiService.generateWithTemperature(
    "What is the capital of France?", 0.1);

// High temperature for creative writing
ChatResponse creative = mistralAiService.generateWithTemperature(
    "Write a story about time travel", 1.0);
```

### 2. Token Limits for Cost Control
```java
// Limit response length to control costs
ChatResponse response = mistralAiService.generateWithMaxTokens(
    "Summarize artificial intelligence", 100);
```

### 3. Safe Content Generation
```java
// Enable safety filters for content moderation
ChatResponse response = mistralAiService.generateWithSafePrompt(
    "Tell me about controversial topics", true);
```

### 4. Multimodal Vision Capabilities
```java
// Analyze images with Pixtral model
ChatResponse response = mistralAiService.generateMultiModal(
    "Describe this architectural drawing",
    "https://example.com/blueprint.jpg");
```

### 5. Deterministic Responses
```java
// Use random seed for reproducible outputs
ChatResponse response = mistralAiService.generateWithRandomSeed(
    "Generate test data", 42);
```

### 6. JSON Structured Output
```java
// Force JSON format for structured data
ChatResponse response = mistralAiService.generateJsonResponse(
    "Create a user profile with name, age, and preferences");
```

### 7. Stop Sequences for Control
```java
// Stop generation at specific points
ChatResponse response = mistralAiService.generateWithStopSequences(
    "List programming languages: Python, Java", "JavaScript", "C++");
```

### 8. Nucleus Sampling (Top-P)
```java
// Control response diversity with top-p
ChatResponse response = mistralAiService.generateWithTopP(
    "Explain quantum computing", 0.8);
```

## Configuration

### Auto-configuration (Recommended)
```properties
# application.properties
spring.ai.mistralai.api-key=${MISTRAL_AI_API_KEY}
spring.ai.mistralai.chat.options.model=mistral-small-latest
spring.ai.mistralai.chat.options.temperature=0.7
spring.ai.mistralai.chat.options.max-tokens=1000
```

### Manual Configuration
```java
@Configuration
@ConditionalOnProperty(name = "mistral.ai.enabled", havingValue = "true")
public class MistralAiConfig {
    
    @Bean
    public MistralAiChatModel mistralAiChatModel() {
        var api = new MistralAiApi(apiKey);
        var options = MistralAiChatOptions.builder()
            .model("mistral-small-latest")
            .temperature(0.7)
            .maxTokens(1000)
            .build();
        return new MistralAiChatModel(api, options);
    }
}
```

## Available Models

### Text Models
- **mistral-small-latest** - Fast, cost-effective for most tasks
- **mistral-large-latest** - Most capable, best reasoning
- **open-mistral-7b** - Open source, good for experimentation
- **open-mixtral-8x7b** - Mixture of experts, balanced performance
- **open-mixtral-8x22b** - Largest open model, best quality

### Multimodal Models
- **pixtral-large-latest** - Vision + text capabilities

### Model Selection Guide
```java
// For cost-sensitive applications
.model("mistral-small-latest")

// For complex reasoning tasks
.model("mistral-large-latest")

// For vision tasks
.model("pixtral-large-latest")

// For open source requirements
.model("open-mistral-7b")
```

## Best Practices

### 1. Model Selection
```java
// Choose based on complexity and budget
var simpleTask = MistralAiChatOptions.builder()
    .model("mistral-small-latest")  // Cost-effective
    .build();

var complexTask = MistralAiChatOptions.builder()
    .model("mistral-large-latest")  // Better reasoning
    .build();
```

### 2. Temperature Tuning
```java
// Factual tasks - low temperature
.temperature(0.1)

// Creative tasks - higher temperature
.temperature(0.8)

// Balanced responses
.temperature(0.7)
```

### 3. Token Management
```java
// Set reasonable limits to control costs
.maxTokens(500)  // For summaries
.maxTokens(1500) // For detailed explanations
.maxTokens(3000) // For long-form content
```

### 4. Safety Configuration
```java
// Enable for user-generated content
.safePrompt(true)

// Disable for trusted internal use
.safePrompt(false)
```

### 5. Multilingual Optimization
```java
// Be explicit about language requirements
String prompt = "Respond in Spanish: " + userQuery;

// Or use system prompts for consistent language
String systemPrompt = "You are a Spanish-speaking assistant";
```

## Error Handling

```java
@Service
public class MistralAiServiceImpl implements MistralAiService {
    
    @Override
    public ChatResponse generateResponse(String message) {
        try {
            return mistralAiChatModel.call(new Prompt(message));
        } catch (Exception e) {
            log.error("Mistral AI API error: {}", e.getMessage());
            throw new ServiceException("Failed to generate response", e);
        }
    }
}
```

## Streaming Responses

```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamResponse(@RequestParam String message) {
    return mistralAiService.generateStreamResponse(message)
        .map(response -> response.getResult().getOutput().getContent())
        .onErrorResume(error -> {
            log.error("Streaming error: {}", error.getMessage());
            return Flux.just("Error: " + error.getMessage());
        });
}
```

## Performance Optimization

### 1. Connection Pooling
```java
@Bean
public MistralAiApi mistralAiApi() {
    return new MistralAiApi(baseUrl, apiKey)
        .withHttpClient(httpClient); // Custom client with pooling
}
```

### 2. Caching Strategies
```java
@Cacheable("mistral-responses")
public ChatResponse generateCachedResponse(String message) {
    return mistralAiService.generateResponse(message);
}
```

### 3. Async Processing
```java
@Async
public CompletableFuture<ChatResponse> generateAsync(String message) {
    return CompletableFuture.completedFuture(
        mistralAiService.generateResponse(message));
}
```

## Monitoring and Metrics

```java
@Component
public class MistralAiMetrics {
    
    private final MeterRegistry meterRegistry;
    private final Counter requestCounter;
    private final Timer responseTimer;
    
    public MistralAiMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestCounter = Counter.builder("mistral.requests")
            .description("Number of Mistral AI requests")
            .register(meterRegistry);
        this.responseTimer = Timer.builder("mistral.response.time")
            .description("Mistral AI response time")
            .register(meterRegistry);
    }
}
```

## Example Use Cases

### Customer Support Bot
```java
@Service
public class CustomerSupportService {
    
    public String handleSupportQuery(String query, String language) {
        String prompt = String.format(
            "Respond in %s: Provide helpful customer support for: %s", 
            language, query);
        
        var response = mistralAiService.generateWithSafePrompt(prompt, true);
        return response.getResult().getOutput().getContent();
    }
}
```

### Code Review Assistant
```java
@Service
public class CodeReviewService {
    
    public String reviewCode(String code, String language) {
        String prompt = String.format(
            "Review this %s code for best practices, bugs, and improvements:\n%s", 
            language, code);
        
        var response = mistralAiService.generateWithTemperature(prompt, 0.3);
        return response.getResult().getOutput().getContent();
    }
}
```

### Educational Content Generator
```java
@Service
public class EducationService {
    
    public String generateExplanation(String topic, String level) {
        String prompt = String.format(
            "Explain %s at a %s level with examples", 
            topic, level);
        
        var response = mistralAiService.generateWithMaxTokens(prompt, 800);
        return response.getResult().getOutput().getContent();
    }
}
```

## Conclusion

Mistral AI offers a compelling combination of:
- **Performance**: Competitive with larger models
- **Cost**: More affordable than many alternatives  
- **Multilingual**: Excellent non-English support
- **Compliance**: European data residency
- **Flexibility**: Multiple model sizes and capabilities

This makes it ideal for European businesses, multilingual applications, cost-conscious projects, and organizations requiring data sovereignty while maintaining high AI capabilities.
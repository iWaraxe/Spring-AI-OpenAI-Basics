# Perplexity Search AI Integration Guide

## Overview

Perplexity AI is a unique AI service that combines language models with real-time web search capabilities. Unlike traditional LLMs with knowledge cutoffs, Perplexity provides up-to-date information by searching the web and synthesizing results with AI responses.

## When to Use Perplexity

### 1. Current Information Queries
```java
// Get latest information without knowledge cutoff
ChatResponse response = perplexityService.getCurrentInformation(
    "What are the latest developments in quantum computing?"
);
```

**Use Case**: News, current events, recent technology updates, stock prices, weather

### 2. Fact-Checking and Verification
```java
// Verify claims with real-time sources
ChatResponse response = perplexityService.factCheck(
    "The James Webb Space Telescope discovered water on an exoplanet"
);
```

**Use Case**: Verify statements, check recent claims, validate information

### 3. Technical Documentation Search
```java
// Search latest docs and tutorials
ChatResponse response = perplexityService.searchTechnicalDocs(
    "Spring Boot 3.2", 
    "What are the new features?"
);
```

**Use Case**: Latest API docs, framework updates, library changes

### 4. Product Comparisons
```java
// Compare products with current specs and reviews
ChatResponse response = perplexityService.compareProducts(
    "iPhone 15 Pro", 
    "Samsung Galaxy S24 Ultra"
);
```

**Use Case**: Shopping decisions, technology comparisons, service evaluations

### 5. Trending Topics Analysis
```java
// Analyze what's currently trending
ChatResponse response = perplexityService.analyzeTrendingTopics(
    "artificial intelligence"
);
```

**Use Case**: Market research, content creation, trend analysis

## Key Advantages

### 1. No Knowledge Cutoff
- Always provides current information
- Searches the web in real-time
- Updates with latest developments

### 2. Automatic Citations
- Provides sources for claims
- Links to original content
- Verifiable information

### 3. Web-Enhanced Responses
- Combines LLM capabilities with search
- Synthesizes multiple sources
- Provides comprehensive answers

### 4. Specialized Search Modes
- Academic search for research
- News search for current events
- Technical search for documentation

## Configuration

```java
@Configuration
public class PerplexityConfig {
    
    @Value("${perplexity.api.key}")
    private String apiKey;
    
    @Bean(name = "perplexityChatModel")
    public ChatModel perplexityChatModel() {
        // Perplexity uses OpenAI-compatible API
        OpenAiApi perplexityApi = OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl("https://api.perplexity.ai")
            .build();
            
        return new OpenAiChatModel(perplexityApi, 
            OpenAiChatOptions.builder()
                .model("llama-3.1-sonar-small-128k-online")
                .temperature(0.2)
                .build()
        );
    }
}
```

## Available Models

### Online Models (with search)
- `llama-3.1-sonar-small-128k-online` - Fast, cost-effective
- `llama-3.1-sonar-large-128k-online` - More capable
- `llama-3.1-sonar-huge-128k-online` - Most powerful

### Offline Models (no search)
- `llama-3.1-sonar-small-128k-chat` - For general chat
- `llama-3.1-sonar-large-128k-chat` - Better reasoning
- `llama-3.1-8b-instruct` - Instruction following
- `llama-3.1-70b-instruct` - Advanced tasks

## Best Practices

### 1. Choose the Right Model
```java
// For quick searches and current info
.model("llama-3.1-sonar-small-128k-online")

// For complex research and analysis
.model("llama-3.1-sonar-large-128k-online")
```

### 2. Optimize Search Queries
```java
// Be specific in search queries
"latest Spring Boot 3.2 features released in 2024"

// Include context for better results
"quantum computing breakthroughs IBM Google 2024"
```

### 3. Handle Search Results
```java
// Check if sources are provided
if (response.getResult().getMetadata().containsKey("sources")) {
    List<String> sources = (List<String>) response.getResult()
        .getMetadata().get("sources");
    // Display or log sources
}
```

### 4. Use Appropriate Temperature
```java
// Lower temperature for factual searches
.temperature(0.1)  // More focused

// Higher temperature for creative tasks
.temperature(0.7)  // More varied
```

## Limitations

1. **API Costs**: Search-enabled models may be more expensive
2. **Rate Limits**: Check current rate limits for your plan
3. **Search Quality**: Results depend on web search quality
4. **Response Time**: May be slower due to search operations

## Example Use Cases

### Research Assistant
```java
@Service
public class ResearchAssistant {
    public ResearchReport generateReport(String topic) {
        // Get current information
        var current = perplexityService.getCurrentInformation(topic);
        
        // Search academic sources
        var academic = perplexityService.searchAcademicPapers(topic);
        
        // Analyze trends
        var trends = perplexityService.analyzeTrendingTopics(topic);
        
        return combineResults(current, academic, trends);
    }
}
```

### Real-Time News Aggregator
```java
@RestController
public class NewsController {
    @GetMapping("/news/{category}")
    public NewsResponse getLatestNews(@PathVariable String category) {
        return perplexityService.searchNews(category, "last 24 hours");
    }
}
```

### Technical Documentation Helper
```java
@Component
public class DocSearcher {
    public String findLatestDocs(String framework, String feature) {
        var response = perplexityService.searchTechnicalDocs(
            framework, 
            "how to implement " + feature
        );
        return extractCodeExamples(response);
    }
}
```

## Conclusion

Perplexity AI bridges the gap between traditional LLMs and search engines, making it ideal for applications that require:
- Current, up-to-date information
- Fact-checking and verification
- Technical research
- Market analysis
- News aggregation

By combining the reasoning capabilities of LLMs with real-time web search, Perplexity enables AI applications that stay current with the rapidly changing world.
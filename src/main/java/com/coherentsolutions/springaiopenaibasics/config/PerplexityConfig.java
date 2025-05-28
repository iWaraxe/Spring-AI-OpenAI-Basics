package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

/**
 * Configuration for Perplexity AI integration using OpenAI client.
 * 
 * Perplexity provides an OpenAI-compatible API endpoint but with unique features:
 * - Real-time web search integration
 * - No knowledge cutoff
 * - Automatic source citations
 * - Search-optimized models
 * 
 * Note: Perplexity doesn't support function calling or multimodal inputs
 */
@Configuration
public class PerplexityConfig {

    @Value("${perplexity.api-key:${spring.ai.openai.api-key:}}")
    private String apiKey;

    @Value("${perplexity.base-url:https://api.perplexity.ai}")
    private String baseUrl;

    @Value("${perplexity.chat.model:llama-3.1-sonar-large-128k-online}")
    private String defaultModel;

    @Value("${perplexity.chat.completions-path:/chat/completions}")
    private String completionsPath;

    /**
     * Creates a Perplexity-specific ChatModel using OpenAI client.
     * 
     * This configuration:
     * - Points to Perplexity's API endpoint
     * - Uses Perplexity's search-enhanced models
     * - Configures appropriate defaults for search tasks
     */
    @Bean(name = "perplexityChatModel")
    public ChatModel perplexityChatModel() {
        // Create OpenAI API client pointing to Perplexity
        OpenAiApi perplexityApi = OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .build();
        
        // Configure default options for Perplexity
        OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
            .model(defaultModel)
            .temperature(0.3) // Lower for factual accuracy
            .topP(0.9)
            .presencePenalty(0.0)
            .frequencyPenalty(1.0) // Perplexity default
            .build();
        
        return new OpenAiChatModel(perplexityApi, defaultOptions);
    }

    /**
     * Alternative configuration for fast searches
     */
    @Bean(name = "perplexityFastChatModel")
    public ChatModel perplexityFastChatModel() {
        OpenAiApi perplexityApi = OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .build();
        
        OpenAiChatOptions fastOptions = OpenAiChatOptions.builder()
            .model("llama-3.1-sonar-small-128k-online") // Faster model
            .temperature(0.2)
            .topP(0.9)
            .build();
        
        return new OpenAiChatModel(perplexityApi, fastOptions);
    }

    /**
     * Bean providing Perplexity model information
     */
    @Bean
    public PerplexityModels perplexityModels() {
        return new PerplexityModels();
    }

    /**
     * Helper class containing Perplexity model information
     */
    public static class PerplexityModels {
        
        public static final String SONAR_SMALL = "llama-3.1-sonar-small-128k-online";
        public static final String SONAR_LARGE = "llama-3.1-sonar-large-128k-online";
        public static final String SONAR_HUGE = "llama-3.1-sonar-huge-128k-online";
        
        public Map<String, String> getModelDescriptions() {
            return Map.of(
                SONAR_SMALL, "8B parameters, fastest responses, good for simple queries",
                SONAR_LARGE, "70B parameters, balanced performance, recommended for most use cases",
                SONAR_HUGE, "405B parameters, highest quality, best for complex research"
            );
        }
        
        public Map<String, Integer> getContextWindows() {
            return Map.of(
                SONAR_SMALL, 127072,
                SONAR_LARGE, 127072,
                SONAR_HUGE, 127072
            );
        }
        
        public List<String> getOnlineModels() {
            return List.of(SONAR_SMALL, SONAR_LARGE, SONAR_HUGE);
        }
    }
}
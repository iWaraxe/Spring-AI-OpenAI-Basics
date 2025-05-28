package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for low-level OpenAI API client.
 * 
 * This configuration demonstrates manual setup of the OpenAI API client,
 * providing full control over the configuration compared to auto-configuration.
 * 
 * Benefits of manual configuration:
 * - Custom base URL configuration
 * - Advanced API client settings
 * - Custom retry policies
 * - Environment-specific configurations
 */
@Configuration
public class LowLevelApiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.openai.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.organization-id:}")
    private String organizationId;

    @Value("${spring.ai.openai.project-id:}")
    private String projectId;

    /**
     * Creates an OpenAiApi bean for low-level API access.
     * 
     * This provides direct access to the OpenAI REST API with full control
     * over request/response handling, allowing for:
     * 
     * - Custom HTTP configurations
     * - Advanced error handling
     * - Direct API feature access
     * - Performance optimizations
     */
    @Bean
    public OpenAiApi openAiApi() {
        OpenAiApi.Builder builder = OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl);

        // Optional organization and project configuration
        if (organizationId != null && !organizationId.isEmpty()) {
            builder.organizationId(organizationId);
        }
        
        if (projectId != null && !projectId.isEmpty()) {
            builder.projectId(projectId);
        }

        return builder.build();
    }

    /**
     * Alternative configuration method showing custom API client setup.
     * This could be used for different environments or custom requirements.
     */
    // @Bean
    // @Profile("custom")
    public OpenAiApi customOpenAiApi() {
        return OpenAiApi.builder()
            .apiKey(apiKey)
            .baseUrl("https://custom-openai-proxy.example.com") // Custom proxy
            .organizationId("custom-org")
            .build();
    }
}
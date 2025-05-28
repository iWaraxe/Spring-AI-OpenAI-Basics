package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for low-level Anthropic API client.
 * 
 * This configuration demonstrates manual setup of the Anthropic API client,
 * providing full control over the configuration compared to auto-configuration.
 * 
 * Benefits of manual configuration:
 * - Custom base URL configuration
 * - Advanced API client settings
 * - Custom retry policies
 * - Environment-specific configurations
 * - Direct access to Anthropic's API features
 */
@Configuration
public class LowLevelAnthropicConfig {

    @Value("${spring.ai.anthropic.api-key}")
    private String apiKey;

    @Value("${spring.ai.anthropic.base-url:https://api.anthropic.com}")
    private String baseUrl;

    @Value("${spring.ai.anthropic.version:2023-06-01}")
    private String version;

    @Value("${spring.ai.anthropic.beta-version:tools-2024-04-04}")
    private String betaVersion;

    /**
     * Creates an AnthropicApi bean for low-level API access.
     * 
     * This provides direct access to the Anthropic REST API with full control
     * over request/response handling, allowing for:
     * 
     * - Custom HTTP configurations
     * - Advanced error handling
     * - Direct access to Claude-specific features
     * - Performance optimizations
     * - Enterprise integration requirements
     */
    @Bean
    public AnthropicApi anthropicApi() {
        AnthropicApi.Builder builder = AnthropicApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .version(version);

        // Optional beta version for experimental features
        if (betaVersion != null && !betaVersion.isEmpty()) {
            builder.betaVersion(betaVersion);
        }

        return builder.build();
    }

    /**
     * Alternative configuration method showing custom API client setup.
     * This could be used for different environments or custom requirements.
     */
    // @Bean
    // @Profile("custom")
    public AnthropicApi customAnthropicApi() {
        return AnthropicApi.builder()
            .apiKey(apiKey)
            .baseUrl("https://custom-anthropic-proxy.example.com") // Custom proxy
            .version("2023-06-01")
            .betaVersion("max-tokens-3-5-sonnet-2024-07-15") // Extended token limit
            .build();
    }

    /**
     * Configuration for specific Claude models with optimized settings
     */
    // @Bean
    // @Qualifier("claude-opus")
    public AnthropicApi claudeOpusApi() {
        return AnthropicApi.builder()
            .apiKey(apiKey)
            .baseUrl(baseUrl)
            .version("2023-06-01")
            .betaVersion("tools-2024-04-04")
            .build();
    }
}
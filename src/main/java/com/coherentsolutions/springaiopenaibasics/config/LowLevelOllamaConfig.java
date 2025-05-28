package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for low-level Ollama API client.
 * 
 * This configuration demonstrates manual setup of the Ollama API client,
 * providing full control over the configuration compared to auto-configuration.
 * 
 * Benefits of manual configuration:
 * - Custom base URL for different Ollama deployments
 * - Advanced API client settings
 * - Multiple Ollama instances support
 * - Custom timeout and connection settings
 */
@Configuration
public class LowLevelOllamaConfig {

    @Value("${spring.ai.ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    /**
     * Creates an OllamaApi bean for low-level API access.
     * 
     * This provides direct access to the Ollama REST API with full control
     * over request/response handling, allowing for:
     * 
     * - Model management operations
     * - Hardware-specific optimizations
     * - Custom streaming implementations
     * - Direct access to model capabilities
     * - Performance optimizations
     */
    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder()
            .baseUrl(baseUrl)
            .build();
    }

    /**
     * Alternative configuration for remote Ollama server
     */
    // @Bean
    // @Profile("remote")
    public OllamaApi remoteOllamaApi() {
        return OllamaApi.builder()
            .baseUrl("http://remote-ollama-server:11434")
            .build();
    }

    /**
     * Configuration for GPU-optimized Ollama instance
     */
    // @Bean
    // @Profile("gpu")
    public OllamaApi gpuOllamaApi() {
        return OllamaApi.builder()
            .baseUrl("http://gpu-server:11434")
            .build();
    }

    /**
     * Configuration for edge deployment with custom settings
     */
    // @Bean
    // @Profile("edge")
    public OllamaApi edgeOllamaApi() {
        return OllamaApi.builder()
            .baseUrl("http://localhost:11434")
            // Custom timeout for edge devices
            .build();
    }
}
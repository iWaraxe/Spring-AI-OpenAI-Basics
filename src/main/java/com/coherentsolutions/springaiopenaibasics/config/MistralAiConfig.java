package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "mistral.ai.enabled", havingValue = "true", matchIfMissing = false)
public class MistralAiConfig {

    @Value("${mistral.ai.api.key:}")
    private String apiKey;

    @Value("${mistral.ai.base.url:https://api.mistral.ai}")
    private String baseUrl;

    @Value("${mistral.ai.model:mistral-small-latest}")
    private String model;

    @Value("${mistral.ai.temperature:0.7}")
    private Double temperature;

    @Value("${mistral.ai.max-tokens:1000}")
    private Integer maxTokens;

    @Bean
    public MistralAiApi mistralAiApi() {
        return new MistralAiApi(baseUrl, apiKey);
    }

    @Bean
    public MistralAiChatModel mistralAiChatModel(MistralAiApi mistralAiApi) {
        var options = MistralAiChatOptions.builder()
                .model(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        return new MistralAiChatModel(mistralAiApi, options);
    }
}
package com.coherentsolutions.springaiopenaibasics.config;

import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for the different ChatModel implementations.
 * Configures and qualifies both OpenAI and Anthropic chat models.
 */
@Configuration
public class ChatModelConfig {

    @Bean
    @Primary
    @Qualifier("openAiChatModel")
    public ChatModel openAiChatModel(OpenAiChatModel openAiChatModel) {
        return openAiChatModel;
    }

    @Bean
    @Qualifier("anthropicChatModel")
    public ChatModel anthropicChatModel(AnthropicChatModel anthropicChatModel) {
        return anthropicChatModel;
    }
}
package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import org.springframework.ai.mistralai.MistralAiChatOptions;
import org.springframework.ai.mistralai.api.MistralAiApi;
import org.springframework.ai.model.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.List;

@Service
public class MistralAiServiceImpl implements MistralAiService {

    private final MistralAiChatModel mistralAiChatModel;

    public MistralAiServiceImpl(MistralAiChatModel mistralAiChatModel) {
        this.mistralAiChatModel = mistralAiChatModel;
    }

    @Override
    public ChatResponse generateResponse(String message) {
        return mistralAiChatModel.call(new Prompt(message));
    }

    @Override
    public Flux<ChatResponse> generateStreamResponse(String message) {
        return mistralAiChatModel.stream(new Prompt(message));
    }

    @Override
    public ChatResponse generateWithTemperature(String message, Double temperature) {
        var options = MistralAiChatOptions.builder()
                .temperature(temperature)
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }

    @Override
    public ChatResponse generateWithMaxTokens(String message, Integer maxTokens) {
        var options = MistralAiChatOptions.builder()
                .maxTokens(maxTokens)
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }

    @Override
    public ChatResponse generateWithSafePrompt(String message, Boolean safePrompt) {
        var options = MistralAiChatOptions.builder()
                .safePrompt(safePrompt)
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }

    @Override
    public ChatResponse generateMultiModal(String message, String imageUrl) {
        var userMessage = new UserMessage(message,
                new Media(MimeTypeUtils.IMAGE_PNG, URI.create(imageUrl)));
        
        var options = MistralAiChatOptions.builder()
                .model(MistralAiApi.ChatModel.PIXTRAL_LARGE.getValue())
                .build();
        
        return mistralAiChatModel.call(new Prompt(userMessage, options));
    }

    @Override
    public ChatResponse generateWithRandomSeed(String message, Integer randomSeed) {
        var options = MistralAiChatOptions.builder()
                .randomSeed(randomSeed)
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }

    @Override
    public ChatResponse generateJsonResponse(String message) {
        var options = MistralAiChatOptions.builder()
                .responseFormat("json_object")
                .build();
        
        return mistralAiChatModel.call(new Prompt(
                "Please respond in valid JSON format: " + message, options));
    }

    @Override
    public ChatResponse generateWithStopSequences(String message, String... stopSequences) {
        var options = MistralAiChatOptions.builder()
                .stop(List.of(stopSequences))
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }

    @Override
    public ChatResponse generateWithTopP(String message, Double topP) {
        var options = MistralAiChatOptions.builder()
                .topP(topP)
                .build();
        
        return mistralAiChatModel.call(new Prompt(message, options));
    }
}
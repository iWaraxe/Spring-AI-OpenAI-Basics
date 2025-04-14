package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of AnthropicService that uses Spring AI's ChatModel
 * to interact with Anthropic's Claude API. This service converts user questions
 * into AI-generated responses.
 */
@Service
public class AnthropicServiceImpl implements AnthropicService {

    private final ChatModel chatModel;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Autowired
    private ObjectMapper objectMapper;

    public AnthropicServiceImpl(@Qualifier("anthropicChatModel") ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public GetCapitalWithInfoResponse getCapitalWithInfo(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<GetCapitalWithInfoResponse> converter = new BeanOutputConverter<>(GetCapitalWithInfoResponse.class);
        String format = converter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);

        Prompt prompt = new Prompt(
                promptTemplate.render(Map.of(
                        "stateOrCountry", getCapitalRequest.stateOrCountry(),
                        "format", format
                ))
        );

        ChatResponse response = chatModel.call(prompt);
        return converter.convert(response.getResult().getOutput().getText());
    }

    @Override
    public GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<GetCapitalResponse> converter = new BeanOutputConverter<>(GetCapitalResponse.class);
        String format = converter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);

        Prompt prompt = new Prompt(
                promptTemplate.render(Map.of(
                        "stateOrCountry", getCapitalRequest.stateOrCountry(),
                        "format", format))
        );

        ChatResponse response = chatModel.call(prompt);
        return converter.convert(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getAnswer(Question question) {
        Prompt prompt = new Prompt(question.question());
        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public String getAnswer(String question) {
        Prompt prompt = new Prompt(question);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of OllamaService that uses Spring AI's OllamaChatModel
 * to interact with locally running Ollama models.
 */
@Service
public class OllamaServiceImpl implements OllamaService {

    private final OllamaChatModel chatModel;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Autowired
    private ObjectMapper objectMapper;

    public OllamaServiceImpl(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public GetCapitalWithInfoResponse getCapitalWithInfo(GetCapitalRequest getCapitalRequest) {
        if (getCapitalRequest == null || getCapitalRequest.stateOrCountry() == null || getCapitalRequest.stateOrCountry().isBlank()) {
            throw new IllegalArgumentException("State or country cannot be null or empty");
        }

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
        if (getCapitalRequest == null || getCapitalRequest.stateOrCountry() == null || getCapitalRequest.stateOrCountry().isBlank()) {
            throw new IllegalArgumentException("State or country cannot be null or empty");
        }

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
        if (question == null || question.question() == null || question.question().isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }

        Prompt prompt = new Prompt(new UserMessage(question.question()));
        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public String getAnswer(String question) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Question cannot be null or empty");
        }

        Prompt prompt = new Prompt(new UserMessage(question));
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }
}
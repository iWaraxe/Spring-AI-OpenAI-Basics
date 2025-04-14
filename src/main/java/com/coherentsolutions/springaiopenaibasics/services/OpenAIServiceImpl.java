package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.Answer;
import com.coherentsolutions.springaiopenaibasics.model.GetCapitalRequest;
import com.coherentsolutions.springaiopenaibasics.model.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of OpenAIService that uses Spring AI's ChatModel
 * to interact with OpenAI's API. This service converts user questions
 * into AI-generated responses.
 */
@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final ChatModel chatModel;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Autowired
    private ObjectMapper objectMapper;

    public OpenAIServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest) {
        // In Spring AI M7, PromptTemplate is in a different package
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);

        // In M7, we use render() and create a Prompt with the rendered string
        Prompt prompt = new Prompt(
                promptTemplate.render(Map.of("stateOrCountry", getCapitalRequest.stateOrCountry()))
        );

        ChatResponse response = chatModel.call(prompt);

        // Using getText() instead of getContent() in M7
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getCapital(GetCapitalRequest getCapitalRequest) {
        // In Spring AI M7, PromptTemplate is in a different package
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);

        // Create the prompt with parameters for the template
        Prompt prompt = new Prompt(
                promptTemplate.render(Map.of("stateOrCountry", getCapitalRequest.stateOrCountry()))
        );

        ChatResponse response = chatModel.call(prompt);

        System.out.println(response.getResult().getOutput().getText());
        String responseString;
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getResult().getOutput().getText());
            responseString = jsonNode.get("answer").asText();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new Answer(responseString);
    }

    @Override
    public Answer getAnswer(Question question) {
        // In Spring AI M7, we create the Prompt directly without PromptTemplate
        Prompt prompt = new Prompt(question.question());
        ChatResponse response = chatModel.call(prompt);

        // In Spring AI M7, we use getText() instead of getContent()
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public String getAnswer(String question) {
        // In Spring AI M7, we can directly create a Prompt without using PromptTemplate
        Prompt prompt = new Prompt(question);

        // Call the ChatModel with our prompt to get a response
        ChatResponse response = chatModel.call(prompt);

        // Extract and return the text content from the response
        return response.getResult().getOutput().getText();
    }
}
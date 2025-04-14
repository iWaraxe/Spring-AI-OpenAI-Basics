package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.Answer;
import com.coherentsolutions.springaiopenaibasics.model.Question;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

/**
 * Implementation of OpenAIService that uses Spring AI's ChatModel
 * to interact with OpenAI's API. This service converts user questions
 * into AI-generated responses.
 */
@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final ChatModel chatModel;

    public OpenAIServiceImpl(ChatModel chatModel) {
        this.chatModel = chatModel;
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
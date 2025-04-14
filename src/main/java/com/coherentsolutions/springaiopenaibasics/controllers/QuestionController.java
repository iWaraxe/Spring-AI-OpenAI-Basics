package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.model.Answer;
import com.coherentsolutions.springaiopenaibasics.model.GetCapitalRequest;
import com.coherentsolutions.springaiopenaibasics.model.Question;
import com.coherentsolutions.springaiopenaibasics.services.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling AI question-answering requests.
 * Exposes endpoints that accept questions and return AI-generated answers.
 */
@RestController
@RequestMapping("/api/ai")
public class QuestionController {

    private final OpenAIService openAIService;

    public QuestionController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @PostMapping("/capital")
    public Answer getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        return openAIService.getAnswer(question);
    }
}
package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.model.*;
import com.coherentsolutions.springaiopenaibasics.services.OllamaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for handling locally running Ollama model requests.
 */
@RestController
@RequestMapping("/api/ollama")
public class OllamaQuestionController {

    private final OllamaService ollamaService;

    public OllamaQuestionController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping("/capitalWithInfo")
    public GetCapitalWithInfoResponse getCapitalWithInfo(@RequestBody GetCapitalRequest getCapitalRequest) {
        if (getCapitalRequest == null || getCapitalRequest.stateOrCountry() == null || getCapitalRequest.stateOrCountry().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "State or country cannot be null or empty");
        }
        return this.ollamaService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capital")
    public GetCapitalResponse getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        if (getCapitalRequest == null || getCapitalRequest.stateOrCountry() == null || getCapitalRequest.stateOrCountry().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "State or country cannot be null or empty");
        }
        return this.ollamaService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        if (question == null || question.question() == null || question.question().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question cannot be null or empty");
        }
        return ollamaService.getAnswer(question);
    }
}
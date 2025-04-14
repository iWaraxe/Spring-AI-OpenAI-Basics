package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.model.*;
import com.coherentsolutions.springaiopenaibasics.services.AnthropicService;
import com.coherentsolutions.springaiopenaibasics.services.OpenAIService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling AI question-answering requests.
 * Exposes endpoints for both OpenAI and Anthropic Claude models.
 */
@RestController
@RequestMapping("/api/claude")
public class AnthropicQuestionController {

    private final AnthropicService anthropicService;

    public AnthropicQuestionController(OpenAIService openAIService, AnthropicService anthropicService) {
        this.anthropicService = anthropicService;
    }

    // Anthropic Claude endpoints
    @PostMapping("/capitalWithInfo")
    public GetCapitalWithInfoResponse getCapitalWithInfoClaude(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.anthropicService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capital")
    public GetCapitalResponse getCapitalClaude(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.anthropicService.getCapital(getCapitalRequest);
    }

    @PostMapping("/ask")
    public Answer askQuestionClaude(@RequestBody Question question) {
        return anthropicService.getAnswer(question);
    }
}
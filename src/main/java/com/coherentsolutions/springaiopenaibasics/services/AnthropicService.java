package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.*;

/**
 * Service interface for Anthropic Claude interactions.
 * Provides methods to communicate with Claude API through Spring AI.
 */
public interface AnthropicService {
    GetCapitalWithInfoResponse getCapitalWithInfo(GetCapitalRequest getCapitalRequest);
    GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest);
    String getAnswer(String question);
    Answer getAnswer(Question question);
}
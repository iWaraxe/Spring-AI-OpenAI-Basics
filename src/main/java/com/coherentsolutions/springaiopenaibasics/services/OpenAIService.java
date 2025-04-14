package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.*;

/**
 * Service interface for OpenAI interactions.
 * Provides methods to communicate with OpenAI's API through Spring AI.
 */
public interface OpenAIService {

    GetCapitalWithInfoResponse getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest);

    String getAnswer(String question);

    Answer getAnswer(Question question);
}
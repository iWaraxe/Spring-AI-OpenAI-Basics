package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.Answer;
import com.coherentsolutions.springaiopenaibasics.model.GetCapitalRequest;
import com.coherentsolutions.springaiopenaibasics.model.GetCapitalResponse;
import com.coherentsolutions.springaiopenaibasics.model.Question;

/**
 * Service interface for OpenAI interactions.
 * Provides methods to communicate with OpenAI's API through Spring AI.
 */
public interface OpenAIService {

    Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest);

    String getAnswer(String question);

    Answer getAnswer(Question question);
}
package com.coherentsolutions.springaiopenaibasics.services;

import com.coherentsolutions.springaiopenaibasics.model.*;

/**
 * Service interface for Ollama interactions.
 * Provides methods to communicate with local Ollama models through Spring AI.
 */
public interface OllamaService {
    GetCapitalWithInfoResponse getCapitalWithInfo(GetCapitalRequest getCapitalRequest);
    GetCapitalResponse getCapital(GetCapitalRequest getCapitalRequest);
    String getAnswer(String question);
    Answer getAnswer(Question question);
}
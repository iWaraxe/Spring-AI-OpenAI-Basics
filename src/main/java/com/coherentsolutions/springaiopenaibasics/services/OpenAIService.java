package com.coherentsolutions.springaiopenaibasics.services;

/**
 * Service interface for OpenAI interactions.
 * Provides methods to communicate with OpenAI's API through Spring AI.
 */
public interface OpenAIService {
    String getAnswer(String question);
}
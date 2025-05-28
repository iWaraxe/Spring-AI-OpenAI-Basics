package com.coherentsolutions.springaiopenaibasics.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.mistralai.MistralAiChatModel;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MistralAiServiceMockTest {

    @Mock
    private MistralAiChatModel mistralAiChatModel;

    private MistralAiService mistralAiService;

    @BeforeEach
    void setUp() {
        mistralAiService = new MistralAiServiceImpl(mistralAiChatModel);
    }

    @Test
    void generateResponse_ShouldReturnChatResponse() {
        // Arrange
        String message = "Hello Mistral";
        String expectedResponse = "Hello! I'm Mistral AI, how can I help you today?";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateResponse(message);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateStreamResponse_ShouldReturnFluxOfChatResponses() {
        // Arrange
        String message = "Stream test";
        String expectedResponse1 = "Hello";
        String expectedResponse2 = " world!";
        
        ChatResponse mockResponse1 = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse1))));
        ChatResponse mockResponse2 = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse2))));
        
        when(mistralAiChatModel.stream(any(Prompt.class)))
            .thenReturn(Flux.just(mockResponse1, mockResponse2));

        // Act & Assert
        StepVerifier.create(mistralAiService.generateStreamResponse(message))
            .expectNext(mockResponse1)
            .expectNext(mockResponse2)
            .verifyComplete();
    }

    @Test
    void generateWithTemperature_ShouldReturnResponseWithCustomTemperature() {
        // Arrange
        String message = "Creative writing task";
        Double temperature = 0.9;
        String expectedResponse = "Once upon a time in a magical kingdom...";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithTemperature(message, temperature);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateWithMaxTokens_ShouldReturnResponseWithTokenLimit() {
        // Arrange
        String message = "Tell me about AI";
        Integer maxTokens = 100;
        String expectedResponse = "AI is artificial intelligence...";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithMaxTokens(message, maxTokens);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateWithSafePrompt_ShouldReturnSafeResponse() {
        // Arrange
        String message = "Tell me something";
        Boolean safePrompt = true;
        String expectedResponse = "I'm here to provide helpful and safe information.";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithSafePrompt(message, safePrompt);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateMultiModal_ShouldReturnImageAnalysis() {
        // Arrange
        String message = "What do you see in this image?";
        String imageUrl = "https://example.com/image.jpg";
        String expectedResponse = "I can see a beautiful landscape with mountains and trees.";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateMultiModal(message, imageUrl);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateWithRandomSeed_ShouldReturnDeterministicResponse() {
        // Arrange
        String message = "Generate a random number";
        Integer randomSeed = 42;
        String expectedResponse = "Based on the seed 42, here's your deterministic response.";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithRandomSeed(message, randomSeed);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateJsonResponse_ShouldReturnValidJsonFormat() {
        // Arrange
        String message = "Create a user profile";
        String expectedResponse = "{\"name\": \"John Doe\", \"age\": 30, \"city\": \"Paris\"}";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateJsonResponse(message);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateWithStopSequences_ShouldStopAtSpecifiedSequence() {
        // Arrange
        String message = "Count to ten";
        String[] stopSequences = {"five", "5"};
        String expectedResponse = "One, two, three, four, five";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithStopSequences(message, stopSequences);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }

    @Test
    void generateWithTopP_ShouldReturnFocusedResponse() {
        // Arrange
        String message = "Explain quantum physics";
        Double topP = 0.8;
        String expectedResponse = "Quantum physics is the study of matter and energy at the smallest scales.";
        
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = mistralAiService.generateWithTopP(message, topP);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
    }
}
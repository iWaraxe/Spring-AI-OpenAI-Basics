package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.MistralAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MistralAiController.class)
class MistralAiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MistralAiService mistralAiService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generate_ShouldReturnGeneratedResponse() throws Exception {
        // Arrange
        String message = "Hello Mistral";
        String expectedResponse = "Hello! I'm Mistral AI.";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateResponse(message)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/mistral/generate")
                .param("message", message))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateStream_ShouldReturnStreamingResponse() throws Exception {
        // Arrange
        String message = "Stream test";
        ChatResponse mockResponse1 = new ChatResponse(
            List.of(new Generation(new AssistantMessage("Hello"))));
        ChatResponse mockResponse2 = new ChatResponse(
            List.of(new Generation(new AssistantMessage(" world!"))));
        
        when(mistralAiService.generateStreamResponse(message))
            .thenReturn(Flux.just(mockResponse1, mockResponse2));

        // Act & Assert
        mockMvc.perform(get("/mistral/stream")
                .param("message", message)
                .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/event-stream;charset=UTF-8"));
    }

    @Test
    void generateWithTemperature_ShouldReturnResponseWithTemperature() throws Exception {
        // Arrange
        String message = "Creative task";
        Double temperature = 0.9;
        String expectedResponse = "Creative response here...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithTemperature(message, temperature))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/temperature")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "temperature", temperature))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateWithMaxTokens_ShouldReturnResponseWithTokenLimit() throws Exception {
        // Arrange
        String message = "Tell me about AI";
        Integer maxTokens = 100;
        String expectedResponse = "AI is...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithMaxTokens(message, maxTokens))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "maxTokens", maxTokens))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateWithSafePrompt_ShouldReturnSafeResponse() throws Exception {
        // Arrange
        String message = "Tell me something";
        Boolean safePrompt = true;
        String expectedResponse = "Safe response here...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithSafePrompt(message, safePrompt))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/safe")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "safePrompt", safePrompt))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateMultiModal_ShouldReturnImageAnalysis() throws Exception {
        // Arrange
        String message = "Describe this image";
        String imageUrl = "https://example.com/image.jpg";
        String expectedResponse = "This image shows...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateMultiModal(message, imageUrl))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/multimodal")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "imageUrl", imageUrl))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateWithRandomSeed_ShouldReturnDeterministicResponse() throws Exception {
        // Arrange
        String message = "Generate something";
        Integer randomSeed = 42;
        String expectedResponse = "Deterministic response...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithRandomSeed(message, randomSeed))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/seed")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "randomSeed", randomSeed))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateJsonResponse_ShouldReturnJsonFormat() throws Exception {
        // Arrange
        String message = "Create user profile";
        String expectedResponse = "{\"name\": \"John\", \"age\": 30}";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateJsonResponse(message))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/json")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("message", message))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateWithStopSequences_ShouldStopAtSequence() throws Exception {
        // Arrange
        String message = "Count to ten";
        List<String> stopSequences = List.of("five", "5");
        String expectedResponse = "One, two, three, four, five";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithStopSequences(eq(message), any(String[].class)))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "stopSequences", stopSequences))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }

    @Test
    void generateWithTopP_ShouldReturnFocusedResponse() throws Exception {
        // Arrange
        String message = "Explain quantum physics";
        Double topP = 0.8;
        String expectedResponse = "Quantum physics is...";
        ChatResponse mockResponse = new ChatResponse(
            List.of(new Generation(new AssistantMessage(expectedResponse))));
        
        when(mistralAiService.generateWithTopP(message, topP))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/mistral/topP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of(
                    "message", message,
                    "topP", topP))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").value(expectedResponse));
    }
}
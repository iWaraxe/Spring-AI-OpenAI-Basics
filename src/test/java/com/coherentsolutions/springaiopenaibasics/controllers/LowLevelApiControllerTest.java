package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LowLevelApiController.class)
class LowLevelApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LowLevelOpenAiService lowLevelService;

    @Test
    void testBasicChat() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletion mockCompletion = createMockChatCompletion("Test response");
        ResponseEntity<OpenAiApi.ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.basicChatCompletion(anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of("message", "Hello");

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").value("Test response"));
    }

    @Test
    void testStreamChat() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletionChunk chunk1 = createMockChatCompletionChunk("Hello");
        OpenAiApi.ChatCompletionChunk chunk2 = createMockChatCompletionChunk(" world!");
        
        Flux<OpenAiApi.ChatCompletionChunk> mockStream = Flux.just(chunk1, chunk2);
        
        when(lowLevelService.streamingChatCompletion(anyString()))
            .thenReturn(mockStream);

        Map<String, String> request = Map.of("message", "Stream test");

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testCustomParamsChat() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletion mockCompletion = createMockChatCompletion("Custom response");
        ResponseEntity<OpenAiApi.ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.customParameterChat(anyString(), anyString(), anyDouble(), anyInt()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Custom test",
            "model", "gpt-4",
            "temperature", 0.5,
            "maxTokens", 100
        );

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/custom-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").value("Custom response"));
    }

    @Test
    void testBatchProcess() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletion completion1 = createMockChatCompletion("Response 1");
        OpenAiApi.ChatCompletion completion2 = createMockChatCompletion("Response 2");
        
        List<ResponseEntity<OpenAiApi.ChatCompletion>> mockResponses = List.of(
            ResponseEntity.ok(completion1),
            ResponseEntity.ok(completion2)
        );
        
        when(lowLevelService.batchProcessMessages(anyList()))
            .thenReturn(mockResponses);

        Map<String, List<String>> request = Map.of(
            "messages", List.of("Message 1", "Message 2")
        );

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].body.choices[0].message.content").value("Response 1"))
            .andExpect(jsonPath("$[1].body.choices[0].message.content").value("Response 2"));
    }

    @Test
    void testSystemUserChat() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletion mockCompletion = createMockChatCompletion("System guided response");
        ResponseEntity<OpenAiApi.ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.systemUserChat(anyString(), anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "systemPrompt", "You are a helpful assistant.",
            "userMessage", "What is AI?"
        );

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/system-user-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").value("System guided response"));
    }

    @Test
    void testGetMetadata() throws Exception {
        // Arrange
        Map<String, Object> mockMetadata = Map.of(
            "httpStatus", 200,
            "model", "gpt-4o-mini",
            "promptTokens", 10,
            "completionTokens", 15,
            "totalTokens", 25,
            "estimatedCost", 0.001
        );
        
        when(lowLevelService.getDetailedResponseMetadata(anyString()))
            .thenReturn(mockMetadata);

        Map<String, String> request = Map.of("message", "Metadata test");

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.model").value("gpt-4o-mini"))
            .andExpect(jsonPath("$.promptTokens").value(10))
            .andExpect(jsonPath("$.completionTokens").value(15))
            .andExpect(jsonPath("$.totalTokens").value(25))
            .andExpect(jsonPath("$.estimatedCost").value(0.001));
    }

    @Test
    void testLogitBiasChat() throws Exception {
        // Arrange
        OpenAiApi.ChatCompletion mockCompletion = createMockChatCompletion("Biased response");
        ResponseEntity<OpenAiApi.ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.chatWithLogitBias(anyString(), anyMap()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Logit bias test",
            "logitBias", Map.of("50256", -100)
        );

        // Act & Assert
        mockMvc.perform(post("/api/lowlevel/logit-bias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").value("Biased response"));
    }

    @Test
    void testComparisonInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/lowlevel/comparison-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.highLevelBenefits").isArray())
            .andExpect(jsonPath("$.lowLevelBenefits").isArray())
            .andExpect(jsonPath("$.useCasesForLowLevel").isArray())
            .andExpect(jsonPath("$.useCasesForHighLevel").isArray())
            .andExpect(jsonPath("$.highLevelBenefits[0]").value("Simplified API usage"))
            .andExpect(jsonPath("$.lowLevelBenefits[0]").value("Direct HTTP response access"));
    }

    // Helper methods
    private OpenAiApi.ChatCompletion createMockChatCompletion(String content) {
        OpenAiApi.ChatCompletionMessage message = new OpenAiApi.ChatCompletionMessage(
            content, OpenAiApi.Role.ASSISTANT);
        OpenAiApi.Choice choice = new OpenAiApi.Choice(0, message, "stop");
        OpenAiApi.Usage usage = new OpenAiApi.Usage(10, 15, 25);
        
        return new OpenAiApi.ChatCompletion(
            "chat-123",
            "chat.completion",
            System.currentTimeMillis() / 1000,
            "gpt-4o-mini",
            "fp_abc123",
            List.of(choice),
            usage
        );
    }

    private OpenAiApi.ChatCompletionChunk createMockChatCompletionChunk(String content) {
        return new OpenAiApi.ChatCompletionChunk(
            "chunk-123",
            "chat.completion.chunk",
            System.currentTimeMillis() / 1000,
            "gpt-4o-mini",
            "fp_abc123",
            List.of()
        );
    }
}
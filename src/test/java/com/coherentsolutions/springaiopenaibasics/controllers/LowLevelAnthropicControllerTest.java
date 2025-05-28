package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelAnthropicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(LowLevelAnthropicController.class)
class LowLevelAnthropicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LowLevelAnthropicService lowLevelService;

    @Test
    void testBasicChat() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Test response");
        ResponseEntity<AnthropicApi.ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.basicChatCompletion(anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of("message", "Hello Claude");

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Test response"));
    }

    @Test
    void testStreamChat() throws Exception {
        // Arrange
        AnthropicApi.StreamResponse event1 = createMockStreamResponse("message_start", "Hello");
        AnthropicApi.StreamResponse event2 = createMockStreamResponse("content_block_delta", " world!");
        
        Flux<AnthropicApi.StreamResponse> mockStream = Flux.just(event1, event2);
        
        when(lowLevelService.streamingChatCompletion(anyString()))
            .thenReturn(mockStream);

        Map<String, String> request = Map.of("message", "Stream test");

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testCustomParamsChat() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Custom response");
        ResponseEntity<AnthropicApi.ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.customParameterChat(anyString(), anyString(), anyDouble(), anyInt(), anyList()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Custom test",
            "model", "claude-3-opus-20240229",
            "temperature", 0.3,
            "maxTokens", 1000,
            "stopSequences", List.of("STOP", "END")
        );

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/custom-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Custom response"));
    }

    @Test
    void testSystemPromptChat() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("System guided response");
        ResponseEntity<AnthropicApi.ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.systemPromptChat(anyString(), anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "systemPrompt", "You are a helpful AI assistant.",
            "userMessage", "What is Anthropic Claude?"
        );

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/system-prompt-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("System guided response"));
    }

    @Test
    void testBatchProcess() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse completion1 = createMockChatCompletionResponse("Response 1");
        AnthropicApi.ChatCompletionResponse completion2 = createMockChatCompletionResponse("Response 2");
        
        List<ResponseEntity<AnthropicApi.ChatCompletionResponse>> mockResponses = List.of(
            ResponseEntity.ok(completion1),
            ResponseEntity.ok(completion2)
        );
        
        when(lowLevelService.batchProcessMessages(anyList()))
            .thenReturn(mockResponses);

        Map<String, List<String>> request = Map.of(
            "messages", List.of("Message 1", "Message 2")
        );

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].body.content[0].text").value("Response 1"))
            .andExpect(jsonPath("$[1].body.content[0].text").value("Response 2"));
    }

    @Test
    void testGetMetadata() throws Exception {
        // Arrange
        Map<String, Object> mockMetadata = Map.of(
            "httpStatus", 200,
            "model", "claude-3-5-sonnet-latest",
            "inputTokens", 15,
            "outputTokens", 25,
            "estimatedCost", 0.002,
            "stopReason", "end_turn"
        );
        
        when(lowLevelService.getDetailedResponseMetadata(anyString()))
            .thenReturn(mockMetadata);

        Map<String, String> request = Map.of("message", "Metadata test");

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.model").value("claude-3-5-sonnet-latest"))
            .andExpect(jsonPath("$.inputTokens").value(15))
            .andExpect(jsonPath("$.outputTokens").value(25))
            .andExpect(jsonPath("$.estimatedCost").value(0.002))
            .andExpect(jsonPath("$.stopReason").value("end_turn"));
    }

    @Test
    void testAdvancedParamsChat() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Advanced response");
        ResponseEntity<AnthropicApi.ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.advancedParameterChat(anyString(), anyInt(), anyDouble()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Advanced test",
            "topK", 40,
            "topP", 0.9
        );

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/advanced-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Advanced response"));
    }

    @Test
    void testMultiTurnConversation() throws Exception {
        // Arrange
        AnthropicApi.ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Multi-turn response");
        ResponseEntity<AnthropicApi.ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);
        
        when(lowLevelService.multiTurnConversation(anyList()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "conversationHistory", List.of(
                Map.of("role", "user", "content", "Hello"),
                Map.of("role", "assistant", "content", "Hi there!"),
                Map.of("role", "user", "content", "How are you?")
            )
        );

        // Act & Assert
        mockMvc.perform(post("/api/anthropic-lowlevel/multi-turn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").value("Multi-turn response"));
    }

    @Test
    void testGetApiInfo() throws Exception {
        // Arrange
        Map<String, Object> mockApiInfo = Map.of(
            "supportedModels", List.of("claude-3-5-sonnet-latest", "claude-3-opus-20240229"),
            "maxTokensLimits", Map.of("claude-3-5-sonnet-latest", 8192),
            "contextWindows", Map.of("claude-3-5-sonnet-latest", 200000),
            "supportedFeatures", List.of("System prompts", "Vision capabilities"),
            "anthropicSpecificFeatures", List.of("Constitutional AI", "Advanced reasoning")
        );
        
        when(lowLevelService.getAnthropicApiInfo())
            .thenReturn(mockApiInfo);

        // Act & Assert
        mockMvc.perform(get("/api/anthropic-lowlevel/api-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.supportedModels").isArray())
            .andExpect(jsonPath("$.supportedModels[0]").value("claude-3-5-sonnet-latest"))
            .andExpect(jsonPath("$.maxTokensLimits").exists())
            .andExpect(jsonPath("$.contextWindows").exists())
            .andExpect(jsonPath("$.supportedFeatures").isArray())
            .andExpect(jsonPath("$.anthropicSpecificFeatures").isArray());
    }

    @Test
    void testComparisonInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/anthropic-lowlevel/comparison-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.highLevelBenefits").isArray())
            .andExpect(jsonPath("$.lowLevelBenefits").isArray())
            .andExpect(jsonPath("$.anthropicSpecificUseCases").isArray())
            .andExpect(jsonPath("$.whenToUseLowLevel").isArray())
            .andExpect(jsonPath("$.whenToUseHighLevel").isArray())
            .andExpect(jsonPath("$.highLevelBenefits[0]").value("Simplified API usage with ChatModel interface"))
            .andExpect(jsonPath("$.lowLevelBenefits[0]").value("Direct access to Anthropic's message structure"));
    }

    // Helper methods
    private AnthropicApi.ChatCompletionResponse createMockChatCompletionResponse(String content) {
        AnthropicApi.ContentBlock contentBlock = new AnthropicApi.ContentBlock(content);
        AnthropicApi.Usage usage = new AnthropicApi.Usage(10, 15);
        
        return new AnthropicApi.ChatCompletionResponse(
            "msg_123",
            "message",
            AnthropicApi.Role.ASSISTANT,
            List.of(contentBlock),
            "claude-3-5-sonnet-latest",
            "end_turn",
            null,
            usage
        );
    }

    private AnthropicApi.StreamResponse createMockStreamResponse(String eventType, String data) {
        return new AnthropicApi.StreamResponse(eventType, data);
    }
}
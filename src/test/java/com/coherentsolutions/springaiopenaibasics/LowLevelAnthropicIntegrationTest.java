package com.coherentsolutions.springaiopenaibasics;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelAnthropicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for low-level Anthropic API functionality.
 * 
 * These tests demonstrate the capabilities and benefits of using
 * the low-level Anthropic API compared to the high-level ChatModel interface.
 * 
 * To run these tests, set ANTHROPIC_API_KEY environment variable.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class LowLevelAnthropicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LowLevelAnthropicService lowLevelService;

    @Test
    void testBasicChatEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "What is Spring AI?");

        mockMvc.perform(post("/api/anthropic-lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").exists())
            .andExpect(jsonPath("$.usage").exists())
            .andExpect(jsonPath("$.model").exists())
            .andExpect(jsonPath("$.role").value("assistant"));
    }

    @Test
    void testStreamingChatEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "Count from 1 to 3");

        mockMvc.perform(post("/api/anthropic-lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testCustomParametersEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "message", "Explain quantum computing in exactly 50 words",
            "model", "claude-3-5-sonnet-latest",
            "temperature", 0.3,
            "maxTokens", 100,
            "stopSequences", List.of("END", "STOP")
        );

        mockMvc.perform(post("/api/anthropic-lowlevel/custom-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").isNotEmpty())
            .andExpect(jsonPath("$.model").value("claude-3-5-sonnet-latest"));
    }

    @Test
    void testSystemPromptChatEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "systemPrompt", "You are a concise technical expert. Answer in exactly one sentence.",
            "userMessage", "What is REST API?"
        );

        mockMvc.perform(post("/api/anthropic-lowlevel/system-prompt-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").isNotEmpty())
            .andExpect(jsonPath("$.role").value("assistant"));
    }

    @Test
    void testMetadataEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "Hello Claude");

        mockMvc.perform(post("/api/anthropic-lowlevel/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.inputTokens").exists())
            .andExpect(jsonPath("$.outputTokens").exists())
            .andExpect(jsonPath("$.estimatedCost").exists())
            .andExpect(jsonPath("$.model").exists())
            .andExpect(jsonPath("$.stopReason").exists());
    }

    @Test
    void testAdvancedParametersEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "message", "Write a creative sentence about AI",
            "topK", 40,
            "topP", 0.9
        );

        mockMvc.perform(post("/api/anthropic-lowlevel/advanced-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").isNotEmpty());
    }

    @Test
    void testMultiTurnConversationEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "conversationHistory", List.of(
                Map.of("role", "user", "content", "Hello"),
                Map.of("role", "assistant", "content", "Hi there! How can I help you?"),
                Map.of("role", "user", "content", "What's 2+2?")
            )
        );

        mockMvc.perform(post("/api/anthropic-lowlevel/multi-turn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].text").isNotEmpty());
    }

    @Test
    void testBatchProcessingEndpoint() throws Exception {
        Map<String, List<String>> request = Map.of(
            "messages", List.of(
                "What is 2+2?",
                "What is the capital of France?",
                "Name one programming language."
            )
        );

        mockMvc.perform(post("/api/anthropic-lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].body.content[0].text").isNotEmpty())
            .andExpect(jsonPath("$[1].body.content[0].text").isNotEmpty())
            .andExpect(jsonPath("$[2].body.content[0].text").isNotEmpty());
    }

    @Test
    void testLowLevelServiceDirectly() {
        // Test basic chat completion
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.basicChatCompletion("What is the meaning of life?");
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().content().isEmpty());
        assertNotNull(response.getBody().content().get(0).text());
    }

    @Test
    void testStreamingDirectly() {
        // Test streaming response
        var stream = lowLevelService.streamingChatCompletion("Tell me a very short joke");
        
        StepVerifier.create(stream.take(5)) // Take first 5 events
            .expectNextMatches(event -> event.type() != null)
            .thenConsumeWhile(event -> true) // Consume remaining events
            .verifyComplete();
    }

    @Test
    void testDetailedMetadataExtraction() {
        Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(
            "Explain Spring Boot in one sentence");
        
        // Verify HTTP metadata
        assertTrue(metadata.containsKey("httpStatus"));
        assertEquals(200, metadata.get("httpStatus"));
        
        // Verify Anthropic response metadata
        assertTrue(metadata.containsKey("id"));
        assertTrue(metadata.containsKey("model"));
        assertTrue(metadata.containsKey("type"));
        assertTrue(metadata.containsKey("role"));
        
        // Verify usage statistics
        assertTrue(metadata.containsKey("inputTokens"));
        assertTrue(metadata.containsKey("outputTokens"));
        assertTrue(metadata.containsKey("estimatedCost"));
        
        // Verify response content
        assertTrue(metadata.containsKey("responseContent"));
        assertTrue(metadata.containsKey("stopReason"));
        assertNotNull(metadata.get("responseContent"));
    }

    @Test
    void testSystemPromptWithService() {
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.systemPromptChat(
                "You are a helpful assistant that always responds with enthusiasm!", 
                "What is AI?"
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        String content = response.getBody().content().get(0).text();
        assertNotNull(content);
        // Should contain AI-related content due to system prompt
        assertTrue(content.toLowerCase().contains("ai") || 
                   content.toLowerCase().contains("artificial intelligence"));
    }

    @Test
    void testCustomParametersWithService() {
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.customParameterChat(
                "Explain machine learning in exactly 20 words", 
                "claude-3-5-sonnet-latest", 
                0.1, // Low temperature for consistency
                50,  // Limited tokens
                List.of("DONE", "FINISH")
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        // Verify the model used
        assertEquals("claude-3-5-sonnet-latest", response.getBody().model());
        
        // Check that token limit was respected
        if (response.getBody().usage() != null) {
            assertTrue(response.getBody().usage().outputTokens() <= 60); // Some buffer
        }
    }

    @Test
    void testAdvancedParametersWithService() {
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.advancedParameterChat(
                "Write a creative sentence about technology", 
                40,  // topK
                0.9  // topP
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        String content = response.getBody().content().get(0).text();
        assertNotNull(content);
        assertFalse(content.isEmpty());
    }

    @Test
    void testMultiTurnConversation() {
        List<Map<String, String>> conversation = List.of(
            Map.of("role", "user", "content", "Hello, my name is Alice"),
            Map.of("role", "assistant", "content", "Hello Alice! Nice to meet you."),
            Map.of("role", "user", "content", "What's my name?")
        );
        
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.multiTurnConversation(conversation);
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        String content = response.getBody().content().get(0).text();
        assertNotNull(content);
        // Should remember the name from the conversation
        assertTrue(content.toLowerCase().contains("alice"));
    }

    @Test
    void testBatchProcessingPerformance() {
        List<String> messages = List.of(
            "What is 1+1?",
            "What is 2+2?", 
            "What is 3+3?",
            "What is 4+4?",
            "What is 5+5?"
        );
        
        long startTime = System.currentTimeMillis();
        List<ResponseEntity<AnthropicApi.ChatCompletionResponse>> responses = 
            lowLevelService.batchProcessMessages(messages);
        long endTime = System.currentTimeMillis();
        
        assertEquals(5, responses.size());
        
        // All responses should be successful
        responses.forEach(response -> {
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().content().isEmpty());
        });
        
        // Log timing for performance analysis
        System.out.println("Batch processing of 5 messages took: " + (endTime - startTime) + "ms");
    }

    @Test
    void testAnthropicApiInfo() {
        Map<String, Object> apiInfo = lowLevelService.getAnthropicApiInfo();
        
        assertNotNull(apiInfo);
        assertTrue(apiInfo.containsKey("supportedModels"));
        assertTrue(apiInfo.containsKey("maxTokensLimits"));
        assertTrue(apiInfo.containsKey("contextWindows"));
        assertTrue(apiInfo.containsKey("supportedFeatures"));
        assertTrue(apiInfo.containsKey("anthropicSpecificFeatures"));

        @SuppressWarnings("unchecked")
        List<String> supportedModels = (List<String>) apiInfo.get("supportedModels");
        assertTrue(supportedModels.contains("claude-3-5-sonnet-latest"));
        
        @SuppressWarnings("unchecked")
        List<String> features = (List<String>) apiInfo.get("supportedFeatures");
        assertTrue(features.contains("System prompts"));
        assertTrue(features.contains("Multi-turn conversations"));
        
        @SuppressWarnings("unchecked")
        List<String> anthropicFeatures = (List<String>) apiInfo.get("anthropicSpecificFeatures");
        assertTrue(anthropicFeatures.contains("Constitutional AI training"));
    }

    @Test
    void testStopSequenceHandling() {
        ResponseEntity<AnthropicApi.ChatCompletionResponse> response = 
            lowLevelService.customParameterChat(
                "Count from 1 to 10: 1, 2, 3, STOP",
                "claude-3-5-sonnet-latest",
                0.7,
                100,
                List.of("STOP")
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        // If stop sequence was encountered, it should be indicated in the response
        if ("stop_sequence".equals(response.getBody().stopReason())) {
            assertEquals("STOP", response.getBody().stopSequence());
        }
    }
}
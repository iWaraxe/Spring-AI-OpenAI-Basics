package com.coherentsolutions.springaiopenaibasics;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOpenAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.openai.api.OpenAiApi;
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
 * Integration tests for low-level OpenAI API functionality.
 * 
 * These tests demonstrate the capabilities and benefits of using
 * the low-level OpenAI API compared to the high-level ChatModel interface.
 * 
 * To run these tests, set OPENAI_API_KEY environment variable.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class LowLevelApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LowLevelOpenAiService lowLevelService;

    @Test
    void testBasicChatEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "What is Spring AI?");

        mockMvc.perform(post("/api/lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices").exists())
            .andExpect(jsonPath("$.usage").exists())
            .andExpect(jsonPath("$.model").exists());
    }

    @Test
    void testStreamingChatEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "Count from 1 to 5");

        mockMvc.perform(post("/api/lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testCustomParametersEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "message", "Explain quantum computing briefly",
            "model", "gpt-4o-mini",
            "temperature", 0.3,
            "maxTokens", 50
        );

        mockMvc.perform(post("/api/lowlevel/custom-params")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").isNotEmpty())
            .andExpect(jsonPath("$.model").value("gpt-4o-mini"));
    }

    @Test
    void testMetadataEndpoint() throws Exception {
        Map<String, String> request = Map.of("message", "Hello OpenAI");

        mockMvc.perform(post("/api/lowlevel/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.httpStatus").value(200))
            .andExpect(jsonPath("$.promptTokens").exists())
            .andExpect(jsonPath("$.completionTokens").exists())
            .andExpect(jsonPath("$.totalTokens").exists())
            .andExpect(jsonPath("$.estimatedCost").exists())
            .andExpect(jsonPath("$.model").exists());
    }

    @Test
    void testSystemUserChatEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "systemPrompt", "You are a concise technical expert. Answer in exactly one sentence.",
            "userMessage", "What is REST API?"
        );

        mockMvc.perform(post("/api/lowlevel/system-user-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.choices[0].message.content").isNotEmpty());
    }

    @Test
    void testBatchProcessingEndpoint() throws Exception {
        Map<String, List<String>> request = Map.of(
            "messages", List.of(
                "What is 2+2?",
                "What is the capital of France?",
                "Name one color."
            )
        );

        mockMvc.perform(post("/api/lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].body.choices[0].message.content").isNotEmpty())
            .andExpect(jsonPath("$[1].body.choices[0].message.content").isNotEmpty())
            .andExpect(jsonPath("$[2].body.choices[0].message.content").isNotEmpty());
    }

    @Test
    void testLowLevelServiceDirectly() {
        // Test basic chat completion
        ResponseEntity<OpenAiApi.ChatCompletion> response = 
            lowLevelService.basicChatCompletion("What is the meaning of life?");
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().choices().isEmpty());
        assertNotNull(response.getBody().choices().get(0).message().content());
    }

    @Test
    void testStreamingDirectly() {
        // Test streaming response
        var stream = lowLevelService.streamingChatCompletion("Tell me a very short joke");
        
        StepVerifier.create(stream.take(5)) // Take first 5 chunks
            .expectNextMatches(chunk -> chunk.id() != null)
            .expectNextMatches(chunk -> chunk.model() != null)
            .thenConsumeWhile(chunk -> true) // Consume remaining chunks
            .verifyComplete();
    }

    @Test
    void testDetailedMetadataExtraction() {
        Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(
            "Explain Spring Boot in one sentence");
        
        // Verify HTTP metadata
        assertTrue(metadata.containsKey("httpStatus"));
        assertEquals(200, metadata.get("httpStatus"));
        
        // Verify OpenAI response metadata
        assertTrue(metadata.containsKey("id"));
        assertTrue(metadata.containsKey("model"));
        assertTrue(metadata.containsKey("created"));
        
        // Verify usage statistics
        assertTrue(metadata.containsKey("promptTokens"));
        assertTrue(metadata.containsKey("completionTokens"));
        assertTrue(metadata.containsKey("totalTokens"));
        assertTrue(metadata.containsKey("estimatedCost"));
        
        // Verify response content
        assertTrue(metadata.containsKey("responseContent"));
        assertNotNull(metadata.get("responseContent"));
    }

    @Test
    void testCustomParametersWithService() {
        ResponseEntity<OpenAiApi.ChatCompletion> response = 
            lowLevelService.customParameterChat(
                "Explain AI in exactly 10 words", 
                "gpt-4o-mini", 
                0.1, // Low temperature for consistency
                15    // Limited tokens
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        // Verify the model used
        assertEquals("gpt-4o-mini", response.getBody().model());
        
        // Check that token limit was respected
        if (response.getBody().usage() != null) {
            assertTrue(response.getBody().usage().completionTokens() <= 20); // Some buffer
        }
    }

    @Test
    void testSystemUserConversation() {
        ResponseEntity<OpenAiApi.ChatCompletion> response = 
            lowLevelService.systemUserChat(
                "You are a math teacher. Always show your work step by step.",
                "What is 15 * 8?"
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        String content = response.getBody().choices().get(0).message().content();
        assertNotNull(content);
        // Should contain the answer and likely show work due to system prompt
        assertTrue(content.contains("120") || content.contains("15") || content.contains("8"));
    }

    @Test
    void testLogitBiasFeature() {
        // Test logit bias - suppress common words to force more creative responses
        Map<String, Integer> logitBias = Map.of(
            "1820", -50,  // "the"
            "264", -50,   // "a" 
            "290", -50    // "an"
        );
        
        ResponseEntity<OpenAiApi.ChatCompletion> response = 
            lowLevelService.chatWithLogitBias(
                "Write a creative sentence about cats", 
                logitBias
            );
        
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        
        String content = response.getBody().choices().get(0).message().content();
        assertNotNull(content);
        assertFalse(content.isEmpty());
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
        List<ResponseEntity<OpenAiApi.ChatCompletion>> responses = 
            lowLevelService.batchProcessMessages(messages);
        long endTime = System.currentTimeMillis();
        
        assertEquals(5, responses.size());
        
        // All responses should be successful
        responses.forEach(response -> {
            assertEquals(200, response.getStatusCode().value());
            assertNotNull(response.getBody());
            assertFalse(response.getBody().choices().isEmpty());
        });
        
        // Log timing for performance analysis
        System.out.println("Batch processing of 5 messages took: " + (endTime - startTime) + "ms");
    }
}
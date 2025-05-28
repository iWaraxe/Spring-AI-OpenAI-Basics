package com.coherentsolutions.springaiopenaibasics;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.ai.ollama.api.OllamaApi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for low-level Ollama API functionality.
 * 
 * These tests demonstrate the capabilities and benefits of using
 * the low-level Ollama API for local model deployment and management.
 * 
 * To run these tests:
 * 1. Install Ollama: https://ollama.ai/
 * 2. Start Ollama: ollama serve
 * 3. Pull a model: ollama pull llama3.2
 * 4. Run tests with: mvn test -Dollama.integration.tests=true
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfSystemProperty(named = "ollama.integration.tests", matches = "true")
class LowLevelOllamaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LowLevelOllamaService lowLevelService;

    @Test
    void testBasicChatEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "message", "What is Spring AI?",
            "model", "llama3.2"
        );

        mockMvc.perform(post("/api/ollama-lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").isNotEmpty())
            .andExpect(jsonPath("$.model").value("llama3.2"))
            .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    void testStreamingChatEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "message", "Count from 1 to 3",
            "model", "llama3.2"
        );

        mockMvc.perform(post("/api/ollama-lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testAdvancedChatEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "message", "Explain quantum computing in 50 words",
            "model", "llama3.2",
            "options", Map.of(
                "temperature", 0.3,
                "numCtx", 2048,
                "seed", 42  // For reproducibility
            )
        );

        mockMvc.perform(post("/api/ollama-lowlevel/advanced-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").isNotEmpty());
    }

    @Test
    void testGenerateEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "prompt", "The capital of France is",
            "model", "llama3.2"
        );

        mockMvc.perform(post("/api/ollama-lowlevel/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response").isNotEmpty())
            .andExpect(jsonPath("$.promptEvalCount").exists())
            .andExpect(jsonPath("$.evalCount").exists());
    }

    @Test
    void testListModelsEndpoint() throws Exception {
        mockMvc.perform(get("/api/ollama-lowlevel/models"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.models").isArray())
            .andExpect(jsonPath("$.models[0].name").exists())
            .andExpect(jsonPath("$.models[0].size").exists());
    }

    @Test
    void testGetModelDetailsEndpoint() throws Exception {
        // Assuming llama3.2 is available
        mockMvc.perform(get("/api/ollama-lowlevel/models/llama3.2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.license").exists())
            .andExpect(jsonPath("$.template").exists())
            .andExpect(jsonPath("$.details").exists());
    }

    @Test
    void testMultiTurnConversationEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "conversationHistory", List.of(
                Map.of("role", "user", "content", "My name is Alice"),
                Map.of("role", "assistant", "content", "Hello Alice! Nice to meet you."),
                Map.of("role", "user", "content", "What's my name?")
            ),
            "model", "llama3.2"
        );

        mockMvc.perform(post("/api/ollama-lowlevel/multi-turn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").isNotEmpty());
    }

    @Test
    void testOptimizedChatEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "systemPrompt", "You are a concise technical expert. Answer in one sentence.",
            "userMessage", "What is Docker?",
            "model", "llama3.2",
            "modelOptions", Map.of()
        );

        mockMvc.perform(post("/api/ollama-lowlevel/optimized-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").isNotEmpty());
    }

    @Test
    void testBatchProcessEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "messages", List.of(
                "What is 2+2?",
                "What is the capital of France?",
                "Name a color."
            ),
            "model", "llama3.2"
        );

        mockMvc.perform(post("/api/ollama-lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3))
            .andExpect(jsonPath("$[0].message.content").isNotEmpty())
            .andExpect(jsonPath("$[1].message.content").isNotEmpty())
            .andExpect(jsonPath("$[2].message.content").isNotEmpty());
    }

    @Test
    void testModelPerformanceEndpoint() throws Exception {
        Map<String, String> request = Map.of("model", "llama3.2");

        mockMvc.perform(post("/api/ollama-lowlevel/model-performance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.modelName").value("llama3.2"))
            .andExpect(jsonPath("$.responseTimeMs").exists())
            .andExpect(jsonPath("$.promptTokensPerSecond").exists())
            .andExpect(jsonPath("$.generateTokensPerSecond").exists());
    }

    @Test
    void testContextOptimizedChatEndpoint() throws Exception {
        Map<String, Object> request = Map.of(
            "message", "Tell me about Spring Boot",
            "model", "llama3.2",
            "contextSize", 4096,
            "useMemoryMapping", true
        );

        mockMvc.perform(post("/api/ollama-lowlevel/context-optimized-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").isNotEmpty());
    }

    @Test
    void testServerInfoEndpoint() throws Exception {
        mockMvc.perform(get("/api/ollama-lowlevel/server-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableModels").isArray())
            .andExpect(jsonPath("$.capabilities").exists())
            .andExpect(jsonPath("$.recommendedModels").exists())
            .andExpect(jsonPath("$.modelSources").isArray());
    }

    @Test
    void testLowLevelServiceDirectly() {
        // Test basic chat completion
        ChatResponse response = lowLevelService.basicChatCompletion(
            "What is the meaning of life?", "llama3.2");
        
        assertNotNull(response);
        assertNotNull(response.message());
        assertNotNull(response.message().content());
        assertEquals(Role.ASSISTANT, response.message().role());
    }

    @Test
    void testStreamingDirectly() {
        // Test streaming response
        var stream = lowLevelService.streamingChatCompletion(
            "Tell me a very short joke", "llama3.2");
        
        StepVerifier.create(stream.take(5)) // Take first 5 chunks
            .expectNextMatches(chunk -> chunk.message() != null)
            .thenConsumeWhile(chunk -> true) // Consume remaining chunks
            .verifyComplete();
    }

    @Test
    void testModelManagement() {
        // Test listing models
        ModelListResponse models = lowLevelService.listAvailableModels();
        assertNotNull(models);
        assertFalse(models.models().isEmpty());
        
        // Test getting model details
        String firstModel = models.models().get(0).name();
        ModelDetailsResponse details = lowLevelService.getModelDetails(firstModel);
        assertNotNull(details);
        assertNotNull(details.template());
    }

    @Test
    void testGenerateTextDirectly() {
        // Test direct text generation (non-chat format)
        GenerateResponse response = lowLevelService.generateText(
            "The quick brown fox", "llama3.2");
        
        assertNotNull(response);
        assertNotNull(response.response());
        assertNotNull(response.model());
        assertTrue(response.promptEvalCount() > 0);
        assertTrue(response.evalCount() > 0);
    }

    @Test
    void testAdvancedParametersWithService() {
        Map<String, Object> options = Map.of(
            "temperature", 0.1,
            "numCtx", 2048,
            "topK", 10,
            "topP", 0.9,
            "repeatPenalty", 1.1,
            "seed", 42
        );
        
        ChatResponse response = lowLevelService.advancedParameterChat(
            "Explain AI in exactly 10 words", "llama3.2", options);
        
        assertNotNull(response);
        assertNotNull(response.message().content());
    }

    @Test
    void testMultiTurnConversationWithService() {
        List<Map<String, String>> conversation = List.of(
            Map.of("role", "system", "content", "You are a helpful math tutor."),
            Map.of("role", "user", "content", "What is 15 * 8?"),
            Map.of("role", "assistant", "content", "15 * 8 = 120"),
            Map.of("role", "user", "content", "And if I add 30?")
        );
        
        ChatResponse response = lowLevelService.multiTurnConversation(conversation, "llama3.2");
        
        assertNotNull(response);
        String content = response.message().content();
        assertNotNull(content);
        // Should contain reference to 150 (120 + 30)
        assertTrue(content.contains("150") || content.contains("one hundred fifty"));
    }

    @Test
    void testModelPerformanceMetrics() {
        Map<String, Object> metrics = lowLevelService.getModelPerformanceMetrics("llama3.2");
        
        assertNotNull(metrics);
        assertEquals("llama3.2", metrics.get("modelName"));
        assertTrue(metrics.containsKey("responseTimeMs"));
        assertTrue(metrics.containsKey("promptTokensPerSecond"));
        assertTrue(metrics.containsKey("generateTokensPerSecond"));
        
        // Performance metrics should be reasonable
        Long responseTime = (Long) metrics.get("responseTimeMs");
        assertTrue(responseTime > 0 && responseTime < 30000); // Less than 30 seconds
        
        Double promptSpeed = (Double) metrics.get("promptTokensPerSecond");
        assertTrue(promptSpeed > 0);
    }

    @Test
    void testBatchProcessingPerformance() {
        List<String> messages = List.of(
            "What is 1+1?",
            "What is 2+2?",
            "What is 3+3?"
        );
        
        long startTime = System.currentTimeMillis();
        List<ChatResponse> responses = lowLevelService.batchProcessMessages(messages, "llama3.2");
        long endTime = System.currentTimeMillis();
        
        assertEquals(3, responses.size());
        
        // All responses should be valid
        responses.forEach(response -> {
            assertNotNull(response);
            assertNotNull(response.message());
            assertNotNull(response.message().content());
        });
        
        // Log timing for performance analysis
        System.out.println("Batch processing of 3 messages took: " + (endTime - startTime) + "ms");
    }

    @Test
    void testContextWindowOptimization() {
        // Test with different context sizes
        ChatResponse smallContext = lowLevelService.chatWithContextOptimization(
            "What is Java?", "llama3.2", 512, true);
        assertNotNull(smallContext);
        
        ChatResponse largeContext = lowLevelService.chatWithContextOptimization(
            "Explain the entire Spring ecosystem", "llama3.2", 4096, true);
        assertNotNull(largeContext);
    }

    @Test
    void testCompareModels() {
        // Compare different models if available
        List<String> availableModels = lowLevelService.listAvailableModels()
            .models().stream()
            .map(ModelListResponse.Model::name)
            .limit(2) // Compare at most 2 models
            .toList();
        
        if (availableModels.size() >= 2) {
            Map<String, ChatResponse> comparison = lowLevelService.compareModels(
                "What is 2+2?", availableModels);
            
            assertEquals(availableModels.size(), comparison.size());
            comparison.forEach((model, response) -> {
                assertNotNull(response);
                assertNotNull(response.message().content());
            });
        }
    }

    @Test
    void testOllamaServerInfo() {
        Map<String, Object> serverInfo = lowLevelService.getOllamaServerInfo();
        
        assertNotNull(serverInfo);
        assertTrue(serverInfo.containsKey("availableModels"));
        assertTrue(serverInfo.containsKey("capabilities"));
        assertTrue(serverInfo.containsKey("recommendedModels"));
        assertTrue(serverInfo.containsKey("modelSources"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> capabilities = (Map<String, Object>) serverInfo.get("capabilities");
        assertTrue((Boolean) capabilities.get("chat"));
        assertTrue((Boolean) capabilities.get("streaming"));
        assertTrue((Boolean) capabilities.get("modelManagement"));
    }
}
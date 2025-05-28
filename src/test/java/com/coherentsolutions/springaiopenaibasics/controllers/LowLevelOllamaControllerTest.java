package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.LowLevelOllamaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.api.OllamaApi.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LowLevelOllamaController.class)
class LowLevelOllamaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LowLevelOllamaService lowLevelService;

    @Test
    void testBasicChat() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Test response");
        
        when(lowLevelService.basicChatCompletion(anyString(), anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of("message", "Hello", "model", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/basic-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").value("Test response"));
    }

    @Test
    void testStreamChat() throws Exception {
        // Arrange
        ChatResponse chunk1 = createMockChatResponse("Hello");
        ChatResponse chunk2 = createMockChatResponse(" world!");
        
        Flux<ChatResponse> mockStream = Flux.just(chunk1, chunk2);
        
        when(lowLevelService.streamingChatCompletion(anyString(), anyString()))
            .thenReturn(mockStream);

        Map<String, String> request = Map.of("message", "Stream test", "model", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/stream-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testAdvancedChat() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Advanced response");
        
        when(lowLevelService.advancedParameterChat(anyString(), anyString(), anyMap()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Advanced test",
            "model", "llama3.2",
            "options", Map.of(
                "temperature", 0.5,
                "numCtx", 4096,
                "numGPU", 1
            )
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/advanced-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").value("Advanced response"));
    }

    @Test
    void testMultiTurn() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Multi-turn response");
        
        when(lowLevelService.multiTurnConversation(anyList(), anyString()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "conversationHistory", List.of(
                Map.of("role", "user", "content", "Hello"),
                Map.of("role", "assistant", "content", "Hi there!"),
                Map.of("role", "user", "content", "How are you?")
            ),
            "model", "llama3.2"
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/multi-turn")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").value("Multi-turn response"));
    }

    @Test
    void testGenerate() throws Exception {
        // Arrange
        GenerateResponse mockResponse = createMockGenerateResponse("Generated text");
        
        when(lowLevelService.generateText(anyString(), anyString()))
            .thenReturn(mockResponse);

        Map<String, String> request = Map.of("prompt", "Complete this:", "model", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.response").value("Generated text"));
    }

    @Test
    void testStreamGenerate() throws Exception {
        // Arrange
        GenerateResponse chunk1 = createMockGenerateResponse("Part 1");
        GenerateResponse chunk2 = createMockGenerateResponse("Part 2");
        
        Flux<GenerateResponse> mockStream = Flux.just(chunk1, chunk2);
        
        when(lowLevelService.streamingGenerateText(anyString(), anyString()))
            .thenReturn(mockStream);

        Map<String, String> request = Map.of("prompt", "Generate stream", "model", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/stream-generate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testListModels() throws Exception {
        // Arrange
        ModelListResponse mockResponse = new ModelListResponse(List.of(
            createMockModel("llama3.2", 2000000000L),
            createMockModel("mistral", 4000000000L)
        ));
        
        when(lowLevelService.listAvailableModels())
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/ollama-lowlevel/models"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.models").isArray())
            .andExpect(jsonPath("$.models[0].name").value("llama3.2"))
            .andExpect(jsonPath("$.models[1].name").value("mistral"));
    }

    @Test
    void testGetModelDetails() throws Exception {
        // Arrange
        ModelDetailsResponse mockResponse = createMockModelDetailsResponse("llama3.2");
        
        when(lowLevelService.getModelDetails(anyString()))
            .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/ollama-lowlevel/models/llama3.2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.details.family").value("llama"))
            .andExpect(jsonPath("$.details.parameterSize").value("7B"));
    }

    @Test
    void testPullModel() throws Exception {
        // Arrange
        PullModelResponse chunk1 = createMockPullModelResponse("downloading", 50);
        PullModelResponse chunk2 = createMockPullModelResponse("complete", 100);
        
        Flux<PullModelResponse> mockStream = Flux.just(chunk1, chunk2);
        
        when(lowLevelService.pullModel(anyString()))
            .thenReturn(mockStream);

        Map<String, String> request = Map.of("modelName", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/pull-model")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testOptimizedChat() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Optimized response");
        
        when(lowLevelService.optimizedChat(anyString(), anyString(), anyString(), anyMap()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "systemPrompt", "You are helpful",
            "userMessage", "What is AI?",
            "model", "llama3.2",
            "modelOptions", Map.of("numGPU", 1)
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/optimized-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").value("Optimized response"));
    }

    @Test
    void testBatchProcess() throws Exception {
        // Arrange
        List<ChatResponse> mockResponses = List.of(
            createMockChatResponse("Response 1"),
            createMockChatResponse("Response 2")
        );
        
        when(lowLevelService.batchProcessMessages(anyList(), anyString()))
            .thenReturn(mockResponses);

        Map<String, Object> request = Map.of(
            "messages", List.of("Message 1", "Message 2"),
            "model", "llama3.2"
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/batch-process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].message.content").value("Response 1"))
            .andExpect(jsonPath("$[1].message.content").value("Response 2"));
    }

    @Test
    void testModelPerformance() throws Exception {
        // Arrange
        Map<String, Object> mockMetrics = Map.of(
            "modelName", "llama3.2",
            "responseTimeMs", 1500L,
            "promptTokensPerSecond", 100.5,
            "generateTokensPerSecond", 50.2
        );
        
        when(lowLevelService.getModelPerformanceMetrics(anyString()))
            .thenReturn(mockMetrics);

        Map<String, String> request = Map.of("model", "llama3.2");

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/model-performance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.modelName").value("llama3.2"))
            .andExpect(jsonPath("$.responseTimeMs").value(1500))
            .andExpect(jsonPath("$.promptTokensPerSecond").value(100.5))
            .andExpect(jsonPath("$.generateTokensPerSecond").value(50.2));
    }

    @Test
    void testContextOptimizedChat() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Context optimized response");
        
        when(lowLevelService.chatWithContextOptimization(anyString(), anyString(), anyInt(), anyBoolean()))
            .thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "message", "Context test",
            "model", "llama3.2",
            "contextSize", 8192,
            "useMemoryMapping", true
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/context-optimized-chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message.content").value("Context optimized response"));
    }

    @Test
    void testCompareModels() throws Exception {
        // Arrange
        Map<String, ChatResponse> mockResponses = Map.of(
            "llama3.2", createMockChatResponse("Llama response"),
            "mistral", createMockChatResponse("Mistral response")
        );
        
        when(lowLevelService.compareModels(anyString(), anyList()))
            .thenReturn(mockResponses);

        Map<String, Object> request = Map.of(
            "message", "Compare this",
            "models", List.of("llama3.2", "mistral")
        );

        // Act & Assert
        mockMvc.perform(post("/api/ollama-lowlevel/compare-models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.llama3\\.2.message.content").value("Llama response"))
            .andExpect(jsonPath("$.mistral.message.content").value("Mistral response"));
    }

    @Test
    void testGetServerInfo() throws Exception {
        // Arrange
        Map<String, Object> mockInfo = Map.of(
            "availableModels", List.of(
                Map.of("name", "llama3.2", "size", 2000000000L)
            ),
            "capabilities", Map.of("chat", true, "streaming", true)
        );
        
        when(lowLevelService.getOllamaServerInfo())
            .thenReturn(mockInfo);

        // Act & Assert
        mockMvc.perform(get("/api/ollama-lowlevel/server-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.availableModels").isArray())
            .andExpect(jsonPath("$.capabilities.chat").value(true))
            .andExpect(jsonPath("$.capabilities.streaming").value(true));
    }

    @Test
    void testComparisonInfo() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/ollama-lowlevel/comparison-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.highLevelBenefits").isArray())
            .andExpect(jsonPath("$.lowLevelBenefits").isArray())
            .andExpect(jsonPath("$.ollamaSpecificUseCases").isArray())
            .andExpect(jsonPath("$.supportedModelTypes").exists())
            .andExpect(jsonPath("$.hardwareOptimizations").exists());
    }

    // Helper methods
    private ChatResponse createMockChatResponse(String content) {
        Message message = Message.builder(Role.ASSISTANT)
            .content(content)
            .build();
        return new ChatResponse(
            "llama3.2",
            Instant.now(),
            message,
            false,
            "stop",
            null,
            1000000000L,
            500000000L,
            15,
            25
        );
    }

    private GenerateResponse createMockGenerateResponse(String response) {
        return new GenerateResponse(
            "llama3.2",
            Instant.now(),
            response,
            false,
            null,
            15,
            25,
            1000000000L,
            500000000L,
            null,
            null
        );
    }

    private ModelListResponse.Model createMockModel(String name, Long size) {
        return new ModelListResponse.Model(
            name,
            name + ":latest",
            Instant.now(),
            size,
            "sha256:abcdef123456",
            new ModelListResponse.Details(
                null, "llama", "7B", null, null, null, null
            )
        );
    }

    private ModelDetailsResponse createMockModelDetailsResponse(String modelName) {
        return new ModelDetailsResponse(
            "MIT",
            "modelfile content",
            "parameters content",
            "template content",
            new ModelListResponse.Details(
                null, "llama", "7B", null, null, null, null
            ),
            new ModelInfo(
                "arch", "llama", null, null, null, null, null, null, null
            )
        );
    }

    private PullModelResponse createMockPullModelResponse(String status, int progress) {
        return new PullModelResponse(
            status,
            "sha256:abcdef123456",
            progress,
            100
        );
    }
}
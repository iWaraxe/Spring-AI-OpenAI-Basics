package com.coherentsolutions.springaiopenaibasics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaApi.*;
import org.springframework.ai.ollama.api.OllamaOptions;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowLevelOllamaServiceMockTest {

    @Mock
    private OllamaApi ollamaApi;

    @InjectMocks
    private LowLevelOllamaServiceImpl lowLevelService;

    @Test
    void testBasicChatCompletion() {
        // Arrange
        String message = "Hello, Ollama!";
        String model = "llama3.2";
        ChatResponse mockResponse = createMockChatResponse("Hello! How can I help you today?");

        when(ollamaApi.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = lowLevelService.basicChatCompletion(message, model);

        // Assert
        assertNotNull(response);
        assertEquals("Hello! How can I help you today?", response.message().content());
        assertEquals(Role.ASSISTANT, response.message().role());

        verify(ollamaApi, times(1)).chat(argThat(request -> 
            request.model().equals(model) && 
            !request.stream() &&
            request.messages().size() == 1
        ));
    }

    @Test
    void testStreamingChatCompletion() {
        // Arrange
        String message = "Tell me a story";
        String model = "llama3.2";
        ChatResponse chunk1 = createMockChatResponse("Once upon");
        ChatResponse chunk2 = createMockChatResponse(" a time");
        
        Flux<ChatResponse> mockStream = Flux.just(chunk1, chunk2);

        when(ollamaApi.streamingChat(any(ChatRequest.class))).thenReturn(mockStream);

        // Act
        Flux<ChatResponse> response = lowLevelService.streamingChatCompletion(message, model);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .verifyComplete();

        verify(ollamaApi, times(1)).streamingChat(argThat(request -> 
            request.stream() && request.model().equals(model)
        ));
    }

    @Test
    void testAdvancedParameterChat() {
        // Arrange
        String message = "Advanced test";
        String model = "llama3.2";
        Map<String, Object> options = Map.of(
            "temperature", 0.5,
            "numCtx", 4096,
            "numGPU", 1,
            "numa", true,
            "useMMap", true
        );

        ChatResponse mockResponse = createMockChatResponse("Advanced response");
        when(ollamaApi.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = lowLevelService.advancedParameterChat(message, model, options);

        // Assert
        assertNotNull(response);
        assertEquals("Advanced response", response.message().content());

        verify(ollamaApi, times(1)).chat(argThat(request -> 
            request.model().equals(model) && 
            request.options() != null
        ));
    }

    @Test
    void testMultiTurnConversation() {
        // Arrange
        List<Map<String, String>> conversationHistory = List.of(
            Map.of("role", "user", "content", "Hello"),
            Map.of("role", "assistant", "content", "Hi there!"),
            Map.of("role", "user", "content", "How are you?")
        );
        String model = "llama3.2";

        ChatResponse mockResponse = createMockChatResponse("I'm doing well, thank you!");
        when(ollamaApi.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = lowLevelService.multiTurnConversation(conversationHistory, model);

        // Assert
        assertNotNull(response);
        assertEquals("I'm doing well, thank you!", response.message().content());

        verify(ollamaApi, times(1)).chat(argThat(request -> 
            request.messages().size() == 3
        ));
    }

    @Test
    void testGenerateText() {
        // Arrange
        String prompt = "Complete this: The sky is";
        String model = "llama3.2";
        GenerateResponse mockResponse = createMockGenerateResponse(" blue and clear today.");

        when(ollamaApi.generate(any(GenerateRequest.class))).thenReturn(mockResponse);

        // Act
        GenerateResponse response = lowLevelService.generateText(prompt, model);

        // Assert
        assertNotNull(response);
        assertEquals(" blue and clear today.", response.response());
        assertFalse(response.done());

        verify(ollamaApi, times(1)).generate(argThat(request -> 
            request.model().equals(model) && 
            request.prompt().equals(prompt) &&
            !request.stream()
        ));
    }

    @Test
    void testStreamingGenerateText() {
        // Arrange
        String prompt = "Generate a poem";
        String model = "llama3.2";
        GenerateResponse chunk1 = createMockGenerateResponse("Roses are");
        GenerateResponse chunk2 = createMockGenerateResponse(" red");
        
        Flux<GenerateResponse> mockStream = Flux.just(chunk1, chunk2);

        when(ollamaApi.streamingGenerate(any(GenerateRequest.class))).thenReturn(mockStream);

        // Act
        Flux<GenerateResponse> response = lowLevelService.streamingGenerateText(prompt, model);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .verifyComplete();

        verify(ollamaApi, times(1)).streamingGenerate(argThat(request -> 
            request.stream() && request.model().equals(model)
        ));
    }

    @Test
    void testListAvailableModels() {
        // Arrange
        ModelListResponse mockResponse = new ModelListResponse(List.of(
            createMockModel("llama3.2", 2000000000L),
            createMockModel("mistral", 4000000000L),
            createMockModel("phi3", 1000000000L)
        ));

        when(ollamaApi.listModels()).thenReturn(mockResponse);

        // Act
        ModelListResponse response = lowLevelService.listAvailableModels();

        // Assert
        assertNotNull(response);
        assertEquals(3, response.models().size());
        assertEquals("llama3.2", response.models().get(0).name());

        verify(ollamaApi, times(1)).listModels();
    }

    @Test
    void testGetModelDetails() {
        // Arrange
        String modelName = "llama3.2";
        ModelDetailsResponse mockResponse = createMockModelDetailsResponse(modelName);

        when(ollamaApi.showModelDetails(any(ModelDetailsRequest.class))).thenReturn(mockResponse);

        // Act
        ModelDetailsResponse response = lowLevelService.getModelDetails(modelName);

        // Assert
        assertNotNull(response);
        assertEquals("llama", response.details().family());
        assertEquals("7B", response.details().parameterSize());

        verify(ollamaApi, times(1)).showModelDetails(argThat(request -> 
            request.name().equals(modelName)
        ));
    }

    @Test
    void testPullModel() {
        // Arrange
        String modelName = "llama3.2";
        PullModelResponse chunk1 = createMockPullModelResponse("pulling manifest", 0);
        PullModelResponse chunk2 = createMockPullModelResponse("downloading", 50);
        PullModelResponse chunk3 = createMockPullModelResponse("success", 100);
        
        Flux<PullModelResponse> mockStream = Flux.just(chunk1, chunk2, chunk3);

        when(ollamaApi.streamingPullModel(any(PullModelRequest.class))).thenReturn(mockStream);

        // Act
        Flux<PullModelResponse> response = lowLevelService.pullModel(modelName);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .expectNext(chunk3)
            .verifyComplete();

        verify(ollamaApi, times(1)).streamingPullModel(argThat(request -> 
            request.name().equals(modelName) && request.stream()
        ));
    }

    @Test
    void testOptimizedChat() {
        // Arrange
        String systemPrompt = "You are a helpful assistant.";
        String userMessage = "What is Spring AI?";
        String model = "llama3.2";
        Map<String, Object> modelOptions = Map.of("numGPU", 1);

        ChatResponse mockResponse = createMockChatResponse("Spring AI is a framework...");
        when(ollamaApi.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = lowLevelService.optimizedChat(systemPrompt, userMessage, model, modelOptions);

        // Assert
        assertNotNull(response);
        assertEquals("Spring AI is a framework...", response.message().content());

        verify(ollamaApi, times(1)).chat(argThat(request -> 
            request.messages().size() == 2 && // system + user messages
            request.messages().get(0).role() == Role.SYSTEM
        ));
    }

    @Test
    void testBatchProcessMessages() {
        // Arrange
        List<String> messages = List.of("Message 1", "Message 2", "Message 3");
        String model = "llama3.2";
        
        ChatResponse mockResponse1 = createMockChatResponse("Response 1");
        ChatResponse mockResponse2 = createMockChatResponse("Response 2");
        ChatResponse mockResponse3 = createMockChatResponse("Response 3");
        ChatResponse warmupResponse = createMockChatResponse("Hello");

        when(ollamaApi.chat(any(ChatRequest.class)))
            .thenReturn(warmupResponse) // For warm-up
            .thenReturn(mockResponse1)
            .thenReturn(mockResponse2)
            .thenReturn(mockResponse3);

        // Act
        List<ChatResponse> responses = lowLevelService.batchProcessMessages(messages, model);

        // Assert
        assertEquals(3, responses.size());
        assertEquals("Response 1", responses.get(0).message().content());
        assertEquals("Response 2", responses.get(1).message().content());
        assertEquals("Response 3", responses.get(2).message().content());

        verify(ollamaApi, times(4)).chat(any(ChatRequest.class)); // 1 warm-up + 3 messages
    }

    @Test
    void testGetModelPerformanceMetrics() {
        // Arrange
        String model = "llama3.2";
        ModelDetailsResponse mockDetails = createMockModelDetailsResponse(model);
        GenerateResponse mockGenerate = createMockGenerateResponseWithMetrics();

        when(ollamaApi.showModelDetails(any(ModelDetailsRequest.class))).thenReturn(mockDetails);
        when(ollamaApi.generate(any(GenerateRequest.class))).thenReturn(mockGenerate);

        // Act
        Map<String, Object> metrics = lowLevelService.getModelPerformanceMetrics(model);

        // Assert
        assertNotNull(metrics);
        assertEquals(model, metrics.get("modelName"));
        assertTrue(metrics.containsKey("responseTimeMs"));
        assertTrue(metrics.containsKey("promptTokensPerSecond"));
        assertTrue(metrics.containsKey("generateTokensPerSecond"));

        verify(ollamaApi, times(1)).showModelDetails(any(ModelDetailsRequest.class));
        verify(ollamaApi, times(1)).generate(any(GenerateRequest.class));
    }

    @Test
    void testChatWithContextOptimization() {
        // Arrange
        String message = "Test context optimization";
        String model = "llama3.2";
        int contextSize = 8192;
        boolean useMemoryMapping = true;

        ChatResponse mockResponse = createMockChatResponse("Optimized response");
        when(ollamaApi.chat(any(ChatRequest.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = lowLevelService.chatWithContextOptimization(
            message, model, contextSize, useMemoryMapping);

        // Assert
        assertNotNull(response);
        assertEquals("Optimized response", response.message().content());

        verify(ollamaApi, times(1)).chat(argThat(request -> 
            request.options() != null
        ));
    }

    @Test
    void testCompareModels() {
        // Arrange
        String message = "Compare this";
        List<String> models = List.of("llama3.2", "mistral", "phi3");
        
        ChatResponse mockResponse1 = createMockChatResponse("Llama response");
        ChatResponse mockResponse2 = createMockChatResponse("Mistral response");
        ChatResponse mockResponse3 = createMockChatResponse("Phi response");

        when(ollamaApi.chat(any(ChatRequest.class)))
            .thenReturn(mockResponse1)
            .thenReturn(mockResponse2)
            .thenReturn(mockResponse3);

        // Act
        Map<String, ChatResponse> responses = lowLevelService.compareModels(message, models);

        // Assert
        assertEquals(3, responses.size());
        assertTrue(responses.containsKey("llama3.2"));
        assertTrue(responses.containsKey("mistral"));
        assertTrue(responses.containsKey("phi3"));

        verify(ollamaApi, times(3)).chat(any(ChatRequest.class));
    }

    @Test
    void testGetOllamaServerInfo() {
        // Arrange
        ModelListResponse mockModels = new ModelListResponse(List.of(
            createMockModel("llama3.2", 2000000000L),
            createMockModel("codellama", 3000000000L)
        ));

        when(ollamaApi.listModels()).thenReturn(mockModels);

        // Act
        Map<String, Object> serverInfo = lowLevelService.getOllamaServerInfo();

        // Assert
        assertNotNull(serverInfo);
        assertTrue(serverInfo.containsKey("availableModels"));
        assertTrue(serverInfo.containsKey("capabilities"));
        assertTrue(serverInfo.containsKey("recommendedModels"));
        assertTrue(serverInfo.containsKey("modelSources"));

        @SuppressWarnings("unchecked")
        Map<String, Object> capabilities = (Map<String, Object>) serverInfo.get("capabilities");
        assertTrue((Boolean) capabilities.get("chat"));
        assertTrue((Boolean) capabilities.get("streaming"));

        verify(ollamaApi, times(1)).listModels();
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

    private GenerateResponse createMockGenerateResponseWithMetrics() {
        return new GenerateResponse(
            "llama3.2",
            Instant.now(),
            "Test response",
            true,
            "test context",
            10,
            20,
            1000000000L, // 1 second
            2000000000L, // 2 seconds
            3000000000L, // 3 seconds total
            100000000L   // 0.1 second load
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
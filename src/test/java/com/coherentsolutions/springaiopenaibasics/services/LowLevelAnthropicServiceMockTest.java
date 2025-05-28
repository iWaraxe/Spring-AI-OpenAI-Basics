package com.coherentsolutions.springaiopenaibasics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionResponse;
import org.springframework.ai.anthropic.api.AnthropicApi.ChatCompletionRequest;
import org.springframework.ai.anthropic.api.AnthropicApi.StreamResponse;
import org.springframework.ai.anthropic.api.AnthropicApi.ContentBlock;
import org.springframework.ai.anthropic.api.AnthropicApi.Usage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowLevelAnthropicServiceMockTest {

    @Mock
    private AnthropicApi anthropicApi;

    @InjectMocks
    private LowLevelAnthropicServiceImpl lowLevelService;

    @Test
    void testBasicChatCompletion() {
        // Arrange
        String message = "Hello, Claude!";
        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Hello! How can I help you today?");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.basicChatCompletion(message);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hello! How can I help you today?", 
            response.getBody().content().get(0).text());

        verify(anthropicApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testStreamingChatCompletion() {
        // Arrange
        String message = "Tell me a story";
        StreamResponse chunk1 = createMockStreamResponse("event1", "Once upon");
        StreamResponse chunk2 = createMockStreamResponse("event2", " a time");
        
        Flux<StreamResponse> mockStream = Flux.just(chunk1, chunk2);

        when(anthropicApi.chatCompletionStream(any(ChatCompletionRequest.class)))
            .thenReturn(mockStream);

        // Act
        Flux<StreamResponse> response = lowLevelService.streamingChatCompletion(message);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .verifyComplete();

        verify(anthropicApi, times(1)).chatCompletionStream(any(ChatCompletionRequest.class));
    }

    @Test
    void testCustomParameterChat() {
        // Arrange
        String message = "Custom test";
        String model = "claude-3-opus-20240229";
        Double temperature = 0.5;
        Integer maxTokens = 1000;
        List<String> stopSequences = List.of("STOP", "END");

        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Custom response");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.customParameterChat(
            message, model, temperature, maxTokens, stopSequences);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify that custom parameters are used
        verify(anthropicApi, times(1)).chatCompletionEntity(
            argThat(request -> 
                request.model().equals(model) && 
                request.temperature().equals(temperature) &&
                request.maxTokens().equals(maxTokens)
            )
        );
    }

    @Test
    void testSystemPromptChat() {
        // Arrange
        String systemPrompt = "You are a helpful assistant specialized in Spring AI.";
        String userMessage = "What is Spring AI?";

        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse(
            "Spring AI is a framework for building AI applications with Spring.");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.systemPromptChat(systemPrompt, userMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify that system prompt is included
        verify(anthropicApi, times(1)).chatCompletionEntity(
            argThat(request -> 
                request.system() != null && 
                request.system().equals(systemPrompt) &&
                request.messages().size() == 1
            )
        );
    }

    @Test
    void testBatchProcessMessages() {
        // Arrange
        List<String> messages = List.of("Message 1", "Message 2", "Message 3");
        
        ChatCompletionResponse mockCompletion1 = createMockChatCompletionResponse("Response 1");
        ChatCompletionResponse mockCompletion2 = createMockChatCompletionResponse("Response 2");
        ChatCompletionResponse mockCompletion3 = createMockChatCompletionResponse("Response 3");

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(ResponseEntity.ok(mockCompletion1))
            .thenReturn(ResponseEntity.ok(mockCompletion2))
            .thenReturn(ResponseEntity.ok(mockCompletion3));

        // Act
        List<ResponseEntity<ChatCompletionResponse>> responses = lowLevelService.batchProcessMessages(messages);

        // Assert
        assertEquals(3, responses.size());
        assertEquals("Response 1", responses.get(0).getBody().content().get(0).text());
        assertEquals("Response 2", responses.get(1).getBody().content().get(0).text());
        assertEquals("Response 3", responses.get(2).getBody().content().get(0).text());

        verify(anthropicApi, times(3)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testGetDetailedResponseMetadata() {
        // Arrange
        String message = "Test metadata";
        
        Usage usage = new Usage(15, 25);
        ChatCompletionResponse mockCompletion = new ChatCompletionResponse(
            "msg_123",
            "message",
            AnthropicApi.Role.ASSISTANT,
            List.of(new ContentBlock("Test response")),
            "claude-3-5-sonnet-latest",
            "end_turn",
            null,
            usage
        );
        
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(message);

        // Assert
        assertNotNull(metadata);
        assertEquals(200, metadata.get("httpStatus"));
        assertEquals("msg_123", metadata.get("id"));
        assertEquals("claude-3-5-sonnet-latest", metadata.get("model"));
        assertEquals(15, metadata.get("inputTokens"));
        assertEquals(25, metadata.get("outputTokens"));
        assertTrue(metadata.containsKey("estimatedCost"));
        assertEquals("Test response", metadata.get("responseContent"));
        assertEquals("end_turn", metadata.get("stopReason"));
    }

    @Test
    void testAdvancedParameterChat() {
        // Arrange
        String message = "Test advanced parameters";
        Integer topK = 40;
        Double topP = 0.9;
        
        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Advanced response");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.advancedParameterChat(message, topK, topP);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(anthropicApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testMultiTurnConversation() {
        // Arrange
        List<Map<String, String>> conversationHistory = List.of(
            Map.of("role", "user", "content", "Hello"),
            Map.of("role", "assistant", "content", "Hi there!"),
            Map.of("role", "user", "content", "How are you?")
        );
        
        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("I'm doing well, thank you!");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.multiTurnConversation(conversationHistory);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify that conversation history is properly converted
        verify(anthropicApi, times(1)).chatCompletionEntity(
            argThat(request -> request.messages().size() == 3)
        );
    }

    @Test
    void testChatWithCustomHeaders() {
        // Arrange
        String message = "Test custom headers";
        Map<String, String> headers = Map.of("X-Custom-Header", "custom-value");
        
        ChatCompletionResponse mockCompletion = createMockChatCompletionResponse("Response with headers");
        ResponseEntity<ChatCompletionResponse> mockResponse = ResponseEntity.ok(mockCompletion);

        when(anthropicApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletionResponse> response = lowLevelService.chatWithCustomHeaders(message, headers);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(anthropicApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testGetAnthropicApiInfo() {
        // Act
        Map<String, Object> apiInfo = lowLevelService.getAnthropicApiInfo();

        // Assert
        assertNotNull(apiInfo);
        assertTrue(apiInfo.containsKey("supportedModels"));
        assertTrue(apiInfo.containsKey("maxTokensLimits"));
        assertTrue(apiInfo.containsKey("contextWindows"));
        assertTrue(apiInfo.containsKey("supportedFeatures"));
        assertTrue(apiInfo.containsKey("anthropicSpecificFeatures"));

        @SuppressWarnings("unchecked")
        List<String> supportedModels = (List<String>) apiInfo.get("supportedModels");
        assertTrue(supportedModels.contains("claude-3-5-sonnet-latest"));
        assertTrue(supportedModels.contains("claude-3-opus-20240229"));
    }

    // Helper methods
    private ChatCompletionResponse createMockChatCompletionResponse(String content) {
        Usage usage = new Usage(10, 15);
        
        return new ChatCompletionResponse(
            "msg_123",
            "message",
            AnthropicApi.Role.ASSISTANT,
            List.of(new ContentBlock(content)),
            "claude-3-5-sonnet-latest",
            "end_turn",
            null,
            usage
        );
    }

    private StreamResponse createMockStreamResponse(String eventType, String data) {
        // Create a simple mock stream response - in real implementation this would be more complex
        return new StreamResponse(
            eventType,
            data
        );
    }
}
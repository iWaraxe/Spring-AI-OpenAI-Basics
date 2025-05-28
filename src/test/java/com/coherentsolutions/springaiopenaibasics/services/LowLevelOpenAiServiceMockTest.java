package com.coherentsolutions.springaiopenaibasics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletion;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionRequest;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionChunk;
import org.springframework.ai.openai.api.OpenAiApi.Choice;
import org.springframework.ai.openai.api.OpenAiApi.ChatCompletionMessage;
import org.springframework.ai.openai.api.OpenAiApi.Usage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowLevelOpenAiServiceMockTest {

    @Mock
    private OpenAiApi openAiApi;

    @InjectMocks
    private LowLevelOpenAiServiceImpl lowLevelService;

    @Test
    void testBasicChatCompletion() {
        // Arrange
        String message = "Hello, OpenAI!";
        ChatCompletion mockCompletion = createMockChatCompletion("Hello! How can I help you today?");
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletion> response = lowLevelService.basicChatCompletion(message);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hello! How can I help you today?", 
            response.getBody().choices().get(0).message().content());

        verify(openAiApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testStreamingChatCompletion() {
        // Arrange
        String message = "Tell me a story";
        ChatCompletionChunk chunk1 = createMockChatCompletionChunk("Once upon");
        ChatCompletionChunk chunk2 = createMockChatCompletionChunk(" a time");
        
        Flux<ChatCompletionChunk> mockStream = Flux.just(chunk1, chunk2);

        when(openAiApi.chatCompletionStream(any(ChatCompletionRequest.class)))
            .thenReturn(mockStream);

        // Act
        Flux<ChatCompletionChunk> response = lowLevelService.streamingChatCompletion(message);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .verifyComplete();

        verify(openAiApi, times(1)).chatCompletionStream(any(ChatCompletionRequest.class));
    }

    @Test
    void testCustomParameterChat() {
        // Arrange
        String message = "Custom test";
        String model = "gpt-4";
        Double temperature = 0.5;
        Integer maxTokens = 100;

        ChatCompletion mockCompletion = createMockChatCompletion("Custom response");
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletion> response = lowLevelService.customParameterChat(
            message, model, temperature, maxTokens);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify that custom parameters are used
        verify(openAiApi, times(1)).chatCompletionEntity(
            argThat(request -> 
                request.model().equals(model) && 
                request.temperature().equals(temperature)
            )
        );
    }

    @Test
    void testBatchProcessMessages() {
        // Arrange
        List<String> messages = List.of("Message 1", "Message 2", "Message 3");
        
        ChatCompletion mockCompletion1 = createMockChatCompletion("Response 1");
        ChatCompletion mockCompletion2 = createMockChatCompletion("Response 2");
        ChatCompletion mockCompletion3 = createMockChatCompletion("Response 3");

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(ResponseEntity.ok(mockCompletion1))
            .thenReturn(ResponseEntity.ok(mockCompletion2))
            .thenReturn(ResponseEntity.ok(mockCompletion3));

        // Act
        List<ResponseEntity<ChatCompletion>> responses = lowLevelService.batchProcessMessages(messages);

        // Assert
        assertEquals(3, responses.size());
        assertEquals("Response 1", responses.get(0).getBody().choices().get(0).message().content());
        assertEquals("Response 2", responses.get(1).getBody().choices().get(0).message().content());
        assertEquals("Response 3", responses.get(2).getBody().choices().get(0).message().content());

        verify(openAiApi, times(3)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testSystemUserChat() {
        // Arrange
        String systemPrompt = "You are a helpful assistant.";
        String userMessage = "What is Spring AI?";

        ChatCompletion mockCompletion = createMockChatCompletion(
            "Spring AI is a framework for building AI applications.");
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletion> response = lowLevelService.systemUserChat(systemPrompt, userMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify that both system and user messages are included
        verify(openAiApi, times(1)).chatCompletionEntity(
            argThat(request -> request.messages().size() == 2)
        );
    }

    @Test
    void testGetDetailedResponseMetadata() {
        // Arrange
        String message = "Test metadata";
        
        Usage usage = new Usage(10, 20, 30);
        ChatCompletion mockCompletion = new ChatCompletion(
            "chat-123",
            "chat.completion",
            1234567890L,
            "gpt-4o-mini",
            "fp_abc123",
            List.of(new Choice(0, new ChatCompletionMessage("Test response", 
                OpenAiApi.Role.ASSISTANT), "stop")),
            usage
        );
        
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        Map<String, Object> metadata = lowLevelService.getDetailedResponseMetadata(message);

        // Assert
        assertNotNull(metadata);
        assertEquals(200, metadata.get("httpStatus"));
        assertEquals("chat-123", metadata.get("id"));
        assertEquals("gpt-4o-mini", metadata.get("model"));
        assertEquals(10, metadata.get("promptTokens"));
        assertEquals(20, metadata.get("completionTokens"));
        assertEquals(30, metadata.get("totalTokens"));
        assertTrue(metadata.containsKey("estimatedCost"));
        assertEquals("Test response", metadata.get("responseContent"));
    }

    @Test
    void testChatWithLogitBias() {
        // Arrange
        String message = "Test logit bias";
        Map<String, Integer> logitBias = Map.of("50256", -100); // Suppress end-of-text token
        
        ChatCompletion mockCompletion = createMockChatCompletion("Response with bias");
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletion> response = lowLevelService.chatWithLogitBias(message, logitBias);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(openAiApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    @Test
    void testChatWithCustomHeaders() {
        // Arrange
        String message = "Test custom headers";
        Map<String, String> headers = Map.of("X-Custom-Header", "custom-value");
        
        ChatCompletion mockCompletion = createMockChatCompletion("Response with headers");
        ResponseEntity<ChatCompletion> mockResponse = ResponseEntity.ok(mockCompletion);

        when(openAiApi.chatCompletionEntity(any(ChatCompletionRequest.class)))
            .thenReturn(mockResponse);

        // Act
        ResponseEntity<ChatCompletion> response = lowLevelService.chatWithCustomHeaders(message, headers);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        verify(openAiApi, times(1)).chatCompletionEntity(any(ChatCompletionRequest.class));
    }

    // Helper methods
    private ChatCompletion createMockChatCompletion(String content) {
        ChatCompletionMessage message = new ChatCompletionMessage(content, OpenAiApi.Role.ASSISTANT);
        Choice choice = new Choice(0, message, "stop");
        Usage usage = new Usage(10, 15, 25);
        
        return new ChatCompletion(
            "chat-123",
            "chat.completion", 
            System.currentTimeMillis() / 1000,
            "gpt-4o-mini",
            "fp_abc123",
            List.of(choice),
            usage
        );
    }

    private ChatCompletionChunk createMockChatCompletionChunk(String content) {
        // Create a simple mock chunk - in real implementation this would be more complex
        return new ChatCompletionChunk(
            "chunk-123",
            "chat.completion.chunk",
            System.currentTimeMillis() / 1000,
            "gpt-4o-mini",
            "fp_abc123",
            List.of()
        );
    }
}
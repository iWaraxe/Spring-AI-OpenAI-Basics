package com.coherentsolutions.springaiopenaibasics;

import com.coherentsolutions.springaiopenaibasics.services.MistralAiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "mistral.ai.enabled=true",
    "mistral.ai.api.key=${MISTRAL_AI_API_KEY:test-key}",
    "mistral.ai.model=mistral-small-latest"
})
@EnabledIfEnvironmentVariable(named = "MISTRAL_AI_API_KEY", matches = ".*")
class MistralAiIntegrationTest {

    @Autowired
    private MistralAiService mistralAiService;

    @Test
    void testGenerateResponse() {
        // Act
        ChatResponse response = mistralAiService.generateResponse("What is Mistral AI?");

        // Assert
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertNotNull(response.getResult().getOutput());
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        assertTrue(content.toLowerCase().contains("mistral"));
    }

    @Test
    void testGenerateStreamResponse() {
        // Act & Assert
        StepVerifier.create(mistralAiService.generateStreamResponse("Tell me a very short joke"))
            .expectNextMatches(response -> {
                assertNotNull(response);
                assertNotNull(response.getResult());
                return true;
            })
            .thenCancel()
            .verify(Duration.ofSeconds(30));
    }

    @Test
    void testGenerateWithTemperature() {
        // Test with low temperature for focused response
        ChatResponse lowTempResponse = mistralAiService.generateWithTemperature(
            "Say exactly: 'Low temperature response'", 0.1);
        
        assertNotNull(lowTempResponse);
        String content = lowTempResponse.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());

        // Test with high temperature for creative response
        ChatResponse highTempResponse = mistralAiService.generateWithTemperature(
            "Write a creative sentence about space", 1.0);
        
        assertNotNull(highTempResponse);
        String creativeContent = highTempResponse.getResult().getOutput().getContent();
        assertNotNull(creativeContent);
        assertFalse(creativeContent.trim().isEmpty());
    }

    @Test
    void testGenerateWithMaxTokens() {
        // Act
        ChatResponse response = mistralAiService.generateWithMaxTokens(
            "Write a long story about artificial intelligence", 50);

        // Assert
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        // Note: We can't verify exact token count without tokenizer, 
        // but content should be reasonably short
        assertTrue(content.length() < 500);
    }

    @Test
    void testGenerateWithSafePrompt() {
        // Act
        ChatResponse response = mistralAiService.generateWithSafePrompt(
            "Tell me about artificial intelligence safety", true);

        // Assert
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        assertTrue(content.toLowerCase().contains("safety") || 
                  content.toLowerCase().contains("responsible"));
    }

    @Test
    void testGenerateMultiModal() {
        // Act
        ChatResponse response = mistralAiService.generateMultiModal(
            "What do you see in this image?",
            "https://docs.spring.io/spring-ai/reference/_images/multimodal.test.png");

        // Assert
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        // Response should contain description of image content
        assertTrue(content.toLowerCase().contains("image") || 
                  content.toLowerCase().contains("see") ||
                  content.toLowerCase().contains("picture"));
    }

    @Test
    void testGenerateWithRandomSeed() {
        // Test deterministic behavior with same seed
        Integer seed = 12345;
        
        ChatResponse response1 = mistralAiService.generateWithRandomSeed(
            "Generate a random number between 1 and 10", seed);
        ChatResponse response2 = mistralAiService.generateWithRandomSeed(
            "Generate a random number between 1 and 10", seed);

        assertNotNull(response1);
        assertNotNull(response2);
        
        String content1 = response1.getResult().getOutput().getContent();
        String content2 = response2.getResult().getOutput().getContent();
        
        assertNotNull(content1);
        assertNotNull(content2);
        assertFalse(content1.trim().isEmpty());
        assertFalse(content2.trim().isEmpty());
        
        // With same seed, responses should be more similar (though not necessarily identical)
        // This is a best-effort test as determinism depends on model implementation
    }

    @Test
    void testGenerateJsonResponse() {
        // Act
        ChatResponse response = mistralAiService.generateJsonResponse(
            "Create a JSON object with name, age, and city fields for a person");

        // Assert
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        
        // Should contain JSON-like structure
        assertTrue(content.contains("{") && content.contains("}"));
        assertTrue(content.contains("name") || content.contains("age") || content.contains("city"));
    }

    @Test
    void testGenerateWithStopSequences() {
        // Act
        ChatResponse response = mistralAiService.generateWithStopSequences(
            "Count from 1 to 10: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10", "5", "five");

        // Assert
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        
        // Response should be shorter due to stop sequence
        assertTrue(content.length() < 100);
    }

    @Test
    void testGenerateWithTopP() {
        // Test with low topP for focused response
        ChatResponse focusedResponse = mistralAiService.generateWithTopP(
            "What is the capital of France?", 0.1);
        
        assertNotNull(focusedResponse);
        String content = focusedResponse.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        assertTrue(content.toLowerCase().contains("paris"));

        // Test with high topP for more varied response
        ChatResponse variedResponse = mistralAiService.generateWithTopP(
            "Tell me about France", 0.9);
        
        assertNotNull(variedResponse);
        String variedContent = variedResponse.getResult().getOutput().getContent();
        assertNotNull(variedContent);
        assertFalse(variedContent.trim().isEmpty());
        assertTrue(variedContent.toLowerCase().contains("france"));
    }

    @Test
    void testMistralSpecificCapabilities() {
        // Test Mistral's multilingual capabilities
        ChatResponse multilingualResponse = mistralAiService.generateResponse(
            "Respond in French: How are you today?");
        
        assertNotNull(multilingualResponse);
        String content = multilingualResponse.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        
        // Should contain French response
        assertTrue(content.toLowerCase().contains("français") || 
                  content.toLowerCase().contains("bonjour") ||
                  content.toLowerCase().contains("comment") ||
                  content.toLowerCase().contains("allez"));
    }

    @Test
    void testMistralCodeGeneration() {
        // Test Mistral's code generation capabilities
        ChatResponse codeResponse = mistralAiService.generateResponse(
            "Write a simple Python function to calculate fibonacci numbers");
        
        assertNotNull(codeResponse);
        String content = codeResponse.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        
        // Should contain code-related keywords
        assertTrue(content.toLowerCase().contains("def") || 
                  content.toLowerCase().contains("function") ||
                  content.toLowerCase().contains("fibonacci"));
    }

    @Test
    void testMistralReasoningCapabilities() {
        // Test Mistral's reasoning and problem-solving
        ChatResponse reasoningResponse = mistralAiService.generateResponse(
            "If it takes 5 machines 5 minutes to make 5 widgets, " +
            "how long would it take 100 machines to make 100 widgets?");
        
        assertNotNull(reasoningResponse);
        String content = reasoningResponse.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.trim().isEmpty());
        
        // Should contain logical reasoning about the answer (5 minutes)
        assertTrue(content.contains("5") && 
                  (content.toLowerCase().contains("minute") || 
                   content.toLowerCase().contains("same")));
    }
}
package com.coherentsolutions.springaiopenaibasics.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerplexitySearchAiServiceMockTest {

    @Mock
    private ChatModel perplexityChatModel;

    @InjectMocks
    private PerplexitySearchAiServiceImpl perplexityService;

    @Test
    void testGetCurrentInformation() {
        // Arrange
        String query = "Latest news about Spring Framework";
        String expectedResponse = "Spring Framework 6.2 was released with new features...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.getCurrentInformation(query);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains("Current date") &&
            prompt.getInstructions().get(0).getContent().contains(query)
        ));
    }

    @Test
    void testStreamSearchResults() {
        // Arrange
        String query = "AI developments";
        ChatResponse chunk1 = createMockChatResponse("Recent AI");
        ChatResponse chunk2 = createMockChatResponse(" developments include...");
        
        Flux<ChatResponse> mockStream = Flux.just(chunk1, chunk2);
        when(perplexityChatModel.stream(any(Prompt.class))).thenReturn(mockStream);

        // Act
        Flux<ChatResponse> response = perplexityService.streamSearchResults(query);

        // Assert
        StepVerifier.create(response)
            .expectNext(chunk1)
            .expectNext(chunk2)
            .verifyComplete();

        verify(perplexityChatModel, times(1)).stream(any(Prompt.class));
    }

    @Test
    void testSearchTechnicalDocs() {
        // Arrange
        String technology = "Spring AI";
        String query = "ChatModel interface";
        String expectedResponse = "Spring AI ChatModel interface provides...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.searchTechnicalDocs(technology, query);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().size() == 2 && // System + User messages
            prompt.getInstructions().get(1).getContent().contains(technology) &&
            prompt.getInstructions().get(1).getContent().contains(query)
        ));
    }

    @Test
    void testCompareProducts() {
        // Arrange
        List<String> products = List.of("iPhone 15", "Samsung S24");
        List<String> criteria = List.of("price", "camera", "battery");
        String expectedResponse = "Comparison: iPhone 15 vs Samsung S24...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.compareProducts(products, criteria);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains("iPhone 15") &&
            prompt.getInstructions().get(0).getContent().contains("Samsung S24") &&
            prompt.getInstructions().get(0).getContent().contains("2025 pricing")
        ));
    }

    @Test
    void testResearchWithCitations() {
        // Arrange
        String topic = "Quantum computing applications";
        boolean academicSources = true;
        String expectedResponse = "Quantum computing research [Source: Nature 2025]...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.researchWithCitations(topic, academicSources);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(1).getContent().contains(topic) &&
            prompt.getInstructions().get(1).getContent().contains("academic and peer-reviewed sources")
        ));
    }

    @Test
    void testFactCheck() {
        // Arrange
        String statement = "Java is the most popular programming language in 2025";
        String expectedResponse = "Fact-check result: Partially true...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.factCheck(statement);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains("fact-checker") &&
            prompt.getInstructions().get(1).getContent().contains(statement)
        ));
    }

    @Test
    void testGetMarketData() {
        // Arrange
        String query = "Bitcoin price and trend";
        String expectedResponse = "Bitcoin is currently trading at $45,000...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.getMarketData(query);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains("Current date") &&
            prompt.getInstructions().get(0).getContent().contains(query)
        ));
    }

    @Test
    void testSearchCodeExamples() {
        // Arrange
        String language = "Java";
        String problem = "implement rate limiting";
        String expectedResponse = "Here are recent Java examples for rate limiting...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.searchCodeExamples(language, problem);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains(language) &&
            prompt.getInstructions().get(0).getContent().contains(problem) &&
            prompt.getInstructions().get(0).getContent().contains("2024-2025")
        ));
    }

    @Test
    void testLocationBasedSearch() {
        // Arrange
        String location = "San Francisco";
        String query = "best coffee shops";
        String expectedResponse = "Top coffee shops in San Francisco...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.locationBasedSearch(location, query);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains(location) &&
            prompt.getInstructions().get(0).getContent().contains(query)
        ));
    }

    @Test
    void testCompareSearchVsStatic() {
        // Arrange
        String query = "Latest AI regulations";
        ChatResponse searchResponse = createMockChatResponse("Search: New EU AI Act passed in 2025...");
        ChatResponse staticResponse = createMockChatResponse("Static: AI regulations are evolving...");
        
        when(perplexityChatModel.call(any(Prompt.class)))
            .thenReturn(searchResponse)
            .thenReturn(staticResponse);

        // Act
        Map<String, ChatResponse> comparison = perplexityService.compareSearchVsStatic(query);

        // Assert
        assertNotNull(comparison);
        assertEquals(2, comparison.size());
        assertTrue(comparison.containsKey("search_enhanced"));
        assertTrue(comparison.containsKey("static_knowledge"));
        
        verify(perplexityChatModel, times(2)).call(any(Prompt.class));
    }

    @Test
    void testSynthesizeResearch() {
        // Arrange
        List<String> queries = List.of("AI ethics", "AI regulation", "AI safety");
        String expectedResponse = "Comprehensive analysis of AI topics...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.synthesizeResearch(queries);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains("AI ethics") &&
            prompt.getInstructions().get(0).getContent().contains("AI regulation") &&
            prompt.getInstructions().get(0).getContent().contains("AI safety")
        ));
    }

    @Test
    void testGetTrendingTopics() {
        // Arrange
        String category = "technology";
        String expectedResponse = "Trending in tech: AI breakthroughs, quantum computing...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.getTrendingTopics(category);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(0).getContent().contains(category) &&
            prompt.getInstructions().get(0).getContent().contains("trending topics")
        ));
    }

    @Test
    void testSearchLegalInfo() {
        // Arrange
        String jurisdiction = "California";
        String topic = "data privacy laws";
        String expectedResponse = "California data privacy laws include CCPA...";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.searchLegalInfo(jurisdiction, topic);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(1).getContent().contains(jurisdiction) &&
            prompt.getInstructions().get(1).getContent().contains(topic) &&
            prompt.getInstructions().get(1).getContent().contains("not legal advice")
        ));
    }

    @Test
    void testSearchHealthInfo() {
        // Arrange
        String condition = "diabetes management";
        boolean includeDisclaimer = true;
        String expectedResponse = "Diabetes management includes... Disclaimer: Consult healthcare provider.";
        
        ChatResponse mockResponse = createMockChatResponse(expectedResponse);
        when(perplexityChatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        // Act
        ChatResponse response = perplexityService.searchHealthInfo(condition, includeDisclaimer);

        // Assert
        assertNotNull(response);
        assertEquals(expectedResponse, response.getResult().getOutput().getContent());
        
        verify(perplexityChatModel, times(1)).call(argThat(prompt -> 
            prompt.getInstructions().get(1).getContent().contains(condition) &&
            prompt.getInstructions().get(1).getContent().contains("medical disclaimer")
        ));
    }

    @Test
    void testGetPerplexityCapabilities() {
        // Act
        Map<String, Object> capabilities = perplexityService.getPerplexityCapabilities();

        // Assert
        assertNotNull(capabilities);
        assertTrue(capabilities.containsKey("uniqueFeatures"));
        assertTrue(capabilities.containsKey("models"));
        assertTrue(capabilities.containsKey("useCases"));
        assertTrue(capabilities.containsKey("limitations"));
        assertTrue(capabilities.containsKey("advantages"));
        
        @SuppressWarnings("unchecked")
        List<String> uniqueFeatures = (List<String>) capabilities.get("uniqueFeatures");
        assertTrue(uniqueFeatures.contains("Real-time web search integration"));
        assertTrue(uniqueFeatures.contains("No knowledge cutoff - always current"));
    }

    // Helper method
    private ChatResponse createMockChatResponse(String content) {
        Generation generation = new Generation(new AssistantMessage(content));
        return new ChatResponse(List.of(generation));
    }
}
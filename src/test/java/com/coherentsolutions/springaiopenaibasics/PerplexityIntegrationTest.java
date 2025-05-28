package com.coherentsolutions.springaiopenaibasics;

import com.coherentsolutions.springaiopenaibasics.services.PerplexitySearchAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.model.ChatResponse;
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
 * Integration tests for Perplexity AI search-enhanced functionality.
 * 
 * These tests demonstrate Perplexity's unique capabilities:
 * - Real-time web search integration
 * - Always current information (no knowledge cutoff)
 * - Automatic source citations
 * - Search-optimized responses
 * 
 * To run these tests, set PERPLEXITY_API_KEY environment variable.
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "PERPLEXITY_API_KEY", matches = ".+")
class PerplexityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PerplexitySearchAiService perplexityService;

    @Test
    void testCurrentInformationEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "query", "What are the latest features in Spring Boot 3.4?"
        );

        mockMvc.perform(post("/api/perplexity/current-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").isNotEmpty());
    }

    @Test
    void testTechnicalDocsSearchEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "technology", "Spring AI",
            "query", "ChatModel interface methods and usage"
        );

        mockMvc.perform(post("/api/perplexity/tech-docs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").isNotEmpty());
    }

    @Test
    void testFactCheckEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "statement", "Spring AI was released in 2024"
        );

        mockMvc.perform(post("/api/perplexity/fact-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").isNotEmpty());
    }

    @Test
    void testMarketDataEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "query", "Current USD to EUR exchange rate"
        );

        mockMvc.perform(post("/api/perplexity/market-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").isNotEmpty());
    }

    @Test
    void testCodeSearchEndpoint() throws Exception {
        Map<String, String> request = Map.of(
            "language", "Java",
            "problem", "implement circuit breaker pattern"
        );

        mockMvc.perform(post("/api/perplexity/code-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").isNotEmpty());
    }

    @Test
    void testPerplexityServiceDirectly() {
        // Test current information retrieval
        ChatResponse response = perplexityService.getCurrentInformation(
            "What are the latest developments in AI as of today?"
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        assertFalse(content.isEmpty());
        
        // Should contain current information
        System.out.println("Current info response: " + content);
    }

    @Test
    void testStreamingDirectly() {
        // Test streaming search results
        var stream = perplexityService.streamSearchResults(
            "Latest breakthroughs in quantum computing 2025"
        );
        
        StepVerifier.create(stream.take(3)) // Take first 3 chunks
            .expectNextMatches(response -> 
                response.getResult() != null && 
                response.getResult().getOutput() != null
            )
            .thenConsumeWhile(response -> true) // Consume remaining
            .verifyComplete();
    }

    @Test
    void testProductComparison() {
        // Test real-time product comparison
        ChatResponse response = perplexityService.compareProducts(
            List.of("iPhone 15 Pro", "Samsung Galaxy S24 Ultra"),
            List.of("price", "camera quality", "battery life", "current availability")
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain current pricing and availability
        assertTrue(content.toLowerCase().contains("price") || 
                   content.toLowerCase().contains("$") ||
                   content.toLowerCase().contains("cost"));
        
        System.out.println("Product comparison: " + content);
    }

    @Test
    void testResearchWithCitations() {
        // Test research capability with citations
        ChatResponse response = perplexityService.researchWithCitations(
            "Recent advancements in transformer architecture for LLMs",
            false // General sources
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain citations or sources
        assertTrue(content.contains("http") || 
                   content.contains("Source:") || 
                   content.contains("[") ||
                   content.toLowerCase().contains("according to"));
        
        System.out.println("Research with citations: " + content);
    }

    @Test
    void testFactChecking() {
        // Test fact-checking capability
        ChatResponse response = perplexityService.factCheck(
            "GPT-4 has 1.76 trillion parameters"
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain verification language
        assertTrue(content.toLowerCase().contains("true") || 
                   content.toLowerCase().contains("false") ||
                   content.toLowerCase().contains("accurate") ||
                   content.toLowerCase().contains("verify") ||
                   content.toLowerCase().contains("confirm"));
    }

    @Test
    void testTechnicalDocumentationSearch() {
        // Test technical documentation search
        ChatResponse response = perplexityService.searchTechnicalDocs(
            "Spring Boot",
            "What are the new features in version 3.3 and 3.4?"
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain version-specific information
        assertTrue(content.contains("3.3") || content.contains("3.4") || 
                   content.toLowerCase().contains("version") ||
                   content.toLowerCase().contains("release"));
    }

    @Test
    void testCompareSearchVsStatic() {
        // Compare search-enhanced vs static responses
        Map<String, ChatResponse> comparison = perplexityService.compareSearchVsStatic(
            "What are the current AI regulations in the European Union?"
        );
        
        assertNotNull(comparison);
        assertEquals(2, comparison.size());
        
        ChatResponse searchResponse = comparison.get("search_enhanced");
        ChatResponse staticResponse = comparison.get("static_knowledge");
        
        assertNotNull(searchResponse);
        assertNotNull(staticResponse);
        
        // Search response should be more specific/current
        String searchContent = searchResponse.getResult().getOutput().getContent();
        String staticContent = staticResponse.getResult().getOutput().getContent();
        
        System.out.println("Search-enhanced: " + searchContent);
        System.out.println("Static knowledge: " + staticContent);
    }

    @Test
    void testTrendingTopics() {
        // Test trending topics retrieval
        ChatResponse response = perplexityService.getTrendingTopics("technology");
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain trending/current language
        assertTrue(content.toLowerCase().contains("trend") ||
                   content.toLowerCase().contains("popular") ||
                   content.toLowerCase().contains("viral") ||
                   content.toLowerCase().contains("current") ||
                   content.toLowerCase().contains("latest"));
    }

    @Test
    void testLocationBasedSearch() {
        // Test location-based search
        ChatResponse response = perplexityService.locationBasedSearch(
            "New York City",
            "best new restaurants opened in 2025"
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain location-specific information
        assertTrue(content.toLowerCase().contains("new york") ||
                   content.toLowerCase().contains("nyc") ||
                   content.toLowerCase().contains("manhattan") ||
                   content.toLowerCase().contains("restaurant"));
    }

    @Test
    void testSynthesizeMultipleQueries() {
        // Test research synthesis from multiple queries
        ChatResponse response = perplexityService.synthesizeResearch(List.of(
            "Current state of AI safety research",
            "Recent AI incidents and failures",
            "Proposed AI regulations worldwide"
        ));
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should cover multiple topics
        assertTrue(content.length() > 500); // Comprehensive response
        assertTrue(content.toLowerCase().contains("safety") ||
                   content.toLowerCase().contains("regulation") ||
                   content.toLowerCase().contains("incident"));
    }

    @Test
    void testPerplexityCapabilities() {
        Map<String, Object> capabilities = perplexityService.getPerplexityCapabilities();
        
        assertNotNull(capabilities);
        assertTrue(capabilities.containsKey("uniqueFeatures"));
        assertTrue(capabilities.containsKey("models"));
        assertTrue(capabilities.containsKey("useCases"));
        assertTrue(capabilities.containsKey("limitations"));
        
        @SuppressWarnings("unchecked")
        List<String> uniqueFeatures = (List<String>) capabilities.get("uniqueFeatures");
        assertTrue(uniqueFeatures.contains("Real-time web search integration"));
        
        @SuppressWarnings("unchecked")
        List<String> limitations = (List<String>) capabilities.get("limitations");
        assertTrue(limitations.contains("No function calling support"));
    }

    @Test
    void testRealTimeDataAccuracy() {
        // Test that Perplexity provides current data
        ChatResponse response = perplexityService.getCurrentInformation(
            "What is today's date and what major events are happening today?"
        );
        
        assertNotNull(response);
        String content = response.getResult().getOutput().getContent();
        assertNotNull(content);
        
        // Should contain date references
        assertTrue(content.matches(".*202[4-9].*") || // Year reference
                   content.toLowerCase().contains("today") ||
                   content.toLowerCase().contains("current"));
    }
}
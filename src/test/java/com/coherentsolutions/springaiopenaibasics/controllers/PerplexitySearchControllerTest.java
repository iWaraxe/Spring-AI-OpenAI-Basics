package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.PerplexitySearchAiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PerplexitySearchController.class)
class PerplexitySearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PerplexitySearchAiService perplexityService;

    @Test
    void testGetCurrentInfo() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Latest Spring AI updates...");
        when(perplexityService.getCurrentInformation(anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of("query", "Latest Spring AI news");

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/current-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Latest Spring AI updates..."));
    }

    @Test
    void testStreamSearch() throws Exception {
        // Arrange
        ChatResponse chunk1 = createMockChatResponse("Searching...");
        ChatResponse chunk2 = createMockChatResponse("Found results...");
        Flux<ChatResponse> mockStream = Flux.just(chunk1, chunk2);
        
        when(perplexityService.streamSearchResults(anyString())).thenReturn(mockStream);

        Map<String, String> request = Map.of("query", "AI developments");

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/stream-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_NDJSON_VALUE));
    }

    @Test
    void testSearchTechDocs() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Spring Boot 3.4 documentation...");
        when(perplexityService.searchTechnicalDocs(anyString(), anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "technology", "Spring Boot",
            "query", "new features in 3.4"
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/tech-docs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Spring Boot 3.4 documentation..."));
    }

    @Test
    void testCompareProducts() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Product comparison results...");
        when(perplexityService.compareProducts(anyList(), anyList())).thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "products", List.of("MacBook Pro", "Dell XPS"),
            "criteria", List.of("price", "performance", "battery")
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/compare-products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Product comparison results..."));
    }

    @Test
    void testResearch() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Research findings with citations...");
        when(perplexityService.researchWithCitations(anyString(), anyBoolean())).thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "topic", "AI Ethics",
            "academicSources", true
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/research")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Research findings with citations..."));
    }

    @Test
    void testFactCheck() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Fact check result: True");
        when(perplexityService.factCheck(anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of("statement", "Spring AI supports 20+ providers");

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/fact-check")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Fact check result: True"));
    }

    @Test
    void testMarketData() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("BTC: $45,000 (+5%)");
        when(perplexityService.getMarketData(anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of("query", "Bitcoin price");

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/market-data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("BTC: $45,000 (+5%)"));
    }

    @Test
    void testCodeSearch() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Code examples found...");
        when(perplexityService.searchCodeExamples(anyString(), anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "language", "Java",
            "problem", "rate limiting"
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/code-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Code examples found..."));
    }

    @Test
    void testLocationSearch() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Local results found...");
        when(perplexityService.locationBasedSearch(anyString(), anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "location", "San Francisco",
            "query", "coffee shops"
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/location-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Local results found..."));
    }

    @Test
    void testCompareSearchVsStatic() throws Exception {
        // Arrange
        ChatResponse searchResponse = createMockChatResponse("Search: Latest info...");
        ChatResponse staticResponse = createMockChatResponse("Static: General info...");
        Map<String, ChatResponse> comparison = Map.of(
            "search_enhanced", searchResponse,
            "static_knowledge", staticResponse
        );
        
        when(perplexityService.compareSearchVsStatic(anyString())).thenReturn(comparison);

        Map<String, String> request = Map.of("query", "AI regulations");

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/compare-search-vs-static")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.search_enhanced.results[0].output.content").value("Search: Latest info..."))
            .andExpect(jsonPath("$.static_knowledge.results[0].output.content").value("Static: General info..."));
    }

    @Test
    void testSynthesizeResearch() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Synthesized research results...");
        when(perplexityService.synthesizeResearch(anyList())).thenReturn(mockResponse);

        Map<String, List<String>> request = Map.of(
            "queries", List.of("AI ethics", "AI safety", "AI regulation")
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/synthesize-research")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Synthesized research results..."));
    }

    @Test
    void testGetTrending() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Trending topics in tech...");
        when(perplexityService.getTrendingTopics(anyString())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/perplexity/trending/technology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Trending topics in tech..."));
    }

    @Test
    void testSearchLegal() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Legal information found...");
        when(perplexityService.searchLegalInfo(anyString(), anyString())).thenReturn(mockResponse);

        Map<String, String> request = Map.of(
            "jurisdiction", "California",
            "topic", "privacy laws"
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/legal-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Legal information found..."));
    }

    @Test
    void testSearchHealth() throws Exception {
        // Arrange
        ChatResponse mockResponse = createMockChatResponse("Health information with disclaimer...");
        when(perplexityService.searchHealthInfo(anyString(), anyBoolean())).thenReturn(mockResponse);

        Map<String, Object> request = Map.of(
            "condition", "diabetes",
            "includeDisclaimer", true
        );

        // Act & Assert
        mockMvc.perform(post("/api/perplexity/health-search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.results[0].output.content").value("Health information with disclaimer..."));
    }

    @Test
    void testGetCapabilities() throws Exception {
        // Arrange
        Map<String, Object> capabilities = Map.of(
            "uniqueFeatures", List.of("Real-time search", "No cutoff"),
            "models", Map.of("sonar-large", "Best quality"),
            "useCases", List.of("Research", "Current events")
        );
        
        when(perplexityService.getPerplexityCapabilities()).thenReturn(capabilities);

        // Act & Assert
        mockMvc.perform(get("/api/perplexity/capabilities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.uniqueFeatures").isArray())
            .andExpect(jsonPath("$.models").exists())
            .andExpect(jsonPath("$.useCases").isArray());
    }

    @Test
    void testGetExampleQueries() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/perplexity/example-queries"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentEvents").isArray())
            .andExpect(jsonPath("$.technicalQueries").isArray())
            .andExpect(jsonPath("$.productResearch").isArray())
            .andExpect(jsonPath("$.factChecking").isArray())
            .andExpect(jsonPath("$.marketData").isArray())
            .andExpect(jsonPath("$.research").isArray());
    }

    // Helper method
    private ChatResponse createMockChatResponse(String content) {
        Generation generation = new Generation(new AssistantMessage(content));
        return new ChatResponse(List.of(generation));
    }
}
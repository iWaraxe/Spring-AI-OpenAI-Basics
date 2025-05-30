package com.coherentsolutions.springaiopenaibasics.controllers;

import com.coherentsolutions.springaiopenaibasics.services.PerplexitySearchAiService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * REST controller demonstrating Perplexity AI's unique search-enhanced capabilities.
 * 
 * This controller showcases why Perplexity is valuable for:
 * 1. Real-time information retrieval
 * 2. Current events and news
 * 3. Technical documentation search
 * 4. Product comparisons with latest data
 * 5. Research with automatic citations
 * 6. Fact-checking and verification
 */
@RestController
@RequestMapping("/api/perplexity")
public class PerplexitySearchController {

    private final PerplexitySearchAiService perplexityService;

    public PerplexitySearchController(PerplexitySearchAiService perplexityService) {
        this.perplexityService = perplexityService;
    }

    /**
     * Get current information with real-time search
     * 
     * Example: Latest news, current events, breaking updates
     */
    @PostMapping("/current-info")
    public ChatResponse getCurrentInfo(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        return perplexityService.getCurrentInformation(query);
    }

    /**
     * Stream search results in real-time
     * 
     * Example: Progressive research results
     */
    @PostMapping(value = "/stream-search", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ChatResponse> streamSearch(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        return perplexityService.streamSearchResults(query);
    }

    /**
     * Search technical documentation
     * 
     * Example: Latest Spring AI docs, API changes, version info
     */
    @PostMapping("/tech-docs")
    public ChatResponse searchTechDocs(@RequestBody Map<String, String> request) {
        String technology = request.get("technology");
        String query = request.get("query");
        return perplexityService.searchTechnicalDocs(technology, query);
    }

    /**
     * Compare products with current data
     * 
     * Example: Compare laptops with 2025 prices and specs
     */
    @PostMapping("/compare-products")
    public ChatResponse compareProducts(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> products = (List<String>) request.get("products");
        @SuppressWarnings("unchecked")
        List<String> criteria = (List<String>) request.get("criteria");
        return perplexityService.compareProducts(products, criteria);
    }

    /**
     * Research with proper citations
     * 
     * Example: Academic research, journalistic investigation
     */
    @PostMapping("/research")
    public ChatResponse researchWithCitations(@RequestBody Map<String, Object> request) {
        String topic = (String) request.get("topic");
        Boolean academicSources = (Boolean) request.getOrDefault("academicSources", false);
        return perplexityService.researchWithCitations(topic, academicSources);
    }

    /**
     * Fact-check a statement
     * 
     * Example: Verify claims, check news accuracy
     */
    @PostMapping("/fact-check")
    public ChatResponse factCheck(@RequestBody Map<String, String> request) {
        String statement = request.get("statement");
        return perplexityService.factCheck(statement);
    }

    /**
     * Get real-time market data
     * 
     * Example: Stock prices, crypto rates, forex
     */
    @PostMapping("/market-data")
    public ChatResponse getMarketData(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        return perplexityService.getMarketData(query);
    }

    /**
     * Search for code examples
     * 
     * Example: Recent Stack Overflow solutions, GitHub examples
     */
    @PostMapping("/code-search")
    public ChatResponse searchCode(@RequestBody Map<String, String> request) {
        String language = request.get("language");
        String problem = request.get("problem");
        return perplexityService.searchCodeExamples(language, problem);
    }

    /**
     * Location-based search
     * 
     * Example: Local businesses, weather, events
     */
    @PostMapping("/location-search")
    public ChatResponse locationSearch(@RequestBody Map<String, String> request) {
        String location = request.get("location");
        String query = request.get("query");
        return perplexityService.locationBasedSearch(location, query);
    }

    /**
     * Compare search-enhanced vs static responses
     * 
     * Demonstrates the value of real-time search
     */
    @PostMapping("/compare-search-vs-static")
    public Map<String, ChatResponse> compareSearchVsStatic(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        return perplexityService.compareSearchVsStatic(query);
    }

    /**
     * Synthesize research from multiple queries
     * 
     * Example: Comprehensive analysis of related topics
     */
    @PostMapping("/synthesize-research")
    public ChatResponse synthesizeResearch(@RequestBody Map<String, List<String>> request) {
        List<String> queries = request.get("queries");
        return perplexityService.synthesizeResearch(queries);
    }

    /**
     * Get trending topics
     * 
     * Example: Viral content, social media trends
     */
    @GetMapping("/trending/{category}")
    public ChatResponse getTrending(@PathVariable String category) {
        return perplexityService.getTrendingTopics(category);
    }

    /**
     * Search legal information
     * 
     * Example: Current laws, regulations, compliance
     */
    @PostMapping("/legal-search")
    public ChatResponse searchLegal(@RequestBody Map<String, String> request) {
        String jurisdiction = request.get("jurisdiction");
        String topic = request.get("topic");
        return perplexityService.searchLegalInfo(jurisdiction, topic);
    }

    /**
     * Search health information
     * 
     * Example: Latest medical research, treatments
     */
    @PostMapping("/health-search")
    public ChatResponse searchHealth(@RequestBody Map<String, Object> request) {
        String condition = (String) request.get("condition");
        Boolean includeDisclaimer = (Boolean) request.getOrDefault("includeDisclaimer", true);
        return perplexityService.searchHealthInfo(condition, includeDisclaimer);
    }

    /**
     * Get Perplexity capabilities information
     */
    @GetMapping("/capabilities")
    public Map<String, Object> getCapabilities() {
        return perplexityService.getPerplexityCapabilities();
    }

    /**
     * Example queries demonstrating Perplexity's strengths
     */
    @GetMapping("/example-queries")
    public Map<String, List<String>> getExampleQueries() {
        return Map.of(
            "currentEvents", List.of(
                "What happened in the tech industry today?",
                "Latest updates on Spring AI framework",
                "Current status of AI regulations in EU"
            ),
            "technicalQueries", List.of(
                "What's new in Spring Boot 3.4?",
                "Latest best practices for microservices in 2025",
                "Recent vulnerabilities in Java libraries"
            ),
            "productResearch", List.of(
                "Compare MacBook Pro M4 vs Dell XPS 15 2025 models",
                "Best AI development laptops under $2000 in 2025",
                "Current prices for NVIDIA GPUs"
            ),
            "factChecking", List.of(
                "Is it true that Java 23 will have value types?",
                "Verify: OpenAI released GPT-5 in 2025",
                "Fact-check: Spring AI supports 30+ LLM providers"
            ),
            "marketData", List.of(
                "Current price of Bitcoin and 24h change",
                "NVIDIA stock performance today",
                "USD to EUR exchange rate now"
            ),
            "research", List.of(
                "Latest research on transformer architectures",
                "Current state of quantum computing in 2025",
                "Recent breakthroughs in AI safety"
            )
        );
    }
}
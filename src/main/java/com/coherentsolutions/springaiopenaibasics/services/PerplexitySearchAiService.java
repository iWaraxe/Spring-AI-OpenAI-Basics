package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * Service interface for demonstrating Perplexity AI's unique capabilities.
 * 
 * Perplexity AI is unique because it combines:
 * 1. Real-time web search results with LLM responses
 * 2. Always up-to-date information (no knowledge cutoff)
 * 3. Automatic source citations
 * 4. Search-optimized models
 * 5. Fact-checking and verification capabilities
 * 
 * Key use cases where Perplexity excels:
 * - Current events and news
 * - Technical documentation lookup
 * - Product comparisons with latest pricing
 * - Research with citations
 * - Fact-checking and verification
 * - Real-time data queries
 */
public interface PerplexitySearchAiService {
    
    /**
     * Get current information with real-time search
     * Use case: Breaking news, current events, latest updates
     */
    ChatResponse getCurrentInformation(String query);
    
    /**
     * Streaming response with search results
     * Use case: Real-time research with progressive results
     */
    Flux<ChatResponse> streamSearchResults(String query);
    
    /**
     * Technical documentation search
     * Use case: Find latest API docs, framework updates, version info
     */
    ChatResponse searchTechnicalDocs(String technology, String query);
    
    /**
     * Product comparison with current prices
     * Use case: E-commerce research, price comparison, feature analysis
     */
    ChatResponse compareProducts(List<String> products, List<String> criteria);
    
    /**
     * Research with citations
     * Use case: Academic research, fact-based writing, journalistic work
     */
    ChatResponse researchWithCitations(String topic, boolean academicSources);
    
    /**
     * Fact-check a statement
     * Use case: Verify claims, check accuracy, validate information
     */
    ChatResponse factCheck(String statement);
    
    /**
     * Get real-time market/financial data
     * Use case: Stock prices, crypto rates, market analysis
     */
    ChatResponse getMarketData(String query);
    
    /**
     * Search for code examples and solutions
     * Use case: Find recent code examples, bug fixes, Stack Overflow solutions
     */
    ChatResponse searchCodeExamples(String language, String problem);
    
    /**
     * Location-based search
     * Use case: Local business info, travel recommendations, weather
     */
    ChatResponse locationBasedSearch(String location, String query);
    
    /**
     * Compare search-enhanced vs regular LLM response
     * Use case: Demonstrate the value of real-time search
     */
    Map<String, ChatResponse> compareSearchVsStatic(String query);
    
    /**
     * Multi-query research synthesis
     * Use case: Comprehensive research on complex topics
     */
    ChatResponse synthesizeResearch(List<String> queries);
    
    /**
     * Get trending topics and analysis
     * Use case: Social media trends, viral content, current discussions
     */
    ChatResponse getTrendingTopics(String category);
    
    /**
     * Legal and regulatory search
     * Use case: Current laws, regulations, compliance updates
     */
    ChatResponse searchLegalInfo(String jurisdiction, String topic);
    
    /**
     * Health and medical information (with disclaimers)
     * Use case: Latest medical research, treatment options, health guidelines
     */
    ChatResponse searchHealthInfo(String condition, boolean includeDisclaimer);
    
    /**
     * Get Perplexity-specific capabilities info
     * Use case: Understanding what makes Perplexity unique
     */
    Map<String, Object> getPerplexityCapabilities();
}
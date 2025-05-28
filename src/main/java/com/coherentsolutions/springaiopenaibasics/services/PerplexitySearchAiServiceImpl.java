package com.coherentsolutions.springaiopenaibasics.services;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation demonstrating Perplexity AI's unique search-enhanced capabilities.
 * 
 * Perplexity combines real-time web search with LLM responses, providing:
 * - Always current information (no knowledge cutoff)
 * - Automatic source citations
 * - Fact-checking capabilities
 * - Search-optimized responses
 */
@Service
public class PerplexitySearchAiServiceImpl implements PerplexitySearchAiService {

    private final ChatModel perplexityChatModel;
    private static final String SEARCH_MODEL = "llama-3.1-sonar-large-128k-online";
    private static final String FAST_MODEL = "llama-3.1-sonar-small-128k-online";

    public PerplexitySearchAiServiceImpl(@Qualifier("perplexityChatModel") ChatModel perplexityChatModel) {
        this.perplexityChatModel = perplexityChatModel;
    }

    @Override
    public ChatResponse getCurrentInformation(String query) {
        // Add current date/time context for better search results
        String enhancedQuery = String.format(
            "Current date is %s. %s Please provide the most recent information available.",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
            query
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(enhancedQuery),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.2) // Lower temperature for factual accuracy
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public Flux<ChatResponse> streamSearchResults(String query) {
        Prompt prompt = new Prompt(
            new UserMessage(query + " Please search for the latest information and provide detailed results."),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.3)
                .streamUsage(true)
                .build()
        );
        
        return perplexityChatModel.stream(prompt);
    }

    @Override
    public ChatResponse searchTechnicalDocs(String technology, String query) {
        String technicalQuery = String.format(
            "Search for the latest official documentation about %s. Specifically: %s. " +
            "Include version numbers, recent updates, and official sources.",
            technology, query
        );
        
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("You are a technical documentation expert. Always cite official sources and include version information."),
                new UserMessage(technicalQuery)
            ),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.1) // Very low for technical accuracy
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse compareProducts(List<String> products, List<String> criteria) {
        String comparisonQuery = String.format(
            "Compare these products with current 2025 pricing and features: %s. " +
            "Use these criteria: %s. " +
            "Include current prices, availability, and recent reviews.",
            String.join(", ", products),
            String.join(", ", criteria)
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(comparisonQuery),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.3)
                .maxTokens(2000) // Longer response for detailed comparison
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse researchWithCitations(String topic, boolean academicSources) {
        String researchQuery = String.format(
            "Research the topic: %s. %s " +
            "Provide a comprehensive overview with proper citations. " +
            "Include recent developments and multiple perspectives.",
            topic,
            academicSources ? "Focus on academic and peer-reviewed sources." : "Use reputable sources."
        );
        
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("You are a research assistant. Always provide citations in [Source: URL] format."),
                new UserMessage(researchQuery)
            ),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.2)
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse factCheck(String statement) {
        String factCheckQuery = String.format(
            "Fact-check this statement: '%s'. " +
            "Search for reliable sources to verify or refute this claim. " +
            "Provide evidence from multiple sources and rate the accuracy.",
            statement
        );
        
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("You are a fact-checker. Be objective and cite all sources."),
                new UserMessage(factCheckQuery)
            ),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.1) // Low temperature for objectivity
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse getMarketData(String query) {
        String marketQuery = String.format(
            "Current date: %s. %s " +
            "Provide the latest market data, prices, and trends. " +
            "Include percentage changes and recent news affecting the market.",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            query
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(marketQuery),
            OpenAiChatOptions.builder()
                .model(FAST_MODEL) // Use fast model for real-time data
                .temperature(0.1)
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse searchCodeExamples(String language, String problem) {
        String codeQuery = String.format(
            "Find recent code examples in %s for: %s. " +
            "Search Stack Overflow, GitHub, and official documentation. " +
            "Include solutions from 2024-2025 preferably, with best practices.",
            language, problem
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(codeQuery),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.2)
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse locationBasedSearch(String location, String query) {
        String locationQuery = String.format(
            "For location: %s, search for: %s. " +
            "Include current information, recent reviews, operating hours, and contact details. " +
            "Current date/time: %s",
            location, query,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(locationQuery),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.3)
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public Map<String, ChatResponse> compareSearchVsStatic(String query) {
        Map<String, ChatResponse> comparison = new HashMap<>();
        
        // Search-enhanced response
        Prompt searchPrompt = new Prompt(
            new UserMessage(query + " (Use web search for latest information)"),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.3)
                .build()
        );
        comparison.put("search_enhanced", perplexityChatModel.call(searchPrompt));
        
        // Simulated static response (without emphasizing search)
        Prompt staticPrompt = new Prompt(
            new UserMessage(query + " (Answer based on general knowledge)"),
            OpenAiChatOptions.builder()
                .model(FAST_MODEL)
                .temperature(0.3)
                .build()
        );
        comparison.put("static_knowledge", perplexityChatModel.call(staticPrompt));
        
        return comparison;
    }

    @Override
    public ChatResponse synthesizeResearch(List<String> queries) {
        String synthesisQuery = String.format(
            "Research and synthesize information on these related topics:\n%s\n" +
            "Provide a comprehensive analysis that connects all topics. " +
            "Include recent developments and cite sources.",
            queries.stream()
                .map(q -> "- " + q)
                .collect(Collectors.joining("\n"))
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(synthesisQuery),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.4)
                .maxTokens(3000) // Longer for comprehensive synthesis
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse getTrendingTopics(String category) {
        String trendingQuery = String.format(
            "What are the current trending topics in %s as of %s? " +
            "Include viral content, popular discussions, and emerging trends. " +
            "Search social media, news sites, and trend aggregators.",
            category,
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        );
        
        Prompt prompt = new Prompt(
            new UserMessage(trendingQuery),
            OpenAiChatOptions.builder()
                .model(FAST_MODEL) // Fast model for trend detection
                .temperature(0.5) // Higher for diverse results
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse searchLegalInfo(String jurisdiction, String topic) {
        String legalQuery = String.format(
            "Search for current legal information in %s regarding: %s. " +
            "Include recent law changes, regulations, and official sources. " +
            "Note: This is for informational purposes only, not legal advice.",
            jurisdiction, topic
        );
        
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("Provide legal information with appropriate disclaimers. Cite official sources."),
                new UserMessage(legalQuery)
            ),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.1) // Very low for legal accuracy
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public ChatResponse searchHealthInfo(String condition, boolean includeDisclaimer) {
        String healthQuery = String.format(
            "Search for current medical information about: %s. " +
            "Include latest research, treatment options, and guidelines from reputable medical sources. " +
            "%s",
            condition,
            includeDisclaimer ? "IMPORTANT: Include medical disclaimer." : ""
        );
        
        Prompt prompt = new Prompt(
            List.of(
                new SystemMessage("Provide health information from reputable sources. Always recommend consulting healthcare professionals."),
                new UserMessage(healthQuery)
            ),
            OpenAiChatOptions.builder()
                .model(SEARCH_MODEL)
                .temperature(0.2)
                .build()
        );
        
        return perplexityChatModel.call(prompt);
    }

    @Override
    public Map<String, Object> getPerplexityCapabilities() {
        return Map.of(
            "uniqueFeatures", List.of(
                "Real-time web search integration",
                "No knowledge cutoff - always current",
                "Automatic source citations",
                "Search-optimized models",
                "Fact-checking capabilities",
                "Multi-source synthesis"
            ),
            "models", Map.of(
                "sonar-large", "Best quality, comprehensive search",
                "sonar-small", "Faster responses, good for simple queries"
            ),
            "useCases", List.of(
                "Current events and news",
                "Technical documentation",
                "Product research and comparison",
                "Academic research with citations",
                "Fact-checking and verification",
                "Market data and financial information",
                "Location-based queries",
                "Trending topics and viral content"
            ),
            "limitations", List.of(
                "No function calling support",
                "No multimodal capabilities",
                "API compatibility limitations with OpenAI",
                "Rate limits on search queries"
            ),
            "advantages", List.of(
                "Always up-to-date information",
                "Built-in fact verification",
                "Reduces hallucination through search",
                "Excellent for research tasks",
                "Real-time data access"
            )
        );
    }
}
# Spring AI OpenAI Basics

A comprehensive tutorial repository demonstrating Spring AI integration with multiple AI providers. This repository contains 17 progressive branches, each showcasing different aspects of AI integration in Spring Boot applications.

## 🚀 Overview

This repository serves as a complete learning resource for Spring AI, covering everything from basic OpenAI integration to advanced provider-specific features and low-level API usage. Each branch builds upon previous concepts while introducing new capabilities.

## 📚 Course Structure

### Part 1: Foundation & High-Level APIs (Branches 1-12)

| Branch | Topic | Description |
|--------|-------|-------------|
| [1-openai-service](../../tree/1-openai-service) | Basic OpenAI Integration | Simple service layer with OpenAI ChatModel |
| [2-explore-llm-capabilities](../../tree/2-explore-llm-capabilities) | LLM Exploration | Understanding model capabilities and limitations |
| [3-create-spring-mvc-controller](../../tree/3-create-spring-mvc-controller) | REST Controllers | Building web endpoints for AI interactions |
| [4-postman-demo](../../tree/4-postman-demo) | API Testing | Postman collection for testing endpoints |
| [5-using-prompt-templates](../../tree/5-using-prompt-templates) | Prompt Templates | Structured prompting with Spring Templates |
| [6-custom-response-format](../../tree/6-custom-response-format) | Response Formatting | Custom response models and formatting |
| [7-response-in-json-format](../../tree/7-response-in-json-format) | JSON Responses | Structured JSON output from AI models |
| [8-response-in-json-format-update](../../tree/8-response-in-json-format-update) | JSON Updates | Enhanced JSON response handling |
| [9-binding-with-json-schema](../../tree/9-binding-with-json-schema) | JSON Schema Binding | Type-safe JSON schema validation |
| [10-binding-with-info](../../tree/10-binding-with-info) | Enhanced Binding | Advanced data binding with metadata |
| [11-anthropic-claude](../../tree/11-anthropic-claude) | Anthropic Claude | High-level Anthropic integration |
| [12-local-ollama](../../tree/12-local-ollama) | Local Ollama | Self-hosted models with Ollama |

### Part 2: Low-Level APIs & Advanced Integrations (Branches 13-17)

| Branch | Provider | Focus | Key Features |
|--------|----------|-------|--------------|
| [13-low-level-OpenAiApi](../../tree/13-low-level-OpenAiApi) | OpenAI | Direct API Control | HTTP metadata, cost tracking, logit bias |
| [14-low-level-AnthropicApi](../../tree/14-low-level-AnthropicApi) | Anthropic | Constitutional AI | System prompts, stop sequences, safety |
| [15-low-level-OllamaApi](../../tree/15-low-level-OllamaApi) | Ollama | Local Deployment | Model management, hardware optimization |
| [16-perplexity-search-ai](../../tree/16-perplexity-search-ai) | Perplexity | Real-time Search | Web search integration, current information |
| [17-mistral-ai](../../tree/17-mistral-ai) | Mistral AI | European AI | Multilingual, cost-effective, GDPR compliant |

## 🛠️ Technologies Used

- **Spring Boot 3.4.4** - Application framework
- **Spring AI 1.0.0-M7** - AI integration framework
- **Java 21** - Programming language
- **Maven** - Build tool
- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework

## 🏃‍♂️ Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- API keys for desired providers (see Configuration section)

### Clone and Setup

```bash
git clone https://github.com/iWaraxe/Spring-AI-OpenAI-Basics.git
cd "Spring AI OpenAI Basics"
```

### Configuration

Create `application.properties` with your API keys:

```properties
# OpenAI (required for most branches)
spring.ai.openai.api-key=${OPENAI_API_KEY}

# Optional: Other providers
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY}
mistral.ai.api.key=${MISTRAL_AI_API_KEY}
perplexity.api.key=${PERPLEXITY_API_KEY}

# Ollama (for local deployment)
spring.ai.ollama.base-url=http://localhost:11434
```

### Run Any Branch

```bash
# Switch to desired branch
git checkout <branch-name>

# Run the application
./mvnw spring-boot:run

# Or run tests
./mvnw test
```

## 📖 Learning Path

### Beginner Track
1. Start with **Branch 1** (Basic OpenAI)
2. Progress through **Branches 2-6** (Core concepts)
3. Explore **Branch 11** (Anthropic) or **Branch 12** (Ollama)

### Advanced Track
1. Master **Branches 1-12** first
2. Dive into **Branches 13-15** (Low-level APIs)
3. Explore **Branches 16-17** (Specialized providers)

### Provider-Specific Learning
- **OpenAI Focus**: Branches 1-10, 13
- **Anthropic Focus**: Branches 11, 14
- **Local AI Focus**: Branches 12, 15
- **Search AI Focus**: Branch 16
- **European AI Focus**: Branch 17

## 🔧 Key Features by Branch

### High-Level Spring AI Features
- ✅ **ChatModel & StreamingChatModel** interfaces
- ✅ **Prompt templates** with Spring Template Engine
- ✅ **JSON schema binding** for type-safe responses
- ✅ **Automatic retries** and error handling
- ✅ **Streaming responses** with reactive programming

### Low-Level API Features
- ✅ **Direct HTTP control** for custom requirements
- ✅ **Provider-specific parameters** not available in high-level APIs
- ✅ **Cost tracking** and usage monitoring
- ✅ **Custom authentication** and security
- ✅ **Performance optimization** for specific use cases

### Provider-Specific Capabilities

#### OpenAI (Branches 1-10, 13)
- GPT-4, GPT-3.5 models
- Function calling
- Logit bias control
- Token usage tracking

#### Anthropic Claude (Branches 11, 14)
- Constitutional AI principles
- System prompt optimization
- Safety and harmlessness
- Long context understanding

#### Ollama (Branches 12, 15)
- Local model deployment
- Privacy and data security
- Custom model management
- Hardware optimization

#### Perplexity (Branch 16)
- Real-time web search
- No knowledge cutoff
- Automatic citations
- Current information retrieval

#### Mistral AI (Branch 17)
- Multilingual excellence
- Cost-effective solutions
- European data compliance
- Strong reasoning capabilities

## 🧪 Testing

Each branch includes comprehensive tests:

```bash
# Run all tests
./mvnw test

# Run specific test categories
./mvnw test -Dtest="*MockTest"      # Unit tests
./mvnw test -Dtest="*ControllerTest" # Controller tests
./mvnw test -Dtest="*IntegrationTest" # Integration tests
```

### Test Categories

- **Mock Tests**: Unit tests with mocked dependencies
- **Controller Tests**: Web layer testing with MockMvc
- **Integration Tests**: Full stack tests with real API calls (require API keys)

## 📚 Documentation

Each advanced branch includes comprehensive guides:

- `LOW_LEVEL_API_GUIDE.md` (Branch 13) - When to use low-level APIs
- `LOW_LEVEL_ANTHROPIC_API_GUIDE.md` (Branch 14) - Anthropic-specific features
- `LOW_LEVEL_OLLAMA_API_GUIDE.md` (Branch 15) - Local deployment strategies
- `PERPLEXITY_SEARCH_AI_GUIDE.md` (Branch 16) - Real-time search integration
- `MISTRAL_AI_GUIDE.md` (Branch 17) - European AI compliance and multilingual support

## 🌟 Best Practices Demonstrated

### Architecture Patterns
- Service layer abstraction
- Controller separation of concerns
- Configuration externalization
- Dependency injection patterns

### AI Integration Patterns
- Prompt engineering techniques
- Response validation and error handling
- Streaming response management
- Cost optimization strategies

### Testing Strategies
- Comprehensive test coverage
- Mock vs integration testing
- API key management in tests
- Environment-specific testing

## 🤝 Contributing

This is an educational repository. To contribute:

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- 📧 **Issues**: Use GitHub Issues for bug reports
- 💡 **Features**: Submit feature requests via GitHub Issues
- 📖 **Documentation**: Each branch contains specific documentation

## 🔗 Useful Links

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [OpenAI API Documentation](https://platform.openai.com/docs)
- [Anthropic Claude Documentation](https://docs.anthropic.com/)
- [Ollama Documentation](https://ollama.ai/docs)
- [Perplexity API Documentation](https://docs.perplexity.ai/)
- [Mistral AI Documentation](https://docs.mistral.ai/)

## 🎯 Learning Objectives

By the end of this tutorial series, you will understand:

1. **Spring AI Fundamentals**: How to integrate AI into Spring applications
2. **Provider Comparison**: When to choose different AI providers
3. **API Patterns**: High-level vs low-level API usage
4. **Production Considerations**: Testing, monitoring, and deployment
5. **Cost Optimization**: Efficient AI usage patterns
6. **Security**: API key management and data privacy
7. **Performance**: Streaming, caching, and optimization techniques

---

**Happy Learning!** 🚀 Start with Branch 1 and progress through the comprehensive Spring AI journey.
# AiMocker

[![Readme 中文](https://img.shields.io/badge/Readme-%E4%B8%AD%E6%96%87-blue)](README_CN.md)[![img](https://jitpack.io/v/GladerJ/AiMocker.svg)](https://jitpack.io/#GladerJ/AiMocker)[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://github.com/GladerJ/AiMocker/blob/master/LICENSE)

------

### Overview

AiMocker is an AI-powered test data mocking framework that integrates seamlessly with JUnit 5. It leverages AI APIs to generate realistic POJO (Plain Old Java Object) test data, significantly reducing the manual effort required for test data preparation.

### ✨ Key Features

- 🤖 **AI-Powered Data Generation**: Automatically generates realistic test data using AI models
- 🚀 **Async Processing**: Uses CompletableFuture for concurrent API calls to improve performance
- 💾 **Smart Caching**: Local hash-based caching in `~/.aimocker` directory to minimize token usage
- 📦 **Flexible Data Types**: Supports single objects, Lists, and Arrays
- 🔧 **Easy Configuration**: Simple YAML/Properties configuration
- 🎯 **JUnit 5 Integration**: Seamless integration with JUnit 5 testing framework
- 🌐 **Multi-Provider Support**: Compatible with OpenAI-compatible APIs (OpenAI, SiliconFlow, etc.)

### 📦 Installation

#### Maven

Add the JitPack repository and dependency to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.GladerJ</groupId>
        <artifactId>AiMocker</artifactId>
        <version>tag</version> <!-- Replace with the latest version (e.g. v1.0.1) -->
        <scope>test</scope>
    </dependency>
    
    <!-- For non-Spring Boot projects, add JUnit 5 manually -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> **Note**: Spring Boot projects already include JUnit 5, so you only need the AiMocker dependency.

#### Gradle, SBT, Leiningen

For other build tools (Gradle, Gradle Kotlin DSL, SBT, Leiningen), please refer to the JitPack documentation
.
But these build tools have not been tested yet — contributions and feedback are welcome.

### ⚙️ Configuration

Create a configuration file in `test/resources/`:

**aimocker.yml** (Recommended):

```yaml
llm:
  api-key: ${OPENAI_API_KEY}
  api-url: https://api.openai.com/v1/chat/completions
  model: gpt-3.5-turbo
  temperature: 1.0
  max-tokens: 2000
```

**aimocker.properties**:

```properties
llm.api-key = ${OPENAI_API_KEY}
llm.api-url = https://api.openai.com/v1/chat/completions
llm.model = gpt-3.5-turbo
llm.temperature = 1.0
llm.max-tokens = 2000
```

> **Configuration Priority**: `aimocker.properties` > `aimocker.yml` > `aimocker.yaml`
- It is recommended to set sensitive configurations, such as the API key, as environment variables in the configuration file.
### 🚀 Quick Start

#### Basic Usage

```java
import org.junit.jupiter.api.Test;
import top.mygld.aimocker.anno.AiMock;
import top.mygld.aimocker.anno.AiMockTest;

@AiMockTest
public class UserServiceTest {
    
    // Field injection
    @AiMock("Generate a mock user with realistic data")
    private User user;
    
    @AiMock("Generate a mock animal with name and species")
    private Animal animal;
    
    @Test
    void testFieldInjection() {
        System.out.println("User: " + user);
        System.out.println("Animal: " + animal);
    }
}
```

#### Parameter Injection

```java
@Test
void testParameterInjection(
    @AiMock(value = "Generate random user data with various countries", cache = true) 
    User user) {
    
    assertNotNull(user);
    System.out.println("Generated user: " + user);
}
```

#### List/Array Support

```java
@Test
void testListGeneration(
    @AiMock(value = "Generate diverse user data from different countries", 
            count = 5, 
            cache = true) 
    List<User> users) {
    
    assertEquals(5, users.size());
    users.forEach(System.out::println);
}

@Test
void testArrayGeneration(
    @AiMock(value = "Generate random international users", count = 10) 
    ArrayList<User> users) {
    
    assertEquals(10, users.size());
}
```

### 📝 Annotation Reference

#### @AiMockTest

- **Target**: Class level
- **Purpose**: Enables AiMocker JUnit extension for the test class

#### @AiMock

- **Target**: Field or Parameter
- Attributes
  - `value`: Prompt description for AI data generation (required)
  - `count`: Number of items to generate for collections (default: 1)
  - `cache`: Enable local caching (default: false)

### 🔄 How It Works

1. **Initialization**: AiMocker reads configuration from `application.yml/properties`
2. **Annotation Processing**: Detects `@AiMock` annotations on fields and parameters
3. **Cache Check**: If caching is enabled, checks local cache first
4. **AI Generation**: Calls AI API with the provided prompt and target class structure
5. **Async Processing**: Uses CompletableFuture for parallel API calls
6. **Data Injection**: Deserializes JSON response and injects into test variables
7. **Cache Storage**: Saves generated data to `~/.aimocker/` using hash-based keys

### 🌟 Best Practices

1. **Use Descriptive Prompts**: Provide clear instructions for better data quality

   ```java
   @AiMock("Generate a user with age between 20-60, from Asian countries")
   ```

2. **Enable Caching for Repeated Tests**: Reduce API calls and token usage

   ```java
   @AiMock(value = "...", cache = true)
   ```

3. **Specify Variety in Prompts**: Get diverse test data

   ```java
   @AiMock("Generate users from various countries with different occupations")
   ```

4. **Appropriate Count Values**: Balance between test coverage and performance

   ```java
   @AiMock(value = "...", count = 10) // For boundary testing
   ```

### 🛠️ Supported AI Providers

AiMocker supports any OpenAI-compatible API:

- OpenAI (GPT-3.5, GPT-4)
- Azure OpenAI
- SiliconFlow
- Anthropic Claude (with compatible proxy)
- Local models via LM Studio, Ollama, etc.

### 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/GladerJ/AiMocker/blob/master/LICENSE) file for details.

### 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

### 📧 Contact

- GitHub: [@GladerJ](https://github.com/GladerJ)
- Issues: [GitHub Issues](https://github.com/GladerJ/AiMocker/issues)


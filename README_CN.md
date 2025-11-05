### 项目简介

AiMocker 是一个基于 AI 的测试数据模拟框架，与 JUnit 5 无缝集成。它利用 AI API 自动生成真实的 POJO 测试数据，大幅减少测试数据准备的手动工作量。

### ✨ 核心特性

- 🤖 **AI 驱动数据生成**：使用 AI 模型自动生成真实的测试数据
- 🚀 **异步处理**：使用 CompletableFuture 并发调用 API，提升性能
- 💾 **智能缓存**：基于哈希的本地缓存（存储在 `~/.aimocker` 目录），最小化 token 使用
- 📦 **灵活的数据类型**：支持单个对象、List 和 Array
- 🔧 **简单配置**：简洁的 YAML/Properties 配置方式
- 🎯 **JUnit 5 集成**：与 JUnit 5 测试框架无缝集成
- 🌐 **多提供商支持**：兼容 OpenAI 格式的 API（OpenAI、SiliconFlow 等）

### 📦 安装

#### Maven

在 `pom.xml` 中添加 JitPack 仓库和依赖：

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
        <version>v1.0.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- 非 Spring Boot 项目需要手动添加 JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> **注意**：Spring Boot 项目已经包含 JUnit 5，只需添加 AiMocker 依赖即可。

#### Gradle、SBT、Leiningen

其他构建工具（Gradle、Gradle Kotlin DSL、SBT、Leiningen）的配置请参考 [JitPack 文档](https://jitpack.io/#GladerJ/AiMocker/1.0.0)

### ⚙️ 配置

在 `test/resources/` 目录下创建配置文件：

**application.yml**（推荐）：

```yaml
aimocker:
  llm:
    api-key: 你的API密钥
    api-url: https://api.openai.com/v1/chat/completions
    model: gpt-3.5-turbo
    temperature: 1.0
    max-tokens: 2000
```

**application.properties**：

```properties
aimocker.llm.api-key=你的API密钥
aimocker.llm.api-url=https://api.openai.com/v1/chat/completions
aimocker.llm.model=gpt-3.5-turbo
aimocker.llm.temperature=1.0
aimocker.llm.max-tokens=2000
```

> **配置优先级**：`application.properties` > `application.yml` > `application.yaml`

### 🚀 快速开始

#### 基础用法

```java
import org.junit.jupiter.api.Test;
import top.mygld.aimocker.anno.AiMock;
import top.mygld.aimocker.anno.AiMockTest;

@AiMockTest
public class UserServiceTest {
    
    // 字段注入
    @AiMock("生成一个包含真实数据的用户对象")
    private User user;
    
    @AiMock("生成一个包含名字和物种的动物对象")
    private Animal animal;
    
    @Test
    void testFieldInjection() {
        System.out.println("用户: " + user);
        System.out.println("动物: " + animal);
    }
}
```

#### 参数注入

```java
@Test
void testParameterInjection(
    @AiMock(value = "生成来自不同国家的随机用户数据", cache = true) 
    User user) {
    
    assertNotNull(user);
    System.out.println("生成的用户: " + user);
}
```

#### List/Array 支持

```java
@Test
void testListGeneration(
    @AiMock(value = "生成来自不同国家的多样化用户数据", 
            count = 5, 
            cache = true) 
    List<User> users) {
    
    assertEquals(5, users.size());
    users.forEach(System.out::println);
}

@Test
void testArrayGeneration(
    @AiMock(value = "生成随机国际用户", count = 10) 
    ArrayList<User> users) {
    
    assertEquals(10, users.size());
}
```

### 📝 注解参考

#### @AiMockTest

- **作用域**：类级别
- **用途**：为测试类启用 AiMocker JUnit 扩展

#### @AiMock

- **作用域**：字段或参数
- 属性
  - `value`：AI 数据生成的提示描述（必填）
  - `count`：为集合生成的元素数量（默认：1）
  - `cache`：启用本地缓存（默认：false）

### 🔄 工作原理

1. **初始化**：AiMocker 从 `application.yml/properties` 读取配置
2. **注解处理**：检测字段和参数上的 `@AiMock` 注解
3. **缓存检查**：如果启用缓存，首先检查本地缓存
4. **AI 生成**：使用提供的提示和目标类结构调用 AI API
5. **异步处理**：使用 CompletableFuture 并行调用 API
6. **数据注入**：反序列化 JSON 响应并注入到测试变量
7. **缓存存储**：使用基于哈希的键将生成的数据保存到 `~/.aimocker/`

### 🌟 最佳实践

1. **使用描述性提示**：提供清晰的指令以获得更好的数据质量

   ```java
   @AiMock("生成年龄在20-60岁之间、来自亚洲国家的用户")
   ```

2. **为重复测试启用缓存**：减少 API 调用和 token 使用

   ```java
   @AiMock(value = "...", cache = true)
   ```

3. **在提示中指定多样性**：获得多样化的测试数据

   ```java
   @AiMock("生成来自不同国家、从事不同职业的用户")
   ```

4. **适当的数量值**：在测试覆盖率和性能之间取得平衡

   ```java
   @AiMock(value = "...", count = 10) // 用于边界测试
   ```

### 🛠️ 支持的 AI 提供商

AiMocker 支持任何 OpenAI 兼容的 API：

- OpenAI（GPT-3.5、GPT-4）
- Azure OpenAI
- SiliconFlow
- Anthropic Claude（通过兼容代理）
- 通过 LM Studio、Ollama 等的本地模型

### 📄 许可证

本项目采用 Apache License 2.0 许可证 - 详见 [LICENSE](https://github.com/GladerJ/AiMocker/blob/master/LICENSE) 文件。

### 🤝 贡献

欢迎贡献！请随时提交 Pull Request。

### 📧 联系方式

- GitHub：[@GladerJ](https://github.com/GladerJ)
- Issues：[GitHub Issues](https://github.com/GladerJ/AiMocker/issues)
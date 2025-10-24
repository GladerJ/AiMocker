# AiMocker — 自由 AI 驱动的智能 Mock 测试框架

## 1. 背景

在传统 Java 测试中，已有一些 Mock 框架可以生成数据，但存在明显局限：

- 数据随机：生成的 POJO 数据大多随机，难以覆盖特定场景
- 规则繁琐：要生成精准数据，必须手动设定条件和约束
- 维护成本高：复杂场景下规则难以管理

------

## 2. AiMocker 的价值

AiMocker 通过 AI 自动生成测试数据，解决传统 Mock 框架的痛点：

- 自然语言描述场景：一句话即可描述所需对象
- 精准生成 POJO 对象：生成数据严格遵循描述，避免随机性
- 轻量级、易用：基于 JUnit 5，开箱即可使用
- 自动注入：通过 `@AiMock` 注解直接注入测试方法参数
- 自由接入任意大模型：不绑定特定 AI，支持自定义 Adapter，可接入 OpenAI、SiliconFlow、LangChain 等各种 AI 模型

------

## 3. 核心功能

### 3.1 自动生成 POJO 数据

```java
@Test
void testUser(@AiMock("一个来自北京、名字叫李明、年龄28岁的VIP用户") User vipUser) {
    System.out.println(vipUser);
}
```

- 执行时，`vipUser` 自动被填充为符合描述的对象
- 无需手动设置字段，也不用编写复杂规则

### 3.2 JUnit 5 扩展

- 内置 `AiMockerExtension` 实现 `ParameterResolver`
- 可通过 `@ExtendWith(AiMockerExtension.class)` 显式注册
- 可通过 `META-INF/services` 自动注册 Extension（命令行运行生效）

### 3.3 灵活适配 AI 模型

- 提供 `LanguageModelAdapter` SPI
- 可实现不同 AI 模型适配器
- 用户可自定义 Adapter，接入任意大模型生成业务场景数据

------

## 4. 使用场景

- 单元测试：快速生成复杂对象
- 接口/集成测试：模拟真实业务场景数据
- 数据驱动测试：快速覆盖多样化场景

------

## 5. 优势对比

| 传统 Mock 框架   | AiMocker                       |
| ---------------- | ------------------------------ |
| 数据随机、不可控 | 精准生成符合自然语言描述的对象 |
| 必须手动设定规则 | 只需自然语言描述场景即可       |
| 维护成本高       | 低维护、直接注入测试方法参数   |

------

## 6. 安装与使用

1. 本地安装：

```bash
mvn clean install
```

2. 添加依赖（当前仅为测试，未上传仓库，建议手动下载 jar 包复制到本地仓库测试）：

```xml
<dependency>
    <groupId>top.mygld.aimocker</groupId>
    <artifactId>aimocker</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

3. 编写测试方法，使用 `@AiMock` 注解即可：

```java
@Test
void testUser(@AiMock("一个中国北京的VIP用户，名字叫李明") User user) {
    System.out.println(user);
}
```

## 7. 当前所支持的功能

当前处于该框架的开发初级阶段，目前实现的功能有限，具体如下：

（1）目前内嵌基于硅基流动官方接口的 qwen 模型，仅需在环境变量中添加 `SILICONFLOW_API_KEY=密钥` 即可，这里为了框架初级测试，仅提供了该接口。

（2）AiMocker 提供 **SPI（Service Provider Interface）机制**，允许用户自由接入其他任意大模型或其他第三方库，例如：

```java
public class MyCustomAdapter implements LanguageModelAdapter {
    @Override
    public <T> T generate(Class<T> clazz, String scenario) {
        // 调用第三方库或自定义 AI 接口生成对象
    }
}
```

（3）注册自定义 Adapter

在项目的 `src/main/resources/META-INF/services/` 目录下创建文件：

```
top.mygld.aimocker.adapter.impl.LanguageModelAdapter
```

文件内容填写自定义实现类的全路径，例如：

```
com.example.ai.MyCustomAdapter
```

框架启动时会通过 **Java ServiceLoader** 自动加载该 Adapter，无需手动注入。

## 8. 具体使用方法

例如我现在有一个 `User` 类：

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private int age;
    private String email;
    private String city;
    private String gender;
    private String status;
    private String phoneNumber;
    private String occupation;
}
```

接下来在测试类中添加注解 `@ExtendWith(AiMockerExtension.class)`，并在为生成 `Mock` 数据的对象添加 `@AiMock` 注解：

```java
@SpringBootTest
@ExtendWith(AiMockerExtension.class)
class DemoApplicationTests {
    @Test
    void test4(@AiMock("随机生成所有成员，每一个数据都不能为空，必须符合规范") User user){
        System.out.println(user);
    }

    @Test
    void testListAiMockUser(@AiMock(value = "数据自拟，答案随机一点，不要固定，都不能为空",count = 5) List<User> users) {
        System.out.println(users);
    }
}
```

运行单元测试：

<img src="https://images.mygld.top/file/1761234782648_image.png" alt="image.png" width=100% />



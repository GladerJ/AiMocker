package top.mygld.aimocker.adapter;

import cn.hutool.core.io.resource.ResourceUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import top.mygld.aimocker.adapter.impl.LanguageModelAdapter;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Default Language Model Adapter implementation.
 * Uses SiliconFlow API as default provider.
 *
 * <p>Configuration example (application.yml):</p>
 * <pre>
 * aimocker:
 *   llm:
 *     api-key: sk-your-api-key-here
 *     api-url: https://api.siliconflow.cn/v1/chat/completions
 *     model: Qwen/Qwen2.5-7B-Instruct
 *     temperature: 1.0
 *     max-tokens: 2000
 * </pre>
 *
 * <p>Configuration priority: Environment Variable > YAML Config > Default Value</p>
 * <p>Note: For test projects, place application.yml in src/test/resources</p>
 */
public class LLMAdapter implements LanguageModelAdapter {

    private static final String DEFAULT_API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct";
    private static final double DEFAULT_TEMPERATURE = 1.0;
    private static final int DEFAULT_MAX_TOKENS = 2000;

    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final double temperature;
    private final Integer maxTokens;

    private final ObjectMapper jsonMapper = new ObjectMapper();
    private static final java.net.http.HttpClient HTTP_CLIENT = java.net.http.HttpClient.newHttpClient();

    public LLMAdapter() {
        Config config = loadConfig();

        // API Key: Environment variable takes priority, then config file
        String envApiKey = System.getenv("SILICONFLOW_API_KEY");
        this.apiKey = (envApiKey != null && !envApiKey.trim().isEmpty())
                ? envApiKey
                : config.apiKey;

        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "SILICONFLOW_API_KEY not found. Please set environment variable or configure in application.yml"
            );
        }

        // Other configurations use config file or default values
        this.apiUrl = config.apiUrl;
        this.model = config.model;
        this.temperature = config.temperature;
        this.maxTokens = config.maxTokens;
    }

    /**
     * Load configuration from application.yml
     * Searches in both test and main resources directories
     */
    private Config loadConfig() {
        Config config = new Config();

        try {
            // Try to load application.yml from classpath
            // Will search both src/test/resources and src/main/resources
            InputStream is = ResourceUtil.getStream("application.yml");
            if (is == null) {
                is = ResourceUtil.getStream("application.yaml");
            }

            if (is != null) {
                ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
                JsonNode root = yamlMapper.readTree(is);

                // Parse aimocker.llm node
                JsonNode aimockerNode = root.path("aimocker").path("llm");

                if (!aimockerNode.isMissingNode()) {
                    config.apiKey = getTextOrNull(aimockerNode, "api-key");
                    config.apiUrl = getTextOrDefault(aimockerNode, "api-url", DEFAULT_API_URL);
                    config.model = getTextOrDefault(aimockerNode, "model", DEFAULT_MODEL);
                    config.temperature = getDoubleOrDefault(aimockerNode, "temperature", DEFAULT_TEMPERATURE);
                    config.maxTokens = getIntegerOrNull(aimockerNode, "max-tokens");
                }
            }
        } catch (Exception e) {
            // Config file loading failed, use default configuration
            System.err.println("Warning: Failed to load application.yml, using default configuration: " + e.getMessage());
        }

        return config;
    }

    @Override
    public CompletableFuture<String> generateAsync(String prompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("temperature", temperature);

            if (maxTokens != null) {
                body.put("max_tokens", maxTokens);
            }

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);

            body.put("messages", messages);

            String jsonBody = jsonMapper.writeValueAsString(body);

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(apiUrl))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            return HTTP_CLIENT.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() != 200) {
                            throw new RuntimeException("LLM API error: " + response.statusCode()
                                    + " -> " + response.body());
                        }
                        try {
                            JsonNode root = jsonMapper.readTree(response.body());
                            return root.path("choices").get(0).path("message").path("content").asText();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response", e);
                        }
                    });
        } catch (Exception e) {
            CompletableFuture<String> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }

    // ==================== Helper Methods ====================

    private String getTextOrNull(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        return field.isMissingNode() ? null : field.asText();
    }

    private String getTextOrDefault(JsonNode node, String fieldName, String defaultValue) {
        String value = getTextOrNull(node, fieldName);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    private double getDoubleOrDefault(JsonNode node, String fieldName, double defaultValue) {
        JsonNode field = node.path(fieldName);
        return field.isMissingNode() ? defaultValue : field.asDouble(defaultValue);
    }

    private Integer getIntegerOrNull(JsonNode node, String fieldName) {
        JsonNode field = node.path(fieldName);
        return field.isMissingNode() ? null : field.asInt();
    }

    // ==================== Config Class ====================

    /**
     * Internal configuration class
     */
    private static class Config {
        String apiKey;
        String apiUrl = DEFAULT_API_URL;
        String model = DEFAULT_MODEL;
        double temperature = DEFAULT_TEMPERATURE;
        Integer maxTokens = DEFAULT_MAX_TOKENS;
    }
}
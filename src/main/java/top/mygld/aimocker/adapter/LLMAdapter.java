package top.mygld.aimocker.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import top.mygld.aimocker.adapter.impl.LanguageModelAdapter;
import top.mygld.aimocker.exception.ConfigException;
import top.mygld.aimocker.exception.GenerationException;
import top.mygld.aimocker.util.ResourceUtil;

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
            Map<String, String> map = ResourceUtil.getProperties("application.properties");

            if (map == null) {
                map = ResourceUtil.getYaml("application.yml", "aimocker");
            }

            if (map == null) {
                map = ResourceUtil.getYaml("application.yaml", "aimocker");
            }

            String apiKey = map.get("aimocker.llm.api-key");
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                config.apiKey = apiKey.trim();
            }
            String apiUrl = map.get("aimocker.llm.api-url");
            if (apiUrl != null && !apiUrl.trim().isEmpty()) {
                config.apiUrl = apiUrl.trim();
            }
            String model = map.get("aimocker.llm.model");
            if (model != null && !model.trim().isEmpty()) {
                config.model = model.trim();
            }
            String temperature = map.get("aimocker.llm.temperature");
            if (temperature != null && !temperature.trim().isEmpty()) {
                try {
                    config.temperature = Double.parseDouble(temperature.trim());
                } catch (NumberFormatException e) {
                    throw new ConfigException("Invalid temperature value: " + temperature, e);
                }
            }
            String maxTokens = map.get("aimocker.llm.max-tokens");
            if (maxTokens != null && !maxTokens.trim().isEmpty()) {
                try {
                    config.maxTokens = Integer.parseInt(maxTokens.trim());
                } catch (NumberFormatException e) {
                    throw new ConfigException("Invalid max-tokens value in properties: " + maxTokens, e);
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to load configuration, using default configuration: " + e.getMessage());
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
                            throw new GenerationException("LLM API error: " + response.statusCode()
                                    + " -> " + response.body());
                        }
                        try {
                            JsonNode root = jsonMapper.readTree(response.body());
                            return root.path("choices").get(0).path("message").path("content").asText();
                        } catch (Exception e) {
                            throw new GenerationException("Failed to parse response", e);
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
        Double temperature = DEFAULT_TEMPERATURE;
        Integer maxTokens = DEFAULT_MAX_TOKENS;
    }
}
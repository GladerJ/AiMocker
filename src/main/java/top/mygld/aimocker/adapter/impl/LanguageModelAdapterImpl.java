package top.mygld.aimocker.adapter.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import top.mygld.aimocker.adapter.LanguageModelAdapter;
import top.mygld.aimocker.exception.ConfigException;
import top.mygld.aimocker.exception.GenerationException;
import top.mygld.aimocker.util.ResourceUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Default Language Model Adapter implementation.
 * JDK 1.8 compatible version (uses HttpURLConnection + CompletableFuture)
 */
public class LanguageModelAdapterImpl implements LanguageModelAdapter {

    private static final double DEFAULT_TEMPERATURE = 1.0;
    private static final int DEFAULT_MAX_TOKENS = 2000;

    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final double temperature;
    private final Integer maxTokens;

    private final Gson gson = new Gson();

    public LanguageModelAdapterImpl() {
        Config config = loadConfig();
        this.apiKey = config.apiKey;

        if (this.apiKey == null || this.apiKey.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Missing API key configuration. Please configure it in one of the following files: " +
                            "aimocker.properties, aimocker.yml, or aimocker.yaml."
            );
        }

        this.apiUrl = config.apiUrl;
        this.model = config.model;
        this.temperature = config.temperature;
        this.maxTokens = config.maxTokens;
    }

    private Config loadConfig() {
        Config config = new Config();
        try {
            Map<String, String> map = ResourceUtil.getProperties("aimocker.properties");
            if (map == null) map = ResourceUtil.getYaml("aimocker.yml", "llm");
            if (map == null) map = ResourceUtil.getYaml("aimocker.yaml", "llm");

            if (map != null) {
                String apiKey = map.get("llm.api-key");
                if (apiKey != null && !apiKey.trim().isEmpty()) config.apiKey = apiKey.trim();

                String apiUrl = map.get("llm.api-url");
                if (apiUrl != null && !apiUrl.trim().isEmpty()) config.apiUrl = apiUrl.trim();

                String model = map.get("llm.model");
                if (model != null && !model.trim().isEmpty()) config.model = model.trim();

                String temperature = map.get("llm.temperature");
                if (temperature != null && !temperature.trim().isEmpty()) {
                    try {
                        config.temperature = Double.parseDouble(temperature.trim());
                    } catch (NumberFormatException e) {
                        throw new ConfigException("Invalid temperature: " + temperature, e);
                    }
                }

                String maxTokens = map.get("llm.max-tokens");
                if (maxTokens != null && !maxTokens.trim().isEmpty()) {
                    try {
                        config.maxTokens = Integer.parseInt(maxTokens.trim());
                    } catch (NumberFormatException e) {
                        throw new ConfigException("Invalid max-tokens: " + maxTokens, e);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to load configuration, using defaults. " + e.getMessage());
        }
        return config;
    }

    @Override
    public CompletableFuture<String> generateAsync(String prompt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> body = new HashMap<>();
                body.put("model", model);
                body.put("temperature", temperature);
                if (maxTokens != null) body.put("max_tokens", maxTokens);

                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", prompt);
                messages.add(message);
                body.put("messages", messages);

                String jsonBody = gson.toJson(body);

                HttpURLConnectionWithHeader conn = new HttpURLConnectionWithHeader(apiUrl, apiKey);
                String response = conn.post(jsonBody);

                JsonObject root = JsonParser.parseString(response).getAsJsonObject();
                return  root.getAsJsonArray("choices")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();

            } catch (Exception e) {
                throw new GenerationException("Failed to generate response", e);
            }
        });
    }


    private static class HttpURLConnectionWithHeader {
        private final String url;
        private final String apiKey;

        HttpURLConnectionWithHeader(String url, String apiKey) {
            this.url = url;
            this.apiKey = apiKey;
        }

        String post(String body) throws Exception {
            java.net.URL u = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(50000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            try (java.io.OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes("UTF-8"));
            }

            java.io.InputStream is = conn.getResponseCode() >= 200 && conn.getResponseCode() < 300
                    ? conn.getInputStream()
                    : conn.getErrorStream();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();
            return sb.toString();
        }
    }

    private static class Config {
        String apiKey;
        String apiUrl;
        String model;
        Double temperature = DEFAULT_TEMPERATURE;
        Integer maxTokens = DEFAULT_MAX_TOKENS;

        @Override
        public String toString() {
            return "Config{" +
                    "apiKey='" + apiKey + '\'' +
                    ", apiUrl='" + apiUrl + '\'' +
                    ", model='" + model + '\'' +
                    ", temperature=" + temperature +
                    ", maxTokens=" + maxTokens +
                    '}';
        }
    }
}

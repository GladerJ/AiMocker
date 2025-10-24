package top.mygld.aimocker.adapter;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.mygld.aimocker.adapter.impl.LanguageModelAdapter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 硅基流动默认适配器实现
 * 直接通过 Hutool 调用 https://api.siliconflow.cn/v1/chat/completions
 */
public class SiliconFlowAdapter implements LanguageModelAdapter {

    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct";

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final java.net.http.HttpClient HTTP_CLIENT = java.net.http.HttpClient.newHttpClient();

    public SiliconFlowAdapter() {
        this.apiKey = System.getenv("SILICONFLOW_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("SILICONFLOW_API_KEY environment variable is not set.");
        }
    }

    @Override
    public CompletableFuture<String> generateAsync(String prompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", DEFAULT_MODEL);
            body.put("temperature", 0.7);

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            messages.add(message);

            body.put("messages", messages);

            String jsonBody = mapper.writeValueAsString(body);

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            return HTTP_CLIENT.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() != 200) {
                            throw new RuntimeException("SiliconFlow API error: " + response.statusCode() + " -> " + response.body());
                        }
                        try {
                            JsonNode root = mapper.readTree(response.body());
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
}

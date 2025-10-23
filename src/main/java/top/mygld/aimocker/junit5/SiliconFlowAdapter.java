package top.mygld.aimocker.junit5;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.mygld.aimocker.spi.LanguageModelAdapter;

import java.io.IOException;
import java.util.*;

/**
 * 硅基流动默认适配器实现
 * 直接通过 Hutool 调用 https://api.siliconflow.cn/v1/chat/completions
 */
public class SiliconFlowAdapter implements LanguageModelAdapter {

    private static final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";
    private static final String DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct";

    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public SiliconFlowAdapter() {
        this.apiKey = System.getenv("SILICONFLOW_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalStateException("SILICONFLOW_API_KEY environment variable is not set.");
        }
    }

    @Override
    public String generate(String prompt) {
        try {
            // 构造请求体
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

            // 发起请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(jsonBody)
                    .timeout(30000)
                    .execute();

            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new RuntimeException("SiliconFlow API error: " + response.getStatus() + " -> " + response.body());
            }

            // 解析结果
            JsonNode root = mapper.readTree(response.body());
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (IOException e) {
            throw new RuntimeException("Failed to call SiliconFlow API", e);
        }
    }
}

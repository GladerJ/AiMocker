package top.mygld.aimocker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import top.mygld.aimocker.core.builder.PromptBuilder;
import top.mygld.aimocker.core.ServiceLocator;
import top.mygld.aimocker.adapter.impl.LanguageModelAdapter;
import top.mygld.aimocker.util.JsonUtil;

import java.util.concurrent.CompletableFuture;

/**
 * The main entry point for the AiMocker framework.
 */
public final class AiMocker {

    private static final LanguageModelAdapter ADAPTER = ServiceLocator.loadAdapter();

    private AiMocker() {}

    /**
     * Creates a mock object of the target class based on a natural language scenario.
     * @param targetClass The class of the object to create.
     * @param scenario A description of the desired mock data.
     * @return An instance of the target class with AI-generated data.
     * @param <T> The type of the object to create.
     */
    public static <T> CompletableFuture<T> createAsync(Class<T> targetClass, String scenario) {
        String prompt = PromptBuilder.build(targetClass, scenario);
        return ADAPTER.generateAsync(prompt)
                .thenApply(jsonResponse -> {
                    if (jsonResponse.startsWith("```")){
                        jsonResponse = jsonResponse.substring(7,jsonResponse.length()-3);
                    }
                    try {
                        return JsonUtil.fromJson(jsonResponse, targetClass);
                    } catch (Exception e) {
                        throw new RuntimeException(
                                "AiMocker failed to deserialize AI response into " + targetClass.getName() +
                                        ".\nRaw response was: " + jsonResponse, e);
                    }
                });
    }

}
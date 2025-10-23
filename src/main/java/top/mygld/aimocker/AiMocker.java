package top.mygld.aimocker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import top.mygld.aimocker.internal.PromptBuilder;
import top.mygld.aimocker.internal.ServiceLocator;
import top.mygld.aimocker.spi.LanguageModelAdapter;

/**
 * The main entry point for the AiMocker framework.
 */
public final class AiMocker {

    private static final LanguageModelAdapter ADAPTER = ServiceLocator.loadAdapter();
    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    private AiMocker() {}

    /**
     * Creates a mock object of the target class based on a natural language scenario.
     * @param targetClass The class of the object to create.
     * @param scenario A description of the desired mock data.
     * @return An instance of the target class with AI-generated data.
     * @param <T> The type of the object to create.
     */
    public static <T> T create(Class<T> targetClass, String scenario) {
        String prompt = PromptBuilder.build(targetClass, scenario);
        String jsonResponse = ADAPTER.generate(prompt);

        try {
            return OBJECT_MAPPER.readValue(jsonResponse, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("AiMocker failed to deserialize AI response into " + targetClass.getName() +
                    ".\nRaw response was: " + jsonResponse, e);
        }
    }
}
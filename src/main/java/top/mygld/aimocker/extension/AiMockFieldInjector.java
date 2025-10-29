package top.mygld.aimocker.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import top.mygld.aimocker.anno.AiMock;

import java.lang.reflect.Field;

/**
 * JUnit 5 extension for injecting @AiMock annotated fields.
 *
 * <p>This processor runs after test instance creation and populates
 * all fields marked with @AiMock annotation with AI-generated mock data.</p>
 *
 * <p>The actual data generation is delegated to {@link AiMockDataGenerator}
 * to maintain separation of concerns and avoid code duplication.</p>
 *
 * @author Glader
 * @since 1.0
 * @see AiMock
 * @see AiMockDataGenerator
 */
public class AiMockFieldInjector implements TestInstancePostProcessor {

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        Class<?> testClass = testInstance.getClass();

        // Process all fields with @AiMock annotation
        for (Field field : testClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AiMock.class)) {
                injectField(testInstance, field);
            }
        }
    }

    /**
     * Inject AI-generated mock data into a single field.
     *
     * @param testInstance the test class instance
     * @param field the field to inject
     * @throws Exception if injection fails
     */
    private void injectField(Object testInstance, Field field) throws Exception {
        AiMock annotation = field.getAnnotation(AiMock.class);

        field.setAccessible(true);

        // Delegate data generation to AiMockDataGenerator
        Object value = AiMockDataGenerator.generate(
                field.getType(),
                field.getGenericType(),
                annotation.value(),
                annotation.count(),
                annotation.cache()
        );

        field.set(testInstance, value);
    }
}
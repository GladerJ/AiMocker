package top.mygld.aimocker.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import top.mygld.aimocker.anno.AiMock;

/**
 * JUnit 5 extension for resolving @AiMock annotated test method parameters.
 *
 * <p>This resolver automatically generates and injects AI-powered mock data
 * for test method parameters marked with @AiMock annotation.</p>
 *
 * <p>The actual data generation is delegated to {@link AiMockDataGenerator}
 * to maintain separation of concerns and avoid code duplication.</p>
 *
 * @author Glader
 * @since 1.0
 * @see AiMock
 * @see AiMockDataGenerator
 */
public class AiMockParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(AiMock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {

        AiMock annotation = parameterContext.findAnnotation(AiMock.class)
                .orElseThrow(() -> new ParameterResolutionException("@AiMock annotation not found"));

        try {
            // Delegate data generation to AiMockDataGenerator
            return AiMockDataGenerator.generate(
                    parameterContext.getParameter().getType(),
                    parameterContext.getParameter().getParameterizedType(),
                    annotation.value(),
                    annotation.count()
            );
        } catch (Exception e) {
            throw new ParameterResolutionException("Failed to generate mock data for parameter: "
                    + parameterContext.getParameter().getName(), e);
        }
    }
}
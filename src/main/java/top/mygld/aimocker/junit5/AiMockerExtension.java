package top.mygld.aimocker.junit5;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import top.mygld.aimocker.AiMocker;

import java.lang.reflect.Field;

public class AiMockerExtension implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(AiMock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        try {
            AiMock aiMockAnnotation = parameterContext.getParameter().getAnnotation(AiMock.class);
            String scenario = aiMockAnnotation.value();
            Class<?> parameterType = parameterContext.getParameter().getType();
            return AiMocker.create(parameterType, scenario);
        } catch (Exception e) {
            throw new ParameterResolutionException("AiMocker failed to resolve parameter.", e);
        }
    }
}
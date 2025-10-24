package top.mygld.aimocker.resolver;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import top.mygld.aimocker.AiMocker;
import top.mygld.aimocker.anno.AiMock;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AiMockParameterResolver implements ParameterResolver {

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(AiMock.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        AiMock annotation = parameterContext.findAnnotation(AiMock.class)
                .orElseThrow(() -> new ParameterResolutionException("@AiMock annotation not found"));

        Class<?> parameterType = parameterContext.getParameter().getType();
        int count = annotation.count();
        String scenario = annotation.value();

        try {
            // ----------------- Collection -----------------
            if (Collection.class.isAssignableFrom(parameterType)) {
                Class<?> elementType = getCollectionElementType(parameterContext);
                Collection<Object> collection = createCollectionInstance(parameterType);

                List<CompletableFuture<?>> futures = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    futures.add(AiMocker.createAsync(elementType, scenario));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                for (CompletableFuture<?> future : futures) {
                    collection.add(future.join()); // get actual object
                }
                return collection;
            }

            // ----------------- Array -----------------
            if (parameterType.isArray()) {
                Class<?> elementType = parameterType.getComponentType();
                Object array = Array.newInstance(elementType, count);

                List<CompletableFuture<?>> futures = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    futures.add(AiMocker.createAsync(elementType, scenario));
                }

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                for (int i = 0; i < count; i++) {
                    Array.set(array, i, futures.get(i).join());
                }
                return array;
            }

            // ----------------- Single object -----------------
            return AiMocker.createAsync(parameterType, scenario).join();

        } catch (Exception e) {
            throw new ParameterResolutionException("Failed to generate mock data", e);
        }
    }

    /**
     * Retrieve the generic type of a Collection parameter.
     * Defaults to Object.class if generic type is not specified.
     */
    private Class<?> getCollectionElementType(ParameterContext parameterContext) {
        Type genericType = parameterContext.getParameter().getParameterizedType();
        if (genericType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        return Object.class;
    }

    /**
     * Create a concrete Collection instance based on the parameter type.
     * Uses ArrayList for List types and HashSet for Set types.
     */
    private Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (Set.class.isAssignableFrom(collectionType)) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }

}
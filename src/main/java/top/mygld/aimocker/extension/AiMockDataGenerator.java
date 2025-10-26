package top.mygld.aimocker.extension;

import top.mygld.aimocker.AiMocker;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Core data generation logic for AI mock objects.
 * Handles single objects, collections, and arrays.
 */
class AiMockDataGenerator {

    /**
     * Generate mock data based on type and annotation parameters.
     *
     * @param targetType the type to generate
     * @param genericType the generic type information (for collections)
     * @param scenario the AI scenario description
     * @param count number of items to generate (for collections/arrays)
     * @return generated mock data
     */
    public static Object generate(Class<?> targetType, Type genericType, String scenario, int count) {
        // ----------------- Collection -----------------
        if (Collection.class.isAssignableFrom(targetType)) {
            Class<?> elementType = extractElementType(genericType);
            Collection<Object> collection = createCollectionInstance(targetType);

            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                futures.add(AiMocker.createAsync(elementType, scenario));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            for (CompletableFuture<?> future : futures) {
                collection.add(future.join());
            }
            return collection;
        }

        // ----------------- Array -----------------
        if (targetType.isArray()) {
            Class<?> elementType = targetType.getComponentType();
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
        return AiMocker.createAsync(targetType, scenario).join();
    }

    /**
     * Extract the element type from a generic type.
     * Returns Object.class if generic type cannot be determined.
     */
    private static Class<?> extractElementType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            if (typeArgs.length == 1 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        return Object.class;
    }

    /**
     * Create a concrete Collection instance based on the type.
     * Uses ArrayList for List types and HashSet for Set types.
     */
    private static Collection<Object> createCollectionInstance(Class<?> collectionType) {
        if (Set.class.isAssignableFrom(collectionType)) {
            return new HashSet<>();
        }
        return new ArrayList<>();
    }
}
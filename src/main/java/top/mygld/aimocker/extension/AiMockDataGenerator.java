package top.mygld.aimocker.extension;

import top.mygld.aimocker.AiMocker;
import top.mygld.aimocker.util.CacheUtil;
import top.mygld.aimocker.util.HashUtil;
import top.mygld.aimocker.util.JsonUtil;

import java.io.File;
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
    public static Object generate(Class<?> targetType, Type genericType, String scenario, int count, boolean cache) {
        // ----------------- Collection -----------------
        if (Collection.class.isAssignableFrom(targetType)) {
            Class<?> elementType = extractElementType(genericType);
            Collection<Object> collection = createCollectionInstance(targetType);

            int realCount = count;

            if(cache){
                List<?> cacheList = CacheUtil.readCacheCollection(elementType,scenario,count);

                if(cacheList != null){
                    for(Object cacheObject : cacheList){
                        collection.add(cacheObject);
                    }
                    realCount = realCount - collection.size();
                }
            }

            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (int i = 0; i < realCount; i++) {
                futures.add(AiMocker.createAsync(elementType, scenario));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            for (CompletableFuture<?> future : futures) {
                Object obj = future.join();
                collection.add(obj);

                if(cache){
                    CacheUtil.writeCache(elementType,scenario,obj);
                }

            }
            return collection;
        }

        // ----------------- Array -----------------
        if (targetType.isArray()) {
            Class<?> elementType = targetType.getComponentType();
            Object array = Array.newInstance(elementType, count);

            int realCount = count;

            int cacheSize = 0;

            if(cache){
                List<?> cacheList = CacheUtil.readCacheCollection(elementType,scenario,count);

                if(cacheList != null){

                    for(int i = 0;i < cacheList.size();i++){
                        Array.set(array,i,cacheList.get(i));
                    }

                    cacheSize = cacheList.size();
                    realCount = realCount - cacheList.size();
                }
            }

            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (int i = 0; i < realCount; i++) {
                futures.add(AiMocker.createAsync(elementType, scenario));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            for (int i = cacheSize; i < count; i++) {
                Object obj = futures.get(i - cacheSize).join();
                Array.set(array, i, obj);

                if(cache){
                    CacheUtil.writeCache(elementType,scenario,obj);
                }
            }
            return array;
        }

        // ----------------- Single object -----------------
        Object obj = null;

        if(cache){
            CacheUtil.readCache(targetType,scenario);
        }

        if(obj == null){
            obj = AiMocker.createAsync(targetType, scenario).join();
        }

        if(cache){
            CacheUtil.writeCache(targetType,scenario,obj);
        }
        return obj;
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
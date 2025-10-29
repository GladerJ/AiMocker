package top.mygld.aimocker.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import top.mygld.aimocker.exception.AiMockerException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * JSON serialization utility using Jackson.
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Object to JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new AiMockerException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * JSON string to Object.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new AiMockerException("Failed to deserialize JSON to " + clazz.getName(), e);
        }
    }

    /**
     * JSON string to generic type (for List<User>, etc.).
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (IOException e) {
            throw new AiMockerException("Failed to deserialize JSON", e);
        }
    }

    /**
     * Get the ObjectMapper instance for advanced usage.
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static void writeJson(String filePath, Object object) {
        filePath = PathUtil.resolve(filePath);

        File file = new File(filePath);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                throw new RuntimeException("Failed to create parent directory: " + parentDir);
            }
        }
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            MAPPER.writeValue(writer, object);
        } catch (IOException e) {
            throw new RuntimeException("Write JSON to file failed: " + filePath, e);
        }
    }

    public static <T> T readJson(String filePath, Class<T> clazz) {
        filePath = PathUtil.resolve(filePath);

        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            return MAPPER.readValue(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Read JSON from file failed: " + filePath, e);
        }
    }

}
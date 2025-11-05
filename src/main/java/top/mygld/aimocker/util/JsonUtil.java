package top.mygld.aimocker.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import top.mygld.aimocker.adapter.impl.InstantAdapter;
import top.mygld.aimocker.adapter.impl.LocalDateAdapter;
import top.mygld.aimocker.adapter.impl.LocalDateTimeAdapter;
import top.mygld.aimocker.adapter.impl.LocalTimeAdapter;
import top.mygld.aimocker.exception.AiMockerException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * JSON serialization utility using Jackson.
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(Instant.class, new InstantAdapter())
            .create();

    /**
     * Object to JSON string.
     */
    public static String toJson(Object obj) {
        try {
            return GSON.toJson(obj);
        } catch (Exception e) {
            throw new AiMockerException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * JSON string to Object.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            throw new AiMockerException("Failed to deserialize JSON to " + clazz.getName(), e);
        }
    }

    /**
     * JSON string to generic type (for List<User>, etc.).
     */
    public static <T> T fromJson(String json, TypeToken<T> typeToken) {
        try {
            return GSON.fromJson(json, typeToken);
        } catch (Exception e) {
            throw new AiMockerException("Failed to deserialize JSON", e);
        }
    }

    /**
     * Get the ObjectMapper instance for advanced usage.
     */
    public static Gson getGSON() {
        return GSON;
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
            writer.write(GSON.toJson(object));
        } catch (IOException e) {
            throw new RuntimeException("Write JSON to file failed: " + filePath, e);
        }
    }

    public static <T> T readJson(String filePath, Class<T> clazz) {
        filePath = PathUtil.resolve(filePath);

        try (Reader reader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Read JSON from file failed: " + filePath, e);
        }
    }


}
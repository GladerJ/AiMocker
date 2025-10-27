package top.mygld.aimocker.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * ResourceUtil is a utility class for loading configuration files from the classpath (resources directory).
 * It supports .properties and .yml/.yaml files.
 * </p>
 *
 * <p>
 * This class provides static methods to read resource files via the class loader and return either an InputStream
 * or parsed objects.
 * </p>
 *
 *
 * <p>
 * This utility is suitable for projects running in IDE or packaged in a JAR.
 * </p>
 *
 * @author Glader
 * @since 1.0
 */
public class ResourceUtil {

    /**
     * Private constructor to prevent instantiation.
     */
    private ResourceUtil() {
        throw new UnsupportedOperationException("ResourceUtil is a utility class and cannot be instantiated");
    }

    /**
     * Retrieves an InputStream for a resource file from the classpath.
     *
     * @param fileName the file name relative to the resources root
     * @return an InputStream for the file; returns null if the file does not exist
     */
    public static InputStream getStream(String fileName) {
        ClassLoader classLoader = ResourceUtil.class.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream(fileName);
        return stream;
    }

    public static Map<String,String> getProperties(String fileName) throws IOException {
        InputStream is = getStream(fileName);

        if(is == null) return null;

        Properties prop = new Properties();
        try (InputStream input = is) {
            prop.load(input);
        }

        Map<String,String> map = new HashMap<>();
        for (String name : prop.stringPropertyNames()) {
            map.put(name, prop.getProperty(name));
        }

        return map;
    }

    public static Map<String, String> getYaml(String fileName, String rootPath) throws IOException {
        InputStream is = getStream(fileName);
        if (is == null) return null;
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = yamlMapper.readTree(is);
        if (rootPath != null && !rootPath.isEmpty()) {
            String[] parts = rootPath.split("\\.");
            for (String part : parts) {
                root = root.path(part);
                if (root.isMissingNode()) {
                    return null;
                }
            }
        }
        Map<String, String> map = new HashMap<>();
        String prefix = rootPath != null && !rootPath.isEmpty() ? rootPath : "";
        parseYamlNode(prefix, root, map);
        return map;
    }

    private static void parseYamlNode(String prefix, JsonNode node, Map<String, String> map) {
        if (node.isObject()) {
            node.fieldNames().forEachRemaining(field -> {
                JsonNode child = node.get(field);
                String key = prefix.isEmpty() ? field : prefix + "." + field;
                parseYamlNode(key, child, map);
            });
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String key = prefix + "[" + i + "]";
                parseYamlNode(key, node.get(i), map);
            }
        } else if (node.isValueNode()) {
            map.put(prefix, node.asText());
        }
    }



    public static void main(String[] args) throws IOException {
        System.out.println(getYaml("application.yml","aimocker"));
    }
}
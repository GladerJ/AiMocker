package top.mygld.aimocker.util;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return classLoader.getResourceAsStream(fileName);
    }

    public static Map<String, String> getProperties(String fileName) throws IOException {
        InputStream is = getStream(fileName);
        if (is == null) return null;

        Properties prop = new Properties();
        try (InputStream input = is) {
            prop.load(input);
        }

        Map<String, String> map = new HashMap<>();
        for (String name : prop.stringPropertyNames()) {
            map.put(name, resolveValue(prop.getProperty(name)));
        }
        return map.isEmpty() ? null : map;
    }

    public static Map<String, String> getYaml(String fileName, String rootPath) throws IOException {
        InputStream is = getStream(fileName);
        if (is == null) return null;

        Yaml yaml = new Yaml();
        Map<String, Object> yamlData = yaml.load(is);

        if (yamlData == null) return null;

        Object root = yamlData;
        if (rootPath != null && !rootPath.isEmpty()) {
            String[] parts = rootPath.split("\\.");
            for (String part : parts) {
                if (root instanceof Map) {
                    root = ((Map<?, ?>) root).get(part);
                    if (root == null) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }

        Map<String, String> map = new HashMap<>();
        String prefix = rootPath != null && !rootPath.isEmpty() ? rootPath : "";
        parseYamlNode(prefix, root, map);
        return map.isEmpty() ? null : map;
    }

    private static void parseYamlNode(String prefix, Object node, Map<String, String> map) {
        if (node instanceof Map) {
            Map<?, ?> mapNode = (Map<?, ?>) node;
            mapNode.forEach((key, value) -> {
                String keyStr = prefix.isEmpty() ? key.toString() : prefix + "." + key;
                parseYamlNode(keyStr, value, map);
            });
        } else if (node instanceof List) {
            List<?> listNode = (List<?>) node;
            for (int i = 0; i < listNode.size(); i++) {
                String key = prefix + "[" + i + "]";
                parseYamlNode(key, listNode.get(i), map);
            }
        } else if (node != null) {
            map.put(prefix, resolveValue(node.toString()));
        }
    }

    private static String resolveValue(String value) {
        Pattern pattern = Pattern.compile("^\\$\\{(.+?)\\}$");
        Matcher matcher = pattern.matcher(value);
        if(matcher.matches()){
            return System.getenv(matcher.group(1));
        }
        return value;
    }

    public static void main(String[] args) throws IOException {
         System.out.println(getYaml("aimocker.yml", "llm"));
    }
}
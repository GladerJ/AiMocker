package top.mygld.aimocker.internal;

import java.lang.reflect.Field;

public class PromptBuilder {

    public static <T> String build(Class<T> targetClass, String scenario) {
        StringBuilder fieldsList = new StringBuilder();
        buildFields(targetClass, fieldsList, 0);

        return "You are an expert Java JSON data generator.\n" +
                "Generate a single, valid JSON object representing an instance of the '" + targetClass.getSimpleName() + "' class.\n\n" +
                "## Class Fields:\n" +
                fieldsList.toString() +
                "\n## Scenario:\n" +
                scenario + "\n\n" +
                "## Rules:\n" +
                "- The output MUST be only the JSON object, without any extra text or markdown.\n" +
                "- All field names in the JSON must exactly match the Java class fields listed above.\n" +
                "- Provide realistic and contextually appropriate values based on the scenario.\n" +
                "- All fields must be present in the output, even if the value is null or empty.\n\n" +
                "Generate the JSON object now.";
    }

    private static void buildFields(Class<?> clazz, StringBuilder builder, int indent) {
        Field[] fields = clazz.getDeclaredFields();
        String prefix = "  ".repeat(indent);

        for (Field field : fields) {
            Class<?> type = field.getType();
            if (isPrimitiveOrWrapper(type) || type == String.class) {
                builder.append(prefix).append("- ").append(field.getName())
                        .append(" (").append(type.getSimpleName()).append(")\n");
            } else {
                builder.append(prefix).append("- ").append(field.getName())
                        .append(" (").append(type.getSimpleName()).append(")\n");
                buildFields(type, builder, indent + 1);
            }
        }
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
                type == Integer.class ||
                type == Long.class ||
                type == Boolean.class ||
                type == Byte.class ||
                type == Short.class ||
                type == Float.class ||
                type == Double.class ||
                type == Character.class;
    }
}

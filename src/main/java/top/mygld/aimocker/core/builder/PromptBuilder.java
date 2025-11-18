package top.mygld.aimocker.core.builder;

import top.mygld.aimocker.util.SimpleTypeUtil;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;

public class PromptBuilder {
    public static <T> String build(Class<T> targetClass, String scenario) {
        StringBuilder fieldsList = new StringBuilder();
        buildFields(targetClass, fieldsList, 0);

       // System.out.println(fieldsList.toString());

        return "You are an expert JSON generator for Java models.\n" +
                "Generate a single, valid JSON object for class '" + targetClass.getSimpleName() + "'.\n\n" +

                "## Class Fields:\n" +
                fieldsList.toString() +

                "\n## Scenario:\n" +
                scenario + "\n\n" +

                "## STRICT RULES:\n" +
                "- OUTPUT MUST BE ONLY VALID JSON (no comments or extra text).\n" +
                "- All fields must be present (null allowed).\n" +
                "- All strings must be one-line JSON strings.\n" +
                "- All collections must be JSON arrays.\n" +
                "- DO NOT include fields not in the class.\n\n" +

                "### Date and Time Format Rules (VERY IMPORTANT):\n" +
                "- For `java.util.Date`: use \"yyyy-MM-dd'T'HH:mm:ssXXX\" (MUST include timezone, e.g. +08:00).\n" +
                "- For `java.time.LocalDateTime`: use \"yyyy-MM-dd'T'HH:mm:ss\" (NO timezone allowed).\n" +
                "- For `java.time.LocalDate`: use \"yyyy-MM-dd\".\n" +
                "- For `java.time.LocalTime`: use \"HH:mm:ss\".\n\n" +

                "DO NOT add timezone for LocalDateTime.\n" +
                "DO NOT omit timezone for java.util.Date.\n\n" +

                "Generate ONLY the JSON object now.";

    }

    private static void buildFields(Class<?> clazz, StringBuilder builder, int indent) {
        Field[] fields = clazz.getDeclaredFields();
        String prefix = String.join("", Collections.nCopies(indent, "  "));

        for (Field field : fields) {
            Class<?> type = field.getType();
            if (SimpleTypeUtil.isPrimitiveOrWrapper(type) || type == String.class) {
                builder.append(prefix).append("- ").append(field.getName())
                        .append(" (").append(type.getSimpleName()).append(")\n");
            } else {
                builder.append(prefix).append("- ").append(field.getName())
                        .append(" (").append(type.getSimpleName()).append(")\n");
                if(!Collection.class.isAssignableFrom(type)) buildFields(type, builder, indent + 1);
            }
        }
    }

}
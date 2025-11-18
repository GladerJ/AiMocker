package top.mygld.aimocker.util;

import top.mygld.aimocker.adapter.SimpleTypeAdapter;

import java.util.*;

public final class SimpleTypeUtil {

    private static final List<SimpleTypeAdapter> ADAPTERS = new ArrayList<>();

    static {
        ServiceLoader<SimpleTypeAdapter> loader = ServiceLoader.load(SimpleTypeAdapter.class);
        for (SimpleTypeAdapter adapter : loader) {
            ADAPTERS.add(adapter);
        }
    }

    private SimpleTypeUtil() {
    }


    public static boolean isPrimitiveOrWrapper(Class<?> type) {
        if (type == null) {
            return false;
        }
        for (SimpleTypeAdapter adapter : ADAPTERS) {
            if (adapter.isPrimitiveOrWrapper(type)) {
                return true;
            }
        }
        return false;
    }

    public static List<SimpleTypeAdapter> getAllAdapters() {
        return Collections.unmodifiableList(ADAPTERS);
    }

}
package top.mygld.aimocker.adapter;

/**
 * Judge a class if it is a simple class.
 */
public interface SimpleTypeAdapter {
    boolean isPrimitiveOrWrapper(Class<?> type);
}

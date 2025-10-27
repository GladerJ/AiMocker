package top.mygld.aimocker.enums;

public enum CacheStrategy {

    /**
     * No cache, use AI every time.
     */
    NONE,

    /**
     * Cache only for the current test session.
     */
    SESSION,

    /**
     * Persist to local file.
     */
    LOCAL,

    /**
     * Automatic judgment (default).
     */
    AUTO
}
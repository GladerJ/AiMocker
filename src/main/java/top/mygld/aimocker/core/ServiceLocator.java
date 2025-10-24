package top.mygld.aimocker.core;

import top.mygld.aimocker.adapter.impl.LanguageModelAdapter;

import java.util.ServiceLoader;

/**
 * Internal utility to locate and load the LanguageModelAdapter service.
 */
public final class ServiceLocator {
    private ServiceLocator() {}

    public static LanguageModelAdapter loadAdapter() {
        return ServiceLoader.load(LanguageModelAdapter.class)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No LanguageModelAdapter implementation found on the classpath. " +
                                "Please add an adapter dependency, e.g., 'aimocker-langchain4j-adapter'."
                ));
    }
}
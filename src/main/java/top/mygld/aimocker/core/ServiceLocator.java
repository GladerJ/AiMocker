package top.mygld.aimocker.core;

import top.mygld.aimocker.adapter.LanguageModelAdapter;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Internal utility to locate and load the LanguageModelAdapter service.
 */
public final class ServiceLocator {
    private ServiceLocator() {}

    public static LanguageModelAdapter loadAdapter() {
        ServiceLoader<LanguageModelAdapter> locator = ServiceLoader.load(LanguageModelAdapter.class);
        Iterator<LanguageModelAdapter> iterator = locator.iterator();

        if(iterator.hasNext()) {
            return iterator.next();
        }

        throw new IllegalStateException(
                "No LanguageModelAdapter implementation found on the classpath. " +
                        "Please add an adapter dependency, e.g., 'aimocker-langchain4j-adapter'."
        );
    }

}
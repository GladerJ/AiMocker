package top.mygld.aimocker.spi;

/**
 * Service Provider Interface (SPI) for language model communication.
 * This is the bridge between the AiMocker core and any specific AI model implementation.
 */
public interface LanguageModelAdapter {
    /**
     * Generates a response from the language model based on the given prompt.
     * @param prompt The complete prompt to send to the AI model.
     * @return The raw string response from the model, expected to be a JSON object.
     */
    String generate(String prompt);
}
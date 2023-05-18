package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import org.gradle.api.provider.Property;

/**
 * @author Roman Shageev
 * @since 19.05.2023
 */
public abstract class MessageSource {
    private final String name;

    public MessageSource(String name) {
        this.name = name;
        getValidateLocalizations().convention(true);
        getValidateParameters().convention(true);
        getEnablePadding().convention(true);
        getDefaultLocale().convention("ru");
        getEnabled().convention(false);
        getOutputDirectory().convention("");
    }

    public String getName() {
        return name;
    }

    public abstract Property<Boolean> getEnabled();
    public abstract Property<String> getSourceYmlFile();
    public abstract Property<String> getOutputDirectory();
    public abstract Property<String> getBaseFileName();
    public abstract Property<String> getDefaultLocale();
    public abstract Property<Boolean> getValidateLocalizations();
    public abstract Property<Boolean> getValidateParameters();
    public abstract Property<Boolean> getEnablePadding();
}

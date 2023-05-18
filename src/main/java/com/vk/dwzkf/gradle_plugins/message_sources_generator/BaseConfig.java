package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.provider.Property;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
public abstract class BaseConfig {
    public abstract Property<Boolean> getEnabled();
    public abstract Property<String> getResourcesPath();
    public abstract Property<String> getOutputDirectory();
    public abstract NamedDomainObjectContainer<MessageSource> getMessageSources();

    public BaseConfig() {
        getEnabled().convention(true);
        getResourcesPath().convention("src/main/resources");
        getOutputDirectory().convention("");
    }
}

package com.vk.dwzkf.gradle_plugins.ex_messages;

import org.gradle.api.provider.Property;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
public abstract class ExMessagesCfg {
    public abstract Property<Boolean> getEnabled();
    public abstract Property<String> getSourceYmlFile();
    public abstract Property<String> getOutputDirectory();
    public abstract Property<String> getBaseFileName();
    public abstract Property<String> getDefaultLocale();
    public abstract Property<String> getResourcesPath();
    public abstract Property<Boolean> getValidateLocalizations();
    public abstract Property<Boolean> getValidateParameters();
    public abstract Property<Boolean> getEnablePadding();


    public ExMessagesCfg() {
        getValidateLocalizations().convention(true);
        getValidateParameters().convention(true);
        getEnablePadding().convention(true);
        getDefaultLocale().convention("ru");
        getResourcesPath().convention("src/main/resources");
        getBaseFileName().convention("exception_messages");
        getEnabled().convention(false);
    }
}

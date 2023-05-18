package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessagesContext {
    private String defaultLocale;
    private Set<String> definedLocales;
    private Map<String, Map<String, String>> locale2message;
}

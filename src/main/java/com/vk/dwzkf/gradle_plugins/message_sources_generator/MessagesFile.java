package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import lombok.*;

import java.util.Map;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessagesFile {
    private String fileName;
    private Map<String, String> messages;
}

package com.vk.dwzkf.gradle_plugins.ex_messages;

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
public class ExceptionMessageFile {
    private String fileName;
    private Map<String, String> messages;
}

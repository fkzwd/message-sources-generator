package com.vk.dwzkf.gradle_plugins.ex_messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.gradle.api.Project;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
@RequiredArgsConstructor
@Getter
public class ExMessageContextBuilder {
    private final Project project;
    private final String defaultLocale;
    private final boolean validateAllLocalizationsExists;
    private final boolean validateParametersMatches;

    public ExMessageContext build(
            Map<String, Map<String, String>> message2localizations
    ) {
        Map<String, Map<String, String>> locale2messages = new LinkedHashMap<>();

        Set<String> definedLocalizations = new LinkedHashSet<>();
        for (Map.Entry<String, Map<String, String>> entry : message2localizations.entrySet()) {
            Set<String> localizations = entry.getValue().keySet();
            definedLocalizations.addAll(localizations);
        }
        project.getLogger().info("Defined localizations:" + definedLocalizations);
        if (validateAllLocalizationsExists) {
            validateAllLocalizationsExists(message2localizations, definedLocalizations);
        }
        if (validateParametersMatches) {
            validateParametersMatches(message2localizations);
        }

        for (Map.Entry<String, Map<String, String>> entry : message2localizations.entrySet()) {
            String messageKey = entry.getKey();
            for (Map.Entry<String, String> locale2message : entry.getValue().entrySet()) {
                locale2messages.computeIfAbsent(locale2message.getKey(), (k) -> new LinkedHashMap<>())
                        .put(messageKey, locale2message.getValue());
            }
        }
        String messagesFound = locale2messages.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue().size())
                .collect(Collectors.joining(",", "[", "]"));
        project.getLogger().info("Prepare of messages end. Messages found:" + messagesFound);
        return new ExMessageContext(defaultLocale, definedLocalizations, locale2messages);
    }

    private final Pattern placeHolderPattern = Pattern.compile("\\{[^}]+\\}");

    private void validateParametersMatches(Map<String, Map<String, String>> message2localizations) {
        for (Map.Entry<String, Map<String, String>> entry : message2localizations.entrySet()) {
            String messageKey = entry.getKey();
            Map<String, Set<String>> locale2placeholders = new LinkedHashMap<>();
            Set<String> definedParameters = new LinkedHashSet<>();
            for (Map.Entry<String, String> locale2message : entry.getValue().entrySet()) {
                Set<String> parameters = locale2placeholders.computeIfAbsent(
                        locale2message.getKey(),
                        k -> new LinkedHashSet<>()
                );
                parameters.addAll(extractParameters(locale2message));
                definedParameters.addAll(parameters);
            }
            for (Map.Entry<String, String> locale2message : entry.getValue().entrySet()) {
                Set<String> localeParameters = extractParameters(locale2message);
                if (!localeParameters.containsAll(definedParameters)) {
                    throw new IllegalStateException(
                            String.format(
                                    "Message:%s defined parameters:%s but locale:%s only found parameters:%s",
                                    messageKey,
                                    definedParameters,
                                    locale2message.getKey(),
                                    localeParameters
                            )
                    );
                }
            }
        }
    }

    private Set<String> extractParameters(Map.Entry<String, String> locale2message) {
        return placeHolderPattern.matcher(locale2message.getValue()).results()
                .map(r -> locale2message.getValue().substring(r.start()+1, r.end()-1))
                .collect(Collectors.toSet());
    }

    private void validateAllLocalizationsExists(
            Map<String, Map<String, String>> message2localizations,
            Set<String> definedLocalizations
    ) {
        for (Map.Entry<String, Map<String, String>> entry : message2localizations.entrySet()) {
            if (entry.getValue().keySet().size() != definedLocalizations.size()) {
                Set<String> lostLocalizations = new LinkedHashSet<>(definedLocalizations);
                lostLocalizations.removeAll(entry.getValue().keySet());
                throw new IllegalStateException(String.format(
                        "Message '%s' lost localization:%s, expected:%s, actual:%s",
                        entry.getKey(),
                        lostLocalizations,
                        definedLocalizations,
                        entry.getValue().keySet()
                ));
            }
        }
    }
}

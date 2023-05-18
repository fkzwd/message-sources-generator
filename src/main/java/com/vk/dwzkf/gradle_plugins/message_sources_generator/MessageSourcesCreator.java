package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
public class MessageSourcesCreator {
    private final Task task;
    private final Project project;
    private final MessageSource cfg;
    private final String projectPath;
    private final BaseConfig extCfg;
    private final MessagesContextBuilder contextBuilder;

    public MessageSourcesCreator(
            Task task,
            Project project,
            MessageSource messageSource,
            BaseConfig extCfg
    ) {
        this.task = Objects.requireNonNull(task, "Task cannot be null");
        this.project = Objects.requireNonNull(project, "Project cannot be null");
        this.cfg = Objects.requireNonNull(messageSource, "Cfg cannot be null");
        this.projectPath = project.getProjectDir().getAbsolutePath();
        this.contextBuilder = new MessagesContextBuilder(
                project,
                cfg.getDefaultLocale().get(),
                cfg.getValidateLocalizations().get(),
                cfg.getValidateParameters().get()
        );
        this.extCfg = extCfg;
    }

    public void createProperties() {
        File srcFile = getFile();
        Map<String, Map<String, String>> message2localizations = readYaml(srcFile);
        MessagesContext ctx = contextBuilder.build(message2localizations);
        List<MessagesFile> outputFiles = buildFiles(ctx);
        project.getLogger()
                .info("Output files:" +
                        outputFiles.stream()
                                .map(MessagesFile::getFileName)
                                .collect(Collectors.joining(","))
                );
        outputFiles.forEach(this::createOutputFile);
    }

    private void createOutputFile(MessagesFile outputFile) {
        Path outputFilePath = Paths.get(
                projectPath,
                extCfg.getResourcesPath().get(),
                extCfg.getOutputDirectory().get(),
                cfg.getOutputDirectory().get(),
                outputFile.getFileName()
        );
        String format;
        if (cfg.getEnablePadding().get()) {
            int minSize = outputFile.getMessages().keySet().stream()
                    .max(Comparator.comparing(String::length))
                    .map(String::length)
                    .orElseThrow(() -> new IllegalStateException("No messages found"));
            format = "%-" + minSize + "s=%s";
        } else {
            format = "%s=%s";
        }

        List<String> outputStrings = outputFile.getMessages().entrySet().stream()
                .map(e -> String.format(format, e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        try {
            Files.createDirectories(outputFilePath.getParent());
            project.getLogger().info("Create/Rewrite file:" + outputFilePath.toString());
            Files.write(
                    outputFilePath,
                    outputStrings,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            project.getLogger().error("Error writing to file " + outputFilePath, e);
            throw new IllegalStateException("Error writing to file " + outputFilePath, e);
        }
    }

    private List<MessagesFile> buildFiles(MessagesContext ctx) {
        List<MessagesFile> files = new ArrayList<>();
        String baseFileName = cfg.getBaseFileName().get();
        Map<String, Map<String, String>> locale2message = ctx.getLocale2message();
        MessagesFile defaultLocaleFile = new MessagesFile();
        defaultLocaleFile.setFileName(baseFileName+".properties");
        defaultLocaleFile.setMessages(locale2message.get(cfg.getDefaultLocale().get()));
        files.add(defaultLocaleFile);
        for (Map.Entry<String, Map<String, String>> entry : locale2message.entrySet()) {
            String locale = entry.getKey();
            MessagesFile file = new MessagesFile();
            String fileName = baseFileName + "_" + locale + ".properties";
            file.setFileName(fileName);
            file.setMessages(entry.getValue());
            files.add(file);
        }
        return files;
    }


    private Map<String, Map<String, String>> readYaml(File srcFile) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            JsonNode readed = mapper.readTree(srcFile);
            if (readed.size() == 0) {
                return new LinkedHashMap<>();
            }
            Map<String, Map<String, String>> result = mapper.convertValue(readed, new TypeReference<Map<String, Map<String, String>>>() {
            });
            project.getLogger().info("Found localization messages count:" + result.size());
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Unable to read file:" + srcFile.getAbsolutePath() + " cause:" + e.getMessage(), e);
        }
    }

    private File getFile() {
        Path srcFilePath = Paths.get(
                projectPath,
                extCfg.getResourcesPath().get(),
                cfg.getSourceYmlFile().get()
        );
        boolean fileExists = Files.isRegularFile(srcFilePath);
        if (!fileExists) {
            throw new IllegalArgumentException("Not found file: " + srcFilePath.toString());
        }
        return new File(srcFilePath.toAbsolutePath().toString());
    }
}

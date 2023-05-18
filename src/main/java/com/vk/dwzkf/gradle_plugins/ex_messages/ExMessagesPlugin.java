package com.vk.dwzkf.gradle_plugins.ex_messages;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
public class ExMessagesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        ExMessagesCfg ext = project.getExtensions()
                .create("exMessagesCfg", ExMessagesCfg.class);

        TaskProvider<Task> task0 = project.getTasks()
                .register("custom-test", task -> {
                    task.setDescription("custom task description");
                    task.setGroup("Custom tasks");
                    if (!ext.getEnabled().get()) {
                        project.getLogger().info("Message localization disabled, skipped.");
                        return;
                    }
                    task.doFirst(t -> {
                        new ExceptionPropertiesCreator(task, project, ext).createProperties();
                    });
                });
        project.afterEvaluate(objectConfigurationAction -> {
            project.getTasks().named("compileJava").configure(t -> {
                t.dependsOn(task0.getName());
            });
        });
    }
}

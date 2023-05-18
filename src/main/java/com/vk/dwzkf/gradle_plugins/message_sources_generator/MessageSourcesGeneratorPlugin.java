package com.vk.dwzkf.gradle_plugins.message_sources_generator;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Roman Shageev
 * @since 18.05.2023
 */
public class MessageSourcesGeneratorPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        BaseConfig ext = project.getExtensions()
                .create("messageSourcesCfg", BaseConfig.class);

        TaskProvider<Task> task0 = project.getTasks()
                .register("generateMessageBundles", task -> {
                    task.setDescription("Generates message bundles from yml file");
                    task.setGroup("Generators");
                    if (!ext.getEnabled().get()) {
                        project.getLogger().info("Message localization disabled, skipped.");
                        return;
                    }
                    task.doFirst(t -> {
                        int idx = 0;
                        for (MessageSource messageSource : ext.getMessageSources()) {
                            if (messageSource.getEnabled().get()) {
                                new MessageSourcesCreator(task, project, messageSource, ext)
                                        .createProperties();
                            } else {
                                project.getLogger().info("Message source "+messageSource.getName()+" is disabled. Skipped.");
                            }
                            idx++;
                        }
                    });
                });
        project.afterEvaluate(objectConfigurationAction -> {
            project.getTasks().named("compileJava").configure(t -> {
                t.dependsOn(task0.getName());
            });
        });
    }
}

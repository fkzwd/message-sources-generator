опубликуйте в своем артифактори
```groovy
artifactory {
    contextUrl = '${artifactory-url}'
    publish {
        repository {
            repoKey = '${repo}'
            username = '${user}'
            password = '${password}'
            maven = true
        }
        defaults {
            publications(publishing.publications.names.toArray())
        }
    }
}
```



Добавьте в build.gradle
```groovy
plugins {
//...
    id 'com.vk.dwzkf.gradle-plugins.message-sources-generator' version '1.0'
}
```
 и в самом низу build.gradle
```groovy
messageSourcesCfg {
    outputDirectory = 'i18n' //default is "" //директория куда сгенерятся проперти файлы
    resourcesPath = 'src/main/resources' //default //директория ресурсов
    enabled = true //default true
    messageSources {
        //можно добавить несколько конфигураций
        //поведение при конфликтующих не тестировалось
        register('validation-error-messages') {
            enabled = true //default is false
            sourceYmlFile = 'message-bundles/message_bundle.yml' //исходный файл
            baseFileName = 'message_bundle' //базовое имя файла для генерации
            defaultLocale = 'ru' //default //дефолтная локаль
            enablePadding = true //default //добавить выравнивание к проперти файлу
            validateLocalizations = true //default //для каждого сообщения 
                                                   //заданы все возможные локали встреченные в файле
            validateParameters = true //default //валидирует что в каждой локали для данного сообщения
                                                //использованы одинаковые параметры
        }
    }
}
```

имеем исходный src/main/resources/message-bundles/message_bundle.yml
```yaml
test_message1:
  ru: "Сообщение {0}"
  en: "Message {0}"
msg2:
  ru: "Сообщение2"
  en: "Message2"
```

генерятся файлы ***src/main/resources/i18n/***:

- **message_bundle.properties:**
```properties
test_message1="Сообщение {0}"
msg2         ="Сообщение2"
```
- **message_bundle_ru.properties:**
```properties
test_message1="Сообщение {0}"
msg2         ="Сообщение2"
```
- **message_bundle_en.properties:**
```properties
test_message1="Message {0}"
msg2         ="Message2"
```
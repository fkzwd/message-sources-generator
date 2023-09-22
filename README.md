# Предназначение

Данный плагин для *gradle* предназначен для упрощения работы с локализацией **message-sources**<br/>
- Он позволяет определять все мессаджи в одном *.yml* файле и <br/>на его основе генерирует необходимые *.properties* файлы
- Проводит проверку на соответствие параметров для каждой локализации в каждом сообщении
- Проводит проверку на наличие сообщений для каждой из определенных в рамках *.yml* файла локализаций 

## Для использования плагина
### склонируйте репозиторий к себе
### опубликуйте в своем артифактори:<br/>
- для этого в корне проекта создайте файл `private_publish.gradle`
- в него внесите такой блок
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
- запустите таску `gradlew artifactoryPublish`

## Использование плагина
- Добавьте в settings.gradle
```groovy
pluginManagement {
    repositories {
        maven {
            url '${your-repo-url}'
            // allowInsecureProtocol = true // if needed
        }
    }
}
```
- Добавьте в build.gradle
```groovy
plugins {
//...
    id 'com.vk.dwzkf.gradle-plugins.message-sources-generator' version '1.0'
}
```
- добавьте в самом низу build.gradle
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
## Usage
имеем исходный src/main/resources/message-bundles/message_bundle.yml
```yaml
test_message1:
  ru: "Сообщение {0}"
  en: "Message {0}"
msg2:
  ru: "Сообщение2"
  en: "Message2"
```
плагин настроен так что перед таской `compileJava` происходит генерация файлов.<br/>
Либо можно запустить вручную `gradlew generateMessageBundles`.<br/>

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

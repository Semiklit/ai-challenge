# AI Challenge

Kotlin CLI проект для изучения работы с AI. Интерактивный чат с поддержкой OpenAI-совместимых API.

## Быстрый старт

### 1. Настройка API ключа

Скопируйте файл с примером конфигурации:
```bash
cp local.properties.example local.properties
```

Откройте `local.properties` и добавьте ваш API ключ:
```properties
openai.api.key=your-api-key-here
openai.base.url=https://api.proxyapi.ru/openai/v1
openai.model=gpt-5-mini
```

### 2. Запуск чата

#### С системным промптом по умолчанию:
```bash
./chat.sh
```

#### С кастомным системным промптом:
```bash
./chat.sh Ты эксперт по программированию на Kotlin
```

Или:
```bash
./chat.sh "Ты дружелюбный помощник, который помогает изучать новые технологии"
```

### 3. Команды в чате

- Просто пишите сообщения для общения с AI
- `/exit` или `/quit` - выход из чата
- `/clear` - очистка истории диалога

## Разработка

### Сборка проекта
```bash
./gradlew build
```

### Запуск через Gradle
```bash
./gradlew run --args="Ваш системный промпт"
```

### Запуск тестов
```bash
./gradlew test
```

## Технологии

- **Kotlin** 2.1.0
- **Ktor Client** 3.2.3 для HTTP запросов
- **kotlinx.serialization** для работы с JSON
- **Gradle** 8.5 для сборки

## Структура проекта

```
├── src/main/kotlin/dev/nsemiklit/
│   ├── Main.kt       # Главный файл с CLI интерфейсом
│   └── Config.kt     # Конфигурация API
├── local.properties  # Секретные ключи (не в репозитории)
├── chat.sh          # Скрипт для удобного запуска
└── build.gradle.kts # Конфигурация сборки
```

## Лицензия

MIT

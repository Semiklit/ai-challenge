#!/bin/bash

# Скрипт для запуска AI чата

# Сборка проекта
./gradlew installDist --quiet

# Чтение local.properties и экспорт переменных
if [ -f "local.properties" ]; then
    export JAVA_OPTS=""
    while IFS='=' read -r key value; do
        # Пропускаем комментарии и пустые строки
        [[ $key =~ ^#.*$ ]] && continue
        [[ -z $key ]] && continue
        # Добавляем system property
        export JAVA_OPTS="$JAVA_OPTS -D$key=$value"
    done < local.properties
fi

# Запуск приложения с передачей всех аргументов
./build/install/ai-challenge/bin/ai-challenge "$@"

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

AI Challenge is a Kotlin project for exploring AI integration. It starts as a CLI tool and will evolve into an Android application. The project uses the **koog** library for AI interactions.

## Technology Stack

- **Language**: Kotlin 2.1.0
- **Build Tool**: Gradle 8.5 with Kotlin DSL
- **JVM**: Toolchain 17
- **HTTP Client**: Ktor 3.2.3 (OkHttp engine)
- **Serialization**: kotlinx-serialization-json 1.7.3
- **Future Platform**: Android

## Development Commands

### Build and Run
```bash
./chat.sh                              # Run interactive chat (recommended)
./chat.sh "System prompt"              # Run with custom system prompt
./chat.sh -t 0.5 "System prompt"       # Run with custom temperature
./gradlew run                          # Run via Gradle
./gradlew build                        # Build the project
./gradlew clean                        # Clean build artifacts
./gradlew installDist                  # Create distribution in build/install/
```

### Testing
```bash
./gradlew test               # Run all tests
./gradlew test --tests "ClassName.testMethod"  # Run a single test
./gradlew test --tests "com.example.*"         # Run tests in a package
```

### Gradle Tasks
```bash
./gradlew tasks              # List all available tasks
./gradlew dependencies       # Show dependency tree
```

## Project Structure

- **Main entry point**: `src/main/kotlin/dev/nsemiklit/Main.kt` - Interactive CLI chat
- **ChatClient class**: Manages conversation history and API communication
- **Package**: `dev.nsemiklit`
- **Configuration**: `src/main/kotlin/dev/nsemiklit/Config.kt` - AI API settings loaded from system properties
- **Launch script**: `chat.sh` - Convenience wrapper for running the app
- Tests follow standard Kotlin/JVM structure in `src/test/kotlin/`

## AI Configuration

API settings are stored in `local.properties` file (not committed to repository):
- **Endpoint**: https://api.proxyapi.ru/openai/v1
- **Model**: gpt-5-mini
- **API Key**: Stored securely in local.properties

To set up the project:
1. Copy `local.properties.example` to `local.properties`
2. Add your API key to `local.properties`
3. Run the project with `./gradlew run`

The project makes direct HTTP requests to OpenAI-compatible API using Ktor client with kotlinx-serialization for JSON handling. Configuration is loaded at runtime from system properties set by Gradle.

## CLI Usage

The application runs in interactive chat mode:
- System prompt is passed as command-line arguments
- Maintains conversation history for context
- Supports commands: `/exit`, `/quit`, `/clear`
- Temperature can be configured via command-line options

### Command-line options:
- `-t, --temperature <value>` - Set model temperature (0.0-2.0, default: 0.7)
- `--temperature=<value>` - Alternative format for temperature

### Examples:
```bash
./chat.sh You are a helpful coding assistant specialized in Kotlin
./chat.sh -t 0.5 Ты полезный ассистент
./chat.sh --temperature=1.2 Be creative and think outside the box
./chat.sh --temperature=0.1 -t will be ignored if --temperature= is used
```

Temperature controls randomness in responses:
- Lower values (0.0-0.3): More focused and deterministic
- Medium values (0.4-0.8): Balanced creativity
- Higher values (0.9-2.0): More creative and varied

## Migration Path

This project will transition from CLI to Android:
- Current phase: CLI tool development
- Future phase: Android application
- Keep koog integration portable between CLI and Android contexts
- Main application logic should be platform-agnostic where possible

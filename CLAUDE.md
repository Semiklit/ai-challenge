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
./gradlew run                # Run the CLI application
./gradlew build              # Build the project
./gradlew clean              # Clean build artifacts
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

- **Main entry point**: `src/main/kotlin/dev/nsemiklit/Main.kt` (currently `dev.nsemiklit.MainKt`)
- **Package**: `dev.nsemiklit`
- **Configuration**: `src/main/kotlin/dev/nsemiklit/Config.kt` - AI API settings
- Tests follow standard Kotlin/JVM structure in `src/test/kotlin/`

## AI Configuration

API settings are stored in `Config.kt`:
- **Endpoint**: https://api.proxyapi.ru/openai/v1
- **Model**: gpt-5-mini
- **API Key**: Stored in repository (Config.API_KEY)

The project makes direct HTTP requests to OpenAI-compatible API using Ktor client with kotlinx-serialization for JSON handling.

## Migration Path

This project will transition from CLI to Android:
- Current phase: CLI tool development
- Future phase: Android application
- Keep koog integration portable between CLI and Android contexts
- Main application logic should be platform-agnostic where possible

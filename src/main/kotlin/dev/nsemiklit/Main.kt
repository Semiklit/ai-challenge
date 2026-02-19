package dev.nsemiklit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>? = null,
    val error: ErrorDetail? = null
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class ErrorDetail(
    val message: String,
    val type: String? = null,
    val code: String? = null
)

data class CommandLineArgs(
    val systemPrompt: String,
    val temperature: Double,
    val model: String
)

fun parseArgs(args: Array<String>): CommandLineArgs {
    var temperature = 0.7
    var model = Config.MODEL
    val promptParts = mutableListOf<String>()

    var i = 0
    while (i < args.size) {
        val arg = args[i]
        when {
            arg.startsWith("--temperature=") -> {
                temperature = arg.substringAfter("=").toDoubleOrNull()
                    ?: throw IllegalArgumentException("Некорректное значение температуры: ${arg.substringAfter("=")}")
            }
            arg == "-t" || arg == "--temperature" -> {
                if (i + 1 >= args.size) {
                    throw IllegalArgumentException("Не указано значение для параметра $arg")
                }
                temperature = args[i + 1].toDoubleOrNull()
                    ?: throw IllegalArgumentException("Некорректное значение температуры: ${args[i + 1]}")
                i++ // Пропускаем следующий аргумент
            }
            arg.startsWith("--model=") -> {
                model = arg.substringAfter("=")
                if (model.isBlank()) {
                    throw IllegalArgumentException("Некорректное значение модели: пустая строка")
                }
            }
            arg == "-m" || arg == "--model" -> {
                if (i + 1 >= args.size) {
                    throw IllegalArgumentException("Не указано значение для параметра $arg")
                }
                model = args[i + 1]
                if (model.isBlank()) {
                    throw IllegalArgumentException("Некорректное значение модели: пустая строка")
                }
                i++ // Пропускаем следующий аргумент
            }
            else -> {
                promptParts.add(arg)
            }
        }
        i++
    }

    val systemPrompt = if (promptParts.isNotEmpty()) {
        promptParts.joinToString(" ")
    } else {
        "Ты полезный ассистент, который отвечает на русском языке."
    }

    // Проверка валидности температуры (обычно от 0 до 2)
    if (temperature < 0.0 || temperature > 2.0) {
        throw IllegalArgumentException("Температура должна быть в диапазоне от 0.0 до 2.0, получено: $temperature")
    }

    return CommandLineArgs(systemPrompt, temperature, model)
}

class ChatClient(
    private val systemPrompt: String,
    private val temperature: Double = 0.7,
    private val model: String = Config.MODEL
) {
    private val client = HttpClient(OkHttp) {
        engine {
            config {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(300, TimeUnit.SECONDS)  // 5 минут на ответ
                writeTimeout(30, TimeUnit.SECONDS)
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private val conversationHistory = mutableListOf<Message>()

    init {
        conversationHistory.add(Message("system", systemPrompt))
    }

    suspend fun sendMessage(userMessage: String): String {
        conversationHistory.add(Message("user", userMessage))

        val request = ChatRequest(
            model = model,
            messages = conversationHistory,
            temperature = temperature
        )

        val httpResponse: HttpResponse = client.post("${Config.BASE_URL}/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${Config.API_KEY}")
            setBody(request)
        }

        // Получаем сырой текст для отладки
        val rawResponse = httpResponse.bodyAsText()

        // Проверяем статус ответа
        if (!httpResponse.status.isSuccess()) {
            throw RuntimeException("HTTP ошибка ${httpResponse.status.value}: $rawResponse")
        }

        val response: ChatResponse = Json {
            ignoreUnknownKeys = true
        }.decodeFromString(rawResponse)

        // Обработка ошибок от API
        if (response.error != null) {
            throw RuntimeException("API ошибка: ${response.error.message} (type: ${response.error.type}, code: ${response.error.code})")
        }

        // Проверка наличия choices
        if (response.choices.isNullOrEmpty()) {
            throw RuntimeException("API вернул пустой ответ. Сырой ответ: $rawResponse")
        }

        val assistantMessage = response.choices.first().message
        conversationHistory.add(assistantMessage)

        return assistantMessage.content
    }

    fun close() {
        client.close()
    }
}

fun main(args: Array<String>) = runBlocking {
    println("╔════════════════════════════════════════╗")
    println("║    AI Challenge - CLI Chat Mode        ║")
    println("╚════════════════════════════════════════╝")
    println()

    // Парсинг аргументов командной строки
    val parsedArgs = try {
        parseArgs(args)
    } catch (e: IllegalArgumentException) {
        println("Ошибка парсинга аргументов: ${e.message}")
        println()
        println("Использование:")
        println("  ./chat.sh [опции] [системный промпт]")
        println()
        println("Опции:")
        println("  -m, --model <название>        Модель AI (по умолчанию gpt-4o)")
        println("  --model=<название>             Альтернативный формат")
        println("  -t, --temperature <значение>  Температура модели (0.0-2.0, по умолчанию 0.7)")
        println("  --temperature=<значение>       Альтернативный формат")
        println()
        println("Примеры:")
        println("  ./chat.sh -m gpt-4o -t 0.5 Ты полезный ассистент")
        println("  ./chat.sh --model=gpt-4-turbo --temperature=1.2 Ответь креативно")
        println("  ./chat.sh -m gpt-3.5-turbo Быстрый ассистент")
        return@runBlocking
    }

    if (args.isEmpty()) {
        println("Системный промпт не указан. Используется промпт по умолчанию.")
    }

    println("Системный промпт: ${parsedArgs.systemPrompt}")
    println("Модель: ${parsedArgs.model}")
    println("Температура: ${parsedArgs.temperature}")
    println()
    println("Введите сообщение для AI (или /exit для выхода, /clear для очистки истории)")
    println("─".repeat(50))
    println()

    val chatClient = ChatClient(parsedArgs.systemPrompt, parsedArgs.temperature, parsedArgs.model)

    try {
        while (true) {
            print("Вы: ")
            System.out.flush()
            val userInput = readlnOrNull()?.trim()

            if (userInput == null) {
                println("\n[Ввод завершён]")
                break
            }

            when {
                userInput.isEmpty() -> continue
                userInput == "/exit" || userInput == "/quit" -> {
                    println("\nЗавершение работы...")
                    break
                }
                userInput == "/clear" -> {
                    println("\n[История диалога очищена]")
                    println()
                    continue
                }
                userInput.startsWith("/") -> {
                    println("Неизвестная команда. Доступные команды: /exit, /quit, /clear")
                    println()
                    continue
                }
            }

            print("AI: ")
            try {
                val response = chatClient.sendMessage(userInput)
                println(response)
                println()
            } catch (e: Exception) {
                println("\n[Ошибка: ${e.message}]")
                println()
            }
        }
    } finally {
        chatClient.close()
    }
}

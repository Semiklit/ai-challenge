package dev.nsemiklit

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: Message
)

class ChatClient(private val systemPrompt: String) {
    private val client = HttpClient(OkHttp) {
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
            model = Config.MODEL,
            messages = conversationHistory
        )

        val response: ChatResponse = client.post("${Config.BASE_URL}/chat/completions") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer ${Config.API_KEY}")
            setBody(request)
        }.body()

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
    val systemPrompt = if (args.isNotEmpty()) {
        args.joinToString(" ")
    } else {
        println("Системный промпт не указан. Используется промпт по умолчанию.")
        "Ты полезный ассистент, который отвечает на русском языке."
    }

    println("Системный промпт: $systemPrompt")
    println("Модель: ${Config.MODEL}")
    println()
    println("Введите сообщение для AI (или /exit для выхода, /clear для очистки истории)")
    println("─".repeat(50))
    println()

    val chatClient = ChatClient(systemPrompt)

    try {
        while (true) {
            print("Вы: ")
            val userInput = readlnOrNull()?.trim() ?: break

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

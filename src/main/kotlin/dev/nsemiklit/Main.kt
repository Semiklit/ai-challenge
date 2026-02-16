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

fun main() = runBlocking {
    println("AI Challenge - OpenAI Integration")
    println("==================================")

    val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    println("\nОтправка запроса к AI...")
    println("Endpoint: ${Config.BASE_URL}")
    println("Model: ${Config.MODEL}\n")

    val request = ChatRequest(
        model = Config.MODEL,
        messages = listOf(
            Message("system", "Ты полезный ассистент, который отвечает на русском языке."),
            Message("user", "Привет! Расскажи коротко, что ты умеешь делать?")
        )
    )

    val response: ChatResponse = client.post("${Config.BASE_URL}/chat/completions") {
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer ${Config.API_KEY}")
        setBody(request)
    }.body()

    println("Ответ AI:")
    println(response.choices.first().message.content)

    client.close()
}

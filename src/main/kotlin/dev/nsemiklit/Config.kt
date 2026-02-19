package dev.nsemiklit

object Config {
    val API_KEY: String = System.getProperty("openai.api.key")
        ?: throw IllegalStateException("API key not found. Please create local.properties file.")

    val BASE_URL: String = System.getProperty("openai.base.url")
        ?: "https://api.proxyapi.ru/openai/v1"

    val MODEL: String = System.getProperty("openai.model")
        ?: "gpt-4o"
}

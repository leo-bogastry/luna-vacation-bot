package com.lunatech

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest.ChatPostMessageRequestBuilder
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

@Serializable
data class BotToken(val access_token: String)

fun main(args: Array<String>) {
    val logger = LoggerFactory.getLogger("mylogger")
    val slack = Slack.getInstance()

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    val channelId = "<replace by my channel>"
    val token = "xoxp-..."

    runBlocking {
        val text = getIfImOnVacation(client)
        val response = slack.methods(token).chatPostMessage { req: ChatPostMessageRequestBuilder ->
            req
                .channel(channelId)
                .text(text)
        }
        logger.info("Slack chatPostMessage response: {}", response)
    }
}

suspend fun getIfImOnVacation(client: HttpClient): String {
    // get jwt token
    val token = client.submitForm(
        url = "http://localhost:8080/realms/lunarealm/protocol/openid-connect/token",
        formParameters = parameters {
            append("grant_type", "client_credentials")
            append("client_id", "luna-vacation-bot")
            append("client_secret", "<replace by client_secret>")
        },
    ).body<BotToken>().access_token

    val response = client.get("http://localhost:5050/imionvacation") {
        bearerAuth(token)
    }.bodyAsText()

    return response
}

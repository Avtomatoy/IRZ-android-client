package ru.avtomaton.irz.app.client

import okhttp3.OkHttpClient

/**
 * @author Anton Akkuzin
 */
data class ClientProperties(
    val proto: String,
    val host: String,
    val port: Int,
    val clientBuilder: OkHttpClient.Builder
)

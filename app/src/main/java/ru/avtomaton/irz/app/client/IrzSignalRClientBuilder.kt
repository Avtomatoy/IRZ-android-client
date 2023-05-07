package ru.avtomaton.irz.app.client

import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

/**
 * @author Anton Akkuzin
 */
object IrzSignalRClientBuilder {

    private lateinit var props: ClientProperties

    fun init(properties: ClientProperties) {
        props = properties
    }

    fun build(): HubConnection = HubConnectionBuilder
        .create("${props.proto}://${props.host}:${props.port}/hubs/chat/")
        .setHttpClientBuilderCallback { builder -> builder.addInterceptor(AuthInterceptor) }
        .build()
}
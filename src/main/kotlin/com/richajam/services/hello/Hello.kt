package com.richajam.services.hello

import org.http4k.core.HttpHandler
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.server.Undertow


object HelloHandler {
    operator fun invoke(): HttpHandler = { Response(Status.OK).body("Hello, World!") }
}

fun main() {
    System.setProperty("org.jboss.logging.provider", "slf4j")

    Undertow(port = 8000)
        .toServer(HelloHandler())
        .start()
        .block()
}

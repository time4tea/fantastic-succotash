package com.richajam.services.hello

import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.Test

class HelloWorldTest {

    val handler: HttpHandler = { Response(Status.OK).body("Hello, World!") }

    @Test
    fun `invoking says hello`() {
        assertThat(
            handler(Request(Method.GET, Uri.of("http://localhost/hello-world"))),
            allOf(
                hasStatus(Status.OK),
                hasBody("Hello, World!")
            )
        )
    }
}
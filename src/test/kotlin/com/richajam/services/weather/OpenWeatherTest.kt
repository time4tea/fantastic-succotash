package com.richajam.services.weather

import com.natpryce.hamkrest.allOf
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class OpenWeatherTest {


    data class GpsCoordinate(val lon: Double, val lat: Double)

    // https://openweathermap.org/current

    data class Weather(val temperature: Temperature, val coordinate: GpsCoordinate)

    data class Temperature(val scale: Scale, val value: Double) {
        enum class Scale {
            KELVIN
        }
    }

    class OpenWeather(httpHandler: HttpHandler) {
        fun current(gpsCoordinate: GpsCoordinate): Weather {
            return Weather(Temperature(Temperature.Scale.KELVIN, 0.0), gpsCoordinate)
        }
    }

    object ParameterRouter {
        operator fun invoke(vararg list: Pair<Set<String>, HttpHandler>): HttpHandler {
            val routes = list.toList()
            return { request ->
                val supplied = request.uri.queries().map { p -> p.first }.toSet()
                routes.find { route -> route.first == supplied }
                    ?.let { it.second(request) }
                    ?: Response(Status.NOT_FOUND)
            }
        }
    }

    @Test
    fun `parameter router`() {
        val server = routes("/bob" bind ParameterRouter(
            setOf("q") to { Response(Status.OK).body("q") },
            setOf("lat", "lon") to { Response(Status.OK).body("lon") },
            setOf("id") to { Response(Status.OK).body("id") }
        ))
        assertThat(server(Request(Method.GET, "/bob").query("q", null)), allOf(hasStatus(Status.OK),hasBody("q")))
        assertThat(server(Request(Method.GET, "/bob").query("lat", null).query("lon", null)), allOf(hasStatus(Status.OK), hasBody("lon")))
        assertThat(server(Request(Method.GET, "/bob").query("id", null)), allOf(hasStatus(Status.OK),hasBody("id")))
        assertThat(server(Request(Method.GET, "/bob").query("lat", null)), hasStatus(Status.NOT_FOUND))
        assertThat(server(Request(Method.GET, "/bob").query("xx", null)), hasStatus(Status.NOT_FOUND))
    }

    object FakeOpenWeather {
        operator fun invoke(): HttpHandler {
            return routes(
                "/data/2.5/weather" bind Method.GET to currentWeather()
            )
        }

        private fun currentWeather(): HttpHandler {
            return { Response(Status.OK) }
        }
    }

    @Test
    @Disabled
    fun `retrieving weather for a gps location`() {
        val client = OpenWeather(FakeOpenWeather())

        val weather = client.current(GpsCoordinate(-122.02, 37.39))

        assertThat(weather.coordinate, equalTo(GpsCoordinate(-122.02, 37.39)))
        assertThat(weather.temperature, equalTo(Temperature(Temperature.Scale.KELVIN, 282.55)))
    }
}
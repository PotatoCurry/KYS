package io.github.potatocurry

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTest {

    @Test
    fun testRoot() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/query").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
            handleRequest(HttpMethod.Get, "/query/625783").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotEquals("The specified student with number 625783 was not found.", response.content)
            }
            handleRequest(HttpMethod.Get, "/query/000000").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("The specified student with number 000000 was not found.", response.content)
            }
        }
    }

}

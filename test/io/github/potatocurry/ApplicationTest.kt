package io.github.potatocurry

import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*

/** Basic tests to ensure database is operational and web server is responsive. */
class ApplicationTest {
    /** Basic unit tests. */
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
                assertNotEquals("The specified student with ID 625783 was not found.", response.content)
                assertNotEquals("Error parsing ID 625783.", response.content)
            }
            handleRequest(HttpMethod.Get, "/query/000000").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("The specified student with ID 000000 was not found.", response.content)
            }
            handleRequest(HttpMethod.Get, "/query/abc").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Error parsing ID abc.", response.content)
            }
        }
    }
}

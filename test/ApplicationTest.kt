package io.github.potatocurry.kys

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.*

/** Basic tests to ensure database is operational and web server is responsive. */
class ApplicationTest {
    /** Tests if web server is responsive and is able to retrieve student data. */
    @Test
    fun testWeb() {
        SheetReader.refreshData()
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
            handleRequest(HttpMethod.Get, "/forms").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
            handleRequest(HttpMethod.Get, "/volunteer-opportunities").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
            handleRequest(HttpMethod.Get, "/query").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
            }
            handleRequest(HttpMethod.Get, "/query/625783").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                val content = response.content
                assertNotNull(content)
                assertTrue(content.contains("Damian Lall"))
                assertNotEquals("Student with ID 625783 not found", content)
                assertNotEquals("Error parsing ID 625783", content)
            }
            handleRequest(HttpMethod.Get, "/query/000000").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals("Student with ID 000000 not found", response.content)
            }
            handleRequest(HttpMethod.Get, "/query/abc").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertNotNull(response.content)
                assertEquals("Error parsing ID abc", response.content)
            }
        }
    }
}

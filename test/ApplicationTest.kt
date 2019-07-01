package io.github.potatocurry.kys

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.*

/** Basic tests to ensure database is operational and web server is responsive. */
class ApplicationTest {
    /** Tests accuracy of [Students] database.
     *  This will need to be periodically updated with new student data.
     */
    @Test
    fun testDatabase() {
        SheetReader.refreshData()
        withTestApplication({ module() }) {
            val student = Students[625783]
            assertNotNull(student)
            assertEquals("Damian Lall", "${student.firstName} ${student.lastName}")
            assertEquals(46.0, student.totalHours)
            assertEquals(0.0, student.totalExtraHours)
        }
    }

    /** Tests if web server is responsive and is able to retrieve student data. */
    @Test
    fun testWeb() {
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

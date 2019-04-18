package io.github.potatocurry

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import kotlinx.html.*

/** Starts main application server. */
fun main(args: Array<String>): Unit = EngineMain.main(args)

/** Main web server listening for requests. */
fun Application.module() {
    HttpClient(Apache)

    routing {
        static("/") {
            resources("static")
            resource("/", "static/index.html")
        }

        get("/query") {
            call.respondText("Go to /query/{number}", ContentType.Text.Plain)
        }

        get("/query/{number}") {
            SheetReader.refreshData()
            val number = call.parameters["number"]
            try {
                number!!.toInt()
            } catch (e: NumberFormatException) {
                call.respondText("Error parsing ID $number.", ContentType.Text.Plain)
                return@get
            }
            val student = Students[number.toInt()]
            if (student == null) {
                call.respondText("The specified student with ID $number was not found.", ContentType.Text.Plain)
            } else {
                call.respondHtml {
                    head {
                        title { +"KYS | ${student.firstName} ${student.lastName}" }
                    }
                    body {
                        h1 { +"${student.firstName} ${student.lastName} (${student.gradClass})" }
                        if (student.totalExtraHours == 0.0)
                            p { +"You have ${student.totalHours} total hours." }
                        else
                            p { +"You have ${student.totalHours} regular hours and ${student.totalExtraHours} extra hours." }
                        h2 { +"Volunteering Records" }
                        student.activities.forEach { va ->
                            if (va.endDate == "")
                                h3 { +"${va.agency}: ${va.startDate}" }
                            else
                                h3 { +"${va.agency}: ${va.startDate} - ${va.endDate}" }
                            p {
                                +"${va.hours} hours"
                                if (va.isSummer)
                                    +" and ${va.extraHours} extra hours"
                            }
                            p { +va.description }
                        }
                    }
                }
            }
        }
    }
}

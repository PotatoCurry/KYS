package io.github.potatocurry

import io.ktor.application.Application
import io.ktor.application.ApplicationCall
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
import kotlinx.css.*
import kotlinx.html.*
import java.lang.NumberFormatException

fun main(args: Array<String>): Unit = EngineMain.main(args)

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
                    body {
                        h1 { +"${student.firstName} ${student.lastName} (${student.gradClass})" }
                        p { +"You have ${student.totalHours} total hours." }
                        h2 { +"Volunteering Records" }
                        for (va in student.activities) {
                            if (va.endDate == "")
                                h3 { +"${va.agency}: ${va.startDate}" }
                            else
                                h3 { +"${va.agency}: ${va.startDate} - ${va.endDate}" }
                            p { +"${va.hours} hours" }
                            p { +va.description }
                        }
                    }
                }
            }
        }

        get("/styles.css") {
            call.respondCss {
                body {
                    backgroundColor = Color.red
                }
                p {
                    fontSize = 2.em
                }
                rule("p.myclass") {
                    color = Color.blue
                }
            }
        }
    }
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

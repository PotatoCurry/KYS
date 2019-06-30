package io.github.potatocurry

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import kotlinx.html.*
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer

/** Starts main application server. */
fun main(args: Array<String>) = EngineMain.main(args)

/** Main web server listening for requests. */
fun Application.module() {
    /** Refresh database every thirty minutes. */
    fixedRateTimer("UpdateDatabase", true, 0, 1800000) {
        SheetReader.refreshData()
        println("Refreshed database - ${LocalDateTime.now()}")
    }

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, "${it.value} ${it.description}")
        }
        exception<Exception> { error ->
            call.respondHtml(HttpStatusCode.InternalServerError) {
                head {
                    title("KYS | Internal Server Error")
                    meta("viewport", "width=device-width, initial-scale=1")
                }
                body {
                    h1 { +"500 Internal Server Error" }
                    p {
                        +"KYS experienced an issue responding to your request. "
                        +"Please ensure the validity of your request or try again later."
                    }
                    p {
                        +"If the issue persists, raise an issue on our "
                        a("https://github.com/PotatoCurry/KYS") { +"GitHub repository" }
                        +", attaching the following error log."
                    }
                    code {
                        +"Path: ${call.request.local.uri}"
                        br
                        +"Error: $error"
                        br
                        +"Stack Trace: ${error.stackTrace}"
                    }
                }
            }
            error.printStackTrace()
            // TODO: Set up alert system to notify me on errors
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

//    install(Authentication) {
//        basic("admin") {
//            realm = "KYS Administrator Portal"
//            validate { credentials ->
//                if (credentials.password == System.getenv("KYS_Pass") ?: System.err.println("KYS_Pass environmental variable not set"))
//                    UserIdPrincipal(credentials.name)
//                else
//                    null
//            }
//        }
//    }

    routing {
        static("/") {
            resources("static")
            resource("/", "static/home.html")
            resource("/forms", "static/forms.html")
            resource("/volunteer-opportunities", "static/volunteer-opportunities.html")
            resource("/query", "static/query.html")
        }

        get("/query/{number}/{json?}") {
            val number = call.parameters["number"]
            when {
                number == "random" -> call.respondRedirect("/query/${Students.getRandomNumber()}/${call.parameters["json"].orEmpty()}")
                number?.toIntOrNull() == null -> call.respondText("Error parsing ID $number", ContentType.Text.Plain)
                else -> {
                    val student = Students[number.toInt()]
                    when {
                        student == null -> call.respondText("Student with ID $number not found", ContentType.Text.Plain)
                        call.parameters["json"] == "json" -> call.respond(student)
                        else -> call.respondHtml {
                            head {
                                title("KYS | ${student.firstName} ${student.lastName}")
                                meta("viewport", "width=device-width, initial-scale=1")
                            }
                            body {
                                h1 { +"${student.firstName} ${student.lastName} (${student.gradClass})" }
                                span { +"${student.totalHours} Total Hours" }
                                if (student.totalExtraHours > 0.0)
                                    span { +" | ${student.totalExtraHours} Total Extra Hours" }
                                h2 { +"Volunteering Records" }
                                student.records.forEach { va ->
                                    if (va.endDate == "")
                                        h3 { +"${va.agency}: ${va.startDate}" }
                                    else
                                        h3 { +"${va.agency}: ${va.startDate} - ${va.endDate}" }
                                    span { +"${va.hours} Hours" }
                                    if (va.extraHours > 0.0)
                                        span { +" | ${va.extraHours} Extra Hours" }
                                    p { +va.description }
                                }
                            }
                        }
                    }
                }
            }
        }

//        authenticate("admin") {
//            get("/admin") {
//                call.respond("you win!")
//            }
//        }
    }
}

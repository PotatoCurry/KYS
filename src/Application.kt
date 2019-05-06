package io.github.potatocurry

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.client.HttpClient
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.isMultipart
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import kotlinx.html.*
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer

/** Start main application. */
fun main(args: Array<String>) {
    /** Refresh database every thirty minutes. */
    fixedRateTimer("UpdateDatabase", true, 0, 1800000) {
        SheetReader.refreshData()
        println("Refreshed database - ${LocalDateTime.now()}")
    }

    EngineMain.main(args)
}

/** Main web server listening for requests. */
fun Application.module() {
    HttpClient()

    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, "${it.value} ${it.description}")
        }
        exception<Exception> {
            call.respond(HttpStatusCode.InternalServerError, "${it.message}\n${it.stackTrace}")
            it.printStackTrace()
            throw it
        }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Authentication) {
        basic("admin") {
            realm = "KYS Administrator Portal"
            validate { credentials ->
                if (credentials.password == System.getenv("KYS_Pass") ?: System.err.println("KYS_Pass environmental variable not set"))
                    UserIdPrincipal(credentials.name)
                else
                    null
            }
        }
    }

    routing {
        static("/") {
            resources("static")
            resource("/", "static/home.html")
            resource("/query", "static/query.html")
            resource("/forms", "static/forms.html")
        }

        get("/query/{number}/{json?}") {
            var number = call.parameters["number"]
            if (number == "random")
                number = Students.getRandomNumber().toString()

            if (number?.toIntOrNull() == null) {
                call.respondText("Error parsing ID $number", ContentType.Text.Plain)
            } else {
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


        authenticate("admin") {
            get("/admin") {
                call.respond("you win!")
            }
        }
    }
}

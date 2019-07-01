package io.github.potatocurry.kys

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.features.origin
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
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.concurrent.fixedRateTimer

val kysLogger = LoggerFactory.getLogger("io.github.potatocurry.kys")

/** Starts main application server. */
fun main(args: Array<String>) = EngineMain.main(args)

/** Main web server listening for requests. */
fun Application.module() {
    /** Refresh database every thirty minutes. */
    fixedRateTimer("UpdateDatabase", true, 0, 1800000) {
        try {
            SheetReader.refreshData()
            kysLogger.info("Refreshed database")
        } catch (e: KotlinNullPointerException) {
            kysLogger.error("Error refreshing database", e)
        }
    }

    install(CallLogging)

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
            kysLogger.error("Server returned 500", error)
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
//                if (credentials.password == System.getenv("KYS_PASS") ?: kysLogger.error("KYS_PASS environmental variable not set"))
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
            resource("/yeselite", "static/yeselite.html")
            resource("/volunteer-opportunities", "static/volunteer-opportunities.html")
            resource("/query", "static/query.html")
        }

        get("/query/{number}/{json?}") {
            val number = call.parameters["number"]
            MDC.put("ip_address", call.request.origin.remoteHost)
            when {
                number == "random" -> {
                    val randomNumber = Students.getRandomNumber()
                    call.respondRedirect("/query/$randomNumber/${call.parameters["json"].orEmpty()}")
                    kysLogger.trace("Redirecting to {}", randomNumber)
                }
                number?.toIntOrNull() == null -> {
                    call.respondText("Error parsing ID $number", ContentType.Text.Plain)
                    kysLogger.trace("Error parsing ID {}", number)
                }
                else -> {
                    val student = Students[number.toInt()]
                    MDC.put("id", number)
                    when {
                        student == null -> {
                            call.respondText("Student with ID $number not found", ContentType.Text.Plain)
                            kysLogger.trace("Student with ID {} not found", number)
                        }
                        call.parameters["json"] == "json" -> {
                            call.respond(student)
                            kysLogger.trace("Responded with student {} JSON", number)
                        }
                        else -> {
                            // TODO: Put this in an HTML template or method to reduce Application.module() size
                            call.respondHtml {
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
                            kysLogger.trace("Responded with student {}", number)
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

package io.github.potatocurry.kys

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.ApplicationCallPipeline
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.jackson.jackson
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.server.netty.EngineMain
import io.ktor.util.AttributeKey
import kotlinx.html.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import kotlin.concurrent.fixedRateTimer

val kysLogger: Logger = LoggerFactory.getLogger("io.github.potatocurry.kys")

/** Starts main application server. */
fun main(args: Array<String>) = EngineMain.main(args)

/** Main web server listening for requests. */
fun Application.module() {
    /** Instantiate database and refresh it hourly. */
    SheetReader.refreshData()
    fixedRateTimer("UpdateDatabase", true, 3600000, 3600000) {
        SheetReader.refreshData()
    }

    install(CallLogging)

    install(CORS)

    install(ContentNegotiation) {
        jackson { enable(SerializationFeature.INDENT_OUTPUT) }
    }

    install(StatusPages) {
        // TODO: Make this more substantial
        status(HttpStatusCode.NotFound) {
            call.respond(HttpStatusCode.NotFound, "${it.value} ${it.description}")
        }
        exception<Exception> { error ->
            call.respondHtml(HttpStatusCode.InternalServerError) {
                head {
                    title("KYS | Internal Server Error")
                    meta("viewport", "width=device-width, initial-scale=1")
                    link("/assets/main.css", "stylesheet")
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
        static {
            resources("static")
            resource("/", "static/home.html")
            resource("forms", "static/forms.html")
            resource("yeselite", "static/yeselite.html")
            resource("volunteer-opportunities", "static/volunteer-opportunities.html")
            resource("query", "static/query.html")
        }

        post("yeselite") {
            val registration = call.receiveParameters()
            if (validateRegistration(registration)) {
                kysLogger.trace("Registration data validated")
                call.respond(HttpStatusCode.Accepted, "Registration data validated")
                EmailHandler.sendRegistration(registration)
            } else {
                kysLogger.warn("Invalid registration data")
                call.respond(HttpStatusCode.UnprocessableEntity, "Invalid registration data")
            }
        }

        route("query/{number}") {
            val studentKey = AttributeKey<Student>("student")

            intercept(ApplicationCallPipeline.Call) {
                val number = call.parameters["number"]

                val student = when {
                    number == "random" -> {
                        val random = students.entries.random()
                        kysLogger.trace("Chose random number {}", random.key)
                        random.value
                    }
                    number?.toIntOrNull() == null -> {
                        call.respondText("Error parsing ID $number")
                        kysLogger.trace("Error parsing ID {}", number)
                        return@intercept finish()
                    }
                    else -> students[number.toInt()]
                }

                if (student == null) {
                    call.respondText("Student with ID $number not found")
                    return@intercept finish()
                }
                MDC.put("id", number)
                call.attributes.put(studentKey, student)
            }

            get {
                val student = call.attributes[studentKey]
                // TODO: Put this in an HTML template or method to reduce Application.module() size
                call.respondHtml {
                    head {
                        title("KYS | ${student.firstName} ${student.lastName}")
                        meta("viewport", "width=device-width, initial-scale=1")
                        link("/assets/main.css", "stylesheet")
                    }
                    body {
                        h1 { +"${student.firstName} ${student.lastName} (${student.gradClass})" }
                        if (student.totalExtraHours == 0.0)
                            p { +"${student.totalHours} total hours" }
                        else
                            p { +"${student.totalHours} total hours | ${student.totalExtraHours} total extra hours" }
                        h2 { +"Volunteering Records" }
                        student.records.forEach { volunteerActivity ->
                            if (volunteerActivity.endDate == "")
                                h3 { +"${volunteerActivity.agency}: ${volunteerActivity.startDate}" }
                            else
                                h3 { +"${volunteerActivity.agency}: ${volunteerActivity.startDate} - ${volunteerActivity.endDate}" }
                            span { +"${volunteerActivity.hours} Hours" }
                            if (volunteerActivity.extraHours == 0.0)
                                p { +"${volunteerActivity.hours} hours" }
                            else
                                p { +"${volunteerActivity.hours} hours | ${volunteerActivity.extraHours} extra hours" }
                            p { +volunteerActivity.description }
                        }
                    }
                }
            }

            get("json") {
                val student = call.attributes[studentKey]
                call.respond(student)
            }
        }

//        authenticate("admin") {
//            get("/admin") {
//                call.respond("you win!")
//            }
//        }
    }
}

private fun validateRegistration(registration: Parameters): Boolean {
    return try {
        require(registration.contains("firstName"))
        require(registration.contains("lastName"))
        require(registration.contains("email"))
        require(registration.contains("id"))
        require(registration.contains("class"))
        require(registration.contains("phone"))
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

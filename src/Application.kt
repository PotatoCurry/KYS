package io.github.potatocurry

import SheetReader
import Students
import io.ktor.application.Application
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.*
import kotlinx.html.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    HttpClient(Apache) {
    }

    routing {
        static("/") {
            resources("static")
        }

        get("/") {
            call.respondHtml {
                body {
                    h1 { +"Clements YES Program" }
                    div("info") {
                        p {
                            strong { +"WE HAVE TEMPORALLY DISABLED THE YES HOUR QUERY. IF YOU WANT TO FIND OUT HOW MANY YES HOURS YOU HAVE PLEASE EMAIL US AT CLEMENTSYES@GMAIL.COM." }
                        }
                    }
                }
            }
        }

        get("/query") {
            call.respondText("Go to /query/{number}", ContentType.Text.Plain)
        }

        get("/query/{number}") {
            SheetReader.refreshData()
            val student = Students.get(call.parameters["number"]?.toInt()!!)
            if (student == null) {
                call.respondText("The specified student with number ${call.parameters["number"]} was not found.")
            } else {
                call.respondHtml {
                    body {
                        h1 { +"${student.firstName} ${student.lastName} (${student.gradClass})" }
                        p { +"You have ${student.totalHours} total hours" }
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

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}

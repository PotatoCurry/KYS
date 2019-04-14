package io.github.potatocurry

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.html.*
import kotlinx.html.*
import kotlinx.css.*
import freemarker.cache.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    val client = HttpClient(Apache) {
    }

    routing {
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
            call.respondText("Go to /query/{number}")
            //call.respond(FreeMarkerContent("query.ftl", mapOf("number" to ), ""))
        }

        get("/query/{number}") {
            SheetReader.refreshData() // TODO: Clear out previous data so refreshing it does not duplicate existing data
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

        static("/") {
            resources("static")
        }
    }
}

data class IndexData(val items: List<Int>)

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

package io.github.potatocurry.kys

import com.sendgrid.*
import java.io.IOException

object EmailHandler {
    private val sendGrid = SendGrid(System.getenv("KYS_SENDGRID_KEY"))

    /*
    fun sendRegistration() {
        val from = Email("test@example.com")
        val subject = "Sending with SendGrid is Fun"
        val to = Email("test@example.com")
        val content = Content("text/plain", "and easy to do anywhere, even with Java")
        val mail = Mail(from, subject, to, content)
        mail.send()
        kysLogger.debug("Sent YESeLITe registration email for (user)")
    }
    */

    /*
    Not yet implemented
    fun sendUpdateNotification() {

    }
    */

    private fun Mail.send() {
        val request = Request()
        try {
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = this.build()
            sendGrid.api(request)
        } catch (e: IOException) {
            kysLogger.error("Error sending email", e)
        }
    }
}

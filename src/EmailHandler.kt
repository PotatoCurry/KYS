package io.github.potatocurry.kys

import com.sendgrid.*
import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.Email
import com.sendgrid.helpers.mail.objects.Personalization
import io.ktor.http.Parameters
import kotlinx.io.errors.IOException
import org.apache.http.HttpStatus

object EmailHandler {
    private val sendGrid = SendGrid(System.getenv("KYS_SENDGRID_KEY"))

    fun sendRegistration(form: Parameters) {
        val mail = Mail()
        mail.setFrom(Email("from@example.com"))
        mail.setTemplateId("d-676462b81ff345b79dcf000f3c306df5")

        val name = "${form["firstName"]} ${form["lastName"]}"
        val personalization = Personalization()
        personalization.addDynamicTemplateData("subject", "New YESeLITe registration - $name")
        personalization.addDynamicTemplateData("name", name)
        personalization.addDynamicTemplateData("email", form["email"])
        personalization.addDynamicTemplateData("id", form["id"])
        personalization.addDynamicTemplateData("class", form["class"])
        personalization.addDynamicTemplateData("phone",
            if (form["phone"].isNullOrBlank())
                "Not provided"
            else
                form["phone"]
        )
        personalization.addTo(Email("damianlall@hotmail.com"))
        mail.addPersonalization(personalization)

        if (mail.send())
            kysLogger.debug("Sent YESeLITe registration email for {}", name)
    }

    /*
    Not yet implemented
    fun sendUpdateNotification() {

    }
    */

    private fun Mail.send(): Boolean {
        return try {
            val request = Request()
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = this.build()

            val status = sendGrid.api(request).statusCode
            if (status != HttpStatus.SC_ACCEPTED)
                throw IOException("Received status code $status when sending email")
            else
                true
        } catch (e: IOException) {
            kysLogger.error("Error sending email", e)
            false
        }
    }
}

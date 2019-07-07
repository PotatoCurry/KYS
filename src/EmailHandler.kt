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
        mail.setFrom(Email("yeselite@chsyes.info", "Kotlin YES System"))
        mail.setTemplateId("d-676462b81ff345b79dcf000f3c306df5")

        val personalization = Personalization()
        with (form) {
            personalization.addDynamicTemplateData("firstName", get("firstName"))
            personalization.addDynamicTemplateData("lastName", get("lastName"))
            personalization.addDynamicTemplateData("email", get("email"))
            personalization.addDynamicTemplateData("id", get("id"))
            personalization.addDynamicTemplateData("class", get("class"))
            personalization.addDynamicTemplateData("phone",
                if (form["phone"].isNullOrBlank())
                    "Not provided"
                else
                    get("phone")
            )
        }
        personalization.addTo(Email("damianlall@hotmail.com"))
        mail.addPersonalization(personalization)

        if (mail.send())
            kysLogger.debug("Sent YESeLITe registration email for {}", form["id"])
    }

    fun sendRecordUpdate(volunteerActivity: VolunteerActivity) {
        val mail = Mail()
        mail.setFrom(Email("notifications@chsyes.info", "Kotlin YES System"))
        mail.setTemplateId("d-7b467aaa69f748d0b9275a0fe01703e2")

        val personalization = Personalization()
        with (volunteerActivity) {
            personalization.addDynamicTemplateData("agency", agency)
            personalization.addDynamicTemplateData("startDate", startDate)
            personalization.addDynamicTemplateData("endDate", endDate)
            personalization.addDynamicTemplateData("hours", hours)
            personalization.addDynamicTemplateData("extraHours", extraHours)
            personalization.addDynamicTemplateData("isSummer", isSummer)
            personalization.addDynamicTemplateData("description", extraHours)
        }
        val recipient = Email("damianlall@hotmail.com")
        personalization.addTo(recipient)
        mail.addPersonalization(personalization)

        if (mail.send())
            kysLogger.debug("Sent record update email to {}", recipient.email)
    }

    private fun Mail.send(): Boolean {
        return try {
            val request = Request()
            request.method = Method.POST
            request.endpoint = "mail/send"
            request.body = this.build()

            val status = sendGrid.api(request).statusCode
            if (status != HttpStatus.SC_ACCEPTED)
                throw IOException("Email not accepted - received status code $status")
            true
        } catch (e: IOException) {
            kysLogger.error("Error sending email", e)
            false
        }
    }
}

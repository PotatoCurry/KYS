package io.github.potatocurry

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.FileReader
import java.io.IOException
import java.security.GeneralSecurityException

object SheetReader {
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
    private const val CREDENTIALS_FILE_PATH = "resources/KYS_Credentials.json"

    /** Returns credential object from credential files. */
    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, FileReader(CREDENTIALS_FILE_PATH))
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(java.io.File("resources")))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /** Reinitialize database with updated values from spreadsheet. */
    @Throws(IOException::class, GeneralSecurityException::class)
    fun refreshData() {
        // Build a new authorized API client service.
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val spreadsheetId: String
        if (System.getenv("KYS_Spreadsheet") == null) {
            System.err.println("KYS_Spreadsheet environmental variable not set")
            return
        } else
            spreadsheetId = System.getenv("KYS_Spreadsheet")
        val range = "Sheet1!A2:K"
        val service = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName("Kotlin YES System")
            .build()
        val response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        val values = response.getValues()
        if (values == null || values.isEmpty()) {
            System.err.println("Unable to retrieve data")
        } else {
            Students.initialize()
            for (row in values) {
                try {
                    val number = (Integer.parseInt(row[0].toString()) - 2424) / 5
                    val agency = row[4].toString()
                    val startDate = row[5].toString()
                    val endDate = row[6].toString()
                    val hours = try {
                        java.lang.Double.parseDouble(row[7].toString())
                    } catch (e: NumberFormatException) {
                        0.0
                    }
                    val summer = row[8].toString() == "SH"
                    val extraHours = try {
                        java.lang.Double.parseDouble(row[9].toString())
                    } catch (e: NumberFormatException) {
                        0.0
                    } catch (e: IndexOutOfBoundsException) {
                        0.0
                    } // extra hours are added into regular hours
                    val description = try {
                        row[10].toString()
                    } catch (e: IndexOutOfBoundsException) {
                        ""
                    }

                    if (!Students.exists(number)) {
                        val firstName = row[1].toString()
                        val lastName = row[2].toString()
                        val gradClass = Integer.parseInt(row[3].toString())
                        Students.add(number, Student(firstName, lastName, gradClass))
                    }
                    Students[number]!!.enterActivity(
                        VolunteerActivity(
                            agency,
                            startDate,
                            endDate,
                            hours + extraHours,
                            summer,
                            description
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    System.err.println("This is the fucking problem child: $row")
                }

            }
        }
    }

}

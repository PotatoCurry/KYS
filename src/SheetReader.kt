package io.github.potatocurry.kys

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
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.security.GeneralSecurityException

/** Handles scraping [Student] data and putting it in [students] database. */
object SheetReader {
    private const val CREDENTIALS_FILE_PATH = "resources/KYS_CREDENTIALS.json"
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)
    private var lastRow = 0

    /** Update [students] database. */
    fun refreshData() {
        val values = scrapeData()
        if (values == null) {
            kysLogger.error("Spreadsheet response is null")
            return
        }
        values.subList(lastRow, values.size).forEach { row ->
            val number = (Integer.parseInt(row[0].toString()) - 2424) / 5
            val firstName = row[1].toString().trim()
            val lastName = row[2].toString().trim()
            val gradClass = Integer.parseInt(row[3].toString())
            val agency = row[4].toString()
            val startDate = row[5].toString()
            val endDate = row[6].toString()
            val hours = row[7].toString().toDoubleOrNull() ?: 0.0
            val summer = row[8].toString() == "SH"
            val extraHours = try {
                row[9].toString().toDoubleOrNull() ?: 0.0
            } catch (e: IndexOutOfBoundsException) {
                0.0
            }
            val description = try {
                row[10].toString()
            } catch (e: IndexOutOfBoundsException) {
                ""
            }

            students.getOrPut(number) {
                Student(firstName, lastName, gradClass)
            }.apply {
                enterActivity(
                    VolunteerActivity(
                        agency,
                        startDate,
                        endDate,
                        hours,
                        extraHours,
                        summer,
                        description
                    )
                ).also {
                    if (lastRow != 0 && 1 == 5) // Not yet activated
                        EmailHandler.sendRecordUpdate(it)
                }
            }
        }
        lastRow = values.size
        kysLogger.info("Refreshed student database")
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun scrapeData(): List<List<Any>>? {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val spreadsheetId = System.getenv("KYS_SPREADSHEET")
        if (spreadsheetId == null) {
            kysLogger.error("KYS_SPREADSHEET environmental variable not set")
            return null
        }
        val range = "Sheet1!A2:K" // TODO: Only read from lastRow + 2
        val service = Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
            .setApplicationName("Kotlin YES System")
            .build()
        val response = service.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        kysLogger.debug("Obtained spreadsheet response")
        return response.getValues()
    }

    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, FileReader(CREDENTIALS_FILE_PATH))
        kysLogger.debug("Loaded Google credentials")
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File("resources")))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetReader {

    private static final String APPLICATION_NAME = "Kotlin YES System";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "resources";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "resources/KYS_Credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_FILE_PATH));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void refreshData() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        final String spreadsheetId;
        if (System.getenv("KYS_Spreadsheet") == null) {
            System.err.println("KYS_Spreadsheet environmental variable not set");
            return;
        }
        spreadsheetId = System.getenv("KYS_Spreadsheet");
        final String range = "Sheet1!A2:K";
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.err.println("Unable to retrieve data");
        } else {
            Students.initialize();
            for (List row : values) {
                try {
                    int number = (Integer.parseInt(row.get(0).toString()) - 2424) / 5;
                    String agency = row.get(4).toString();
                    String startDate = row.get(5).toString();
                    String endDate = row.get(6).toString();
                    double hours;
                    try {
                        hours = Double.parseDouble(row.get(7).toString());
                    } catch (NumberFormatException e) {
                        hours = 0;

                    }
                    boolean summer = row.get(8).toString().equals("SH");
                    double extraHours; // extra hours are added into regular hours
                    try {
                        extraHours = Double.parseDouble(row.get(9).toString());
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        extraHours = 0;
                    }
                    String description;
                    try {
                        description = row.get(10).toString();
                    } catch (IndexOutOfBoundsException e) {
                        description = "";
                    }

                    if (!Students.exists(number)) {
                        String firstName = row.get(1).toString();
                        String lastName = row.get(2).toString();
                        int gradClass = Integer.parseInt(row.get(3).toString());
                        Students.add(number, new Student(firstName, lastName, gradClass));
                    }
                    Students.get(number).enterActivity(new VolunteerActivity(agency, startDate, endDate, hours + extraHours, summer, description));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("This is the fucking problem child: " + row);
                }
            }
        }
    }

}

public class VolunteerActivity {

    private String agency;
    private String startDate;
    private String endDate;
    private double hours;
    private boolean summer;
    private String description;

    public VolunteerActivity(String a, String sd, String ed, double h, boolean s, String d) {
        agency = a;
        startDate = sd;
        endDate = ed;
        hours = h;
        summer = s;
        description = d;
    }

    public String getAgency() {
        return agency;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public double getHours() {
        return hours;
    }

    public boolean isSummer() {
        return summer;
    }

    public String getDescription() {
        return description;
    }
}

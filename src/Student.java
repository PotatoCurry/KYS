import java.util.ArrayList;

public class Student {

    private String firstName;
    private String lastName;
    private int gradClass;
    private ArrayList<VolunteerActivity> activities;

    public Student(String fn, String ln, int c) {
        firstName = fn;
        lastName = ln;
        gradClass = c;
        activities = new ArrayList<>();
    }

    public void enterActivity(VolunteerActivity va) {
        activities.add(va);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getGradClass() {
        return gradClass;
    }
}

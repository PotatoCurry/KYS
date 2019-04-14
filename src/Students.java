import java.util.HashMap;

public class Students {

    static HashMap<Integer, Student> students;

    public static void initialize() {
        students = new HashMap<>();
    }

    public static boolean exists(int id) {
        return students.containsKey(id);
    }

    public static Student add(int number, Student student) {
        return students.put(number, student);
    }

    public static Student get(int number) {
        return students.get(number);
    }

}

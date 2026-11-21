import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MiniBlackboardData implements Serializable {
    private static final long serialVersionUID = 1L;

    List<GradeActivity.Course> courses;
    List<String> students;
    Map<String, Double> grades;

    public MiniBlackboardData(List<GradeActivity.Course> courses,
                              List<String> students,
                              Map<String, Double> grades) {
        this.courses = new ArrayList<>(courses);
        this.students = new ArrayList<>(students);
        this.grades = new LinkedHashMap<>(grades);
    }
}

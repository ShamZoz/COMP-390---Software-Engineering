package src;
import java.io.Serializable;
import java.util.*;

import src.GradeActivity;

public class MiniBlackboardData implements Serializable {
    private static final long serialVersionUID = 1L;
    List<GradeActivity.Course> courses;
    List<String> students;
    Map<String, Double> grades;
    Map<String, StudentAccount> studentAccounts;
    Map<String, StudentAccount> teacherAccounts;

    public MiniBlackboardData(List<GradeActivity.Course> courses,
                              List<String> students,
                              Map<String, Double> grades,
                              Map<String, StudentAccount> studentAccounts,
                              Map<String, StudentAccount> teacherAccounts) {
        this.courses = new ArrayList<>(courses);
        this.students = new ArrayList<>(students);
        this.grades = new LinkedHashMap<>(grades);
        this.studentAccounts = new LinkedHashMap<>(studentAccounts);
        this.teacherAccounts = new LinkedHashMap<>(teacherAccounts);
    }
}


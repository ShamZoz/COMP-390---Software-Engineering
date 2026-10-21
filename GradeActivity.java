import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

// Main class for the grade activity
public class GradeActivity {
    private double score;
    private final JFrame frame = new JFrame("Mini Blackboard");
    private final List<Course> courses = new ArrayList<>();
    private final List<String> students = new ArrayList<>();
    private final Map<String, Double> grades = new LinkedHashMap<>();
// Inner classes for Course, Test, and Question

    static class Course {
        String name;
        List<String> enrolled = new ArrayList<>();
        List<Test> tests = new ArrayList<>();
        Course(String name) { this.name = name; }
        public String toString() { return name; }
    }
    static class Test {
        String name;
        List<Question> questions = new ArrayList<>();
        Test(String name) { this.name = name; }
        public String toString() { return name; }
    }
    static class Question {
        String prompt, a, b;
        int correct;
        Question(String prompt, String a, String b, int correct) {
            this.prompt = prompt; this.a = a; this.b = b; this.correct = correct;
        }
    }
// Methods for GradeActivity such as setting score, getting score, and determining letter grade
    public GradeActivity() { score = 0.0; }
    public void setScore(double s) { score = s; }
    public double getScore() { return score; }
    public char getGrade() {
        if (score >= 90) return 'A';
        else if (score >= 80) return 'B';
        else if (score >= 70) return 'C';
        else if (score >= 60) return 'D';
        else return 'F';
    }
// GUI methods for the application such as creating buttons
    private JButton button(String label, Runnable action) {
        JButton b = new JButton(label);
        b.addActionListener(e -> action.run());
        return b;
    }
    private void page(String heading, JButton... buttons) {
        JPanel p = new JPanel(new GridLayout(0, 1, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        JLabel title = new JLabel(heading, SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        p.add(title);
        for (JButton b : buttons) p.add(b);
        frame.setContentPane(p);
        frame.revalidate();
        frame.repaint();
    }
    private String ask(String message) {
        String s = JOptionPane.showInputDialog(frame, message);
        return s == null || s.trim().isEmpty() ? null : s.trim();
    }
    private <T> T choose(String message, List<T> items) {
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Nothing available yet.");
            return null;
        }
        @SuppressWarnings("unchecked")
        T selected = (T) JOptionPane.showInputDialog(frame, message, "Mini Blackboard",
                JOptionPane.QUESTION_MESSAGE, null, items.toArray(), items.get(0));
        return selected;
    }
    private void message(String text) { JOptionPane.showMessageDialog(frame, text); }
// Main GUI methods for the application such as home, teacher, student, and their respective functionalities
    
    private void home() {
        page("Mini Blackboard", button("Teacher Dashboard", this::teacher),
                button("Student Dashboard", this::student), button("Exit", frame::dispose));
    }
    private void teacher() {
        page("Teacher Dashboard", button("Add Class", this::addClass),
                button("Add Student", this::addStudent),
                button("Enroll Student", this::enroll),
                button("Create Test", this::createTest),
                button("View Grades", this::viewGrades), button("Back", this::home));
    }
    private void addClass() {
        String name = ask("Class name (e.g. COMP 101):");
        if (name != null) { courses.add(new Course(name)); message("Class created!"); }
        teacher();
    }
    private void addStudent() {
        String name = ask("Student name:");
        if (name != null && !students.contains(name)) {
            students.add(name);
            message("Student added!");
        }
        teacher();
    }
    private void enroll() {
        String name = choose("Select student:", students);
        if (name != null) {
            Course c = choose("Select class:", courses);
            if (c != null) {
                if (!c.enrolled.contains(name)) c.enrolled.add(name);
                message(name + " enrolled in " + c.name);
            }
        }
        teacher();
    }
    private void createTest() {
        Course c = choose("Choose class:", courses);
        if (c == null) { teacher(); return; }
        String name = ask("Test name:");
        if (name == null) { teacher(); return; }
        Test test = new Test(name);
        while (true) {
            String prompt = ask("Enter a question (Cancel to finish):");
            if (prompt == null) break;
            String a = ask("Answer A:");
            if (a == null) break;
            String b = ask("Answer B:");
            if (b == null) break;
            String correct = ask("Correct answer (A or B):");
            if (correct == null) break;
            if (!correct.equalsIgnoreCase("A") && !correct.equalsIgnoreCase("B")) {
                message("Please enter A or B. Question not added.");
                continue;
            }
            test.questions.add(new Question(prompt, a, b, correct.equalsIgnoreCase("A") ? 0 : 1));
        }
        if (!test.questions.isEmpty()) {
            c.tests.add(test);
            message("Test created with " + test.questions.size() + " questions!");
        } else message("No questions added; test was not saved.");
        teacher();
    }
    private void viewGrades() {
        if (grades.isEmpty()) { message("No grades yet."); return; }
        StringBuilder sb = new StringBuilder("Grades:\n\n");
        grades.forEach((key, value) -> {
            setScore(value);
            sb.append(key).append(": ").append(String.format("%.1f", value))
              .append("% (").append(getGrade()).append(")\n");
        });
        JTextArea area = new JTextArea(sb.toString(), 15, 40);
        area.setEditable(false);
        JOptionPane.showMessageDialog(frame, new JScrollPane(area), "Gradebook", JOptionPane.INFORMATION_MESSAGE);
    }
    private void student() {
        String name = choose("Select your student name:", students);
        if (name == null) { home(); return; }
        studentDashboard(name);
    }
    private void studentDashboard(String name) {
        page("Student: " + name,
                button("View My Classes", () -> viewClasses(name)),
                button("Take Test", () -> selectTest(name)),
                button("My Grades", () -> myGrades(name)),
                button("Back", this::home));
    }
    private List<Course> enrolledCourses(String name) {
        List<Course> result = new ArrayList<>();
        for (Course c : courses) if (c.enrolled.contains(name)) result.add(c);
        return result;
    }
    private void viewClasses(String name) {
        List<Course> list = enrolledCourses(name);
        if (list.isEmpty()) message("You are not enrolled in any classes.");
        else {
            StringBuilder sb = new StringBuilder("Your classes:\n");
            for (Course c : list) sb.append("- ").append(c.name).append("\n");
            message(sb.toString());
        }
        studentDashboard(name);
    }
    private void selectTest(String name) {
        Course c = choose("Choose your class:", enrolledCourses(name));
        if (c == null) { studentDashboard(name); return; }
        Test t = choose("Choose a test:", c.tests);
        if (t == null) { studentDashboard(name); return; }
        takeTest(name, c, t);
    }
    private void takeTest(String student, Course course, Test test) {
        String key = student + " | " + course.name + " | " + test.name;
        if (grades.containsKey(key)) {
            message("You have already submitted this test.");
            studentDashboard(student);
            return;
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        List<ButtonGroup> groups = new ArrayList<>();
        List<JRadioButton> correctButtons = new ArrayList<>();
        int number = 1;
        for (Question q : test.questions) {
            panel.add(new JLabel(number++ + ". " + q.prompt));
            JRadioButton a = new JRadioButton("A. " + q.a);
            JRadioButton b = new JRadioButton("B. " + q.b);
            ButtonGroup group = new ButtonGroup();
            group.add(a); group.add(b);
            groups.add(group);
            correctButtons.add(q.correct == 0 ? a : b);
            panel.add(a); panel.add(b);
            panel.add(Box.createVerticalStrut(10));
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setPreferredSize(new Dimension(460, 320));
        while (true) {
            int result = JOptionPane.showConfirmDialog(frame, scroll, test.name,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) break;
            boolean incomplete = false;
            for (ButtonGroup g : groups) if (g.getSelection() == null) incomplete = true;
            if (incomplete) { message("Please answer all questions."); continue; }
            int correct = 0;
            for (JRadioButton b : correctButtons) if (b.isSelected()) correct++;
            setScore(100.0 * correct / test.questions.size());
            grades.put(key, getScore());
            message("Exam submitted!\nCorrect: " + correct + "/" + test.questions.size()
                    + "\nScore: " + String.format("%.1f", getScore()) + "%\nGrade: " + getGrade());
            break;
        }
        studentDashboard(student);
    }
    private void myGrades(String name) {
        StringBuilder sb = new StringBuilder("Your grades:\n\n");
        boolean found = false;
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            if (entry.getKey().startsWith(name + " | ")) {
                found = true;
                setScore(entry.getValue());
                sb.append(entry.getKey().substring(name.length() + 3)).append(": ")
                  .append(String.format("%.1f", getScore())).append("% (")
                  .append(getGrade()).append(")\n");
            }
        }
        message(found ? sb.toString() : "No grades yet.");
        studentDashboard(name);
    }
    public void showExam() {
        frame.setSize(520, 460);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        home();
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradeActivity().showExam());
    }
}

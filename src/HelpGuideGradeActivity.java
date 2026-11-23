import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.swing.*;

// Extends GradeActivity with a Help Guide (password resets) and a Search menu.
// Teachers search all students, classes, and tests on the Teacher Dashboard.
// Students search only their own classes and grades on their dashboard.
public class HelpGuideGradeActivity extends GradeActivity {

    private static final int MIN_STUDENT_PASSWORD = 6;
    private static final String STUDENT_HEADING = "Student: ";
    private final StudentPasswords studentPasswords;

    // null on the Teacher Dashboard (search everything); otherwise the logged-in student's name
    private String searchScope;
    private JMenuBar searchBar;

    public HelpGuideGradeActivity() {
        try {
            studentPasswords = new StudentPasswords();
        } catch (IOException e) {
            throw new RuntimeException("Could not load student passwords", e);
        }
    }

    // ---- Search menu: shown only on the Teacher and Student dashboards ----
    @Override 
    public void page(String heading, JButton... buttons) {
        super.page(heading, buttons);
        boolean teacher = heading.equals("Teacher Dashboard");
        boolean student = heading.startsWith(STUDENT_HEADING);
        if (teacher) searchScope = null;
        else if (student) searchScope = heading.substring(STUDENT_HEADING.length());
        getFrame().setJMenuBar(teacher || student ? getSearchBar() : null);
        getFrame().revalidate();
        getFrame().repaint();
    }

    private JMenuBar getSearchBar() {
        if (searchBar == null) {
            searchBar = new JMenuBar();
            JMenu menu = new JMenu("Search");
            JMenuItem open = new JMenuItem("Search");
            open.addActionListener(e -> openSearch());
            menu.add(open);
            searchBar.add(menu);
        }
        return searchBar;
    }

    private void openSearch() {
        JDialog dialog = new JDialog(getFrame(), "Search", true);
        JTextField input = new JTextField(20);
        JTextArea results = new JTextArea(18, 45);
        results.setEditable(false);
        JButton go = new JButton("Search");

        Runnable run = () -> results.setText(search(input.getText()));
        go.addActionListener(e -> run.run());
        input.addActionListener(e -> run.run());

        JPanel top = new JPanel(new BorderLayout(6, 6));
        top.add(new JLabel("Name, class, or test:"), BorderLayout.WEST);
        top.add(input, BorderLayout.CENTER);
        top.add(go, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(top, BorderLayout.NORTH);
        root.add(new JScrollPane(results), BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setLocationRelativeTo(getFrame());
        dialog.setVisible(true);
    }

    private String search(String query) {
        String q = query.trim().toLowerCase();
        if (q.isEmpty()) return "Type part of a name, class, or test.";
        return searchScope == null ? searchAll(q) : searchOwn(searchScope, q);
    }

    // Teacher: everything
    private String searchAll(String q) {
        StringBuilder out = new StringBuilder();
        boolean any = false;

        for (String student : getStudents()) {
            if (!student.toLowerCase().contains(q)) continue;
            any = true;
            out.append("STUDENT: ").append(student).append("\n");
            List<String> classNames = new java.util.ArrayList<>();
            for (GradeActivity.Course c : getCourses())
                if (c.enrolled.contains(student)) classNames.add(c.name);
            out.append("  Classes: ")
               .append(classNames.isEmpty() ? "none" : String.join(", ", classNames))
               .append("\n");
            for (Map.Entry<String, Double> g : getGrades().entrySet()) {
                String[] parts = g.getKey().split(" \\| ");
                if (parts.length == 3 && parts[0].equals(student))
                    out.append("  ").append(parts[2])
                       .append(" (").append(parts[1]).append("): ")
                       .append(fmt(g.getValue())).append("\n");
            }
            out.append("\n");
        }

        for (GradeActivity.Course c : getCourses()) {
            if (!c.name.toLowerCase().contains(q)) continue;
            any = true;
            out.append("CLASS: ").append(c.name).append("\n");
            out.append("  Students: ")
               .append(c.enrolled.isEmpty() ? "none" : String.join(", ", c.enrolled))
               .append("\n");
            List<String> testNames = new java.util.ArrayList<>();
            for (GradeActivity.Test t : c.tests) testNames.add(t.name);
            out.append("  Tests: ")
               .append(testNames.isEmpty() ? "none" : String.join(", ", testNames))
               .append("\n\n");
        }

        for (GradeActivity.Course c : getCourses())
            for (GradeActivity.Test t : c.tests) {
                if (!t.name.toLowerCase().contains(q)) continue;
                any = true;
                out.append("TEST: ").append(t.name)
                   .append(" (").append(c.name)
                   .append(", ").append(t.questions.size())
                   .append(" questions)\n");
                for (Map.Entry<String, Double> g : getGrades().entrySet()) {
                    String[] parts = g.getKey().split(" \\| ");
                    if (parts.length == 3 && parts[1].equals(c.name) && parts[2].equals(t.name))
                        out.append("  ").append(parts[0])
                           .append(": ").append(fmt(g.getValue())).append("\n");
                }
                out.append("\n");
            }

        return any ? out.toString()
                   : "No students, classes, or tests match \"" + q + "\".";
    }

    // Student: only the classes they are enrolled in and their own grades
    private String searchOwn(String student, String q) {
        StringBuilder out = new StringBuilder();
        boolean any = false;

        for (GradeActivity.Course c : getCourses()) {
            if (!c.enrolled.contains(student)) continue;
            boolean classMatches = c.name.toLowerCase().contains(q);
            StringBuilder lines = new StringBuilder();
            for (GradeActivity.Test t : c.tests) {
                if (!classMatches && !t.name.toLowerCase().contains(q)) continue;
                Double score = getGrades().get(student + " | " + c.name + " | " + t.name);
                lines.append("  ").append(t.name).append(": ")
                     .append(score == null ? "not taken yet" : fmt(score)).append("\n");
            }
            if (classMatches || lines.length() > 0) {
                any = true;
                out.append("CLASS: ").append(c.name).append("\n").append(lines).append("\n");
            }
        }

        return any ? out.toString()
                   : "No classes or tests of yours match \"" + q + "\".";
    }

    // Formats a score as "85.0% (B)" using the parent's grade rules
    private String fmt(double score) {
        setScore(score);
        return String.format("%.1f%% (%c)", score, getGrade());
    }

    // ---- Help Guide window (Teacher and Student tabs) ----

    @Override
    protected void showHelpGuide() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Teacher", teacherHelp());
        tabs.addTab("Student", studentHelp());

        JDialog dialog = new JDialog(getFrame(), "Help Guide", true);
        dialog.setContentPane(tabs);
        dialog.setSize(460, 320);
        dialog.setLocationRelativeTo(getFrame());
        dialog.setVisible(true);
    }

    private JPanel teacherHelp() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        p.add(new JLabel("<html>The Teacher Dashboard lets you add classes and students, "
                + "enroll students, create tests, and view grades.<br><br>"
                + "Search finds any student, class, or test.<br><br>"
                + "To change the teacher password, enter the current password first.</html>"),
                BorderLayout.CENTER);
        JButton reset = new JButton("Reset Teacher Password");
        reset.addActionListener(e -> TeacherLogin.changePassword(getFrame()));
        p.add(reset, BorderLayout.SOUTH);
        return p;
    }

    private JPanel studentHelp() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        p.add(new JLabel("<html>Students pick their name and then enter their password. "
                + "Students without a password can log in until one is set.<br><br>"
                + "On your dashboard, Search shows your classes, tests, and grades.<br><br>"
                + "The teacher sets or resets a student's password here. "
                + "Teacher login is required.</html>"),
                BorderLayout.CENTER);
        JButton reset = new JButton("Reset Student Password");
        reset.addActionListener(e -> resetStudentPassword());
        p.add(reset, BorderLayout.SOUTH);
        return p;
    }

    // Teacher picks a student and sets a new password for them
    private void resetStudentPassword() {
        if (!TeacherLogin.verifyAccess(getFrame())) return;

        List<String> students = getStudents();
        if (students.isEmpty()) {
            JOptionPane.showMessageDialog(getFrame(), "No students yet.");
            return;
        }
        String name = (String) JOptionPane.showInputDialog(getFrame(), "Select student:",
                "Reset Student Password", JOptionPane.QUESTION_MESSAGE, null,
                students.toArray(), students.get(0));
        if (name == null) return;

        char[] password = promptPassword("New password for " + name + ":");
        if (password == null) return;
        try {
            if (password.length < MIN_STUDENT_PASSWORD) {
                JOptionPane.showMessageDialog(getFrame(),
                        "Password must be at least " + MIN_STUDENT_PASSWORD + " characters.");
                return;
            }
            studentPasswords.setPassword(name, password);
            JOptionPane.showMessageDialog(getFrame(), "Password set for " + name + ".");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getFrame(), "Could not save password: " + e.getMessage());
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // ---- Student login check (hook called by GradeActivity.student()) ----

    @Override
    protected boolean checkStudentPassword(String name) {
        if (!studentPasswords.hasPassword(name)) return true; // no password set yet
        char[] password = promptPassword("Password for " + name + ":");
        if (password == null) return false;
        try {
            if (studentPasswords.verify(name, password)) return true;
            JOptionPane.showMessageDialog(getFrame(), "Incorrect password.");
            return false;
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // Masked password box; returns null if cancelled
    private char[] promptPassword(String label) {
        JPasswordField field = new JPasswordField(15);
        int result = JOptionPane.showConfirmDialog(getFrame(), new Object[]{label, field},
                "Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION ? field.getPassword() : null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HelpGuideGradeActivity().showExam());
    }
}
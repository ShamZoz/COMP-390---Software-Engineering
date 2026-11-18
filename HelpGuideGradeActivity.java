import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Extends GradeActivity with a Help Guide that lets teachers reset passwords
public class HelpGuideGradeActivity extends GradeActivity {

    private static final int MIN_STUDENT_PASSWORD = 6;
    private final StudentPasswords studentPasswords;

    public HelpGuideGradeActivity() {
        try {
            studentPasswords = new StudentPasswords();
        } catch (IOException e) {
            throw new RuntimeException("Could not load student passwords", e);
        }
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
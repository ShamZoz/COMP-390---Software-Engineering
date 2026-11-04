import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.io.*;

// Main class for the grade activity
public class GradeActivity {
    private double score;
    private final JFrame frame = new JFrame("Mini Blackboard");
    private final List<Course> courses = new ArrayList<>();
    private final List<String> students = new ArrayList<>();
    private final Map<String, Double> grades = new LinkedHashMap<>();
// Inner classes for Course, Test, and Question

    static class Course implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
        String name;
        List<String> enrolled = new ArrayList<>();
        List<Test> tests = new ArrayList<>();
        Course(String name) { this.name = name; }
        public String toString() { return name; }
    }
    static class Test implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
        String name;
        List<Question> questions = new ArrayList<>();
        Test(String name) { this.name = name; }
        public String toString() { return name; }
    }
    static class Question implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
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
    // Cosmetic styling only: keep every original button action.
    private JButton button(String label, Runnable action) {
        JButton b = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint rounded cards consistently, including on macOS.
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getBackground();
                if (getModel().isPressed()) base = base.darker();
                else if (getModel().isRollover()) base = base.brighter();
                g2.setPaint(new GradientPaint(
                    0, 0, base, getWidth(), getHeight(), base.darker()
                ));
                g2.fillRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 24, 24);

                // Keep a visible focus outline for keyboard navigation.
                if (isFocusOwner()) {
                    g2.setColor(new Color(25, 30, 55));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(5, 5, getWidth() - 11,
                                     getHeight() - 11, 20, 20);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.addActionListener(e -> action.run());

        // Let the custom background show through the standard button label.
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setFont(new Font("SansSerif", Font.BOLD, 17));
        b.setForeground(new Color(24, 29, 48));
        b.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        b.setPreferredSize(new Dimension(280, 104));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void page(String heading, JButton... buttons) {
        // Rainbow palette: dark labels stay readable on the bright cards.
        Color[] rainbow = {
            new Color(255, 139, 157), // Red
            new Color(255, 183, 105), // Orange
            new Color(255, 222, 104), // Yellow
            new Color(120, 222, 170), // Green
            new Color(116, 190, 255), // Blue
            new Color(167, 158, 255), // Indigo
            new Color(221, 155, 251)  // Violet
        };
        Color background = new Color(19, 23, 40);
        Color muted = new Color(177, 187, 212);
        JPanel p = new JPanel(new BorderLayout(0, 24));
        p.setBackground(background);
        p.setBorder(BorderFactory.createEmptyBorder(28, 30, 22, 30));

        // A full rainbow stripe gives every screen the same visual identity.
        JPanel stripe = new JPanel(new GridLayout(1, rainbow.length));
        stripe.setPreferredSize(new Dimension(1, 7));
        for (Color color : rainbow) {
            JPanel segment = new JPanel();
            segment.setBackground(color);
            stripe.add(segment);
        }
        JPanel headings = new JPanel();
        headings.setLayout(new BoxLayout(headings, BoxLayout.Y_AXIS));
        headings.setOpaque(false);
        headings.setBorder(BorderFactory.createEmptyBorder(20, 0, 4, 0));

        JLabel brand = new JLabel("MINI BLACKBOARD / YOUR LEARNING SPACE");
        brand.setFont(new Font("SansSerif", Font.BOLD, 11));
        brand.setForeground(rainbow[6]);
        JLabel title = new JLabel(heading);
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Choose your next step. Make it a bright one.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(muted);
        headings.add(brand);
        headings.add(Box.createVerticalStrut(10));
        headings.add(title);
        headings.add(Box.createVerticalStrut(8));
        headings.add(subtitle);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(stripe, BorderLayout.NORTH);
        header.add(headings, BorderLayout.CENTER);
        p.add(header, BorderLayout.NORTH);

        // Two columns replace the long list; original button order is preserved.
        JPanel cards = new JPanel(new GridLayout(0, 2, 16, 16));
        cards.setOpaque(false);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setBackground(rainbow[i % rainbow.length]);
            cards.add(buttons[i]);
        }
        JPanel cardArea = new JPanel(new BorderLayout());
        cardArea.setOpaque(false);
        cardArea.add(cards, BorderLayout.NORTH);

        // Scrolling keeps actions reachable if the window is made smaller.
        JScrollPane scroll = new JScrollPane(cardArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );
        JPanel dashboard = new JPanel(new BorderLayout(0, 14));
        dashboard.setOpaque(false);
        JLabel section = new JLabel("YOUR DASHBOARD");
        section.setFont(new Font("SansSerif", Font.BOLD, 11));
        section.setForeground(muted);
        dashboard.add(section, BorderLayout.NORTH);
        dashboard.add(scroll, BorderLayout.CENTER);
        p.add(dashboard, BorderLayout.CENTER);

        JLabel footer = new JLabel("Learn. Create. Achieve. / Mini Blackboard");
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(muted);
        p.add(footer, BorderLayout.SOUTH);

        frame.setContentPane(p);
        frame.revalidate();
        frame.repaint();
    }
    public String ask(String message) {
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
    page("Mini Blackboard",
        button("Teacher Dashboard", this::teacherLogin),
        button("Student Dashboard", this::student),
        button("Exit", frame::dispose)
    );
}
    
    private void teacher() {

    if (!teacherAuthenticated) {
        teacherLogin();
        return;
    }

    page("Teacher Dashboard",
        button("Add Class", this::addClass),
        button("Add Student", this::addStudent),
        button("Enroll Student", this::enroll),
        button("Create Test", this::createTest),
        button("View Grades", this::viewGrades),
        button("Logout", this::teacherLogout));

}
private boolean teacherAuthenticated = false;

// Verify the teacher before allowing access
private void teacherLogin() {
    if (TeacherLogin.verifyAccess(frame)) {
        teacherAuthenticated = true;
        teacher();
    }
}

// Log out of the teacher dashboard
private void teacherLogout() {
    teacherAuthenticated = false;
    home();
}
private boolean requireTeacher() {
    if (!teacherAuthenticated) {
        message("Access Denied. Teacher login required.");
        home();
        return false;
    }
    return true;
}
    private void addClass() {
        if (!requireTeacher()) return;
        String name = ask("Class name (e.g. COMP 101):");
        if (name != null) {
            courses.add(new Course(name));
            saveData();
            message("Class created!");
        }
        teacher();
    }

    private void addStudent() {
     if (!requireTeacher()) return;
        String name = ask("Student name:");
        if (name != null && !students.contains(name)) {
            students.add(name);
            saveData();
            message("Student added!");
        }
        teacher();
    }
    private void enroll() {
     if (!requireTeacher()) return;
        String name = choose("Select student:", students);
        if (name != null) {
            Course c = choose("Select class:", courses);
            if (c != null) {
                if (!c.enrolled.contains(name)) c.enrolled.add(name);
                saveData();
                message(name + " enrolled in " + c.name);
            }
        }
        teacher();
    }
    private void createTest() {
     if (!requireTeacher()) return;
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
            saveData();
            message("Test created with " + test.questions.size() + " questions!");
        } else message("No questions added; test was not saved.");
        teacher();
    }
    private void viewGrades() {
        if (!requireTeacher()) return;  
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
            saveData();
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
        // Wider frame fits two columns of rainbow dashboard cards.
        frame.setSize(820, 700);
        frame.setMinimumSize(new Dimension(680, 580));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        loadData();

        home();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GradeActivity().showExam());
    }

    // Mini Blackboard Data to keep and store all information
    private static final String DATA_FILE = "blackboard.dat";

    // Save all application data
    private void saveData() {
        MiniBlackboardData data =
             new MiniBlackboardData(courses, students, grades);

        try (ObjectOutputStream out =
                new ObjectOutputStream(
                    new FileOutputStream(DATA_FILE))) {

            out.writeObject(data);
            System.out.println("Data saved successfully.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                frame, "Error saving data: " + e.getMessage()
            );
        }
    }

    // Load previously saved data
    private void loadData() {
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream in =
                new ObjectInputStream(
                    new FileInputStream(file))) {

            MiniBlackboardData data =
                (MiniBlackboardData) in.readObject();

            courses.clear();
            courses.addAll(data.courses);

            students.clear();
            students.addAll(data.students);

            grades.clear();
            grades.putAll(data.grades);

            System.out.println("Data loaded successfully.");

        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                frame, "Error loading data: " + e.getMessage()
            );
        }
    }
    
}

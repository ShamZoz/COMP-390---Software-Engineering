import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GradebookApp extends JFrame {
    // student name and student scores
    static class StudentScore {
        String name;
        double score;

        StudentScore(String name, double score) {
            this.name = name;
            this.score = score;
        }
    }
    static class Assignment {
        String id;
        String title;
        double maxScore;
        List<StudentScore> scores = new ArrayList<>();

        Assignment(String title, double maxScore) {
            this.id = UUID.randomUUID().toString();
            this.title = title;
            this.maxScore = maxScore;
        }
        @Override
        public String toString() {
            return title + " (Max : " + maxScore + " pts)";
        }
    }
    private final DefaultListModel<Assignment> assignmentListModel = new DefaultListModel<>();
    private final JList<Assignment> assignmentList = new JList<>(assignmentListModel);

    private final DefaultTableModel scoreTableModel = new DefaultTableModel(new Object[]{"Student Name", "Score", "Percentage"}, 0)
    {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; //read-only directly in table; edit via buttons
        }
    };
    private final JTable scoreTable = new JTable(scoreTableModel);
    private final JLabel assigmentDetailsLabel = new JLabel("Select an assigment to view and enter grades");

    public GradebookApp() {
        setTitle("Simple Gradebook and & Assigment manager");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        //populate the sample data
        initSampleData();

        //the main layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createAssignmentPanel(), createGradesPanel());
        splitPane.setDividerLocation(320);
        add(splitPane, BorderLayout.CENTER);

        // the selection listener
        assignmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                refreshGradesTable();
            }
        });
        // Slect the first by default
        if (!assignmentListModel.isEmpty()) {
            assignmentList.setSelectedIndex(0);
        }
    }

    private JPanel createAssignmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Assignments"));
        panel.add(new JScrollPane(assignmentList), BorderLayout.CENTER);

        //Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        //actions
        btnAdd.addActionListener(e -> addAssignment());
        btnEdit.addActionListener(e -> editAssignment());
        btnDelete.addActionListener(e -> deleteAssignment());
        return panel;
    }

    private JPanel createGradesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Student Grades"));

        panel.add(assigmentDetailsLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(scoreTable), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddScore = new JButton("Add/UPGRADTED Score");
        JButton btnDeleteGrade = new JButton("Remove Grade");

        buttonPanel.add(btnAddScore);
        buttonPanel.add(btnDeleteGrade);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        //Actions
        btnAddScore.addActionListener(e -> addOrUpdateGrade());
        btnDeleteGrade.addActionListener(e -> deleteGrade());
        return panel;
    }

    // Assigment Operations

    private void addAssignment() {
        JTextField titleField = new JTextField();
        JTextField maxScoreField = new JTextField("100");
        
        Object[] message = {
            "Title:", titleField,
            "Max Score:", maxScoreField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "New Assignment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                double max = Double.parseDouble(maxScoreField.getText().trim());
                Assignment a = new Assignment(title, max);
                assignmentListModel.addElement(a);
                assignmentList.setSelectedValue(a, true);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Max score must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editAssignment() {
        Assignment selected = assignmentList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField titleField = new JTextField(selected.title);
        JTextField maxScoreField = new JTextField(String.valueOf(selected.maxScore));
        Object[] message = {
            "Title:", titleField,
            "Max Score:", maxScoreField
        };
        int option = JOptionPane.showConfirmDialog(this, message, "Edit Assignment", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String newTitle = titleField.getText().trim();
            if (newTitle.isEmpty()) return;
            try {
                double newMax = Double.parseDouble(maxScoreField.getText().trim());
                selected.title = newTitle;
                selected.maxScore = newMax;
                assignmentList.repaint();
                refreshGradesTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Max score must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAssignment() {
        int index = assignmentList.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Please select an assignment to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, " Delete this assigment and all its grades?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            assignmentListModel.remove(index);
            refreshGradesTable();
        }
    }

    // grade operations

    private void addOrUpdateGrade() {
        Assignment selected = assignmentList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select an assignment first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTextField nameField = new JTextField();
        JTextField scoreField = new JTextField();

        // If a row is a selected, pre-fill the fields with existing data
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow != -1) {
            nameField.setText((String) scoreTableModel.getValueAt(selectedRow, 0));
            scoreField.setText(String.valueOf(scoreTableModel.getValueAt(selectedRow, 1)));
        }

        Object[] message = {
            "Student Name:", nameField,
            "Score ( Out of " + selected.maxScore + "):", scoreField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Enter Grade", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;

            try {
                double score = Double.parseDouble(scoreField.getText().trim());

                //Checking if student exist in this assigment if so update other then that add
                boolean found = false;
                for (StudentScore s : selected.scores) {
                    if (s.name.equalsIgnoreCase(name)) {
                        s.score = score;
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    selected.scores.add(new StudentScore(name, score));
                }
                refreshGradesTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Score must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }  
    
    private void deleteGrade() {
        Assignment selected = assignmentList.getSelectedValue();
        int selectedRow = scoreTable.getSelectedRow();

        if (selected == null || selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student record to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String studentName = (String) scoreTableModel.getValueAt(selectedRow, 0);
        selected.scores.removeIf(s -> s.name.equalsIgnoreCase(studentName));
        refreshGradesTable();
    }

    private void refreshGradesTable() {
        scoreTableModel.setRowCount(0);
        Assignment selected = assignmentList.getSelectedValue();
        if (selected == null) {
            assigmentDetailsLabel.setText("No assigment selected");
            return;
        }

        assigmentDetailsLabel.setText("Grades for: " + selected.title + " (Out of " + selected.maxScore + "pts)");

        for (StudentScore s : selected.scores) {
            double percent = (s.score / selected.maxScore) * 100.0;
            scoreTableModel.addRow(new Object[]{
                s.name,
                s.score,
                String.format("%.2f%%", percent)
            });
        }
    }

    private void initSampleData() {
        Assignment a1 = new Assignment("Math Quiz 1 ", 50);
        a1.scores.add(new StudentScore("Alice Wilson", 45));
        a1.scores.add(new StudentScore("Travis Smith", 60));

        Assignment a2 = new Assignment("Science Project", 100);
        a2.scores.add(new StudentScore("Alice Wilson", 85));
        a2.scores.add(new StudentScore("Travis Smith", 90));

        assignmentListModel.addElement(a1);
        assignmentListModel.addElement(a2);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GradebookApp().setVisible(true);
        });
    }
}

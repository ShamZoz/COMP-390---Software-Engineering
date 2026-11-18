import javax.swing.*;                       
import java.awt.*;                          
import java.lang.reflect.Field;             
import java.util.*;                         
import java.util.List;                      


public class SearchGradeActivity extends GradeActivity {

    // References to the parent's private data, filled in when the app starts
    private JFrame frame;                   
    private List<String> students;          // all student names (private in GradeActivity)
    private List<Course> courses;           // all classes, each holding its enrolled students and tests
    private Map<String, Double> grades;     // grade records with keys like "Mailcky | COMP 351 | Test 1"

    // Overrides the parent's startup method so we can add our menu after the window is created
    @Override
    public void showExam() {
        try {
            frame = field("frame");         
            students = field("students");   
            courses = field("courses");     
            grades = field("grades");       
        } catch (Exception e) {
            throw new RuntimeException("Could not read GradeActivity data", e);  
        }

        super.showExam();                   

        JMenuBar bar = new JMenuBar();      // create an empty menu bar
        JMenu menu = new JMenu("Search");   // create a menu titled "Search"
        JMenuItem open = new JMenuItem("Find students, classes, or tests");  // the clickable entry in that menu
        open.addActionListener(e -> openSearch());  // when clicked, open the search window
        menu.add(open);                     // put the item inside the menu
        bar.add(menu);                      // put the menu inside the bar
        frame.setJMenuBar(bar);             // attach the bar to the window; it stays visible on every page
        frame.revalidate();                 // tell Swing to redraw the layout with the new bar
    }

    // Helper that reads one private field from the parent class using reflection
    @SuppressWarnings("unchecked")          
    private <T> T field(String name) throws Exception {
        Field f = GradeActivity.class.getDeclaredField(name);  // find the field by its name in GradeActivity
        f.setAccessible(true);              
        return (T) f.get(this);             // read its value from this object and cast it to the needed type
    }

    // Opens a popup window that contains a search box and a results area
    private void openSearch() {
        JDialog dialog = new JDialog(frame, "Search", true);  // modal window: blocks the main window until closed
        JTextField input = new JTextField(20);                // text box where the user types the search
        JTextArea results = new JTextArea(18, 45);            // large read-only area for the results
        results.setEditable(false);                           // the user can read results but not type in them
        JButton go = new JButton("Search");                   // button that starts the search

        Runnable run = () -> results.setText(search(input.getText()));  // run search and show output in results
        go.addActionListener(e -> run.run());                 // clicking the button runs the search
        input.addActionListener(e -> run.run());              // pressing Enter in the text box also searches

        JPanel top = new JPanel(new BorderLayout(6, 6));      // row holding label, text box, and button
        top.add(new JLabel("Name, class, or test:"), BorderLayout.WEST);  // label on the left
        top.add(input, BorderLayout.CENTER);                  // text box in the middle, stretches to fill
        top.add(go, BorderLayout.EAST);                       // button on the right

        JPanel root = new JPanel(new BorderLayout(8, 8));     // main container for the whole dialog
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));  // padding around the edges
        root.add(top, BorderLayout.NORTH);                    // search row at the top
        root.add(new JScrollPane(results), BorderLayout.CENTER);  // results below, scrollable if long

        dialog.setContentPane(root);                          // use this panel as the dialog's content
        dialog.pack();                                        // size the dialog to fit its contents
        dialog.setLocationRelativeTo(frame);                  // center the dialog over the main window
        dialog.setVisible(true);                              // show the dialog (blocks until closed)
    }

    // Builds the text of the search results for the given query
    private String search(String query) {
        String q = query.trim().toLowerCase();                
        if (q.isEmpty()) return "Type part of a student name, class name, or test name.";  // nothing typed yet

        StringBuilder out = new StringBuilder();              // collects all result lines into one string
        boolean any = false;                                  // becomes true if at least one match is found

        // ---- STUDENT SEARCH ----
        for (String student : students) {                     // check every saved student name
            if (!student.toLowerCase().contains(q)) continue; 
            any = true;                                       
            out.append("STUDENT: ").append(student).append("\n");  // printing the student's name as a heading
            List<String> classNames = new ArrayList<>();      // will hold the names of the classes they're in
            for (Course c : courses)                          // look at every class
                if (c.enrolled.contains(student)) classNames.add(c.name);  // keep the ones this student is enrolled in
            out.append("  Classes: ")                         // label for the class list
               .append(classNames.isEmpty() ? "none" : String.join(", ", classNames))  // list them or say "none"
               .append("\n");
            for (Map.Entry<String, Double> g : grades.entrySet()) {  // look through all saved grades
                String[] parts = g.getKey().split(" \\| ");   
                if (parts.length == 3 && parts[0].equals(student))  // keep only grades that belong to this student
                    out.append("  ").append(parts[2])        // show the test name
                       .append(" (").append(parts[1]).append("): ")  // show the class name in brackets
                       .append(fmt(g.getValue())).append("\n");     // show the score and letter grade
            }
            out.append("\n");                                 
        }

        // ---- CLASS SEARCH ----
        for (Course c : courses) {                            // check every class
            if (!c.name.toLowerCase().contains(q)) continue;  // skip classes whose name doesn't match
            any = true;                                       // mark that we found a match
            out.append("CLASS: ").append(c.name).append("\n");     // class name as a heading
            out.append("  Students: ")                        // label for enrolled students
               .append(c.enrolled.isEmpty() ? "none" : String.join(", ", c.enrolled))  // list them or say "none"
               .append("\n");
            List<String> testNames = new ArrayList<>();       
            for (Test t : c.tests) testNames.add(t.name);     // collects each test name in the class
            out.append("  Tests: ")                           // label for the test list
               .append(testNames.isEmpty() ? "none" : String.join(", ", testNames))  // list them or say "none"
               .append("\n\n");                               
        }

        // ---- TEST SEARCH ----
        for (Course c : courses)                              // check every class
            for (Test t : c.tests) {                          // check every test inside that class
                if (!t.name.toLowerCase().contains(q)) continue;  // skip tests whose name doesn't match
                any = true;                                   // mark that we found a match
                out.append("TEST: ").append(t.name)           // test name as a heading
                   .append(" (").append(c.name)               // which class it belongs to
                   .append(", ").append(t.questions.size())   // number of questions it has
                   .append(" questions)\n");
                for (Map.Entry<String, Double> g : grades.entrySet()) {  // look through all saved grades
                    String[] parts = g.getKey().split(" \\| ");  
                    if (parts.length == 3 && parts[1].equals(c.name) && parts[2].equals(t.name))  // match  test exactly
                        out.append("  ").append(parts[0])    // show the student who took it
                           .append(": ").append(fmt(g.getValue())).append("\n");  // show their score and grade
                }
                out.append("\n");                             
            }

        return any ? out.toString()                           
                   : "No students, classes, or tests match \"" + query.trim() + "\".";  // otherwise a no-match message
    }

    // Formats a score as "85.0% (B)" using the parent's own grade rules
    private String fmt(double score) {
        setScore(score);                                      // storing the score in the parent's score field
        return String.format("%.1f%% (%c)", score, getGrade());  // format the number and call getGrade() for the letter
    }

    // Starting the app using this subclass so the Search menu appears
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SearchGradeActivity().showExam());  // create and show the window on Swing's thread
    }
}
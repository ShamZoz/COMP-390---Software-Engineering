// This file creates the database that the system uses
// The code was modified from the example presented in the FindMyStudyPartner_Prototype1 app)

// To do:
// Implement foreign keys and grades
// Implement join tables
// Fix Bugs

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize(Database database) throws SQLException {

        // THIS LINE IS HAVING ISSUES. IT DOESN'T RECOGNIZE STATEMENT.
        // Perhaps another file needs statement defined?
        try (Connection conn = database.getConnection(); Statement st = conn.createStatement()) {


            // Create a student table
            st.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    student_id TEXT PRIMARY KEY AUTOINCREMENT,
                    student_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE
                )
            """);

            // Create a course table
            st.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    course_id TEXT PRIMARY KEY AUTOINCREMENT,
                    course_title TEXT NOT NULL,
                )
            """);

            // Create an assignment table
            st.execute("""
                CREATE TABLE IF NOT EXISTS assignments (
                    assignment_id TEXT PRIMARY KEY AUTOINCREMENT,
                    assignment_title TEXT NOT NULL,
                )
            """);


            // Fill example data into student table
            st.execute("""
                INSERT OR IGNORE INTO students(student_id, student_name, email) VALUES
                ('S01','Cool Kid','coolkid@bridgew.edu'),
                ('S02','Joe','Cool','joecool@msn.com'),
            """);

            // Fill example data into course table
            st.execute("""
                INSERT OR IGNORE INTO courses(course_id, course_title) VALUES
                ('COMP152','Comp Sci 2: The Epic Sequel')
            """);

            // Fill example data into assignment table
            st.execute("""
                INSERT OR IGNORE INTO assignments(assignment_id, assignment_title) VALUES
                ('Test 1','Unit 3.14159265 Exam')
            """);
        }
    }
}

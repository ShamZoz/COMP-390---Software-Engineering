// This file creates the database that the system uses
// The code was modified from the example presented in the FindMyStudyPartner_Prototype1 app)

import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initialize(Database database) throws SQLException {

        // THIS IS HAVING ISSUES. IT DOESN'T RECOGNIZE CONNECTION OR STATEMENT
        try (Connection conn = database.getConnection(); Statement st = conn.createStatement()) {


            // Create a student table
            st.execute("""
                CREATE TABLE IF NOT EXISTS students (
                    student_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    student_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE
                )
            """);

            // Create a course table
            st.execute("""
                CREATE TABLE IF NOT EXISTS courses (
                    course_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    course_title TEXT NOT NULL,
                )
            """);

            // Create an assignment table
            st.execute("""
                CREATE TABLE IF NOT EXISTS assignments (
                    assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    assignment_title TEXT NOT NULL,
                )
            """);
        }
}

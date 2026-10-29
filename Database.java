// Import needed libraries
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Initialize a database for mini blackboard (This code was modified from the example presented in
// the FindMyStudyPartner_Prototype1 app)
public class Database {

    private final String jdbcUrl; // The jdbUrl's purpose is to connect the Java system to the database
                                  // Info source: https://www.baeldung.com/java-jdbc-url-format

    public Database(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }

    // Establish the connection by defining jdbcUrl
    public static Database production() { return new Database("jdbc:sqlite:mini_blackboard.db"); }
    public Connection getConnection() throws SQLException {
        Connection c = DriverManager.getConnection(jdbcUrl);
        c.createStatement().execute("PRAGMA foreign_keys = ON");
        return c;
    }
}

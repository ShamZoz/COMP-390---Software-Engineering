import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.swing.*;

public class TeacherLogin {

    // Hardcoded salt and hash for the program
    private static final String STORED_SALT = "PMlxpO7s1CyTvxnMtfZUPg==";
    private static final String STORED_HASH = "SVz5udQxIOMsps1zO5ZMzANaLvlQgAZSvM5MqnjLF/Q=";

    // Original hardcoded salt and hash. Used only until the teacher changes the password.
    private static final String DEFAULT_SALT = "PMlxpO7s1CyTvxnMtfZUPg==";
    private static final String DEFAULT_HASH = "SVz5udQxIOMsps1zO5ZMzANaLvlQgAZSvM5MqnjLF/Q=";

    // After a change, the salt (line 1) and hash (line 2) are saved in this file
    private static final Path KEY_FILE = Paths.get("teacher.key");
    private static final int MIN_LENGTH = 6;

    // Returns {salt, hash}: from teacher.key if it exists, otherwise the original defaults
    private static byte[][] loadCredentials() throws IOException {
        if (Files.exists(KEY_FILE)) {
            List<String> lines = Files.readAllLines(KEY_FILE);
            return new byte[][]{
                Base64.getDecoder().decode(lines.get(0).trim()),
                Base64.getDecoder().decode(lines.get(1).trim())
            };
        }
        return new byte[][]{
            Base64.getDecoder().decode(DEFAULT_SALT),
            Base64.getDecoder().decode(DEFAULT_HASH)
        };
    }

    private static boolean matches(char[] password) throws IOException {
        byte[][] c = loadCredentials();
        return PasswordSecurity.verifyPassword(password, c[0], c[1]);
    }

    // Shows a masked password box; returns null if the user cancels
    private static char[] promptPassword(JFrame parent, String title, String label) {
        JPasswordField field = new JPasswordField(15);
        int result = JOptionPane.showConfirmDialog(parent, new Object[]{label, field}, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return result == JOptionPane.OK_OPTION ? field.getPassword() : null;
    }

    private static void showError(JFrame parent, IOException e) {
        JOptionPane.showMessageDialog(parent, "Error: " + e.getMessage());
    }

    // Verify the teacher before allowing access
    public static boolean verifyAccess(JFrame parent) {
        char[] password = promptPassword(parent, "Teacher Login", "Password:");
        
        JPasswordField passwordField = new JPasswordField(15);

        int result = JOptionPane.showConfirmDialog(
            parent,
            passwordField,
            "Teacher Login",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }
        
        
        if (password == null) return false;
        try {


            if (matches(password)) {
                JOptionPane.showMessageDialog(parent, "Access Granted!");
                return true;
            }
            JOptionPane.showMessageDialog(parent, "Incorrect Password.");
            return false;
        } catch (IOException e) {
            showError(parent, e);
            return false;
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    // Changes the teacher password. Requires the current password first.
    public static void changePassword(JFrame parent) {
        char[] current = promptPassword(parent, "Change Teacher Password", "Current password:");
        if (current == null) return;
        boolean ok;
        try {
            ok = matches(current);
        } catch (IOException e) {
            showError(parent, e);
            return;
        } finally {
            Arrays.fill(current, '\0');
        }
        if (!ok) {
            JOptionPane.showMessageDialog(parent, "Incorrect current password.");
            return;
        }

        char[] first = promptPassword(parent, "Change Teacher Password", "New password:");
        char[] second = null;
        try {
            if (first == null) return;
            second = promptPassword(parent, "Change Teacher Password", "Confirm new password:");
            if (second == null) return;
            if (!Arrays.equals(first, second)) {
                JOptionPane.showMessageDialog(parent, "Passwords do not match. Password not changed.");
                return;
            }
            if (first.length < MIN_LENGTH) {
                JOptionPane.showMessageDialog(parent, "Password must be at least " + MIN_LENGTH + " characters.");
                return;
            }
            byte[] salt = PasswordSecurity.generateSalt();
            byte[] hash = PasswordSecurity.hashPassword(first, salt);
            Files.write(KEY_FILE, Arrays.asList(
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash)));
            JOptionPane.showMessageDialog(parent, "Teacher password changed.");
        } catch (IOException e) {
            showError(parent, e);
        } finally {
            if (first != null) Arrays.fill(first, '\0');
            if (second != null) Arrays.fill(second, '\0');
        }
    }
}
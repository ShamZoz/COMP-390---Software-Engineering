import javax.swing.*;
import java.util.Base64;
import java.util.Arrays;

public class TeacherLogin {

    // Hardcoded salt and hash for the program
   private static final String STORED_SALT = "PMlxpO7s1CyTvxnMtfZUPg==";
   private static final String STORED_HASH = "SVz5udQxIOMsps1zO5ZMzANaLvlQgAZSvM5MqnjLF/Q=";
    public static boolean verifyAccess(JFrame parent) {

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

        char[] password = passwordField.getPassword();

        try {
            byte[] salt = Base64.getDecoder().decode(STORED_SALT);
            byte[] hash = Base64.getDecoder().decode(STORED_HASH);

            boolean valid = PasswordSecurity.verifyPassword(
                password, salt, hash
            );

            if (valid) {
                JOptionPane.showMessageDialog(parent, "Access Granted!");
                return true;
            }

            JOptionPane.showMessageDialog(parent, "Incorrect Password.");
            return false;

        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
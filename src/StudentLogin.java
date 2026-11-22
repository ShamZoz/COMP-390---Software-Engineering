package src;
import javax.swing.*;
import java.awt.*;
import java.util.*;

public class StudentLogin {
    public static String verifyAccess(JFrame parent, Map<String, StudentAccount> accounts) {
        JTextField username = new JTextField(16);
        JPasswordField password = new JPasswordField(16);
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Username:")); panel.add(username);
        panel.add(new JLabel("Password:")); panel.add(password);
        int result = JOptionPane.showConfirmDialog(parent, panel, "Student Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        char[] entered = password.getPassword();
        try {
            String name = username.getText().trim();
            StudentAccount account = accounts.get(name);
            if (account != null && account.verify(entered)) return name;
            JOptionPane.showMessageDialog(parent, "Invalid username or password.\n" +
                    "For older students, ask the teacher to set a password.");
            return null;
        } finally {
            Arrays.fill(entered, '\0');
            password.setText("");
        }
    }

    public static char[] askNewPassword(JFrame parent, String name) {
        JPasswordField first = new JPasswordField(16);
        JPasswordField confirm = new JPasswordField(16);
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Password:")); panel.add(first);
        panel.add(new JLabel("Confirm:")); panel.add(confirm);
        int result = JOptionPane.showConfirmDialog(parent, panel,
                "Set password for " + name, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return null;
        char[] a = first.getPassword();
        char[] b = confirm.getPassword();
        try {
            if (a.length < 8 || !Arrays.equals(a, b)) {
                JOptionPane.showMessageDialog(parent,
                    "Passwords must match and be at least 8 characters.");
                return null;
            }
            return a.clone();
        } finally {
            Arrays.fill(a, '\0');
            Arrays.fill(b, '\0');
        }
    }
}


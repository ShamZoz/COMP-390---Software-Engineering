import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Stores one salted, hashed password per student in students.pw (name, salt, hash; tab-separated)
public class StudentPasswords {

    private static final Path FILE = Paths.get("students.pw");

    // student name -> {salt, hash}
    private final Map<String, byte[][]> records = new LinkedHashMap<>();

    public StudentPasswords() throws IOException {
        if (!Files.exists(FILE)) return;
        for (String line : Files.readAllLines(FILE)) {
            String[] parts = line.split("\t");
            if (parts.length == 3) {
                records.put(parts[0], new byte[][]{
                    Base64.getDecoder().decode(parts[1]),
                    Base64.getDecoder().decode(parts[2])
                });
            }
        }
    }

    public boolean hasPassword(String name) {
        return records.containsKey(name);
    }

    public boolean verify(String name, char[] password) {
        byte[][] r = records.get(name);
        return r != null && PasswordSecurity.verifyPassword(password, r[0], r[1]);
    }

    public void setPassword(String name, char[] password) throws IOException {
        byte[] salt = PasswordSecurity.generateSalt();
        byte[] hash = PasswordSecurity.hashPassword(password, salt);
        records.put(name, new byte[][]{salt, hash});
        save();
    }

    private void save() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<String, byte[][]> e : records.entrySet()) {
            lines.add(e.getKey() + "\t"
                    + Base64.getEncoder().encodeToString(e.getValue()[0]) + "\t"
                    + Base64.getEncoder().encodeToString(e.getValue()[1]));
        }
        Files.write(FILE, lines);
    }
}
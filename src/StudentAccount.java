package src;
import java.io.Serializable;


public class StudentAccount implements Serializable {
    private static final long serialVersionUID = 1L;
    private final byte[] salt;
    private final byte[] hash;

    private StudentAccount(byte[] salt, byte[] hash) {
        this.salt = salt.clone();
        this.hash = hash.clone();
    }

    public static StudentAccount create(char[] password) {
        byte[] salt = PasswordSecurity.generateSalt();
        byte[] hash = PasswordSecurity.hashPassword(password, salt);
        return new StudentAccount(salt, hash);
    }

    public boolean verify(char[] password) {
        return PasswordSecurity.verifyPassword(password, salt, hash);
    }
}


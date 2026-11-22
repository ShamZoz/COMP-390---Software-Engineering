package src;
 import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Arrays;

public class PasswordSecurity {
    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
    public static byte[] hashPassword(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("Password hashing failed", e);
        } finally {
            spec.clearPassword();
        }
    }
    public static boolean verifyPassword(char[] password, byte[] salt, byte[] storedHash) {
        byte[] entered = hashPassword(password, salt);
        try { return MessageDigest.isEqual(entered, storedHash); }
        finally { Arrays.fill(entered, (byte) 0); }
    }
}

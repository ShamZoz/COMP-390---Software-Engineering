import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Arrays;

public class PasswordSecurity {

    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;

    // Generate a random salt
    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Hash the password with PBKDF2
    public static byte[] hashPassword(
            char[] password, byte[] salt) {

        PBEKeySpec spec = new PBEKeySpec(
            password, salt, ITERATIONS, KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256"
                );

            return factory.generateSecret(spec)
                          .getEncoded();

        } catch (Exception e) {
            throw new IllegalStateException(
                "Password hashing failed", e
            );
        } finally {
            spec.clearPassword();
        }
    }

    // Verify the entered password
    public static boolean verifyPassword(
            char[] password,
            byte[] salt,
            byte[] storedHash) {

        byte[] enteredHash =
            hashPassword(password, salt);

        try {
            return MessageDigest.isEqual(
                enteredHash, storedHash
            );
        } finally {
            Arrays.fill(enteredHash, (byte) 0);
        }
    }
}


import java.security.MessageDigest;
import java.util.Arrays;

public class CryptoMock {

    public static byte[] sha256(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash160(byte[] data) {
        return Arrays.copyOf(sha256(data), 20);
    }
}
package urlshortener.bangladeshgreen.secure;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used to provide methods for security reasons.
 * This class is used when a user is registered or when logs in.
 * This class has methods to encrypt the password (in one way), for improved security.
 */
public class Hash {

    public static String makeHash(String text) {
        MessageDigest md;
        String response = null;
        try {
            // Acquire the algorithm "SHA-512" for encryption
            md = MessageDigest.getInstance("SHA-512");
            // Uses "UTF-8" encoding for converting to bytes
            md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            // Create the new Hash with specified algorithm
            byte[] digest = md.digest();
            // Formats result string to hexadecimal with left zero padding (for better management).
            response = String.format("%064x", new java.math.BigInteger(1, digest));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Fatal error: could not encrypt text with SHA-512 algorithm.");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Fatal error: UnsupportedEncodingException making hash from text.");
        }
        return response;
    }
}

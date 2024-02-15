/**
 * The {@code EncryptionUtil} class provides utility methods for encrypting and decrypting text using a simple shift cipher.
 * It shifts characters within the English alphabet by a fixed number of positions.
 */
public class EncryptionUtil {
/**
     * The shift offset for the cipher. The number of positions each character in the text will be moved.
     */
    // Simple shift cipher for demonstration purposes
    private static final int SHIFT = 4;

    /**
     * Encrypts the provided text using a shift cipher.
     *
     * @param text The text to be encrypted.
     * @return The encrypted text.
     */
    public static String encrypt(String text) {
        return shift(text, SHIFT);
    }
 /**
     * Decrypts the provided text which was encrypted using the shift cipher.
     *
     * @param text The text to be decrypted.
     * @return The decrypted text.
     */

    public static String decrypt(String text) {
        return shift(text, -SHIFT);
    }

     /**
     * Performs a character shift operation in the text.
     *
     * @param text  The input text to be shifted.
     * @param shift The number of positions to shift the characters. Can be positive for encrypting, negative for decrypting.
     * @return The shifted text.
     */
    private static String shift(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char character : text.toCharArray()) {
            if (character >= 'a' && character <= 'z') {
                char shifted = (char) (((character - 'a' + shift + 26) % 26) + 'a');
                result.append(shifted);
            } else if (character >= 'A' && character <= 'Z') {
                char shifted = (char) (((character - 'A' + shift + 26) % 26) + 'A');
                result.append(shifted);
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }
}

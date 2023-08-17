import java.security.SecureRandom;
import java.util.Base64;

//generator that creates a secretkey once

public class KeyGenerator {

        public static void main(String[] args) {
            //Generate a random array of length 64
            byte[] randomBytes = new byte[64];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(randomBytes);

            //Encode the byte array using Base64
            String encodedKey = Base64.getEncoder().encodeToString(randomBytes);

            System.out.println("Generated Key: " + encodedKey);
        }
}

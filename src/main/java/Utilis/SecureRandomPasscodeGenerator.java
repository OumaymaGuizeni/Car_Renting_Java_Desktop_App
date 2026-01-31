package Utilis;

import java.security.SecureRandom;

public class SecureRandomPasscodeGenerator {

    // Method to generate a secure random number
    public static String generateRandomPasscode() {
        // Define the character set
        String characters = "0123456789"; // For numeric random number
        int length = 8; // Desired length

        // Create a SecureRandom instance
        SecureRandom secureRandom = new SecureRandom();

        // Generate the random number
        StringBuilder randomNumber = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            randomNumber.append(characters.charAt(randomIndex));
        }

        // Return the result
        return randomNumber.toString();
    }

    // Main method for testing
    public static void main(String[] args) {
        // Generate and save the code into a variable
        String generatedCode = generateRandomPasscode();

        // Print the generated code
        // System.out.println("Secure Random Code: " + generatedCode);
    }
}

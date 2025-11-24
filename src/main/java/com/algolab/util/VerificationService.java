package com.algolab.util;

import com.algolab.model.UserAccount;

public class VerificationService {

    private static String email; // user email
    private static String correctCode; // the 6-digit code
    private static UserAccount pendingUser;

    public static void setPendingUser(UserAccount user) {
        pendingUser = user;
    }

    public static UserAccount getPendingUser() {
        return pendingUser;
    }

    // Call this when user signs up
    public static void startVerification(String emailAddress) {
        email = emailAddress;
        correctCode = generateCode();
    }

    // Generate a 6-digit number as a string
    public static String generateCode() {
        int n = (int) (Math.random() * 1_000_000);
        return String.format("%06d", n);
    }

    // Check if the user input matches the code
    public static boolean isCorrect(String input) {
        return input.equals(correctCode);
    }

    public static String getEmail() {
        return email;
    }

    public static String getCode() {
        return correctCode;
    }
}

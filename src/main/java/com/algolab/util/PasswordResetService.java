package com.algolab.util;

import com.algolab.model.UserAccount;

public class PasswordResetService {

    private static String email;
    private static String correctCode;
    private static boolean isVerified = false;

    // Initiate reset flow
    public static void initiateReset(String emailAddress) {
        email = emailAddress;
        correctCode = VerificationService.generateCode(); // Reuse generation logic
        isVerified = false;
    }

    // Verify the code entered by user
    public static boolean verifyCode(String inputCode) {
        if (inputCode.equals(correctCode)) {
            isVerified = true;
            return true;
        }
        return false;
    }

    // Check if currently verified (security check before allowing password update)
    public static boolean isVerified() {
        return isVerified;
    }

    public static String getEmail() {
        return email;
    }

    public static String getCode() {
        return correctCode;
    }

    // Clear state after successful reset
    public static void clear() {
        email = null;
        correctCode = null;
        isVerified = false;
    }
}

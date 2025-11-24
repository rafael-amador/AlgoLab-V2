package com.algolab.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.prefs.Preferences;

public class SessionManager {

    private static final String PREF_TOKEN_KEY = "session_token";
    private static final Preferences prefs = Preferences.userNodeForPackage(SessionManager.class);
    // In-memory current email for the running app (no persistence)
    private static String currentEmail = null;

    /**
     * Generate a cryptographically secure random token
     */
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Create a new session in the database and save token locally
     * 
     * @param email     User's email
     * @param daysValid Number of days the session should be valid
     * @return true if session was created successfully
     */
    public static boolean createSession(String email, int daysValid) {
        String token = generateToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(daysValid);

        String sql = "INSERT INTO sessions (gmail, token, expires_at) VALUES (?, ?, ?)";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, token);
            stmt.setTimestamp(3, Timestamp.valueOf(expiresAt));

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                saveTokenLocally(token);
                // Store current email in memory for immediate session-aware UI
                currentEmail = email;
                return true;
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Validate if a token exists and hasn't expired
     * 
     * @param token Session token to validate
     * @return true if token is valid and not expired
     */
    public static boolean validateSession(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String sql = "SELECT expires_at FROM sessions WHERE token = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expiresAt = rs.getTimestamp("expires_at");
                LocalDateTime expiration = expiresAt.toLocalDateTime();

                // Check if token hasn't expired
                return LocalDateTime.now().isBefore(expiration);
            }

            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the email associated with a valid session token
     * 
     * @param token Session token
     * @return Email address or null if token is invalid
     */
    public static String getEmailForToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        String sql = "SELECT gmail, expires_at FROM sessions WHERE token = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expiresAt = rs.getTimestamp("expires_at");
                LocalDateTime expiration = expiresAt.toLocalDateTime();

                // Check if token hasn't expired
                if (LocalDateTime.now().isBefore(expiration)) {
                    return rs.getString("gmail");
                }
            }

            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete a session from the database
     * 
     * @param token Session token to delete
     * @return true if session was deleted
     */
    public static boolean deleteSession(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        String sql = "DELETE FROM sessions WHERE token = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token);
            int rows = stmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete all sessions for a specific email
     * 
     * @param email User's email
     * @return true if sessions were deleted
     */
    public static boolean deleteAllSessionsForEmail(String email) {
        String sql = "DELETE FROM sessions WHERE gmail = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clean up expired sessions from the database
     */
    public static void cleanupExpiredSessions() {
        String sql = "DELETE FROM sessions WHERE expires_at < NOW()";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save token to local storage using Java Preferences API
     * 
     * @param token Session token to save
     */
    private static void saveTokenLocally(String token) {
        prefs.put(PREF_TOKEN_KEY, token);
        try {
            prefs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // In-memory accessors for current email (used when user logs in without persistent session)
    public static void setCurrentEmail(String email) {
        currentEmail = email;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }

    /**
     * Load token from local storage
     * 
     * @return Saved token or null if none exists
     */
    public static String loadTokenLocally() {
        return prefs.get(PREF_TOKEN_KEY, null);
    }

    /**
     * Clear token from local storage
     */
    public static void clearLocalToken() {
        prefs.remove(PREF_TOKEN_KEY);
        try {
            prefs.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Complete logout: clear local token and delete from database
     */
    public static void logout() {
        String token = loadTokenLocally();
        if (token != null) {
            deleteSession(token);
        }
        // Ensure the local preference is removed and flushed so other parts
        // of the application (or immediate checks) do not see a stale token.
        clearLocalToken();
        try {
            prefs.flush();
        } catch (Exception e) {
            // flush can throw BackingStoreException on some platforms — log and continue
            e.printStackTrace();
        }
    }
}

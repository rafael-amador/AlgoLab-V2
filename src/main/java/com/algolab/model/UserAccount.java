package com.algolab.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;
import com.algolab.util.DB;

public class UserAccount {

    private String password;
    private String gmail;
    private String lastError;

    // Constructors
    public UserAccount() {
        this("", "");
    }

    public UserAccount(String gmail, String password) {
        this.gmail = gmail;
        this.password = password;
        this.lastError = "";
    }

    // Getters
    public String getPassword() {
        return password;
    }

    public String getGmail() {
        return gmail;
    }

    public String getLastError() {
        return lastError;
    }

    // Setters
    public void setPassword(String password) {
        this.password = password;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    // -----------------------------------------------------------
    // LOGIN
    // -----------------------------------------------------------
    public boolean login() {
        String sql = "SELECT password FROM accounts WHERE gmail = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gmail);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, hashedPassword)) {
                    lastError = "";
                    return true;
                } else {
                    lastError = "Invalid email or password";
                    return false;
                }
            } else {
                lastError = "Invalid email or password";
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lastError = "Database error, please try again.";
            return false;
        }
    }

    // -----------------------------------------------------------
    // CHECK IF EMAIL IS AVAILABLE (BEFORE verification)
    // -----------------------------------------------------------
    public boolean isAvailable() {
        String sql = "SELECT gmail FROM accounts WHERE gmail = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, gmail);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                lastError = "Email already registered";
                return false;
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            lastError = "Database error. Try again.";
            return false;
        }
    }

    // -----------------------------------------------------------
    // SIGNUP (INSERT INTO DATABASE AFTER verification)
    // -----------------------------------------------------------
    public boolean signup() {
        String insertSql = "INSERT INTO accounts (password, gmail) VALUES (?, ?)";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

            stmt.setString(1, hashed);
            stmt.setString(2, gmail);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                lastError = "";
                return true;
            } else {
                lastError = "Failed to create account";
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            lastError = "Database error. Try again.";
            return false;
        }
    }

    // -----------------------------------------------------------
    // CHECK IF EMAIL EXISTS (FOR PASSWORD RESET)
    // -----------------------------------------------------------
    public static boolean emailExists(String email) {
        String sql = "SELECT gmail FROM accounts WHERE gmail = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Returns true if email exists

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // -----------------------------------------------------------
    // UPDATE PASSWORD (FOR PASSWORD RESET)
    // -----------------------------------------------------------
    public static boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE accounts SET password = ? WHERE gmail = ?";

        try (Connection conn = DB.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            stmt.setString(1, hashed);
            stmt.setString(2, email);

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

# AlgoLabV2 Setup Guide

## Prerequisites
- Java 11 or higher
- Maven
- MySQL Server
- Gmail account (for email verification features)

## Database Setup

1. **Install MySQL** if you haven't already

2. **Create the database:**
   ```sql
   CREATE DATABASE javafx_AlgoLab;
   ```

3. **Create required tables:**
   ```sql
   USE javafx_AlgoLab;
   
   CREATE TABLE accounts (
       id INT AUTO_INCREMENT PRIMARY KEY,
       gmail VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE TABLE sessions (
       id INT AUTO_INCREMENT PRIMARY KEY,
       gmail VARCHAR(255) NOT NULL,
       token VARCHAR(255) UNIQUE NOT NULL,
       expires_at TIMESTAMP NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

## Configuration

### 1. Database Configuration

Copy the example file and add your credentials:

```bash
cp src/main/java/com/algolab/util/DB.java.example src/main/java/com/algolab/util/DB.java
```

Then edit `DB.java` and replace:
- `your_database_name` with your database name (e.g., `javafx_AlgoLab`)
- `your_mysql_username` with your MySQL username (e.g., `root`)
- `your_mysql_password` with your MySQL password

### 2. Email Configuration

Copy the example file and add your credentials:

```bash
cp src/main/java/com/algolab/util/EmailSender.java.example src/main/java/com/algolab/util/EmailSender.java
```

Then edit `EmailSender.java` and replace:
- `your_email@gmail.com` with your Gmail address
- `your_gmail_app_password` with your Gmail App Password

#### How to get a Gmail App Password:

1. Go to your Google Account settings
2. Navigate to Security → 2-Step Verification (enable if not already)
3. Scroll down to "App passwords"
4. Generate a new app password for "Mail"
5. Copy the 16-character password and paste it in `EmailSender.java`

## Build and Run

1. **Build the project:**
   ```bash
   mvn clean install
   ```

2. **Run the application:**
   ```bash
   mvn javafx:run
   ```

## Important Notes

⚠️ **Security Warning:** 
- Never commit `DB.java` or `EmailSender.java` to version control
- These files contain sensitive credentials and are listed in `.gitignore`
- Only commit the `.example` files

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check your database credentials in `DB.java`
- Ensure the database exists

### Email Sending Issues
- Verify you're using an App Password, not your regular Gmail password
- Check that 2-Step Verification is enabled on your Google Account
- Ensure "Less secure app access" is not required (App Passwords bypass this)

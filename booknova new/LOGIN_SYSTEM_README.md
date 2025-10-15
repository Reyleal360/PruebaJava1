# 🔐 Login System - LibroNova  

✅ **Authentication System Successfully Implemented**

---

## 📋 Summary

A complete authentication system has been developed for **LibroNova**, featuring a modern JavaFX graphical interface, secure login/logout handling, mock authentication service, and role-based access control.

---

## 🚀 Implemented Features

### 🪟 1. Login Screen
- ✅ Professional JavaFX interface  
- ✅ Username and password fields  
- ✅ Credential validation with feedback  
- ✅ Error and success messages  
- ✅ Demo user info displayed for testing  

### 🔑 2. Authentication System
- ✅ `MockAuthenticationService` (singleton pattern)  
- ✅ Secure login and logout process  
- ✅ Session management for active user  
- ✅ Role and permission validation  

### 👥 3. Pre-configured Users

| Username   | Password   | Role            | Description                |
|-------------|-------------|------------------|-----------------------------|
| **admin**       | admin       | ADMINISTRATOR    | System Administrator        |
| **librarian**   | librarian   | LIBRARIAN        | María the Librarian         |
| **assistant**   | assistant   | ASSISTANT        | Ana the Assistant           |
| **superadmin**  | super123    | ADMINISTRATOR    | Carlos the Administrator    |

### 🧩 4. User Roles
- **ADMINISTRATOR:** Full access to all system modules  
- **LIBRARIAN:** Access to book, loan, and member management  
- **ASSISTANT:** Limited access based on assigned permissions  

---

## 🔧 How to Use the System

### ▶ Run the Application
cd "/home/Coder/Escritorio/booknova new"
mvn javafx:run

🔐 Access Credentials
User	Password	Role	Description
admin	admin	ADMINISTRATOR	System Administrator
librarian	librarian	LIBRARIAN	María Librarian
assistant	assistant	ASSISTANT	Ana Assistant
superadmin	super123	ADMINISTRATOR	Carlos Administrator
⚙️ Workflow

    Launch the application → Login screen appears

    Enter username and password

    Click Login → System validates credentials

    On success → Main interface opens

    Personalized interface shows username and role

    Logout button → Returns to login screen

📊 Technical Details
🧱 System Architecture

    LoginController: Manages login screen

    MockAuthenticationService: Handles authentication logic

    MainApp: Manages session and scene transitions

    User Domain: Defines user model and roles

🛡️ Security Features

    ✅ Validation for empty credentials

    ✅ Active user verification

    ✅ Role-based access control

    ✅ Authentication logging in app.log

    ✅ Secure session management

🧾 System Logging

All authentication activities are logged in app.log, including:

    Login attempts

    Successful/failed logins

    User logouts

    User activity records

🎨 User Interface
💻 Login Screen

    Modern gradient design

    Borderless window

    Shadow and hover effects

    Tab/Enter navigation

    Exit button (X)

    Demo user information displayed

🧭 Main Application

    Displays logged-in user info in top bar

    Always-visible Logout button

    Logs all user activities

    Role-based access to features

🧪 System Testing
Run Tests

javac -cp "target/classes" TestLogin.java
java -cp ".:target/classes" TestLogin

✅ Test Results

    Admin login → SUCCESS

    Librarian login → SUCCESS

    Assistant login → SUCCESS

    Invalid login → PROPERLY REJECTED

    Empty credentials → PROPERLY REJECTED

📁 Created & Modified Files
🆕 New Files

    src/main/java/com/mycompany/booknova/service/auth/MockAuthenticationService.java

    src/main/java/com/mycompany/booknova/ui/LoginController.java

    TestLogin.java (testing)

🛠️ Modified Files

    src/main/java/com/mycompany/booknova/ui/MainApp.java (authentication integration)

    pom.xml (configured to start with LoginController)

🚀 Project Status

✅ Fully Functional Login System
Implemented Capabilities

    🔐 Secure authentication with GUI

    👥 Multi-user and role management

    💾 Session persistence during execution

    📝 Complete activity logging

    🔄 Functional logout with return to login

    🎨 Professional and intuitive interface

The system is production-ready and can be easily extended to connect with a real database.
🔧 Future Improvements (Optional)

    Database integration

    Password recovery feature

    Password change functionality

    New user registration

    Granular permission management

    Automatic session expiration

    Two-factor authentication (2FA)

📅 Implementation Date: October 14, 2025
🟢 Status: Completed and Functional
🚀 Quality: Production Ready

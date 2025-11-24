import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;

public class App {
    private static Connection connection;
    private static String loggedInUser;

    public static void main(String[] args) {
        try {
            // Connect to the MySQL database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/online_exam", "root", "TIGER");
            System.out.println("Database connected successfully.");

            // Launch the login page
            showLoginPage();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Show Login Page
    private static void showLoginPage() {
        JFrame loginFrame = new JFrame("Login Page");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 300);
        loginFrame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        JTextField userField = new JTextField();
        userField.setBounds(150, 50, 150, 30);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 100, 100, 30);
        JPasswordField passField = new JPasswordField();
        passField.setBounds(150, 100, 150, 30);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 150, 100, 30);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (authenticateUser(username, password)) {
                loggedInUser = username;
                JOptionPane.showMessageDialog(loginFrame, "Login successful! Welcome, " + loggedInUser);
                loginFrame.dispose();
                showMenuPage();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);
    }

    // Authenticate User
    private static boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            var ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            var rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Show Menu Page
    private static void showMenuPage() {
        JFrame menuFrame = new JFrame("Menu Page");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 300);
        menuFrame.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser);
        welcomeLabel.setBounds(100, 50, 200, 30);

        JButton takeExamButton = new JButton("Take Exam");
        takeExamButton.setBounds(100, 100, 200, 30);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(100, 150, 200, 30);

        takeExamButton.addActionListener(e -> {
            menuFrame.dispose();
            ExamSection.startExam(connection, loggedInUser); // Call the ExamSection
        });

        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(menuFrame, "Logged out successfully.");
            menuFrame.dispose();
            showLoginPage();
        });

        menuFrame.add(welcomeLabel);
        menuFrame.add(takeExamButton);
        menuFrame.add(logoutButton);
        menuFrame.setVisible(true);
    }
}

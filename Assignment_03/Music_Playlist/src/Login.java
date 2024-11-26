import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.util.Scanner;

public class Login {

    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nWelcome to the Jungwoni Music!\nSelect what you want to do:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    register();
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    public void login() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your ID: ");
        int id = scanner.nextInt();
        System.out.print("Enter your Password: ");
        String password = scanner.next();

        if (isManager(id, password)) {
            System.out.print("Enter Manager PIN: ");
            int pin = scanner.nextInt();
            if (validateManagerPin(pin)) {
                Manager manager = new Manager();
                manager.showManagerMenu();
            } else {
                System.out.println("Invalid Manager PIN. Access denied.");
            }
        } else if (isUser(id, password)) {
            User user = new User();
            user.showUserMenu();
        } else {
            System.out.println("Invalid login credentials.");
        }
    }

    public void register() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nRegister Menu");
        System.out.println("1. Register as User");
        System.out.println("2. Register as Manager");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                registerUser();
                break;
            case 2:
                registerManager();
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Sign-up Date (YYYY-MM-DD): ");
        String signUpDate = scanner.nextLine();

        try {
            String query = "INSERT INTO User (User_Id, User_Name, User_Phone, User_Email, User_Password, Sign_up_Date) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, password);
            pstmt.setString(6, signUpDate);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " user(s) registered successfully!");
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private void registerManager() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your ID: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Manager PIN: ");
        int pin = scanner.nextInt();

        try {
            String query = "INSERT INTO Manager (Manager_Id, Manager_Name, Manager_Phone, Manager_Email, Manager_Password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, phone);
            pstmt.setString(4, email);
            pstmt.setString(5, password);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " manager(s) registered successfully!");
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private boolean isManager(int id, String password) {
        // Query the database to check if this is a manager
        return id == 100 && password.equals("manager123");
    }

    private boolean isUser(int id, String password) {
        // Query the database to check if this is a regular user
        return id == 200 && password.equals("user123");
    }

    private boolean validateManagerPin(int pin) {
        // Validate manager-specific PIN
        return pin == 1234;
    }
}

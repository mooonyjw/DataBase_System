package UserService;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import Service.AuditService;
import Utils.ValidationUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import static Auth.AuthUtil.hashPassword;
import static Auth.Register.*;
import static Utils.IsValidUtil.*;
import static Utils.ValidationUtil.*;

public class UserUpdateService {
    public void updateUser() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Phone");
            System.out.println("3. Email");
            System.out.println("4. Password");
            System.out.println("5. Go back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt = null;

            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine().trim();
                    query = "UPDATE User SET User_Name = ? WHERE User_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
                    pstmt.setInt(2, AuthUtil.currentUserId);
                    break;
                case 2:
                    String newPhoneNumber;

                    while (true) {
                        newPhoneNumber = ValidationUtil.getValidPhoneNumber(scanner);

                        if (!isUPhoneTaken(newPhoneNumber)) {
                            break; // 중복되지 않은 경우 루프 종료
                        }

                        System.out.println("The phone number is already in use by another user. Please use a different phone number.");
                    }
                    query = "UPDATE User SET User_Phone = ? WHERE User_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newPhoneNumber);
                    pstmt.setInt(2, AuthUtil.currentUserId);
                    break;
                case 3:
                    String newEmail = ValidationUtil.getValidEmail(scanner);
                    query = "UPDATE User SET User_Email = ? WHERE User_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newEmail);
                    pstmt.setInt(2, AuthUtil.currentUserId);
                    break;
                case 4:
                    System.out.print("Enter new Password: ");
                    String newPassword = scanner.nextLine().trim();

                    // Generate a new salt and hash the password
                    String newSalt = generateSalt();
                    String hashedPassword = hashPassword(newPassword, newSalt);

                    // Update the password and salt in the database
                    query = "UPDATE User SET User_Password = ?, Salt = ? WHERE User_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, hashedPassword);
                    pstmt.setString(2, newSalt);
                    pstmt.setInt(3, AuthUtil.currentUserId);
                    break;
                case 5:
                    System.out.println("Exiting update menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");

            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating user: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating user: " + e.getMessage());
        }
    }
}

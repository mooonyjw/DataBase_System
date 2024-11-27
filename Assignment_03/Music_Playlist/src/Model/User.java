package Model;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class User {
    public void showUserMenu(String userName) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("\nWelcome back, " + userName + "! Let's dive into the music world!");

        do {
            System.out.println("\nUser Menu");
            System.out.println("1. View Music");
            System.out.println("2. Create Playlist");
            System.out.println("3. View Playlists");
            System.out.println("4. Like Music");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewMusic();
                    break;
                case 2:
                    createPlaylist();
                    break;
                case 3:
                    viewPlaylists();
                    break;
                case 4:
                    likeMusic();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    private void viewMusic() {
        System.out.println("Viewing music...");
        // Add database query logic
    }

    private void createPlaylist() {
        System.out.println("Creating playlist...");
        // Add database query logic
    }

    private void viewPlaylists() {
        System.out.println("Viewing playlists...");
        // Add database query logic
    }

    private void likeMusic() {
        System.out.println("Liking music...");
        // Add database query logic
    }

    public String getUserName(String email) {
        try {
            String query = "SELECT User_Name FROM User WHERE User_Email = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("User_Name"); // 사용자 이름 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User"; // 기본값
    }


}

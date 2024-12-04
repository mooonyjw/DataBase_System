package Model;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import UserService.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class User {
    private UserSearchService userSearchService = new UserSearchService();
    private ListenedService listenedService = new ListenedService();
    private PlaylistService playlistService = new PlaylistService();
    private UserUpdateService userupdateService = new UserUpdateService();
    public void showUserMenu(String userName) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("\nWelcome back, " + userName + "! Let's dive into the music world!");

        do {
            System.out.println("\nUser Menu");
            System.out.println("1. Search");
            System.out.println("2. Show Listen History");
            System.out.println("3. Create Playlist");
            System.out.println("4. View Playlists");
            System.out.println("5. Liked Music");
            System.out.println("6. Settings");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    userSearchService.usersearchOption();
                    break;
                case 2:
                    listenedService.showListenHistory(AuthUtil.currentUserId);
                    break;
                case 3:
                    playlistService.createPlaylist(AuthUtil.currentUserId);
                    break;
                case 4:
                    playlistService.viewPlaylists(AuthUtil.currentUserId);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                case 6:
                    userupdateService.updateManager();
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

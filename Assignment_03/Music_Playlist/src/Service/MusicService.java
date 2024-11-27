package Service;

import Security.DatabaseUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class MusicService {

    public void addOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat would you like to add?");
        System.out.println("1. Album");
        System.out.println("2. Music");
        System.out.print("Enter your choice: ");
        int addChoice = scanner.nextInt();
        scanner.nextLine();

        switch (addChoice) {
            case 1:
                addAlbum();
                break;
            case 2:
                addMusic();
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    public void addAlbum(){
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("You are adding a new album to the platform.\n");
            System.out.print("Enter Album Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Album Release Date (YYYY-MM-DD): ");
            String releaseDate = scanner.nextLine();

            System.out.print("Enter Artist ID: ");
            int artistId = scanner.nextInt();

            String query = "INSERT INTO Album (Album_Name, Release_Date, Manager_Id, Artist_Id) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, releaseDate);
            pstmt.setInt(3, managerId);
            pstmt.setInt(4, artistId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Album added successfully!");
            } else {
                System.out.println("Failed to add album.");
            }

        } catch (Exception e) {
            System.out.println("Error while adding album: " + e.getMessage());
        }
    }
    public void addMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("You are adding a new music to the platform.\n");
            System.out.print("Enter Music Title: ");
            String title = scanner.nextLine();

            System.out.print("Enter Music Length (in seconds): ");
            int length = scanner.nextInt();

            System.out.print("Enter Artist ID: ");
            int artistId = scanner.nextInt();

            System.out.print("Enter Album ID: ");
            int albumId = scanner.nextInt();

            System.out.print("Enter Genre ID: ");
            int genreId = scanner.nextInt();

            String query = "INSERT INTO Music (Title, Length, Manager_Id, Artist_Id, Album_Id, Genre_Id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setInt(2, length);
            pstmt.setInt(3, managerId);
            pstmt.setInt(4, artistId);
            pstmt.setInt(5, albumId);
            pstmt.setInt(6, genreId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Music added successfully!");
            } else {
                System.out.println("Failed to add music.");
            }

        } catch (Exception e) {
            System.out.println("Error while adding music: " + e.getMessage());
        }
    }

    public void searchArtist() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Artist Name to search: ");
        String artistName = scanner.nextLine();

        try {
            String query = "SELECT Artist_Id, Artist_Name FROM Artist WHERE Artist_Name = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, artistName);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Search Results:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("Artist_Id") + ", Name: " + rs.getString("Artist_Name"));
            }
        } catch (Exception e) {
            System.out.println("Error while searching for artist: " + e.getMessage());
        }
    }
}

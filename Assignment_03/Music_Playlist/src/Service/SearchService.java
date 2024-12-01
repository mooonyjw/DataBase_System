package Service;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class SearchService {

    public void searchOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat you are searching for?");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.println("5. Back to main menu");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    int addChoice = scanner.nextInt();
        scanner.nextLine();

        switch (addChoice) {
        case 1:
            searchArtistId();
            break;
        case 2:
            searchAlbumId();
            break;
        case 3:
            searchMusicId();
            break;
        case 4:
            searchGenreId();
            break;
        case 5:
            System.out.println("Returning to main menu.");
            return;
        case 6:
            System.out.println("Exiting the program. Goodbye!");
            System.exit(0);
            break;
        default:
            System.out.println("Invalid choice. Returning to main menu.");
    }
}

    // Artist ID 찾기
    public int searchArtistId() {
        Scanner scanner = new Scanner(System.in);
        int artistId = -1;
        try {
            System.out.print("Enter Artist Name: ");
            String artistName = scanner.nextLine();

            String query = "SELECT Artist_Id FROM Artist WHERE Artist_Name = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, artistName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                artistId = rs.getInt("Artist_Id");
                System.out.println("Artist found: ID = " + artistId);
            } else {
                System.out.println("Artist not found. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error while searching for Artist ID: " + e.getMessage());
        }
        return artistId;
    }

    // Album ID 찾기
    public int searchAlbumId() {
        Scanner scanner = new Scanner(System.in);
        int albumId = -1;
        try {
            System.out.print("Enter Album Name: ");
            String albumName = scanner.nextLine();

            String query = "SELECT Album_Id FROM Album WHERE Album_Name = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, albumName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                albumId = rs.getInt("Album_Id");
                System.out.println("Album found: ID = " + albumId);
            } else {
                System.out.println("Album not found. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error while searching for Album ID: " + e.getMessage());
        }
        return albumId;
    }

    // Music ID 찾기
    public int searchMusicId() {
        Scanner scanner = new Scanner(System.in);
        int musicId = -1;
        try {
            System.out.print("Enter Music Name: ");
            String musicName = scanner.nextLine();

            String query = "SELECT Music_Id FROM Music WHERE Title = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, musicName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                musicId = rs.getInt("Music_Id");
                System.out.println("Music found: ID = " + musicId);
            } else {
                System.out.println("Music not found. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error while searching for Music ID: " + e.getMessage());
        }
        return musicId;
    }

    // Genre ID 찾기
    public int searchGenreId() {
        Scanner scanner = new Scanner(System.in);
        int genreId = -1;
        try {
            System.out.print("Enter Genre Name: ");
            String genreName = scanner.nextLine();

            String query = "SELECT Genre_Id FROM Genre WHERE Genre_Name = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                genreId = rs.getInt("Genre_Id");
                System.out.println("Genre found: ID = " + genreId);
            } else {
                System.out.println("Genre not found. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error while searching for Genre ID: " + e.getMessage());
        }
        return genreId;
    }
}

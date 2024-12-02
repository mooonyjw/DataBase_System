package Service;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Utils.*;

public class AddService {

    public void addOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat would you like to add?");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.print("Enter your choice: ");
        int addChoice = scanner.nextInt();
        scanner.nextLine();

        switch (addChoice) {
            case 1:
                addArtist();
                break;
            case 2:
                addAlbum();
                break;
            case 3:
                addMusic();
                break;
            case 4:
                addGenre();
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    public void addArtist(){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.print("\nYou are adding a new artist to the platform.\n");

            // 아티스트 이름 입력
            System.out.print("Enter Artist Name: ");
            String name = scanner.nextLine();

            // 아티스트 데뷔 날짜 입력
            LocalDate debutDate = ValidationUtil.getValidDate(scanner);

            // 아티스트 소속사 입력
            System.out.print("Enter Artist Agency: ");
            String agency = scanner.nextLine();

            String query = "INSERT INTO Artist (Artist_Name, Debut_Date, Agency) VALUES (?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(debutDate));
            pstmt.setString(3, agency);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Artist added successfully!");
            } else {
                System.out.println("Failed to add artist.");
            }

        } catch (Exception e) {
            System.out.println("Error while adding artist: " + e.getMessage());
        }
    }

    public void addAlbum(){
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("\nYou are adding a new album to the platform.\n");

            // 앨범 이름 입력
            System.out.print("Enter Album Name: ");
            String name = scanner.nextLine();

            // 아티스트 ID 입력
            int artistId = ValidationUtil.getValidArtistID(scanner);

            // 트랙 수 입력
            System.out.print("Enter Total Tracks: ");
            int totalTracks = scanner.nextInt();
            scanner.nextLine(); // 입력 버퍼에 남아 있는 줄바꿈 문자 소비

            // 발매 날짜 입력
            LocalDate releaseDate = ValidationUtil.getValidDate(scanner);

            String query = "INSERT INTO Album (Album_Name, artistId, Total_Tracks, Release_Date) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setInt(2, artistId);
            pstmt.setInt(3, totalTracks);
            pstmt.setDate(4, java.sql.Date.valueOf(releaseDate));


            int rows = pstmt.executeUpdate();
            if (rows > 0) {
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
            System.out.print("\nYou are adding a new music to the platform.\n");

            // 음악 제목 입력
            System.out.print("Enter Music Title: ");
            String title = scanner.nextLine();

            // 음악 길이 입력
            System.out.print("Enter Music Length (in seconds): ");
            int length = scanner.nextInt();

            // 앨범 ID 입력
            System.out.print("Enter Album ID: ");
            int albumId = scanner.nextInt();

            System.out.print("Enter Genre ID: ");
            int genreId = scanner.nextInt();

            String query = "INSERT INTO Music (Title, Length, managerId, albumId, genreId) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, title);
            pstmt.setInt(2, length);
            pstmt.setInt(3, AuthUtil.currentManagerId);
            pstmt.setInt(4, albumId);
            pstmt.setInt(5, genreId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Music added successfully!");
            } else {
                System.out.println("Failed to add music.");
            }

        } catch (Exception e) {
            System.out.println("Error while adding music: " + e.getMessage());
        }
    }

    public void addGenre() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\nYou are adding a new genre to the platform.");

            // 장르 이름 입력
            System.out.print("Enter Genre Name: ");
            String genreName = scanner.nextLine();

            // 중복 확인
            if (isGenreExists(genreName)) {
                System.out.println("This genre already exists. Please try a different genre.");
                return; // 중복이면 함수 종료
            }

            // 데이터베이스에 장르 추가
            String query = "INSERT INTO Genre (Genre_Name) VALUES (?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Genre added successfully!");
            } else {
                System.out.println("Failed to add genre.");
            }

        } catch (Exception e) {
            System.out.println("Error while adding genre: " + e.getMessage());
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




    private boolean isGenreExists(String genreName) {
        try {
            String query = "SELECT Genre_Id FROM Genre WHERE Genre_Name = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // 결과가 존재하면 중복
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}

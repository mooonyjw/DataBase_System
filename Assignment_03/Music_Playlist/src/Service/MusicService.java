package Service;

import Security.DatabaseUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class MusicService {

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
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    public void addArtist(){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.print("You are adding a new artist to the platform.\n");

            // 아티스트 이름 입력
            System.out.print("Enter Artist Name: ");
            String name = scanner.nextLine();

            // 아티스트 데뷔 날짜 입력
            LocalDate debutDate = getValidDate(scanner);

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
            System.out.print("You are adding a new album to the platform.\n");

            // 앨범 이름 입력
            System.out.print("Enter Album Name: ");
            String name = scanner.nextLine();

            // 아티스트 ID 입력
            int artistId = getValidArtistID(scanner);

            // 트랙 수 입력
            System.out.print("Enter Total Tracks: ");
            int totalTracks = scanner.nextInt();

            // 발매 날짜 입력
            LocalDate releaseDate = getValidDate(scanner);

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


    // 여기부터 해야됨. 장르 add하는 함수 작성하고 앨범 id 유효성 검사하는 함수 작성
    public void addMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("You are adding a new music to the platform.\n");

            // 음악 제목 입력
            System.out.print("Enter Music Title: ");
            String title = scanner.nextLine();

            // 음악 길이 입력
            System.out.print("Enter Music Length (in seconds): ");
            int length = scanner.nextInt();


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

    public LocalDate getValidDate(Scanner scanner) {
        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter Date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();

            if (dateInput.isEmpty()) {
                System.out.println("Date cannot be empty. Please try again.");
                continue;
            }

            try {
                // Validate and parse date
                date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        }
        return date;
    }

    public int getValidArtistID(Scanner scanner) {
        int artistId = -1;  // 아티스트 ID 초깃값
        boolean isValid = false;  // 유효성 플래그

        while (!isValid) {
            System.out.print("Enter Artist ID: ");
            artistId = scanner.nextInt();

            try {
                // DB에서 Artist ID 존재 여부 확인
                String query = "SELECT Artist_Id FROM Artist WHERE Artist_Id = ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                pstmt.setInt(1, artistId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isValid = true;  // Artist ID가 유효하면 반복 종료
                } else {
                    System.out.println("Invalid Artist ID. Please enter a valid ID.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while validating Artist ID. Try again.");
            }
        }

        return artistId;
    }


}

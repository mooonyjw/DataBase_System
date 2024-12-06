package Service;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Utils.*;

import static Utils.IsValidUtil.*;
import static Utils.ValidationUtil.*;

public class AddService {

    public void addOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat would you like to add?");
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

    public void addArtist(){
        Scanner scanner = new Scanner(System.in);

        try{
            System.out.print("\nYou are adding a new artist to the platform.\n");

            // 아티스트 이름 입력
            String name;
            while (true) {
                System.out.print("Enter Artist Name: ");
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Artist name cannot be empty. Please try again.");
                    continue;
                }
                if (isArtistExists(name)) {
                    System.out.println("Artist name already exists. Please try a different name.");
                    continue;
                }
                break; // 유효한 이름일 경우 루프 종료
            }

            // 아티스트 데뷔 날짜 입력
            LocalDate debutDate = ValidationUtil.getValidDate(scanner);

            // 아티스트 소속사 입력
            String agency;
            while (true) {
                System.out.print("Enter Artist Agency: ");
                agency = scanner.nextLine().trim();
                if (agency.isEmpty()) {
                    System.out.println("Agency name cannot be empty. Please try again.");
                    continue;
                }
                break;
            }

            String query = "INSERT INTO Artist (Artist_Name, Debut_Date, Agency) VALUES (?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setDate(2, java.sql.Date.valueOf(debutDate));
            pstmt.setString(3, agency);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Artist added successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Artist", "CREATE");
            } else {
                System.out.println("Failed to add artist.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate artist name detected. Please use a unique name.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public void addAlbum(){
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("\nYou are adding a new album to the platform.\n");

            // 앨범 이름 입력
            String name;
            while (true) {
                System.out.print("Enter Album Name: ");
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Album name cannot be empty. Please try again.");
                    continue;
                }
                if (name.length() > 255) {
                    System.out.println("Album name is too long. Please enter a shorter name.");
                    continue;
                }
                break;
            }

            // 아티스트 ID 입력
            int artistId = ValidationUtil.getValidArtistID(scanner);

            // 발매 날짜 입력
            LocalDate releaseDate = ValidationUtil.getValidDate(scanner);

            String query = "INSERT INTO Album (Album_Name, artistId, Release_Date) VALUES (?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setInt(2, artistId);
            pstmt.setDate(3, java.sql.Date.valueOf(releaseDate));


            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Album added successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Album", "CREATE");
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
            String title;
            while (true) {
                System.out.print("Enter Music Title: ");
                title = scanner.nextLine().trim();
                if (title.isEmpty()) {
                    System.out.println("Music title cannot be empty. Please try again.");
                    continue;
                }
                break;
            }
            // 음악 길이 입력
            int length;
            while (true) {
                System.out.print("Enter Music Length (in seconds): ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a valid length in seconds.");
                    scanner.next(); // 잘못된 입력 소비
                    continue;
                }
                length = scanner.nextInt();
                scanner.nextLine(); // 입력 버퍼 정리
                if (length <= 0) {
                    System.out.println("Music length must be a positive number. Please try again.");
                    continue;
                }
                break;
            }

            // 앨범 ID 입력
            int albumId = ValidationUtil.getValidAlbumID(scanner);

            // 장르 ID 입력
            int genreId = ValidationUtil.getValidGenreID(scanner);

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

                updateAlbumTotalTracks(albumId);
                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Music", "CREATE");
            } else {
                System.out.println("Failed to add music.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate entry detected. Please check the music details and try again.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    public void addGenre() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\nYou are adding a new genre to the platform.");
            String genreName;

            // 유효한 장르 이름을 입력받을 때까지 반복
            while (true) {
                System.out.print("Enter Genre Name: ");
                genreName = scanner.nextLine().trim();

                // 입력값이 비어있으면 다시 요청
                if (genreName.isEmpty()) {
                    System.out.println("Genre name cannot be empty. Please enter a valid genre name.");
                    continue;
                }

                // 중복 확인
                if (isGenreExists(genreName)) {
                    System.out.println("This genre already exists. Please try a different genre.");
                    continue;
                }

                // 중복도 아니고 비어있지도 않으면 반복 종료
                break;
            }

            // 데이터베이스에 장르 추가
            String query = "INSERT INTO Genre (Genre_Name) VALUES (?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Genre added successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Genre", "CREATE");
            } else {
                System.out.println("Failed to add genre.");
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("This genre already exists in the database. Please try with a different name.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void updateAlbumTotalTracks(int albumId) {
        try {
            String updateQuery = "UPDATE Album SET Total_Tracks = Total_Tracks + 1 WHERE Album_Id = ?";
            PreparedStatement updatePstmt = DatabaseUtil.getConnection().prepareStatement(updateQuery);
            updatePstmt.setInt(1, albumId);
            int updatedRows = updatePstmt.executeUpdate();

            if (updatedRows > 0) {
                System.out.println("Album's total tracks updated successfully!");
            } else {
                System.out.println("Failed to update album's total tracks.");
            }
        } catch (Exception e) {
            System.out.println("Error while updating album's total tracks: " + e.getMessage());
        }
    }
}

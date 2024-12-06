package Service;

import Auth.AuthUtil;
import Auth.Register;
import Security.DatabaseUtil;
import Utils.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Scanner;
import java.sql.ResultSet;

import static Auth.AuthUtil.hashPassword;
import static Auth.Register.*;
import static Utils.IsValidUtil.*;
import static Utils.ValidationUtil.*;


public class UpdateService {
    public void updateOption() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nWhat you are updating?");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.println("5. Manager");
        System.out.println("6. Back to main menu");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");

        int updateChoice = scanner.nextInt();
        scanner.nextLine();

        switch (updateChoice) {
            case 1:
                updateArtist();
                break;
            case 2:
                updateAlbum();
                break;
            case 3:
                updateMusic();
                break;
            case 4:
                updateGenre();
                break;
            case 5:
                updateManager();
                break;
            case 6:
                System.out.println("Returning to main menu.");
                return;
            case 7:
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    public void updateArtist() {
        Scanner scanner = new Scanner(System.in);

        try {
            int artistId = getValidArtistID(scanner);

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Debut Date");
            System.out.println("3. Agency");
            System.out.println("4. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;

            switch (choice) {
                case 1:
                    // 이름 변경
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine().trim();
                    if (newName.isEmpty()) {
                        System.out.println("Name cannot be empty. Operation cancelled.");
                        return;
                    }
                    query = "UPDATE Artist SET Artist_Name = ? WHERE Artist_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
                    pstmt.setInt(2, artistId);
                    break;
                case 2:
                    // 데뷔 날짜 변경
                    LocalDate newDate = getValidDate(scanner);
                    query = "UPDATE Artist SET Debut_Date = ? WHERE Artist_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setDate(1, Date.valueOf(newDate));
                    pstmt.setInt(2, artistId);
                    break;
                case 3:
                    // 소속사 변경
                    System.out.print("Enter new Agency: ");
                    String newAgency = scanner.nextLine().trim();
                    if (newAgency.isEmpty()) {
                        System.out.println("Agency cannot be empty. Operation cancelled.");
                        return;
                    }
                    query = "UPDATE Artist SET Agency = ? WHERE Artist_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newAgency);
                    pstmt.setInt(2, artistId);
                    break;
                case 4:
                    System.out.println("Exiting update menu...");
                    return; // Exit the function
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return; // Return to prevent execution of invalid query
            }

            // Update 실행
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");
                // Audit 로그 추가
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Artist", "UPDATE");
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating artist: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating artist: " + e.getMessage());
        }
    }

    // 앨범 정보 업데이트
    public void updateAlbum() {
        Scanner scanner = new Scanner(System.in);

        try {
            int albumId = getValidAlbumID(scanner);

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Album Name");
            System.out.println("2. Release Date");
            System.out.println("3. Artist ID");
            System.out.println("4. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt = null;

            switch (choice) {
                case 1:
                    // 앨범 이름 변경
                    System.out.print("Enter new Album Name: ");
                    String newAlbumName = scanner.nextLine().trim();
                    if (newAlbumName.isEmpty()) {
                        System.out.println("Album name cannot be empty.");
                        return;
                    }
                    query = "UPDATE Album SET Album_Name = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newAlbumName);
                    pstmt.setInt(2, albumId);
                    break;
                case 2:
                    // 발매 날짜 변경
                    LocalDate newReleaseDate = getValidDate(scanner);
                    query = "UPDATE Album SET Release_Date = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setDate(1, Date.valueOf(newReleaseDate));
                    pstmt.setInt(2, albumId);
                    break;
                case 3:
                    // 아티스트 ID 변경
                    int newArtistId = getValidArtistID(scanner);
                    query = "UPDATE Album SET ArtistId = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newArtistId);
                    pstmt.setInt(2, albumId);
                    break;
                case 4:
                    System.out.println("Exiting update menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }

            // Update 실행
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");
                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Album", "UPDATE");
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating album: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating album: " + e.getMessage());
        }
    }

    // 음악 정보 업데이트
    public void updateMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            int musicId = getValidMusicID(scanner);

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Title");
            System.out.println("2. Length");
            System.out.println("3. Album ID");
            System.out.println("4. Genre ID");
            System.out.println("5. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;

            switch (choice) {
                case 1:
                    // 제목 변경
                    System.out.print("Enter new Title: ");
                    String newTitle= scanner.nextLine().trim();
                    if (newTitle.isEmpty() || newTitle.length() > 255) {
                        System.out.println("Invalid title. Please try again.");
                        return;
                    }
                    query = "UPDATE Music SET Title = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newTitle);
                    pstmt.setInt(2, musicId);
                    break;
                case 2:
                    System.out.print("Enter new Length (in seconds): ");
                    int newLength = scanner.nextInt();
                    if (newLength <= 0) {
                        System.out.println("Invalid length. Please enter a positive value.");
                        return;
                    }
                    query = "UPDATE Music SET Length = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newLength);
                    pstmt.setInt(2, musicId);
                    break;
                case 3:
                    int newAlbumId = getValidAlbumID(scanner);

                    // 먼저 현재 곡의 albumId를 가져옴
                    String currentAlbumQuery = "SELECT albumId FROM Music WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(currentAlbumQuery);
                    pstmt.setInt(1, musicId);
                    ResultSet currentAlbumRs = pstmt.executeQuery();

                    if (currentAlbumRs.next()) {
                        int currentAlbumId = currentAlbumRs.getInt("albumId");

                        // 기존 앨범의 Total_Tracks 줄이기
                        String decrementOldAlbumQuery = "UPDATE Album SET Total_Tracks = Total_Tracks - 1 WHERE Album_Id = ?";
                        pstmt = DatabaseUtil.getConnection().prepareStatement(decrementOldAlbumQuery);
                        pstmt.setInt(1, currentAlbumId);
                        pstmt.executeUpdate();

                        // 새로운 앨범의 Total_Tracks 증가
                        String incrementNewAlbumQuery = "UPDATE Album SET Total_Tracks = Total_Tracks + 1 WHERE Album_Id = ?";
                        pstmt = DatabaseUtil.getConnection().prepareStatement(incrementNewAlbumQuery);
                        pstmt.setInt(1, newAlbumId);
                        pstmt.executeUpdate();

                        // Music 테이블에서 albumId 업데이트
                        String updateMusicQuery = "UPDATE Music SET albumId = ? WHERE Music_Id = ?";
                        pstmt = DatabaseUtil.getConnection().prepareStatement(updateMusicQuery);
                        pstmt.setInt(1, newAlbumId);
                        pstmt.setInt(2, musicId);
                    }
                    break;

                case 4:
                    int newGenreId = getValidGenreID(scanner);
                    query = "UPDATE Music SET genreId = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newGenreId);
                    pstmt.setInt(2, musicId);
                    break;
                case 5:
                    System.out.println("Exiting update menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }

            // Update 실행
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");
                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Music", "UPDATE");
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating music: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating music: " + e.getMessage());
        }
    }

    public void updateGenre() {
        Scanner scanner = new Scanner(System.in);

        try {
            int genreId = getValidGenreID(scanner);

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;

            switch (choice) {
                case 1:
                    // 장르 이름 변경
                    String newGenreName;
                    while (true) {
                        System.out.print("Enter Genre Name: ");
                        newGenreName = scanner.nextLine().trim();

                        if (newGenreName.isEmpty()) {
                            System.out.println("Genre name cannot be empty. Please enter a valid genre name.");
                            continue;
                        }

                        // 중복 확인
                        if (isGenreExists(newGenreName)) {
                            System.out.println("This genre already exists. Please try a different genre.");
                            continue;
                        }

                        // 중복도 아니고 비어있지도 않으면 반복 종료
                        break;
                    }
                    query = "UPDATE Genre SET Genre_Name = ? WHERE Genre_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newGenreName);
                    pstmt.setInt(2, genreId);
                    break;
                case 2:
                    System.out.println("Exiting update menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }

            // Update 실행
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");
                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Genre", "UPDATE");
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating genre: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating genre: " + e.getMessage());
        }
    }
// 여기 함수 update 다 되는지 확인 안 해봄     // 앨범 id 유효성 검사하는 함수 작성
    public void updateManager() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Phone");
            System.out.println("3. Email");
            System.out.println("4. Password");
            System.out.println("5. PIN");
            System.out.println("6. Back to main menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";

            PreparedStatement pstmt = null;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine().trim();
                    query = "UPDATE Manager SET Manager_Name = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 2:
                    String newPhoneNumber;
                    while (true) {
                        newPhoneNumber = getValidPhoneNumber(scanner);

                        if (!isMPhoneTaken(newPhoneNumber)) {
                            break; // 중복되지 않은 경우 루프 종료
                        }

                        System.out.println("This phone number is already registered. Please try a different phone number.");
                    }
                    query = "UPDATE Manager SET Manager_Phone = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newPhoneNumber);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 3:
                    String newEmail = getValidEmail(scanner);
                    query = "UPDATE Manager SET Manager_Email = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newEmail);
                    pstmt.setInt(2, AuthUtil.currentManagerId);

                    break;
                case 4:
                    System.out.print("Enter new Password: ");
                    String newPassword = scanner.nextLine().trim();

                    // Generate a new salt and hash the password
                    String newSalt = generateSalt();
                    String hashedPassword = hashPassword(newPassword, newSalt);

                    // Update the password and salt in the database
                    query = "UPDATE Manager SET Manager_Password = ?, Salt = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, hashedPassword);
                    pstmt.setString(2, newSalt);
                    pstmt.setInt(3, AuthUtil.currentManagerId);

                    break;

                case 5:
                    int newPin = getValidManagerPin(scanner);
                    query = "UPDATE Manager SET Manager_PIN = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newPin);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 6:
                    System.out.println("Exiting update menu... Going back to main menu.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return;
            }
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Information updated successfully!");
                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Manager", "UPDATE");
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while updating manager: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while updating manager: " + e.getMessage());
        }
    }
}

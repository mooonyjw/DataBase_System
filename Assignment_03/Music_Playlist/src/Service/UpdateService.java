package Service;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import Utils.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.ResultSet;


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
            System.out.print("Enter Artist ID to update: ");
            int artistId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Debut Date");
            System.out.println("3. Agency");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine();
                    query = "UPDATE Artist SET Artist_Name = ? WHERE Artist_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
                    pstmt.setInt(2, artistId);
                    break;
                case 2:
                    System.out.print("Enter new Debut Date (YYYY-MM-DD): ");
                    String newDate = scanner.nextLine();
                    query = "UPDATE Artist SET Debut_Date = ? WHERE Artist_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newDate);
                    pstmt.setInt(2, artistId);
                    break;
                case 3:
                    System.out.print("Enter new Agency: ");
                    String newAgency = scanner.nextLine();
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
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (Exception e) {
            System.out.println("Error while updating information: " + e.getMessage());
        }
    }

    // 앨범 정보 업데이트
    public void updateAlbum() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Album ID to update: ");
            int albumId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Album Name");
            System.out.println("2. Total Tracks");
            System.out.println("3. Release Date");
            System.out.println("4. Artist ID");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt = null;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Album Name: ");
                    String newAlbumName = scanner.nextLine();
                    query = "UPDATE Album SET Album_Title = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newAlbumName);
                    pstmt.setInt(2, albumId);
                    break;
                case 2:
                    System.out.print("Enter new Total Tracks: ");
                    int newTotalTracks = scanner.nextInt();
                    query = "UPDATE Album SET Total_Tracks = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newTotalTracks);
                    pstmt.setInt(2, albumId);
                    break;
                case 3:
                    System.out.print("Enter new Release Date (YYYY-MM-DD): ");
                    String newReleaseDate = scanner.nextLine();
                    query = "UPDATE Album SET Release_Date = ? WHERE Album_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setDate(1, Date.valueOf(newReleaseDate));
                    pstmt.setInt(2, albumId);
                    break;
                case 4:
                    System.out.print("Enter new Artist ID: ");
                    int newArtistId = scanner.nextInt();
                    if (IsValidUtil.isValidArtistId(newArtistId)) {
                        query = "UPDATE Album SET ArtistId = ? WHERE Album_Id = ?";
                        pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                        pstmt.setInt(1, newArtistId);
                        pstmt.setInt(2, albumId);
                    } else {
                        System.out.println("Invalid Artist ID. Please ensure the Artist ID exists.");
                    }
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
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (Exception e) {
            System.out.println("Error while updating information: " + e.getMessage());
        }
    }

    // 음악 정보 업데이트
    public void updateMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Music ID: ");
            int musicId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Title");
            System.out.println("2. Length");
            System.out.println("3. Album ID");
            System.out.println("4. Genre ID");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Title: ");
                    String newTitle= scanner.nextLine();
                    query = "UPDATE Music SET Title = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newTitle);
                    pstmt.setInt(2, musicId);
                    break;
                case 2:
                    System.out.print("Enter new Length (in seconds): ");
                    int newLength = scanner.nextInt();
                    query = "UPDATE Music SET Length = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newLength);
                    pstmt.setInt(2, musicId);
                    break;
                case 3:
                    System.out.print("Enter new Album ID: ");
                    int newAlbumId = scanner.nextInt();
                    query = "UPDATE Music SET Album_Id = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newAlbumId);
                    pstmt.setInt(2, musicId);
                    break;
                case 4:
                    System.out.print("Enter new Genre ID: ");
                    int newGenreId = scanner.nextInt();
                    query = "UPDATE Music SET Genre_Id = ? WHERE Music_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setInt(1, newGenreId);
                    pstmt.setInt(2, musicId);
                    break;
                case 5:
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
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (Exception e) {
            System.out.println("Error while updating information: " + e.getMessage());
        }
    }

    public void updateGenre() {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter Genre ID to update: ");
            int genreId = scanner.nextInt();
            scanner.nextLine();

            System.out.println("\nWhat information do you want to update?");
            System.out.println("1. Name");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String query = "";
            PreparedStatement pstmt;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine();
                    query = "UPDATE Genre SET Genre_Name = ? WHERE Genre_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
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
            } else {
                System.out.println("Failed to update information.");
            }

        } catch (Exception e) {
            System.out.println("Error while updating information: " + e.getMessage());
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
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            String query = "";
            PreparedStatement pstmt;
            switch (choice) {
                case 1:
                    System.out.print("Enter new Name: ");
                    String newName = scanner.nextLine();
                    query = "UPDATE Manager SET Manager_Name = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newName);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 2:
                    System.out.print("Enter new Phone Number (11 digits, no hyphens): ");
                    String newPhoneNumber = ValidationUtil.getValidPhoneNumber(scanner);
                    query = "UPDATE Manager SET Manager_Phone = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newPhoneNumber);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 3:
                    System.out.print("Enter new Email: ");
                    String newEmail = ValidationUtil.getValidEmail(scanner);
                    query = "UPDATE Manager SET Manager_Email = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newEmail);
                    pstmt.setInt(2, AuthUtil.currentManagerId);

                    break;
                case 4:
                    System.out.print("Enter new Password: ");
                    String newPassword = scanner.nextLine();
                    query = "UPDATE Manager SET Manager_Password = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newPassword);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 5:
                    System.out.print("Enter new PIN: ");
                    String newPin = scanner.nextLine();
                    query = "UPDATE Manager SET Manager_PIN = ? WHERE Manager_Id = ?";
                    pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    pstmt.setString(1, newPin);
                    pstmt.setInt(2, AuthUtil.currentManagerId);
                    break;
                case 6:
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

        } catch (Exception e) {
            System.out.println("Error while updating information: " + e.getMessage());
        }
    }





}

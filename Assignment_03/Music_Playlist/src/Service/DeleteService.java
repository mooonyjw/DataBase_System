package Service;

import Auth.AuthUtil;
import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;



import static Utils.IsValidUtil.*;
import static Utils.ValidationUtil.*;

public class DeleteService {
    public void deleteOption() {
        // 메뉴 표시
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat would you like to delete?");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.println("5. User");
        System.out.println("6. Back to main menu");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
        int deleteChoice = scanner.nextInt();
        scanner.nextLine();

        switch (deleteChoice) {
            case 1:
                deleteArtist();
                break;
            case 2:
                deleteAlbum();
                break;
            case 3:
                deleteMusic();
                break;
            case 4:
                deleteGenre();
                break;
            case 5:
                deleteUser();
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

    private void deleteArtist() {
        Scanner scanner = new Scanner(System.in);

        try {
            int artistId = getValidArtistID(scanner);

            // 삭제할 아티스트 확인
            System.out.println("Fetching artist details...");
            String fetchArtistQuery = "SELECT Artist_Name FROM Artist WHERE Artist_Id = ?";
            PreparedStatement fetchArtistStmt = DatabaseUtil.getConnection().prepareStatement(fetchArtistQuery);
            fetchArtistStmt.setInt(1, artistId);
            ResultSet artistRs = fetchArtistStmt.executeQuery();

            if (!artistRs.next()) {
                System.out.println("Artist not found. Please check the ID and try again.");
                return;
            }

            String artistName = artistRs.getString("Artist_Name");
            System.out.println("Artist Name: " + artistName);

            // 연관된 앨범 확인
            String fetchAlbumQuery = "SELECT Album_Id FROM Album WHERE artistId = ?";
            PreparedStatement fetchAlbumStmt = DatabaseUtil.getConnection().prepareStatement(fetchAlbumQuery);
            fetchAlbumStmt.setInt(1, artistId);
            ResultSet albumRs = fetchAlbumStmt.executeQuery();

            if (albumRs.next()) {
                System.out.println("This artist has related albums. Related albums and their music will also be deleted.");
                System.out.print("Do you want to proceed? (y/n): ");
                String confirmation = scanner.nextLine().trim();
                if (!confirmation.equalsIgnoreCase("y")) {
                    System.out.println("Deletion cancelled. Returning to menu.");
                    return;
                }

                // 연관된 음악 삭제
                String deleteMusicQuery = "DELETE FROM Music WHERE albumId IN (SELECT Album_Id FROM Album WHERE artistId = ?)";
                PreparedStatement deleteMusicStmt = DatabaseUtil.getConnection().prepareStatement(deleteMusicQuery);
                deleteMusicStmt.setInt(1, artistId);
                deleteMusicStmt.executeUpdate();

                // 연관된 앨범 삭제
                String deleteAlbumQuery = "DELETE FROM Album WHERE artistId = ?";
                PreparedStatement deleteAlbumStmt = DatabaseUtil.getConnection().prepareStatement(deleteAlbumQuery);
                deleteAlbumStmt.setInt(1, artistId);
                deleteAlbumStmt.executeUpdate();
            }

            // 아티스트 삭제
            String deleteArtistQuery = "DELETE FROM Artist WHERE Artist_Id = ?";
            PreparedStatement deleteArtistStmt = DatabaseUtil.getConnection().prepareStatement(deleteArtistQuery);
            deleteArtistStmt.setInt(1, artistId);
            int rows = deleteArtistStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Artist deleted successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Artist", "DELETE");
            } else {
                System.out.println("No artist found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting artist: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }
    }


    private void deleteAlbum() {
        Scanner scanner = new Scanner(System.in);

        try {
            int albumId = getValidAlbumID(scanner);

            // 삭제 확인
            String checkQuery = "SELECT COUNT(*) AS MusicCount FROM Music WHERE albumId = ?";
            PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, albumId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("MusicCount") > 0) {
                System.out.println("This album has related music. Related music will also be deleted.");
                System.out.print("Do you want to proceed? (y/n): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();

                if (!confirmation.equals("y")) {
                    System.out.println("Album deletion cancelled.");
                    return;
                }

                // 연관된 Music 데이터 삭제
                String deleteMusicQuery = "DELETE FROM Music WHERE albumId = ?";
                PreparedStatement deleteMusicStmt = DatabaseUtil.getConnection().prepareStatement(deleteMusicQuery);
                deleteMusicStmt.setInt(1, albumId);
                deleteMusicStmt.executeUpdate();
                System.out.println("Associated music entries deleted successfully.");
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Music", "DELETE");
            }

            // Album 삭제
            String deleteAlbumQuery = "DELETE FROM Album WHERE Album_Id = ?";
            PreparedStatement deleteAlbumStmt = DatabaseUtil.getConnection().prepareStatement(deleteAlbumQuery);
            deleteAlbumStmt.setInt(1, albumId);
            int rows = deleteAlbumStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Album deleted successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Album", "DELETE");
            } else {
                System.out.println("Failed to delete album.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting album: " + e.getMessage());
        }
    }

    private void deleteMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            int musicId = getValidMusicID(scanner);

            // 삭제할 음악 정보 확인
            System.out.println("Fetching music details...");
            String fetchQuery = "SELECT Title, albumId FROM Music WHERE Music_Id = ?";
            PreparedStatement fetchStmt = DatabaseUtil.getConnection().prepareStatement(fetchQuery);
            fetchStmt.setInt(1, musicId);
            ResultSet rs = fetchStmt.executeQuery();

            String musicTitle = null;
            int albumId = -1;

            if (rs.next()) {
                musicTitle = rs.getString("Title");
                albumId = rs.getInt("albumId");
                System.out.println("Music Title: " + musicTitle);
            } else {
                System.out.println("Music not found. Please check the ID and try again.");
                return;
            }

            // 삭제 확인
            System.out.print("Are you sure you want to delete this music? (y/n): ");
            String confirmation = scanner.nextLine().trim();
            if (!confirmation.equalsIgnoreCase("y")) {
                System.out.println("Music deletion cancelled. Returning to menu.");
                return;
            }

            // 음악 삭제
            String deleteQuery = "DELETE FROM Music WHERE Music_Id = ?";
            PreparedStatement deleteStmt = DatabaseUtil.getConnection().prepareStatement(deleteQuery);
            deleteStmt.setInt(1, musicId);
            int rows = deleteStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Music deleted successfully!");

                // 앨범의 Total_Tracks 감소
                String updateAlbumQuery = "UPDATE Album SET Total_Tracks = Total_Tracks - 1 WHERE Album_Id = ?";
                PreparedStatement updateStmt = DatabaseUtil.getConnection().prepareStatement(updateAlbumQuery);
                updateStmt.setInt(1, albumId);
                updateStmt.executeUpdate();

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Music", "DELETE");
            } else {
                System.out.println("No music found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting music: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }
    }


    private void deleteGenre() {
        Scanner scanner = new Scanner(System.in);

        try {
            int genreId = getValidGenreID(scanner);

            // 참조 무결성 확인
            String checkQuery = "SELECT COUNT(*) AS count FROM Music WHERE genreId = ?";
            PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, genreId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                System.out.println("This genre is currently being used in music records and cannot be deleted.");
                return;
            }

            // 삭제 확인
            System.out.print("Are you sure you want to delete this genre? (y/n): ");
            String confirmation = scanner.nextLine().trim();
            if (!confirmation.equalsIgnoreCase("y")) {
                System.out.println("Genre deletion cancelled.");
                return;
            }

            // 장르 삭제
            String query = "DELETE FROM Genre WHERE Genre_Id = ?";
            PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, genreId);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Genre deleted successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "Genre", "DELETE");
            } else {
                System.out.println("No genre found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting genre: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }
    }

//    private void deleteManager() {
//        Scanner scanner = new Scanner(System.in);
//
//        try {
//            int managerId = getValidManagerId(scanner);
//
//            // 자기 자신인지 확인
//            if (managerId == AuthUtil.currentManagerId) {
//                System.out.println("You cannot delete your own account.");
//                return;
//            }
//
//            // 삭제 확인
//            System.out.print("Are you sure you want to delete this manager? (y/n): ");
//            String confirmation = scanner.nextLine().trim();
//            if (!confirmation.equalsIgnoreCase("y")) {
//                System.out.println("Manager deletion cancelled.");
//                return;
//            }
//
//            // 관리자 삭제
//            String query = "DELETE FROM Manager WHERE Manager_Id = ?";
//            PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(query);
//            preparedStatement.setInt(1, managerId);
//            int rows = preparedStatement.executeUpdate();
//
//            if (rows > 0) {
//                System.out.println("Manager deleted successfully!");
//
//                // Audit 로그 기록
//                AuditService auditService = new AuditService();
//                auditService.logAction(AuthUtil.currentManagerId, "Manager", "DELETE");
//            } else {
//                System.out.println("No manager found with the given ID.");
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Error deleting manager: " + e.getMessage());
//        } catch (Exception e) {
//            System.out.println("Unexpected error occurred: " + e.getMessage());
//        }
//    }

    private void deleteUser() {
        Scanner scanner = new Scanner(System.in);

        try {
            int userId = getValidUserId(scanner);

            // 삭제 확인
            System.out.print("Are you sure you want to delete this user? (y/n): ");
            String confirmation = scanner.nextLine().trim();
            if (!confirmation.equalsIgnoreCase("y")) {
                System.out.println("User deletion cancelled.");
                return;
            }

            // 사용자 삭제
            String query = "DELETE FROM User WHERE User_Id = ?";
            PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(query);
            preparedStatement.setInt(1, userId);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("User deleted successfully!");

                // Audit 로그 기록
                AuditService auditService = new AuditService();
                auditService.logAction(AuthUtil.currentManagerId, "User", "DELETE");
            } else {
                System.out.println("No user found with the given ID.");
            }

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error occurred: " + e.getMessage());
        }
    }

}

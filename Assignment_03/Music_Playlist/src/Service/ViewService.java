package Service;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ViewService {
    public void viewOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nView Options:");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.println("5. View Reports");
        System.out.println("6. Back to main menu");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");

        int addChoice = scanner.nextInt();
        scanner.nextLine();

        switch (addChoice) {
            case 1:

                try {
                    // 아티스트 목록을 데이터베이스에서 가져오기
                    String query = "SELECT * FROM Artist";
                    PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery();

                    // 헤더 출력
                    System.out.println("\n--- Artist List ---");
                    System.out.printf("%-10s | %-20s | %-12s | %-20s%n", "Artist ID", "Artist Name", "Debut Date", "Agency");
                    System.out.println("----------------------------------------------------------------------");

                    // 데이터 출력
                    while (rs.next()) {
                        System.out.printf(
                                "%-10d | %-20s | %-12s | %-20s%n",
                                rs.getInt("Artist_Id"),
                                rs.getString("Artist_Name"),
                                rs.getString("Debut_Date"),
                                rs.getString("Agency")
                        );
                    }
                } catch (SQLException e) {
                    System.out.println("Error while printing: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                }

                break;
            case 2:
                try {
                    String query = "SELECT * FROM Album";
                    PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery();

                    // 헤더 출력
                    System.out.println("\n--- Album List ---");
                    System.out.printf("%-10s | %-20s | %-15s | %-12s | %-10s%n",
                            "Album ID", "Album Name", "Total Tracks", "Release Date", "Artist ID");
                    System.out.println("----------------------------------------------------------------------");

                    // 데이터 출력
                    while (rs.next()) {
                        System.out.printf(
                                "%-10d | %-30s | %-15d | %-12s | %-10d%n",
                                rs.getInt("Album_Id"),
                                rs.getString("Album_Name"),
                                rs.getInt("Total_Tracks"),
                                rs.getString("Release_Date"),
                                rs.getInt("artistId")
                        );
                    }
                } catch (SQLException e) {
                    System.out.println("Error while printing: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                }

                break;

            case 3:
                try {
                    String query = "SELECT * FROM Music";
                    PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery();

                    // 헤더 출력
                    System.out.println("\n--- Music List ---");
                    System.out.printf("%-10s | %-30s | %-10s | %-12s | %-10s | %-10s%n",
                            "Music ID", "Title", "Length", "Manager ID", "Album ID", "Genre ID");
                    System.out.println("------------------------------------------------------------------------------------------");

                    // 데이터 출력
                    while (rs.next()) {
                        System.out.printf(
                                "%-10d | %-30s | %-10d | %-12d | %-10d | %-10d%n",
                                rs.getInt("Music_Id"),
                                rs.getString("Title"),
                                rs.getInt("Length"),
                                rs.getInt("managerId"),
                                rs.getInt("albumId"),
                                rs.getInt("genreId")
                        );
                    }
                } catch (SQLException e) {
                    System.out.println("Error while printing: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                }


                break;
            case 4:
                try {
                    String query = "SELECT * FROM Genre";
                    PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                    ResultSet rs = pstmt.executeQuery();

                    // 헤더 출력
                    System.out.println("\n--- Genre List ---");
                    System.out.printf("%-10s | %-20s%n", "Genre ID", "Genre Name");
                    System.out.println("----------------------------------");

                    // 데이터 출력
                    while (rs.next()) {
                        System.out.printf(
                                "%-10d | %-20s%n",
                                rs.getInt("Genre_Id"),
                                rs.getString("Genre_Name")
                        );
                    }
                } catch (SQLException e) {
                    System.out.println("Error while printing: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Unexpected error: " + e.getMessage());
                }

                break;
            case 5:
                viewReports();
                return;
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

    private void viewReports() {
        try {
            String userQuery = "SELECT COUNT(*) AS TotalUsers FROM User";
            String musicQuery = "SELECT COUNT(*) AS TotalMusic FROM Music";
            String albumQuery = "SELECT COUNT(*) AS TotalAlbums FROM Album";
            String genreQuery = "SELECT COUNT(*) AS TotalGenres FROM Genre";

            PreparedStatement userStmt = DatabaseUtil.getConnection().prepareStatement(userQuery);
            PreparedStatement musicStmt = DatabaseUtil.getConnection().prepareStatement(musicQuery);
            PreparedStatement albumStmt = DatabaseUtil.getConnection().prepareStatement(albumQuery);
            PreparedStatement genreStmt = DatabaseUtil.getConnection().prepareStatement(genreQuery);

            ResultSet userRs = userStmt.executeQuery();
            ResultSet musicRs = musicStmt.executeQuery();
            ResultSet albumRs = albumStmt.executeQuery();
            ResultSet genreRs = genreStmt.executeQuery();

            if (userRs.next() && musicRs.next() && albumRs.next() && genreRs.next()) {
                System.out.println("\n--- Platform Reports ---");
                System.out.println("Total Users: " + userRs.getInt("TotalUsers"));
                System.out.println("Total Music: " + musicRs.getInt("TotalMusic"));
                System.out.println("Total Albums: " + albumRs.getInt("TotalAlbums"));
                System.out.println("Total Genres: " + genreRs.getInt("TotalGenres"));
            } else {
                System.out.println("Unable to retrieve report data.");
            }
        } catch (Exception e) {
            System.out.println("Error while viewing reports: " + e.getMessage());
        }

    }


}

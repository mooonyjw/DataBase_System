package UserService;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LikeService {
    public void viewLikedSongs(int userId) {
        try {
            String query = """
        SELECT m.Title, a.Album_Name, ar.Artist_Name
        FROM Likes l
        JOIN Music m ON l.liked_music_id = m.Music_Id
        JOIN Album a ON m.albumId = a.Album_Id
        JOIN Artist ar ON a.artistId = ar.Artist_Id
        WHERE l.like_user_id = ?
        """;

            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Liked Songs ---");
            System.out.printf("%-30s | %-20s | %-20s%n", "Title", "Album", "Artist");
            System.out.println("-----------------------------------------------------------------------");

            boolean hasLikedSongs = false;

            while (rs.next()) {
                hasLikedSongs = true;
                System.out.printf(
                        "%-30s | %-20s | %-20s%n",
                        rs.getString("Title"),
                        rs.getString("Album_Name"),
                        rs.getString("Artist_Name")
                );
            }

            if (!hasLikedSongs) {
                System.out.println("You have not liked any songs yet.");
            }

        } catch (Exception e) {
            System.out.println("Error retrieving liked songs: " + e.getMessage());
        }
    }


    public void manageLikedSongs(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 노래 검색
            System.out.print("Enter a song title to search: ");
            String searchKeyword = scanner.nextLine().trim();

            if (searchKeyword.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            // 2. 검색 쿼리 실행
            String searchQuery = """
            SELECT m.Music_Id, m.Title, a.Album_Name, ar.Artist_Name
            FROM Music m
            JOIN Album a ON m.albumId = a.Album_Id
            JOIN Artist ar ON a.artistId = ar.Artist_Id
            WHERE m.Title LIKE ?
        """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(searchQuery);
            pstmt.setString(1, "%" + searchKeyword + "%");
            ResultSet rs = pstmt.executeQuery();

            // 3. 검색 결과 출력
            System.out.println("\n--- Search Results ---");
            System.out.printf("%-5s | %-30s | %-20s | %-20s%n", "No.", "Title", "Album", "Artist");
            System.out.println("----------------------------------------------------------------------");

            Map<Integer, Integer> searchResults = new HashMap<>();
            int index = 1; // 결과 목록 번호
            while (rs.next()) {
                int musicId = rs.getInt("Music_Id");
                searchResults.put(index, musicId);
                System.out.printf(
                        "%-5d | %-30s | %-20s | %-20s%n",
                        index,
                        rs.getString("Title"),
                        rs.getString("Album_Name"),
                        rs.getString("Artist_Name")
                );
                index++;
            }

            // 검색 결과가 없을 경우 처리
            if (searchResults.isEmpty()) {
                System.out.println("No results found for your search.");
                return;
            }

            // 4. 사용자 선택
            System.out.print("\nEnter the number of the song you want to like or unlike (or press ENTER to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Operation cancelled.");
                return;
            }

            int userChoice;
            try {
                userChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                return;
            }

            if (!searchResults.containsKey(userChoice)) {
                System.out.println("Invalid selection. Please try again.");
                return;
            }

            int selectedMusicId = searchResults.get(userChoice);

            // 5. 좋아요 추가/제거
            String checkQuery = "SELECT * FROM Likes WHERE liked_music_id = ? AND like_user_id = ?";
            PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, selectedMusicId);
            checkStmt.setInt(2, userId);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // 이미 좋아요한 곡 -> 제거
                String deleteQuery = "DELETE FROM Likes WHERE liked_music_id = ? AND like_user_id = ?";
                PreparedStatement deleteStmt = DatabaseUtil.getConnection().prepareStatement(deleteQuery);
                deleteStmt.setInt(1, selectedMusicId);
                deleteStmt.setInt(2, userId);

                int rows = deleteStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Song removed from Liked Songs.");
                } else {
                    System.out.println("Failed to remove song from Liked Songs.");
                }
            } else {
                // 좋아요하지 않은 곡 -> 추가
                String insertQuery = "INSERT INTO Likes (liked_music_id, like_user_id) VALUES (?, ?)";
                PreparedStatement insertStmt = DatabaseUtil.getConnection().prepareStatement(insertQuery);
                insertStmt.setInt(1, selectedMusicId);
                insertStmt.setInt(2, userId);

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("Song added to Liked Songs.");
                } else {
                    System.out.println("Failed to add song to Liked Songs.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error managing Liked Songs: " + e.getMessage());
        }
    }

}

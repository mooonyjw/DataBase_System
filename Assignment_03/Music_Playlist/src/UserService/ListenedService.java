package UserService;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ListenedService {
    public void showListenHistory(int userId) {
        try {
            String query = """
            SELECT m.Title, m.Length, a.Album_Name, ar.Artist_Name, l.m_Timestamp
            FROM Listens l
            JOIN Music m ON l.musicId = m.Music_Id
            JOIN Album a ON m.albumId = a.Album_Id
            JOIN Artist ar ON a.artistId = ar.Artist_Id
            WHERE l.userId = ?
            ORDER BY l.m_Timestamp DESC
            LIMIT 10
        """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Recent Listens ---");
            System.out.printf("%-30s | %-7s | %-20s | %-20s | %-20s%n", "Title", "Length", "Album", "Artist", "Timestamp");
            System.out.println("-----------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-30s | %-7d | %-20s | %-20s | %-20s%n",
                        rs.getString("Title"),
                        rs.getInt("Length"),
                        rs.getString("Album_Name"),
                        rs.getString("Artist_Name"),
                        rs.getString("m_Timestamp"));
            }

        } catch (Exception e) {
            System.out.println("Error while fetching recent listens: " + e.getMessage());
        }
    }

    static void updateListenHistory(int userId, int musicId, int listeningTime) {
        try {
            // 청취 기록 삽입 또는 업데이트
            String query = """
        INSERT INTO Listens (musicId, userId, Total_Listening_Time, m_TimeStamp)
        VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        ON DUPLICATE KEY UPDATE
            Total_Listening_Time = Total_Listening_Time + VALUES(Total_Listening_Time),
            m_TimeStamp = CURRENT_TIMESTAMP
        """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, listeningTime);
            pstmt.executeUpdate();

            System.out.println("Listen history updated successfully.");
        } catch (Exception e) {
            System.out.println("Error updating listen history: " + e.getMessage());
        }
    }
    public void viewMostPlayedSongs(int userId) {
        try {
            String query = """
        SELECT m.Title, SUM(l.Total_Listening_Time) AS TotalListeningTime, a.Album_Name, ar.Artist_Name
        FROM Listens l
        JOIN Music m ON l.musicId = m.Music_Id
        JOIN Album a ON m.albumId = a.Album_Id
        JOIN Artist ar ON a.artistId = ar.Artist_Id
        WHERE l.userId = ?
        GROUP BY m.Music_Id
        ORDER BY TotalListeningTime DESC
        LIMIT 10
        """;

            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Most Played Songs by Listening Time ---");
            System.out.printf("%-30s | %-20s | %-20s | %-20s%n", "Title", "Total Listening Time (seconds)", "Album", "Artist");
            System.out.println("---------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf(
                        "%-30s | %-30d | %-20s | %-20s%n",
                        rs.getString("Title"),
                        rs.getInt("TotalListeningTime"),
                        rs.getString("Album_Name"),
                        rs.getString("Artist_Name")
                );
            }
        } catch (Exception e) {
            System.out.println("Error retrieving most-played songs: " + e.getMessage());
        }
    }

}

package UserService;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ListenedService {
    public void showListenHistory(int userId) {
        try {
            String query = """
                SELECT m.Title, l.Total_Listening_Time, l.Listening_Date
                FROM Listens l
                JOIN Music m ON l.musicId = m.Music_Id
                WHERE l.userId = ?
                ORDER BY l.Listening_Date DESC
                """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Listen History ---");
            System.out.println("Title | Total Listening Time (seconds) | Last Listening Date");
            while (rs.next()) {
                String title = rs.getString("Title");
                int totalListeningTime = rs.getInt("Total_Listening_Time");
                String listeningDate = rs.getString("Listening_Date");

                System.out.println(title + " | " + totalListeningTime + " | " + listeningDate);
            }
        } catch (Exception e) {
            System.out.println("Error fetching listen history: " + e.getMessage());
        }
    }

    static void updateListenHistory(int userId, int musicId, int listeningTime) {
        try {
            // 먼저 기존 기록 확인
            String checkQuery = "SELECT * FROM Listens WHERE userId = ? AND musicId = ?";
            PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, musicId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 기존 기록이 있을 경우 업데이트
                String updateQuery = """
                    UPDATE Listens 
                    SET Total_Listening_Time = Total_Listening_Time + ?, 
                        Listening_Date = CURDATE() 
                    WHERE userId = ? AND musicId = ?
                    """;
                PreparedStatement updateStmt = DatabaseUtil.getConnection().prepareStatement(updateQuery);
                updateStmt.setInt(1, listeningTime);
                updateStmt.setInt(2, userId);
                updateStmt.setInt(3, musicId);
                updateStmt.executeUpdate();
            } else {
                // 기존 기록이 없을 경우 새로 삽입
                String insertQuery = """
                    INSERT INTO Listens (musicId, userId, Total_Listening_Time, Listening_Date) 
                    VALUES (?, ?, ?, CURDATE())
                    """;
                PreparedStatement insertStmt = DatabaseUtil.getConnection().prepareStatement(insertQuery);
                insertStmt.setInt(1, musicId);
                insertStmt.setInt(2, userId);
                insertStmt.setInt(3, listeningTime);
                insertStmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error updating listen history: " + e.getMessage());
        }
    }

}

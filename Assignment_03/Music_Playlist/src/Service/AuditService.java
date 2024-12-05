package Service;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;

public class AuditService {

    public void logAction(int manageId, String tableName, String actionType) {
        try {
            String query = "INSERT INTO Audit (ManageId, Table_Name, Action_Type) VALUES (?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, manageId); // 매니저 ID
            pstmt.setString(2, tableName); // 변경된 테이블 이름
            pstmt.setString(3, actionType); // 작업 종류 (CREATE, UPDATE, DELETE)
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logListen(int userId, int musicId, int listeningTime) {
        try {
            String query = "INSERT INTO Listens (musicId, userId, Total_Listening_Time, Listening_Date, m_Timestamp) " +
                    "VALUES (?, ?, ?, CURRENT_DATE, CURRENT_TIMESTAMP) " +
                    "ON DUPLICATE KEY UPDATE Total_Listening_Time = Total_Listening_Time + ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, musicId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, listeningTime);
            pstmt.setInt(4, listeningTime);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

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
}

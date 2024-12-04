package Auth;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthUtil {
    public static int currentManagerId = -1;  // 로그인된 매니저 ID 저장 (기본값 -1)
    public static int currentUserId = -1;  // 로그인된 사용자 ID 저장 (기본값 -1)

    public static boolean isManager(String email, String password) {
        try {
            String query = "SELECT * FROM Manager WHERE Manager_Email = ? AND Manager_Password = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentManagerId = rs.getInt("Manager_Id"); // 로그인된 매니저 ID 저장
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isUser(String email, String password) {
        try {
            String query = "SELECT * FROM User WHERE User_Email = ? AND User_Password = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                currentUserId = rs.getInt("User_Id"); // 로그인된 매니저 ID 저장
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateManagerPin(String email, String password, int pin) {
        try {
            String query = "SELECT * FROM Manager WHERE Manager_Email = ? AND Manager_Password = ? AND Manager_PIN = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setInt(3, pin); // PIN도 추가로 확인
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 조건을 만족하는 행이 있으면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

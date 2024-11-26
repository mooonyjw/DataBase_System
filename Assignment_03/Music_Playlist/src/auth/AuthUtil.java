package Auth;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthUtil {
    public static boolean isManager(String email, String password) {
        try {
            String query = "SELECT * FROM Manager WHERE Manager_Id = ? AND Manager_Password = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // If a row exists, it's valid
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isUser(String email, String password) {
        try {
            String query = "SELECT * FROM User WHERE User_Id = ? AND User_Password = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validateManagerPin(int pin) {
        return pin == 1234;
    }
}

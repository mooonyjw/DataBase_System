package Auth;

import Security.DatabaseUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static boolean validateManagerPin(String email, String inputPassword, int pin) {
        try {
            // 이메일로 관리자 정보 가져오기
            String query = "SELECT Manager_Password, Salt, Manager_PIN FROM Manager WHERE Manager_Email = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 데이터베이스에서 가져온 해싱된 비밀번호와 Salt, PIN
                String dbHashedPassword = rs.getString("Manager_Password");
                String dbSalt = rs.getString("Salt");
                int dbPin = rs.getInt("Manager_PIN");

                // 입력된 비밀번호를 DB의 Salt로 해싱
                String hashedInputPassword = hashPassword(inputPassword, dbSalt);

//                디버깅
//                System.out.println("Original Input Password: " + inputPassword); // 원래 비밀번호
//                System.out.println("Salt from DB: " + dbSalt);
//                System.out.println("Recomputed Hash: " + hashedInputPassword);
//                System.out.println("DB Hash: " + dbHashedPassword);
//                System.out.println("DB PIN: " + dbPin);
//                System.out.println("Input PIN: " + pin);
                // 해싱된 비밀번호와 PIN 검증
                return dbHashedPassword.equals(hashedInputPassword) && dbPin == pin;
            } else {
                System.out.println("No manager found with the given email.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // 조건 불충족 시 false 반환
    }

//    public static String hashPassword(String password, String salt) {
//        try {
//            // SHA-256 알고리즘 객체 생성
//            MessageDigest md = MessageDigest.getInstance("SHA-256");
//
//            // 비밀번호와 Salt를 결합하여 해싱
//            md.update((password + salt).getBytes());
//            byte[] hashedBytes = md.digest();
//
//            // 바이트 배열을 16진수 문자열로 변환
//            StringBuilder sb = new StringBuilder();
//            for (byte b : hashedBytes) {
//                sb.append(String.format("%02x", b));
//            }
//
//            return sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error hashing password: " + e.getMessage());
//        }
//    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((password + salt).getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }


}

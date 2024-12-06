package Utils;


import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IsValidUtil {

    // 아티스트 ID 유효성 확인
    public static boolean isValidArtistId(int artistId) {
        try {
            String query = "SELECT Artist_Id FROM Artist WHERE Artist_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, artistId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if ArtistId exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 앨범 ID 유효성 확인
    public static boolean isValidAlbumId(int albumId) {
        try {
            String query = "SELECT Album_Id FROM Album WHERE Album_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, albumId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if AlbumId exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 음악 ID 유효성 확인
    public static boolean isValidMusicId(int musicId) {
        try {
            String query = "SELECT Music_Id FROM Music WHERE Music_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, musicId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if MusicId exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 장르 ID 유효성 확인
    public static boolean isValidGenreId(int genreId) {
        try {
            String query = "SELECT Genre_Id FROM Genre WHERE Genre_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, genreId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if GenreId exists
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isGenreExists(String genreName) {
        try {
            // 대소문자를 무시하기 위해 LOWER를 사용
            String query = "SELECT Genre_Id FROM Genre WHERE LOWER(Genre_Name) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName); // 입력값 설정
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 존재하면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isArtistExists(String artistName) {
        try {
            // 대소문자를 무시하기 위해 LOWER를 사용
            String query = "SELECT Artist_Id FROM Artist WHERE LOWER(Artist_Name) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, artistName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 존재하면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isMPhoneTaken(String phone) {
        try {
            String query = """
        SELECT COUNT(*) AS count
        FROM Manager
        WHERE Manager_Phone = ?
        """;

            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, phone);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0; // 중복된 전화번호가 있는 경우 true 반환
            }
        } catch (SQLException e) {
            System.out.println("Error checking phone: " + e.getMessage());
        }
        return false; // 중복된 전화번호가 없으면 false 반환
    }

    public static boolean isUPhoneTaken(String phone) {
        try {
            String query = """
        SELECT COUNT(*) AS count
        FROM User
        WHERE User_Phone = ?
        """;

            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, phone);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0; // 중복된 전화번호가 있는 경우 true 반환
            }
        } catch (SQLException e) {
            System.out.println("Error checking user phone: " + e.getMessage());
        }
        return false; // 중복된 전화번호가 없으면 false 반환
    }

}

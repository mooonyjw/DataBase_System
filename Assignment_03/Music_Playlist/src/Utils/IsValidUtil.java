package Utils;


import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
}

package Security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "URL"; // 데이터베이스 URL
    private static final String USER = "USER"; // MySQL 사용자 이름
    private static final String PASSWORD = "PASSWORD"; // MySQL 비밀번호

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.");
        }
    }
}

package Security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class DatabaseUtil {
    //Map<String, String> env = getenv();
    private static final String URL = System.getenv("DBURL"); // 데이터베이스 URL
    private static final String USER =  System.getenv("DBUSER"); // MySQL 사용자 이름
    private static final String PASSWORD =  System.getenv("DBPASSWORD"); // MySQL 비밀번호

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to the database.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

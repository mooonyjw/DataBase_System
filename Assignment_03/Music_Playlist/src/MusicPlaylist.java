import java.sql.*;
import java.util.Scanner;

public class MusicPlaylist {

    private static final String URL = "jdbc:mysql://localhost:3306/music_playlist";
    private static final String USER = "root";
    private static final String PASSWORD = "useruser";

    private Connection con;

    public MusicPlaylist() {
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB 연결
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully!");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nWelcome to Jungwoni Music!");
            System.out.println("1. Manager Menu");
            System.out.println("2. ");
            System.out.println("3. Delete Music");
            System.out.println("4. View Music");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    insertMusic();
                    break;
                case 2:
                    updateMusic();
                    break;
                case 3:
                    deleteMusic();
                    break;
                case 4:
                    selectMusic();
                    break;
                case 5:
                    close();
                    System.out.println("Exiting the application.");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    public void insertMusic() {
        // Insert logic here
    }

    public void updateMusic() {
        // Update logic here
    }

    public void deleteMusic() {
        // Delete logic here
    }

    public void selectMusic() {
        // Select logic here
    }

    public void close() {
        try {
            if (con != null) {
                con.close();
                System.out.println("Disconnected from the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

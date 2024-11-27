package Model;

import Security.DatabaseUtil;
import Service.MusicService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Manager {
    private MusicService musicService = new MusicService();

    public void showManagerMenu(String managerName) {

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nWelcome, Manager " + managerName + "! Ready to oversee the music platform?\n");
            System.out.println("1. Add Album or Music");
            System.out.println("2. Search Artist or Album");
            System.out.println("3. Update Music");
            System.out.println("4. Delete Music");
            System.out.println("5. View Reports");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    musicService.addOption();
                    break;
                case 2:
                    searchArtist();
                    break;
                case 3:
                    updateMusic();
                    break;
                case 4:
                    deleteMusic();
                    break;
                case 5:
                    viewReports();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);
    }

    private void addMusic() {
        System.out.println("Adding new music...");
        // Add database query logic
    }

    private void updateMusic() {
        System.out.println("Updating music...");
        // Add database query logic
    }

    private void deleteMusic() {
        System.out.println("Deleting music...");
        // Add database query logic
    }

    private void viewReports() {
        System.out.println("Viewing reports...");
        // Add database query logic
    }

    public String getManagerName(String email) {
        try {
            String query = "SELECT Manager_Name FROM Manager WHERE Manager.Manager_Email = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);  // 첫 번쨰 매개변수에 email 값 설정
            ResultSet rs = pstmt.executeQuery();  // query 실행
            if (rs.next()) {  // query의 결과 존재
                return rs.getString("Manager_Name");  // 필드 값 반환
            }
        } catch (Exception e) {
            e.printStackTrace();  // 예외 발생 시 출력
        }
        return "Manager";  // 결과가 없거나 예외 발생 시 기본값 반환
    }
}
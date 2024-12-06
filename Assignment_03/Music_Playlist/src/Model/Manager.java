package Model;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import Service.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Manager {
    private AddService addService = new AddService();
    private SearchService searchService = new SearchService();
    private UpdateService updateService = new UpdateService();
    private DeleteService deleteService = new DeleteService();
    private ViewService viewService = new ViewService();

    public void showManagerMenu(String managerName) {

        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nWelcome, Manager " + getManagerName() + "! Ready to oversee the music platform?\n");
            System.out.println("1. Add");
            System.out.println("2. Search");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. View Reports");
            System.out.println("6. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addService.addOption();
                    break;
                case 2:
                    searchService.searchOption();
                    break;
                case 3:
                    updateService.updateOption();
                    break;
                case 4:
                    deleteService.deleteOption();
                    break;
                case 5:
                    viewService.viewOption();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);
    }

    public String getManagerName() {
        try {
            String query = "SELECT Manager_Name FROM Manager WHERE Manager_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, AuthUtil.currentManagerId); // 현재 로그인된 Manager의 ID 사용
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Manager_Name"); // DB에서 Manager 이름 가져오기
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Manager"; // 기본값 반환
    }

}
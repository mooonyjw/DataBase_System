package Auth;

import Model.Manager;
import Model.User;
import Security.DatabaseUtil;

import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import static Auth.AuthUtil.*;

public class Login {
    // 로그인 메뉴 표시
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nWelcome to the Jungwoni Music!\nSelect what you want to do:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // 로그인 실행
                    login();
                    break;
                case 2:

                    Register register = new Register();
                    // 회원가입 실행
                    register.register();
                    break;
                case 3:
                    // 종료
                    System.out.println("Goodbye! See you soon!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 3);
    }

    // 로그인
    public void login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Enter your Password: ");
        String password = scanner.nextLine().trim();

        try {
            // 사용자 또는 관리자의 정보를 가져오는 쿼리
            String query = """
        SELECT Manager_Id AS Id, Manager_Password AS HashedPassword, Salt, 'Manager' AS UserType
        FROM Manager WHERE Manager_Email = ?
        UNION
        SELECT User_Id AS Id, User_Password AS HashedPassword, Salt, 'User' AS UserType
        FROM User WHERE User_Email = ?
        """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setString(2, email);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // 계정 정보 가져오기
                String dbHashedPassword = rs.getString("HashedPassword");
                String salt = rs.getString("Salt");
                String userType = rs.getString("UserType");
                int id = rs.getInt("Id");

                // 입력된 비밀번호 해싱
                String hashedInputPassword = hashPassword(password, salt);

                if (dbHashedPassword.equals(hashedInputPassword)) {
                    // 비밀번호가 일치하는 경우
                    if ("Manager".equals(userType)) {
                        System.out.print("Enter Manager PIN: ");
                        int pin = scanner.nextInt();
                        scanner.nextLine(); // 버퍼 정리

                        if (validateManagerPin(email, password, pin)) {
                            Manager manager = new Manager();
                            currentManagerId = id;
                            manager.showManagerMenu(manager.getManagerName());
                        } else {
                            System.out.println("Invalid Manager PIN. Access denied.");
                        }
                    } else if ("User".equals(userType)) {
                        User user = new User();
                        currentUserId = id;
                        user.showUserMenu(user.getUserName(email));
                    }
                } else {
                    System.out.println("Invalid password. Access denied.");
                }
            } else {
                System.out.println("Invalid email or account does not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        }
    }



}
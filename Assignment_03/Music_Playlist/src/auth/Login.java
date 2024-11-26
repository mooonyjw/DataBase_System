package Auth;

import Model.Manager;
import Model.User;
import Security.DatabaseUtil;

import java.sql.PreparedStatement;
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
        String email = scanner.nextLine();

        System.out.print("Enter your Password: ");
        String password = scanner.next();

        // 관리자 여부 확인
        if (isManager(email, password)) {
            System.out.print("Enter Manager PIN: ");
            int pin = scanner.nextInt();
            if (validateManagerPin(pin)) {
                Manager manager = new Manager();
                manager.showManagerMenu();
            } else {
                System.out.println("Invalid Manager PIN. Access denied.");
            }
        } else if (isUser(email, password)) {
            User user = new User();
            user.showUserMenu();
        } else {
            System.out.println("Invalid login credentials.");
        }
    }
}
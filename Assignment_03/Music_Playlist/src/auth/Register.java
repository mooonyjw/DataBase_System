package Auth;

import Security.DatabaseUtil;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.time.LocalDate;

public class Register {
    public void register() {

        // 메뉴 표시
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nRegister Menu");
        System.out.println("1. Register as User");
        System.out.println("2. Register as Manager");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                // 사용자 등록
                registerUser();
                break;
            case 2:
                // 관리자 등록
                registerManager();
                break;
            default:
                // 잘못된 선택
                System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    private void registerUser() {
        Scanner scanner = new Scanner(System.in);

        // 사용자 이름 입력
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();

        // 사용자 전화번호 입력
        String phone = getValidPhoneNumber(scanner);

        // 사용자 이메일 입력
        String email = getValidEmail(scanner);

        // 사용자 비밀번호 입력
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();

        // 회원가입한 날짜
        LocalDate signUpDate = LocalDate.now();


        try {
            String query = "INSERT INTO User (User_Name, User_Phone, User_Email, User_Password, Sign_up_Date) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setDate(5, java.sql.Date.valueOf(signUpDate));
            int rows = pstmt.executeUpdate();  // 쿼리 실행 후 영향 받은 행 수 반환

            // 사용자 등록 성공 여부 출력
            if (rows > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("User registration failed. Please try again.");
            }
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.out.println("This user already exists. Returning to the main menu.");
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

    }

    // 여기할차례
    private void registerManager() {
        Scanner scanner = new Scanner(System.in);

        // 관리자 이름 입력
        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();

        // 관리자 전화번호 입력
        String phone = getValidPhoneNumber(scanner);

        // 관리자 이메일 입력
        String email = getValidEmail(scanner);

        // 관리자 비밀번호 입력
        System.out.print("Enter your Password: ");
        String password = scanner.nextLine();

        // 관리자 PIN 입력
        int pin = getValidManagerPin(scanner);

        try {
            String query = "INSERT INTO Manager (Manager_Name, Manager_Phone, Manager_Email, Manager_Password, Manager_PIN) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, password);
            pstmt.setInt(5, pin);

            int rows = pstmt.executeUpdate();
            System.out.println(rows + " manager(s) registered successfully!");
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.out.println("This manager already exists. Returning to the main menu.");
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    // 전화번호 유효성 확인 함수
    private String getValidPhoneNumber(Scanner scanner) {
        String phone;
        do {
            System.out.print("Enter your Phone (11 digits, no hyphens): ");
            phone = scanner.nextLine().replace("-", ""); // - 하이픈 제거

            // 11자리 유효성 검사
            if (!phone.matches("\\d{11}")) {
                System.out.println("Invalid phone number. Please enter exactly 11 digits (e.g., 01012345678).");
                continue;
            }
            break; // 올바른 형식일 경우 루프 종료
        } while (true);

        return phone;
    }

    private String getValidEmail(Scanner scanner) {
        String email;
        do {
            System.out.print("Enter your Email: ");
            email = scanner.nextLine().trim(); // 공백 제거

            if (!isValidEmail(email)) {
                System.out.println("This email is not valid. Please try with a valid email.");
                continue;
            }

            break; // 이메일이 유효하고 중복되지 않은 경우 루프 종료
        } while (true);

        return email;

    }

    // 이메일 유효성 확인 함수
    private boolean isValidEmail(String email) {
        email = email.trim(); // 공백 제거
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    // 관리자 PIN 유효성 확인 함수
    public int getValidManagerPin(Scanner scanner) {
        String pin;

        do {
            System.out.print("Enter Manager PIN (4 digits): ");
            pin = scanner.nextLine().trim(); // 공백 제거

            // 유효성 검사: 4자리 숫자인지 확인
            if (pin.matches("\\d{4}")) {
                return Integer.parseInt(pin); // 4자리 숫자인 경우 정수로 변환 후 반환
            } else {
                System.out.println("Invalid PIN. Please enter exactly 4 digits.");
            }
        } while (true);
    }

}

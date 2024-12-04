package Utils;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static Auth.AuthUtil.currentManagerId;

public class ValidationUtil {

    // 유효한 이메일 반환 함수
    public static String getValidEmail(Scanner scanner) {
        String email;
        do {
            System.out.print("Enter your Email: ");
            email = scanner.nextLine().trim(); // 공백 제거

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                System.out.println("This email is not valid. Please try with a valid email.");
                continue;
            }

            // 중복 확인
            if (isDuplicateEmail(email)) {
                System.out.println("This email is already in use. Please try another email.");
                continue;
            }

            break; // 이메일이 유효하고 중복되지 않은 경우 루프 종료
        } while (true);

        return email;

    }

    // 유효한 전화번호 반환 함수
    public static String getValidPhoneNumber(Scanner scanner) {
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

    // 유효한 관리자 PIN 반환 함수
    public static int getValidManagerPin(Scanner scanner) {
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

    // 유효한 날짜 반환 함수
    public static LocalDate getValidDate(Scanner scanner) {
        LocalDate date = null;

        while (date == null) {
            System.out.print("Enter Date (YYYYMMDD or YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();

            if (dateInput.isEmpty()) {
                System.out.println("Date cannot be empty. Please try again.");
                continue;
            }

            // 하이픈 자동 삽입
            if (dateInput.matches("\\d{8}")) {  // 숫자 8자리일 경우
                dateInput = dateInput.substring(0, 4) + "-" + dateInput.substring(4, 6) + "-" + dateInput.substring(6);
            }

            try {
                // Validate and parse date
                date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD or YYYYMMDD format.");
            }
        }
        return date;
    }

    // 유효한 아티스트 ID 반환 함수
    public static int getValidArtistID(Scanner scanner) {
        int artistId = -1;  // 아티스트 ID 초깃값
        boolean isValid = false;  // 유효성 플래그

        while (!isValid) {
            System.out.print("Enter Artist ID: ");
            artistId = scanner.nextInt();
            scanner.nextLine();

            try {
                // DB에서 Artist ID 존재 여부 확인
                String query = "SELECT Artist_Id FROM Artist WHERE Artist_Id = ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                pstmt.setInt(1, artistId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isValid = true;  // Artist ID가 유효하면 반복 종료
                } else {
                    System.out.println("Invalid Artist ID. Please enter a valid ID.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while validating Artist ID. Try again.");
            }
        }
        return artistId;
    }

    // 유효한 앨범 ID 반환 함수
    public static int getValidAlbumID(Scanner scanner) {
        int albumId = -1;  // 앨범 ID 초깃값
        boolean isValid = false;  // 유효성 플래그

        while (!isValid) {
            System.out.print("Enter Album ID: ");
            albumId = scanner.nextInt();
            scanner.nextLine();

            try {
                // DB에서 Album ID 존재 여부 확인
                String query = "SELECT Album_Id FROM Album WHERE Album_Id = ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                pstmt.setInt(1, albumId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isValid = true;  // Album ID가 유효하면 반복 종료
                } else {
                    System.out.println("Invalid Album ID. Please enter a valid ID.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while validating Album ID. Try again.");
            }
        }
        return albumId;
    }

    // 유효한 음악 ID 반환 함수
    public static int getValidMusicID(Scanner scanner) {
        int musicId = -1;  // 음악 ID 초깃값
        boolean isValid = false;  // 유효성 플래그

        while (!isValid) {
            System.out.print("Enter Music ID: ");
            musicId = scanner.nextInt();
            scanner.nextLine();

            try {
                // DB에서 Music ID 존재 여부 확인
                String query = "SELECT Music_Id FROM Music WHERE Music_Id = ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                pstmt.setInt(1, musicId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isValid = true;  // Music ID가 유효하면 반복 종료
                } else {
                    System.out.println("Invalid Music ID. Please enter a valid ID.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while validating Music ID. Try again.");
            }
        }
        return musicId;
    }

    // 유효한 장르 ID 반환 함수
    public static int getValidGenreID(Scanner scanner) {
        int genreId = -1;  // 장르 ID 초깃값
        boolean isValid = false;  // 유효성 플래그

        while (!isValid) {
            System.out.print("Enter Genre ID: ");
            genreId = scanner.nextInt();
            scanner.nextLine();

            try {
                // DB에서 Genre ID 존재 여부 확인
                String query = "SELECT Genre_Id FROM Genre WHERE Genre_Id = ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
                pstmt.setInt(1, genreId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    isValid = true;  // Genre ID가 유효하면 반복 종료
                } else {
                    System.out.println("Invalid Genre ID. Please enter a valid ID.");
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error while validating Genre ID. Try again.");
            }
        }
        return genreId;
    }

    // 중복 이메일 확인 함수
    private static boolean isDuplicateEmail(String email) {
        try {
            String query = "SELECT Manager_Id FROM Manager WHERE Manager_Email = ? AND Manager_Id != ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            pstmt.setInt(2, currentManagerId); // 현재 로그인된 manager 제외
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 중복된 이메일이 있으면 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 에러 발생 시 중복으로 간주
        }
    }

    // 중복 아티스트 이름 확인 함수
    public static boolean isDuplicateArtistName(String artistName) {
        try {
            String query = "SELECT Artist_Id FROM Artist WHERE LOWER(Artist_Name) = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, artistName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // 결과가 존재하면 중복
        } catch (Exception e) {
            e.printStackTrace();
            return true; // 에러 발생 시 중복으로 간주
        }
    }


}

package Service;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchService {
    // 이름으로 id만 찾을까 아니면 다른 기능들도 넣을까... 흠 ..
    public void searchOption() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nWhat you are searching for?");
        System.out.println("1. Artist");
        System.out.println("2. Album");
        System.out.println("3. Music");
        System.out.println("4. Genre");
        System.out.println("5. Back to main menu");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
        int addChoice = scanner.nextInt();
        scanner.nextLine();

        switch (addChoice) {
        case 1:
            searchArtistId();
            break;
        case 2:
            searchAlbumId();
            break;
        case 3:
            searchMusicId();
            break;
        case 4:
            searchGenreId();
            break;
        case 5:
            System.out.println("Returning to main menu.");
            return;
        case 6:
            System.out.println("Exiting the program. Goodbye!");
            System.exit(0);
            break;
        default:
            System.out.println("Invalid choice. Returning to main menu.");
        }
    }

    // Artist ID 찾기
    public void searchArtistId() {
        Scanner scanner = new Scanner(System.in);
        int artistId = -1;
        try {
            String artistName;
            while (true) {
                System.out.print("Enter Artist Name: ");
                artistName = scanner.nextLine().trim();

                if (artistName.isEmpty()) {
                    System.out.println("Artist name cannot be empty. Please try again.");
                    continue;
                }
                break;
            }
            // 아티스트 이름을 대소문자 구분 없이 검색 (부분 일치)
            String query = "SELECT Artist_Id FROM Artist WHERE LOWER(Artist_Name) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, artistName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                artistId = rs.getInt("Artist_Id");
                System.out.println("Artist found: ID = " + artistId);
            } else {
                System.out.println("Artist not found. Please try again.");
            }
        } catch (SQLException e) {
            System.out.println("Error while searching for Artist ID: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    // Artist 이름 찾기
//    public void searchArtist() {
//        Scanner scanner = new Scanner(System.in);
//        String artistName;
//
//        try {
//            // 유효한 아티스트 이름을 입력받을 때까지 반복
//            while (true) {
//                System.out.print("Enter Artist Name to search: ");
//                artistName = scanner.nextLine().trim();
//
//                // 입력값이 비어있으면 다시 요청
//                if (artistName.isEmpty()) {
//                    System.out.println("Artist name cannot be empty. Please enter a valid name.");
//                    continue;
//                }
//
//                String query = "SELECT Artist_Id, Artist_Name FROM Artist WHERE Artist_Name = ?";
//                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
//                pstmt.setString(1, artistName);
//                ResultSet rs = pstmt.executeQuery();
//
//                // 결과가 존재하는지 확인
//                if (rs.next()) {
//                    System.out.println("Search Results:");
//                    do {
//                        System.out.println("ID: " + rs.getInt("Artist_Id") + ", Name: " + rs.getString("Artist_Name"));
//                    } while (rs.next());
//                    break; // 유효한 결과가 있으면 반복 종료
//                } else {
//                    System.out.println("No artist found with the name \"" + artistName + "\". Please try again.");
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error while searching for artist: " + e.getMessage());
//        }
//    }
//
    // Album ID 찾기
    public void searchAlbumId() {
        Scanner scanner = new Scanner(System.in);
        int albumId = -1;
        try {
            String albumName;
            while (true) {
                System.out.print("Enter Album Name: ");
                albumName = scanner.nextLine().trim();

                if (albumName.isEmpty()) {
                    System.out.println("Album name cannot be empty. Please try again.");
                    continue;
                }
                break;
            }
            // 대소문자 구분 없이 검색
            String query = "SELECT Album_Id, Album_Name FROM Album WHERE LOWER(Album_Name) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, albumName);
            ResultSet rs = pstmt.executeQuery();

            // 여러 결과가 나올 가능성 처리
            List<Integer> albumIds = new ArrayList<>();
            while (rs.next()) {
                albumIds.add(rs.getInt("Album_Id"));
                System.out.println("Found Album: ID = " + rs.getInt("Album_Id") + ", Name = " + rs.getString("Album_Name"));
            }

            if (albumIds.isEmpty()) {
                System.out.println("Album not found. Please try again.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while searching for Album ID: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while searching for Album ID: " + e.getMessage());
        }
    }

    // Music ID 찾기
    public void searchMusicId() {
        Scanner scanner = new Scanner(System.in);
        int musicId = -1;

        try {
            String musicName;
            while (true) {
                System.out.print("Enter Music Name: ");
                musicName = scanner.nextLine().trim();  // 양쪽 공백 제거

                // 입력값 검증
                if (musicName.isEmpty()) {
                    System.out.println("Music name cannot be empty. Please enter a valid name.");
                    continue;
                }
                break;
            }

            // 대소문자 구분 없는 검색
            String query = "SELECT Music_Id, Title FROM Music WHERE LOWER(Title) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, musicName);
            ResultSet rs = pstmt.executeQuery();

            // 검색 결과 처리
            List<Integer> musicIds = new ArrayList<>();
            while (rs.next()) {
                musicIds.add(rs.getInt("Music_Id"));
                System.out.println("Found Music: ID = " + rs.getInt("Music_Id") + ", Title = " + rs.getString("Title"));
            }

            if (musicIds.isEmpty()) {
                System.out.println("Music not found. Please try again.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while searching for Music ID: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while searching for Music ID: " + e.getMessage());
        }
    }

    // Genre ID 찾기
    public void searchGenreId() {
        Scanner scanner = new Scanner(System.in);
        int genreId = -1;

        try {
            String genreName;
            while (true) {
                System.out.print("Enter Genre Name: ");
                genreName = scanner.nextLine().trim();

                // 입력값 검증
                if (genreName.isEmpty()) {
                    System.out.println("Genre name cannot be empty. Please enter a valid genre name.");
                    continue;
                }
                break;
            }

            // 대소문자 구분 없는 검색
            String query = "SELECT Genre_Id, Genre_Name FROM Genre WHERE LOWER(Genre_Name) = LOWER(?)";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, genreName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                genreId = rs.getInt("Genre_Id");
                System.out.println("Genre found: ID = " + genreId + ", Name = " + rs.getString("Genre_Name"));
            } else {
                System.out.println("Genre not found. Please try again.");
            }

        } catch (SQLException e) {
            System.out.println("Database error while searching for Genre ID: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error while searching for Genre ID: " + e.getMessage());
        }
    }
}

package UserService;

import Auth.AuthUtil;
import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.time.Duration;
import java.time.Instant;

import static UserService.ListenedService.updateListenHistory;

public class UserSearchService {
    public void usersearchOption() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nWhat do you want to search for?");
            System.out.println("0. Search and play music");
            System.out.println("1. Music");
            System.out.println("2. Artist");
            System.out.println("3. Album");
            System.out.println("4. Genre and show songs by genre");
            System.out.println("5. Back to main menu");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int searchChoice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            switch (searchChoice) {
                case 0:
                    searchAndPlayMusic();
                    break;
                case 1:
                    searchMusic();
                    break;
                case 2:
                    searchArtist();
                    break;
                case 3:
                    searchAlbum();
                    break;
                case 4:
                    searchGenre();
                    showSongsByGenre();
                    break;
                case 5:
                    System.out.println("Returning to main menu...");
                    return; // Exit the function and go back to the main menu
                case 6:
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0); // Terminate the program
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private void searchAndPlayMusic() {
        Scanner scanner = new Scanner(System.in);

        try {
            // 검색어 입력
            System.out.print("Enter Music Title or Keywords: ");
            String musicTitle = scanner.nextLine().trim();

            // 검색 쿼리
            String query = """
                SELECT m.Music_Id, m.Title, m.Length, a.Album_Name, ar.Artist_Name
                FROM Music m
                JOIN Album a ON m.albumId = a.Album_Id
                JOIN Artist ar ON a.artistId = ar.Artist_Id
                WHERE m.Title LIKE ?
                """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + musicTitle + "%");
            ResultSet rs = pstmt.executeQuery();

            // 검색 결과 출력
            System.out.println("\n--- Search Results ---");
            System.out.println("ID | Title | Length (seconds) | Album | Artist");
            Map<Integer, String> searchResults = new HashMap<>();
            while (rs.next()) {
                int id = rs.getInt("Music_Id");
                String title = rs.getString("Title");
                searchResults.put(id, title); // 결과 저장
                System.out.println(
                        id + " | " +
                                title + " | " +
                                rs.getInt("Length") + " | " +
                                rs.getString("Album_Name") + " | " +
                                rs.getString("Artist_Name")
                );
            }

            // 검색 결과가 없을 경우
            if (searchResults.isEmpty()) {
                System.out.println("No results found for your search.");
                return;
            }

            // 재생할 음악 입력
            System.out.print("What do you want to listen? Enter the exact title: ");
            String chosenTitle = scanner.nextLine().trim();

            // 제목 일치 여부 확인
            boolean isFound = false;
            for (Map.Entry<Integer, String> entry : searchResults.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(chosenTitle)) {
                    playMusic(AuthUtil.currentUserId, entry.getKey()); // 음악 재생
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                System.out.println("No matching music found. Please try again.");
            }

        } catch (Exception e) {
            System.out.println("Error searching and playing music: " + e.getMessage());
        }
    }

    static void playMusic(int userId, int musicId) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Music is now playing...");

        // 시작 시간 기록
        Instant startTime = Instant.now();

        // 종료 확인 요청
        System.out.print("Press ENTER to stop music playback...");
        scanner.nextLine(); // 사용자 입력 대기

        // 종료 시간 기록
        Instant endTime = Instant.now();

        // 재생 시간 계산
        Duration playbackDuration = Duration.between(startTime, endTime);
        int listeningTime = (int) playbackDuration.getSeconds(); // 초 단위로 변환

        System.out.println("Music stopped. Playback duration: " + listeningTime + " seconds.");

        // 청취 기록 업데이트
        updateListenHistory(userId, musicId, listeningTime);
        System.out.println("Music played and listen history updated.");
    }

    private void searchMusic() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Music Title: ");
            String musicTitle = scanner.nextLine();

            // Music, Album, Artist 테이블을 JOIN
            String query =
                    "SELECT m.Title, m.Length, a.Artist_Name " +
                            "FROM Music m " +
                            "JOIN Album al ON m.albumId = al.Album_Id " +
                            "JOIN Artist a ON al.artistId = a.Artist_Id " +
                            "WHERE m.Title LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + musicTitle + "%");
            ResultSet rs = pstmt.executeQuery();

            // 결과 출력
            System.out.println("\n--- Music Results ---");
            System.out.println("Title | Length (seconds) | Artist");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Title") + " | " +
                                rs.getInt("Length") + " | " +
                                rs.getString("Artist_Name")
                );
            }

        } catch (Exception e) {
            System.out.println("Error searching music: " + e.getMessage());
        }
    }

    private void searchArtist() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Artist Name: ");
            String artistName = scanner.nextLine();

            String query = "SELECT * FROM Artist WHERE Artist_Name LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + artistName + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Artist Results ---");
            System.out.println("Name | Debut Date | Agency");
            while (rs.next()) {
                System.out.println(rs.getString("Artist_Name") + " | " + rs.getString("Debut_Date") + " | " + rs.getString("Agency"));
            }
        } catch (Exception e) {
            System.out.println("Error searching artist: " + e.getMessage());
        }
    }

    private void searchAlbum() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Album Name: ");
            String albumName = scanner.nextLine();

            // Album과 Artist 테이블을 JOIN
            String query =
                    "SELECT al.Album_Name, al.Total_Tracks, al.Release_Date, ar.Artist_Name " +
                            "FROM Album al " +
                            "JOIN Artist ar ON al.artistId = ar.Artist_Id " +
                            "WHERE al.Album_Name LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + albumName + "%");
            ResultSet rs = pstmt.executeQuery();

            // 결과 출력
            System.out.println("\n--- Album Results ---");
            System.out.println("Name | Total Tracks | Release Date | Artist");
            while (rs.next()) {
                System.out.println(
                        rs.getString("Album_Name") + " | " +
                                rs.getInt("Total_Tracks") + " | " +
                                rs.getString("Release_Date") + " | " +
                                rs.getString("Artist_Name")
                );
            }

        } catch (Exception e) {
            System.out.println("Error searching album: " + e.getMessage());
        }
    }


    private void searchGenre() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Genre Name: ");
            String genreName = scanner.nextLine();

            String query = "SELECT * FROM Genre WHERE Genre_Name LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + genreName + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Genre Results ---");
            System.out.println("Name");
            while (rs.next()) {
                System.out.println(rs.getString("Genre_Name"));
            }
        } catch (Exception e) {
            System.out.println("Error searching genre: " + e.getMessage());
        }
    }

    private void showSongsByGenre() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Genre Name: ");
            String genreName = scanner.nextLine();

            // 장르 확인
            String genreQuery = "SELECT Genre_Id FROM Genre WHERE Genre_Name = ?";
            PreparedStatement genreStmt = DatabaseUtil.getConnection().prepareStatement(genreQuery);
            genreStmt.setString(1, genreName);
            ResultSet genreRs = genreStmt.executeQuery();

            if (genreRs.next()) {
                int genreId = genreRs.getInt("Genre_Id");

                // 해당 장르의 노래 가져오기
                String musicQuery = "SELECT Title, Length FROM Music WHERE genreId = ?";
                PreparedStatement musicStmt = DatabaseUtil.getConnection().prepareStatement(musicQuery);
                musicStmt.setInt(1, genreId);
                ResultSet musicRs = musicStmt.executeQuery();

                System.out.println("\n--- Songs in Genre: " + genreName + " ---");
                System.out.println("Title | Length (seconds)");
                while (musicRs.next()) {
                    System.out.println(musicRs.getString("Title") + " | " + musicRs.getInt("Length"));
                }
            } else {
                System.out.println("Genre not found.");
            }
        } catch (Exception e) {
            System.out.println("Error while fetching songs by genre: " + e.getMessage());
        }
    }
}

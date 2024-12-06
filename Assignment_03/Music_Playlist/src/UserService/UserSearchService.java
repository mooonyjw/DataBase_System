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
            System.out.println("5. Go back to Explore Music");
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

            // 검색어가 비어 있는 경우 처리
            if (musicTitle.isEmpty()) {
                System.out.println("Search term cannot be empty. Please enter a valid keyword.");
                return;
            }

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
            System.out.printf("%-5s | %-30s | %-15s | %-20s | %-20s%n", "No.", "Title", "Length (seconds)", "Album", "Artist");
            System.out.println("---------------------------------------------------------------------------------------------");

            Map<Integer, Integer> searchResults = new HashMap<>();
            int index = 1; // 결과 목록 번호
            while (rs.next()) {
                int id = rs.getInt("Music_Id");
                searchResults.put(index, id); // 사용자 선택 번호와 ID 매핑
                System.out.printf(
                    "%-5d | %-30s | %-16d | %-20s | %-20s%n",
                    index,
                    rs.getString("Title"),
                    rs.getInt("Length"),
                    rs.getString("Album_Name"),
                    rs.getString("Artist_Name")
                );
                index++;
            }

            // 검색 결과가 없을 경우
            if (searchResults.isEmpty()) {
                System.out.println("No results found for your search.");
                return;
            }

            // 사용자로부터 선택 입력 받기
            System.out.print("\nEnter the number of the song you want to listen (or 0 to quit): ");
            int userChoice = scanner.nextInt();
            scanner.nextLine();

            if (searchResults.containsKey(userChoice)) {
                int selectedMusicId = searchResults.get(userChoice);
                playMusic(AuthUtil.currentUserId, selectedMusicId); // 음악 재생
            } else {
                System.out.println("Quit detected. Exiting search and play music...");
            }

        } catch (Exception e) {
            System.out.println("Error searching and playing music: " + e.getMessage());
        }
    }


    static void playMusic(int userId, int musicId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 노래 길이 가져오기
            String query = "SELECT Length FROM Music WHERE Music_Id = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, musicId);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Error: Music not found.");
                return;
            }

            int maxLength = rs.getInt("Length"); // 노래의 최대 길이

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

            // 재생 시간 제한 적용
            if (listeningTime > maxLength) {
                listeningTime = maxLength;
            }

            System.out.println("Music stopped. Playback duration: " + listeningTime + " seconds.");

            // 청취 기록 업데이트
            updateListenHistory(userId, musicId, listeningTime);
            System.out.println("Music played and listen history updated.");

        } catch (Exception e) {
            System.out.println("Error while playing music: " + e.getMessage());
        }
    }


    private void searchMusic() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Music Title (or press ENTER to cancel): ");
            String musicTitle = scanner.nextLine().trim();

            // 검색어가 비어있으면 취소
            if (musicTitle.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            // Music, Album, Artist 테이블을 JOIN
            String query = """
                SELECT m.Music_Id, m.Title, m.Length, a.Artist_Name
                FROM Music m
                JOIN Album al ON m.albumId = al.Album_Id
                JOIN Artist a ON al.artistId = a.Artist_Id
                WHERE m.Title LIKE ?
                """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + musicTitle + "%");
            ResultSet rs = pstmt.executeQuery();

            // 결과 출력
            System.out.println("\n--- Music Results ---");
            System.out.printf("%-5s | %-30s | %-16s | %-20s%n", "No.", "Title", "Length (seconds)", "Artist");
            System.out.println("---------------------------------------------------------------------------");

            Map<Integer, Integer> searchResults = new HashMap<>();
            int index = 1; // 검색 결과의 인덱스 번호

            while (rs.next()) {
                searchResults.put(index, rs.getInt("Music_Id")); // 인덱스와 Music_Id를 매핑
                System.out.printf(
                    "%-5d | %-30s | %-15d | %-20s%n",
                    index,
                    rs.getString("Title"),
                    rs.getInt("Length"),
                    rs.getString("Artist_Name")
                );
                index++;
            }

            // 검색 결과가 없을 경우 처리
            if (searchResults.isEmpty()) {
                System.out.println("No results found for your search.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error searching music: " + e.getMessage());
        }
    }

    private void searchArtist() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Artist Name (or press ENTER to cancel): ");
            String artistName = scanner.nextLine().trim();

            // 검색어가 비어있으면 취소
            if (artistName.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            String query = "SELECT * FROM Artist WHERE Artist_Name LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + artistName + "%");
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Artist Results ---");
            System.out.printf("%-5s | %-30s | %-15s | %-20s%n", "No.", "Name", "Debut Date", "Agency");
            System.out.println("--------------------------------------------------------------------");

            int index = 1; // 검색 결과 인덱스
            while (rs.next()) {
                System.out.printf(
                    "%-5d | %-30s | %-15s | %-20s%n",
                    index,
                    rs.getString("Artist_Name"),
                    rs.getString("Debut_Date") != null ? rs.getString("Debut_Date") : "N/A", // Null 값 처리
                    rs.getString("Agency") != null ? rs.getString("Agency") : "N/A"          // Null 값 처리
                );
                index++;
            }

            // 검색 결과가 없을 경우 처리
            if (index == 1) {
                System.out.println("No results found for your search.");
            }

        } catch (Exception e) {
            System.out.println("Error searching artist: " + e.getMessage());
        }
    }


    private void searchAlbum() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Album Name (or press ENTER to cancel): ");
            String albumName = scanner.nextLine().trim();

            // 검색어가 비어있으면 취소
            if (albumName.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            // Album과 Artist 테이블을 JOIN
            String query = """
                SELECT al.Album_Name, al.Total_Tracks, al.Release_Date, ar.Artist_Name
                FROM Album al
                JOIN Artist ar ON al.artistId = ar.Artist_Id
                WHERE al.Album_Name LIKE ?
                """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + albumName + "%");
            ResultSet rs = pstmt.executeQuery();

            // 결과 출력
            System.out.println("\n--- Album Results ---");
            System.out.printf("%-30s | %-15s | %-15s | %-20s%n", "Album Name", "Total Tracks", "Release Date", "Artist");
            System.out.println("--------------------------------------------------------------------------------------");

            int index = 1; // 인덱스 번호
            while (rs.next()) {
                System.out.printf(
                        "%-30s | %-15d | %-15s | %-20s%n",
                        rs.getString("Album_Name"),
                        rs.getInt("Total_Tracks"),
                        rs.getString("Release_Date"),
                        rs.getString("Artist_Name")
                );
                index++;
            }

            // 검색 결과가 없을 경우 처리
            if (index == 1) {
                System.out.println("No results found for your search.");
            }

        } catch (Exception e) {
            System.out.println("Error searching album: " + e.getMessage());
        }
    }


    private void searchGenre() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Genre Name (or press ENTER to cancel): ");
            String genreName = scanner.nextLine().trim();

            // 검색어가 비어있으면 취소
            if (genreName.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            String query = """
                SELECT Genre_Name
                FROM Genre
                WHERE Genre_Name LIKE ?
                """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, "%" + genreName + "%");
            ResultSet rs = pstmt.executeQuery();

            // 결과 출력
            System.out.println("\n--- Genre Results ---");
            System.out.printf("%-5s | %-30s%n", "No.", "Genre Name");
            System.out.println("----------------------------------");

            int index = 1; // 인덱스 번호
            while (rs.next()) {
                System.out.printf("%-5d | %-30s%n", index, rs.getString("Genre_Name"));
                index++;
            }

            // 검색 결과가 없을 경우 처리
            if (index == 1) {
                System.out.println("No results found for your search.");
            }

        } catch (Exception e) {
            System.out.println("Error searching genre: " + e.getMessage());
        }
    }

    private void showSongsByGenre() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Enter Genre Name (or press ENTER to cancel): ");
            String genreName = scanner.nextLine().trim();

            // 검색어가 비어있으면 취소
            if (genreName.isEmpty()) {
                System.out.println("Search cancelled.");
                return;
            }

            // 장르 확인
            String genreQuery = "SELECT Genre_Id FROM Genre WHERE Genre_Name = ?";
            PreparedStatement genreStmt = DatabaseUtil.getConnection().prepareStatement(genreQuery);
            genreStmt.setString(1, genreName);
            ResultSet genreRs = genreStmt.executeQuery();

            if (genreRs.next()) {
                int genreId = genreRs.getInt("Genre_Id");

                // 해당 장르의 노래 가져오기
                String musicQuery = """
                    SELECT Title, Length
                    FROM Music
                    WHERE genreId = ?
                    """;
                PreparedStatement musicStmt = DatabaseUtil.getConnection().prepareStatement(musicQuery);
                musicStmt.setInt(1, genreId);
                ResultSet musicRs = musicStmt.executeQuery();

                // 결과 출력
                System.out.println("\n--- Songs in Genre: " + genreName + " ---");
                System.out.printf("%-5s | %-30s | %-15s%n", "No.", "Title", "Length (seconds)");
                System.out.println("---------------------------------------------------------");

                int index = 1; // 검색 결과의 인덱스 번호
                while (musicRs.next()) {
                    System.out.printf(
                            "%-5d | %-30s | %-15d%n",
                            index,
                            musicRs.getString("Title"),
                            musicRs.getInt("Length")
                    );
                    index++;
                }

                // 결과가 없을 경우 처리
                if (index == 1) {
                    System.out.println("No songs found in this genre.");
                }

            } else {
                System.out.println("Genre not found.");
            }

        } catch (Exception e) {
            System.out.println("Error while fetching songs by genre: " + e.getMessage());
        }
    }

    public void viewTopListenedMusic() {  // 실시간 인기 차트 (재생 시간 순)
        try {
            String query = """
            SELECT m.Title, SUM(l.Total_Listening_Time) AS TotalTime
            FROM Music m
            JOIN Listens l ON m.Music_Id = l.musicId
            GROUP BY m.Music_Id, m.Title
            ORDER BY TotalTime DESC
            LIMIT 10
        """;
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Top Listened Songs ---");
            System.out.printf("%-5s | %-30s | %-25s%n", "No.", "Title", "Total Listening Time (seconds)");
            System.out.println("-------------------------------------------------------------------");

            int index = 1; // 순위 번호
            while (rs.next()) {
                System.out.printf(
                        "%-5d | %-30s | %-25d%n",
                        index,
                        rs.getString("Title"),
                        rs.getInt("TotalTime")
                );
                index++;
            }

            // 결과가 없을 경우 처리
            if (index == 1) {
                System.out.println("No data available for top listened songs.");
            }

        } catch (Exception e) {
            System.out.println("Error fetching top listened music: " + e.getMessage());
        }
    }

//
//    public void viewTopListenedMusic(int userId) {
//        try {
//            String query = """
//            SELECT
//                Music.Title AS Music_Title,
//                Listens.Total_Listening_Time AS Listening_Time,
//                Listens.m_timestamp AS Last_Listen_Time
//            FROM
//                Listens
//            JOIN
//                Music ON Listens.musicId = Music.Music_Id
//            WHERE
//                Listens.userId = ?
//            ORDER BY
//                Listens.Total_Listening_Time DESC,
//                Listens.m_timestamp DESC;
//        """;
//
//            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
//            pstmt.setInt(1, userId);
//
//            ResultSet rs = pstmt.executeQuery();
//
//            System.out.println("\n--- Top Listened Music ---");
//            System.out.println("Music Title | Total Listening Time (seconds) | Last Listen Time");
//            while (rs.next()) {
//                String title = rs.getString("Music_Title");
//                int listeningTime = rs.getInt("Listening_Time");
//                String lastListenTime = rs.getString("Last_Listen_Time");
//                System.out.println(title + " | " + listeningTime + " | " + lastListenTime);
//            }
//        } catch (Exception e) {
//            System.out.println("Error fetching top listened music: " + e.getMessage());
//        }
//    }


}

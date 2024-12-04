package UserService;

import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class PlaylistService {
    public void createPlaylist(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 플레이리스트 이름 입력받기
            System.out.print("Enter Playlist Name: ");
            String playlistName = scanner.nextLine().trim();
            if (playlistName.isEmpty()) {
                System.out.println("Playlist name cannot be empty. Please try again.");
                return;
            }

            // 2. 플레이리스트 생성
            String insertPlaylistQuery = "INSERT INTO Playlist (Playlist_Name, userId) VALUES (?, ?)";
            PreparedStatement playlistStmt = DatabaseUtil.getConnection().prepareStatement(insertPlaylistQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            playlistStmt.setString(1, playlistName);
            playlistStmt.setInt(2, userId);

            int rows = playlistStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Playlist created successfully!");
            } else {
                System.out.println("Failed to create playlist.");
                return;
            }

            // 3. 생성된 Playlist_Id 가져오기
            ResultSet generatedKeys = playlistStmt.getGeneratedKeys();
            int playlistId = -1;
            if (generatedKeys.next()) {
                playlistId = generatedKeys.getInt(1);
            }

            // 4. 곡 추가 반복
            boolean addingSongs = true;
            while (addingSongs) {
                System.out.println("\n--- Search and Add Songs ---");
                String songToAdd = searchAndAddMusic(); // 사용자가 검색하고 추가할 곡 ID 반환
                if (songToAdd == null) {
                    System.out.println("No song selected. Exiting...");
                    break;
                }

                if (isduplicatedsong(Integer.parseInt(songToAdd), playlistId)) {
                    System.out.println("This song is already in the playlist. Please select a different song.");
                    continue; // 다음 곡 추가로 넘어감
                }

                // 5. 곡을 플레이리스트에 추가
                String addSongQuery = "INSERT INTO Contains (contains_m_id, playlistId) VALUES (?, ?)";
                PreparedStatement addSongStmt = DatabaseUtil.getConnection().prepareStatement(addSongQuery);
                addSongStmt.setInt(1, Integer.parseInt(songToAdd));
                addSongStmt.setInt(2, playlistId);

                int songRows = addSongStmt.executeUpdate();
                if (songRows > 0) {
                    System.out.println("Song added to the playlist successfully!");
                } else {
                    System.out.println("Failed to add song to the playlist.");
                }

                // 6. 계속 추가 여부 확인
                System.out.print("Do you want to add more songs? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if (!choice.equals("y")) {
                    addingSongs = false;
                }
            }

            System.out.println("Playlist setup complete!");

        } catch (Exception e) {
            System.out.println("Error while creating playlist: " + e.getMessage());
        }
    }
    private boolean isduplicatedsong(int songId, int playlistId) {
        try {
            String checkQuery = "SELECT * FROM Contains WHERE contains_m_id = ? AND playlistId = ?";
            PreparedStatement checkStmt = DatabaseUtil.getConnection().prepareStatement(checkQuery);
            checkStmt.setInt(1, songId);
            checkStmt.setInt(2, playlistId);

            ResultSet rs = checkStmt.executeQuery();
            return rs.next(); // 이미 존재하면 true 반환
        } catch (Exception e) {
            System.out.println("Error while checking song existence: " + e.getMessage());
            return false;
        }
    }

    private String searchAndAddMusic() {
        Scanner scanner = new Scanner(System.in);

        // 음악 검색
        System.out.print("Enter a song title to search: ");
        String searchKeyword = scanner.nextLine().trim();

        try {
            String searchQuery = "SELECT Music_Id, Title FROM Music WHERE Title LIKE ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(searchQuery);
            pstmt.setString(1, "%" + searchKeyword + "%");
            ResultSet rs = pstmt.executeQuery();

            // 검색 결과 출력
            System.out.println("\n--- Search Results ---");
            System.out.println("ID | Title");
            while (rs.next()) {
                System.out.println(rs.getInt("Music_Id") + " | " + rs.getString("Title"));
            }

            // 곡 선택
            System.out.print("Enter the ID of the song you want to add (or press ENTER to cancel): ");
            String songId = scanner.nextLine().trim();
            if (songId.isEmpty()) {
                return null; // 취소 처리
            }

            return songId; // 선택한 곡 ID 반환

        } catch (Exception e) {
            System.out.println("Error while searching for music: " + e.getMessage());
        }

        return null;
    }

    public void viewPlaylists(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 해당 사용자의 플레이리스트 조회
            String query = "SELECT Playlist_Id, Playlist_Name FROM Playlist WHERE userId = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Your Playlists ---");
            if (!rs.isBeforeFirst()) { // 사용자가 플레이리스트가 없는 경우
                System.out.println("You have no playlists. Create one first!");
                return;
            }

            // 플레이리스트 출력
            System.out.println("ID | Name");
            while (rs.next()) {
                System.out.println(rs.getInt("Playlist_Id") + " | " + rs.getString("Playlist_Name"));
            }

            // 2. 특정 플레이리스트의 곡 보기
            System.out.print("\nEnter the ID of the playlist you want to view (or press ENTER to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Returning to main menu.");
                return;
            }

            int playlistId = Integer.parseInt(input);

            // 플레이리스트에 포함된 곡 조회
            query = """
                SELECT m.Music_Id, m.Title, m.Length, a.Album_Name, ar.Artist_Name
                FROM Contains c
                JOIN Music m ON c.contains_m_id = m.Music_Id
                JOIN Album a ON m.albumId = a.Album_Id
                JOIN Artist ar ON a.artistId = ar.Artist_Id
                WHERE c.playlistId = ?""";
            pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, playlistId);
            rs = pstmt.executeQuery();

            System.out.println("\n--- Playlist Songs ---");
            System.out.println("ID | Title | Length (seconds) | Album | Artist");
            if (!rs.isBeforeFirst()) { // 플레이리스트에 곡이 없는 경우
                System.out.println("This playlist has no songs.");
            } else {
                while (rs.next()) {
                    System.out.printf("%d | %s | %d | %s | %s%n",
                            rs.getInt("Music_Id"),
                            rs.getString("Title"),
                            rs.getInt("Length"),
                            rs.getString("Album_Name"),
                            rs.getString("Artist_Name"));
                }
            }

        } catch (Exception e) {
            System.out.println("Error while viewing playlists: " + e.getMessage());
        }
    }

}

package UserService;

import Auth.AuthUtil;
import Security.DatabaseUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PlaylistService {
    public void createPlaylist(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. Enter playlist name
            System.out.print("Enter Playlist Name: ");
            String playlistName = scanner.nextLine().trim();
            if (playlistName.isEmpty()) {
                System.out.println("Playlist name cannot be empty. Please try again.");
                return;
            }

            // 2. Insert the playlist into the database
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

            // 3. Retrieve the generated Playlist_Id
            ResultSet generatedKeys = playlistStmt.getGeneratedKeys();
            int playlistId = -1;
            if (generatedKeys.next()) {
                playlistId = generatedKeys.getInt(1);
            } else {
                System.out.println("Failed to retrieve playlist ID.");
                return;
            }

            // 4. Add songs to the playlist
            addSongsToPlaylist(playlistId);

        } catch (Exception e) {
            System.out.println("Error while creating playlist: " + e.getMessage());
        }
    }


    private boolean isDuplicatedSong(int songId, int playlistId) {
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

        while (true) {
            // 음악 검색
            System.out.print("Enter a song title to search (or press ENTER to cancel): ");
            String searchKeyword = scanner.nextLine().trim();

            if (searchKeyword.isEmpty()) {
                System.out.println("Search cancelled.");
                return null; // 취소 처리
            }

            try {
                String searchQuery = "SELECT Music_Id, Title FROM Music WHERE Title LIKE ?";
                PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(searchQuery);
                pstmt.setString(1, "%" + searchKeyword + "%");
                ResultSet rs = pstmt.executeQuery();

                // 검색 결과 출력
                System.out.println("\n--- Search Results ---");
                System.out.printf("%-5s | %-30s%n", "No.", "Title");
                System.out.println("-------------------------------------------");

                Map<Integer, Integer> searchResults = new HashMap<>();
                int index = 1; // 순서 번호

                while (rs.next()) {
                    int musicId = rs.getInt("Music_Id");
                    searchResults.put(index, musicId); // 인덱스와 Music_Id 매핑
                    System.out.printf("%-5d | %-30s%n", index, rs.getString("Title"));
                    index++;
                }

                // 검색 결과가 없을 경우 처리
                if (searchResults.isEmpty()) {
                    System.out.println("No results found for your search. Please try again.");
                    continue; // 루프 반복으로 다시 검색
                }

                // 곡 선택
                System.out.print("Enter the number of the song you want to add (or press ENTER to cancel): ");
                String userInput = scanner.nextLine().trim();

                if (userInput.isEmpty()) {
                    System.out.println("Selection cancelled.");
                    return null; // 선택 취소 처리
                }

                try {
                    int userChoice = Integer.parseInt(userInput);
                    if (searchResults.containsKey(userChoice)) {
                        return String.valueOf(searchResults.get(userChoice)); // 선택한 Music_Id 반환
                    } else {
                        System.out.println("Invalid selection. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                }

            } catch (Exception e) {
                System.out.println("Error while searching for music: " + e.getMessage());
                return null;
            }
        }
    }


    public void viewPlaylists(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 해당 사용자의 플레이리스트 조회
            String query = "SELECT Playlist_Id, Playlist_Name FROM Playlist WHERE userId = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- My Playlists ---");
            if (!rs.isBeforeFirst()) { // 사용자가 플레이리스트가 없는 경우
                System.out.println("You have no playlists. Create one first!");
                return;
            }

            // 플레이리스트 출력
            System.out.printf("%-5s | %-30s%n", "No.", "Name");
            System.out.println("-----------------------------------");

            Map<Integer, Integer> playlistMap = new HashMap<>();
            int index = 1;
            while (rs.next()) {
                playlistMap.put(index, rs.getInt("Playlist_Id"));
                System.out.printf("%-5d | %-30s%n", index, rs.getString("Playlist_Name"));
                index++;
            }

            // 2. 특정 플레이리스트의 곡 보기
            System.out.print("\nEnter the number of the playlist you want to view (or press ENTER to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Returning to main menu.");
                return;
            }

            int userChoice;
            try {
                userChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Returning to main menu.");
                return;
            }

            if (!playlistMap.containsKey(userChoice)) {
                System.out.println("Invalid selection. Returning to main menu.");
                return;
            }

            int playlistId = playlistMap.get(userChoice);

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
            System.out.printf("%-5s | %-30s | %-17s | %-20s | %-20s%n", "No.", "Title", "Length (seconds)", "Album", "Artist");
            System.out.println("--------------------------------------------------------------------------------------------");

            if (!rs.isBeforeFirst()) { // 플레이리스트에 곡이 없는 경우
                System.out.println("This playlist has no songs.");
            } else {
                index = 1;
                while (rs.next()) {
                    System.out.printf("%-5d | %-30s | %-15d | %-20s | %-20s%n",
                            index,
                            rs.getString("Title"),
                            rs.getInt("Length"),
                            rs.getString("Album_Name"),
                            rs.getString("Artist_Name"));
                    index++;
                }
            }

        } catch (Exception e) {
            System.out.println("Error while viewing playlists: " + e.getMessage());
        }
    }

    public void editPlaylist(int userId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 사용자의 플레이리스트 조회
            String query = "SELECT Playlist_Id, Playlist_Name FROM Playlist WHERE userId = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- My Playlists ---");
            Map<Integer, Integer> playlistMap = new HashMap<>();
            int index = 1;

            if (!rs.isBeforeFirst()) { // 플레이리스트가 없는 경우
                System.out.println("You have no playlists to edit. Create one first!");
                return;
            }

            System.out.printf("%-5s | %-30s%n", "No.", "Playlist Name");
            System.out.println("--------------------------------------");

            while (rs.next()) {
                playlistMap.put(index, rs.getInt("Playlist_Id"));
                System.out.printf("%-5d | %-30s%n", index, rs.getString("Playlist_Name"));
                index++;
            }

            // 2. 사용자로부터 선택 입력 받기
            System.out.print("\nEnter the number of the playlist you want to edit (or press ENTER to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Returning to main menu.");
                return;
            }

            int userChoice;
            try {
                userChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Returning to main menu.");
                return;
            }

            if (!playlistMap.containsKey(userChoice)) {
                System.out.println("Invalid selection. Returning to main menu.");
                return;
            }

            int playlistId = playlistMap.get(userChoice);

            // 3. 편집 메뉴
            while (true) {
                System.out.println("\n--- Edit Playlist Menu ---");
                System.out.println("1. Add a Song");
                System.out.println("2. Remove a Song");
                System.out.println("3. Rename Playlist");
                System.out.println("4. Delete Playlist");
                System.out.println("5. Back to Playlist Menu");
                System.out.print("Enter your choice: ");

                int editChoice = scanner.nextInt();
                scanner.nextLine();

                switch (editChoice) {
                    case 1:
                        addSongsToPlaylist(playlistId);
                        break;
                    case 2:
                        removeSongFromPlaylist(playlistId);
                        break;
                    case 3:
                        renamePlaylist(playlistId);
                        break;
                    case 4:
                        removePlaylist(playlistId);
                        break;
                    case 5:
                        System.out.println("Returning to Playlist Menu.");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error editing playlist: " + e.getMessage());
        }
    }


    private void addSongsToPlaylist(int playlistId) {
        Scanner scanner = new Scanner(System.in);

        try {
            boolean addingSongs = true;
            while (addingSongs) {
                System.out.println("\n--- Search and Add Songs ---");
                String songToAdd = searchAndAddMusic(); // Assume this method returns the song ID or null if the user cancels
                if (songToAdd == null) {
                    System.out.println("No song selected. Exiting...");
                    break;
                }

                int musicId = Integer.parseInt(songToAdd);

                // Check for duplicate songs
                if (isDuplicatedSong(musicId, playlistId)) {
                    System.out.println("This song is already in the playlist. Please select a different song.");
                    continue;
                }

                // Add the song to the playlist
                String addSongQuery = "INSERT INTO Contains (contains_m_id, playlistId) VALUES (?, ?)";
                PreparedStatement addSongStmt = DatabaseUtil.getConnection().prepareStatement(addSongQuery);
                addSongStmt.setInt(1, musicId);
                addSongStmt.setInt(2, playlistId);

                int songRows = addSongStmt.executeUpdate();
                if (songRows > 0) {
                    System.out.println("Song added to the playlist successfully!");
                } else {
                    System.out.println("Failed to add song to the playlist.");
                }

                // Ask if the user wants to continue adding songs
                System.out.print("Do you want to add more songs? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();
                if (!choice.equals("y")) {
                    addingSongs = false;
                }
            }

            System.out.println("Playlist setup complete!");

        } catch (Exception e) {
            System.out.println("Error while adding songs to playlist: " + e.getMessage());
        }
    }

    private void removeSongFromPlaylist(int playlistId) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 1. 플레이리스트에 있는 곡 목록 조회
            String query = """
            SELECT m.Music_Id, m.Title, m.Length, a.Album_Name, ar.Artist_Name
            FROM Contains c
            JOIN Music m ON c.contains_m_id = m.Music_Id
            JOIN Album a ON m.albumId = a.Album_Id
            JOIN Artist ar ON a.artistId = ar.Artist_Id
            WHERE c.playlistId = ?""";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setInt(1, playlistId);
            ResultSet rs = pstmt.executeQuery();

            // Playlist 이름 가져오기
            String nameQuery = "SELECT Playlist_Name FROM Playlist WHERE Playlist_Id = ?";
            PreparedStatement nameStmt = DatabaseUtil.getConnection().prepareStatement(nameQuery);
            nameStmt.setInt(1, playlistId);
            ResultSet nameRs = nameStmt.executeQuery();

            String playlistName = "";
            if (nameRs.next()) {
                playlistName = nameRs.getString("Playlist_Name");
            } else {
                System.out.println("Playlist not found.");
                return;
            }

            // 결과 출력
            System.out.println("\n--- Songs in Playlist: " + playlistName + " ---");
            System.out.printf("%-5s | %-30s | %-15s | %-20s | %-20s%n", "No.", "Title", "Length (seconds)", "Album", "Artist");
            System.out.println("---------------------------------------------------------------------------------------------");

            Map<Integer, Integer> songMap = new HashMap<>();
            int index = 1;

            while (rs.next()) {
                songMap.put(index, rs.getInt("Music_Id")); // 인덱스와 곡 ID 매핑
                System.out.printf(
                        "%-5d | %-30s | %-15d | %-20s | %-20s%n",
                        index,
                        rs.getString("Title"),
                        rs.getInt("Length"),
                        rs.getString("Album_Name"),
                        rs.getString("Artist_Name")
                );
                index++;
            }

            if (songMap.isEmpty()) {
                System.out.println("This playlist has no songs.");
                return;
            }

            // 2. 사용자 입력
            System.out.print("\nEnter the number of the song you want to remove (or press ENTER to cancel): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Returning to playlist menu.");
                return;
            }

            int userChoice;
            try {
                userChoice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Returning to playlist menu.");
                return;
            }

            if (!songMap.containsKey(userChoice)) {
                System.out.println("Invalid selection. Returning to playlist menu.");
                return;
            }

            int musicId = songMap.get(userChoice);

            // 3. 곡 제거
            String deleteQuery = "DELETE FROM Contains WHERE playlistId = ? AND contains_m_id = ?";
            PreparedStatement deleteStmt = DatabaseUtil.getConnection().prepareStatement(deleteQuery);
            deleteStmt.setInt(1, playlistId);
            deleteStmt.setInt(2, musicId);

            int rows = deleteStmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Song removed from the playlist successfully!");
            } else {
                System.out.println("Song not found in the playlist.");
            }

        } catch (Exception e) {
            System.out.println("Error removing song from playlist: " + e.getMessage());
        }
    }


    private void renamePlaylist(int playlistId) {
        Scanner scanner = new Scanner(System.in);

        try {
            // 1. 새 이름 입력
            System.out.print("Enter the new name for the playlist: ");
            String newName = scanner.nextLine().trim();

            if (newName.isEmpty()) {
                System.out.println("Playlist name cannot be empty.");
                return;
            }

            // 2. 이름 업데이트
            String updateQuery = "UPDATE Playlist SET Playlist_Name = ? WHERE Playlist_Id = ?";
            PreparedStatement updateStmt = DatabaseUtil.getConnection().prepareStatement(updateQuery);
            updateStmt.setString(1, newName);
            updateStmt.setInt(2, playlistId);
            int rows = updateStmt.executeUpdate();

            // 3. 결과 출력
            if (rows > 0) {
                System.out.println("Playlist renamed successfully!");
            } else {
                System.out.println("Failed to rename the playlist. Please check the Playlist ID.");
            }

        } catch (Exception e) {
            System.out.println("Error renaming playlist: " + e.getMessage());
        }
    }



    private void removePlaylist(int playlistId) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 삭제 확인
            System.out.print("Are you sure you want to delete this playlist? This action cannot be undone. (y/n): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("y")) {
                System.out.println("Delete operation cancelled.");
                return;
            }

            // 1. 관련 데이터 삭제 (Contains 테이블)
            String deleteContainsQuery = "DELETE FROM Contains WHERE playlistId = ?";
            PreparedStatement deleteContainsStmt = DatabaseUtil.getConnection().prepareStatement(deleteContainsQuery);
            deleteContainsStmt.setInt(1, playlistId);
            deleteContainsStmt.executeUpdate();

            // 2. 플레이리스트 삭제
            String deletePlaylistQuery = "DELETE FROM Playlist WHERE Playlist_Id = ?";
            PreparedStatement deletePlaylistStmt = DatabaseUtil.getConnection().prepareStatement(deletePlaylistQuery);
            deletePlaylistStmt.setInt(1, playlistId);
            int rows = deletePlaylistStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("Playlist deleted successfully!");
            } else {
                System.out.println("Failed to delete playlist. It may not exist.");
            }

        } catch (Exception e) {
            System.out.println("Error deleting playlist: " + e.getMessage());
        }
    }




}

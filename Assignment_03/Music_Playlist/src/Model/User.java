package Model;

import Auth.AuthUtil;
import Security.DatabaseUtil;
import UserService.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class User {
    private UserSearchService userSearchService = new UserSearchService();
    private ListenedService listenedService = new ListenedService();
    private PlaylistService playlistService = new PlaylistService();
    private UserUpdateService userupdateService = new UserUpdateService();
    private LikeService likeService = new LikeService();
    public void showUserMenu(String userName) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        System.out.println("\nWelcome back, " + userName + "! Let's dive into the music world!");

        do {
            System.out.println("\nUser Menu");
            System.out.println("1. Explore Music");
            System.out.println("2. Manage Playlists");
            System.out.println("3. Listening History");
            System.out.println("4. Liked Music");
            System.out.println("5. Account Settings");
            System.out.println("6. Logout");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        int subChoice;
                        do {
                            System.out.println("\nExplore Music:");
                            System.out.println("1. Search");
                            System.out.println("2. View trending on Songs");  // 실시간 인기 차트
                            System.out.println("3. Go back to main menu");
                            System.out.print("Enter your choice: ");

                            subChoice = scanner.nextInt();
                            scanner.nextLine(); // 버퍼 정리

                            switch (subChoice) {
                                case 1:
                                    userSearchService.usersearchOption();
                                    break;
                                case 2:
                                    userSearchService.viewTopListenedMusic();
                                    break;
                                case 3:
                                    System.out.println("Going back to main menu...");
                                    break; // 서브 메뉴 반복 종료
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        } while (subChoice != 3);
                        break; // 메인 메뉴로 복귀
                    case 2:
                        do {
                            System.out.println("\nManage Playlists:");
                            System.out.println("1. Create Playlist");
                            System.out.println("2. View Playlists");
                            System.out.println("3. Edit a Playlist");
                            System.out.println("4. Delete Playlist");
                            System.out.println("5. Go back to main menu");
                            System.out.print("Enter your choice: ");

                            subChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (subChoice) {
                                case 1:
                                    playlistService.createPlaylist(AuthUtil.currentUserId);
                                    break;
                                case 2:
                                    playlistService.viewPlaylists(AuthUtil.currentUserId);
                                    break;
                                case 3:
                                    playlistService.editPlaylist(AuthUtil.currentUserId);
                                    break;
                                case 4:
                                    playlistService.removePlaylist(AuthUtil.currentUserId);
                                    break;
                                case 5:
                                    System.out.println("Going back to main menu...");
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        } while (subChoice != 5);
                        break; // 메인 메뉴로 복귀
                    case 3:
                        do {
                            System.out.println("\nListening History:");
                            System.out.println("1. View Recently Listened");  // 최근 재생한
                            System.out.println("2. View Most Played Music");  // 많이 재생한
                            System.out.println("3. Go back to main menu");
                            System.out.print("Enter your choice: ");
                            subChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (subChoice) {
                                case 1:
                                    listenedService.showListenHistory(AuthUtil.currentUserId);
                                    break;
                                case 2:
                                    listenedService.viewMostPlayedSongs(AuthUtil.currentUserId);
                                    break;
                                case 3:
                                    System.out.println("Going back to main menu...");
                                    break; // 서브 메뉴 반복 종료
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        } while (subChoice != 3);
                        break; // 메인 메뉴로 복귀
                    case 4:
                        do {
                            System.out.println("\nLiked Music:");
                            System.out.println("1. View Liked Songs");  // 좋아요 누른
                            System.out.println("2. Add or Remove Songs form Liked");  // 좋아요 누르기 또는 취소
                            System.out.println("3. Go back to main menu");
                            System.out.print("Enter your choice: ");
                            subChoice = scanner.nextInt();
                            scanner.nextLine();

                            switch (subChoice) {
                                case 1:
                                    likeService.viewLikedSongs(AuthUtil.currentUserId);
                                    break;
                                case 2:
                                    likeService.manageLikedSongs(AuthUtil.currentUserId);
                                    break;
                                case 3:
                                    System.out.println("Going back to main menu...");
                                    break; // 서브 메뉴 반복 종료
                                default:
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        } while (subChoice != 3);
                        break; // 메인 메뉴로 복귀

                    case 5:
                        userupdateService.updateUser();
                        break;
                    case 6:
                        System.out.println("Logging out...");
                        return;
                    case 7:
                        System.out.println("Exiting...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 7.");
                scanner.next(); // 버퍼 정리
                choice = -1;
            }
        } while (choice != 7);
    }

    public String getUserName(String email) {
        try {
            String query = "SELECT User_Name FROM User WHERE User_Email = ?";
            PreparedStatement pstmt = DatabaseUtil.getConnection().prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("User_Name"); // 사용자 이름 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "User"; // 기본값
    }




}

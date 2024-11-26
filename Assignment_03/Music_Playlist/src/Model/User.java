package Model;

import java.util.Scanner;

public class User {
    public void showUserMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nUser Menu");
            System.out.println("1. View Music");
            System.out.println("2. Create Playlist");
            System.out.println("3. View Playlists");
            System.out.println("4. Like Music");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewMusic();
                    break;
                case 2:
                    createPlaylist();
                    break;
                case 3:
                    viewPlaylists();
                    break;
                case 4:
                    likeMusic();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    private void viewMusic() {
        System.out.println("Viewing music...");
        // Add database query logic
    }

    private void createPlaylist() {
        System.out.println("Creating playlist...");
        // Add database query logic
    }

    private void viewPlaylists() {
        System.out.println("Viewing playlists...");
        // Add database query logic
    }

    private void likeMusic() {
        System.out.println("Liking music...");
        // Add database query logic
    }
}

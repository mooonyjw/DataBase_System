package Model;

import java.util.Scanner;

public class Manager {
    public void showManagerMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nManager Menu");
            System.out.println("1. Add Music");
            System.out.println("2. Update Music");
            System.out.println("3. Delete Music");
            System.out.println("4. View Reports");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    addMusic();
                    break;
                case 2:
                    updateMusic();
                    break;
                case 3:
                    deleteMusic();
                    break;
                case 4:
                    viewReports();
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 5);
    }

    private void addMusic() {
        System.out.println("Adding new music...");
        // Add database query logic
    }

    private void updateMusic() {
        System.out.println("Updating music...");
        // Add database query logic
    }

    private void deleteMusic() {
        System.out.println("Deleting music...");
        // Add database query logic
    }

    private void viewReports() {
        System.out.println("Viewing reports...");
        // Add database query logic
    }
}

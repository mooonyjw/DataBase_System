import Auth.Login;

import java.sql.*; // Import SQL packages
import java.util.Scanner; // Import Scanner class

public class Main {
    public static void main(String[] args) {
        Login login = new Login();
        login.showMenu();
    }
}

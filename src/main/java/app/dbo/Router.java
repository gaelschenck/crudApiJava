package app.dbo;

import java.util.Scanner;

public class Router {
    private AuthService authService = new AuthService();
    private UserDAO userDAO = new UserDAO();
    private BookDAO bookDAO = new BookDAO();

    public void start() {
        Scanner scanner = new Scanner(System.in);
    }
}

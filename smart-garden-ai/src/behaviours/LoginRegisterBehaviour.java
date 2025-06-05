package behaviours;

import agents.UserAgent;
import dao.UserDAO;
import jade.core.behaviours.OneShotBehaviour;

import java.util.Scanner;

public class LoginRegisterBehaviour extends OneShotBehaviour {




    @Override
    public void action() {
        UserAgent ua = (UserAgent) myAgent;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Изберете: [1] Вход  [2] Регистрация");
        String choice = scanner.nextLine();

        System.out.print("Потребителско име: ");
        String username = scanner.nextLine();
        System.out.print("Парола: ");
        String password = scanner.nextLine();

        UserDAO userDAO = new UserDAO();

        try {
            if (choice.equals("2")) {
                // Регистрация
                if (userDAO.createUser(username, password)) {
                    System.out.println("✅ Регистрация успешна!");
                } else {
                    System.out.println("⚠ Потребител с това име вече съществува!");
                    return;
                }
            } else if (choice.equals("1")) {
                // Вход
                int userId = userDAO.getUserId(username, password);
                if (userId == -1) {
                    System.out.println("❌ Невалидни данни за вход.");
                    return;
                }
                ua.currentUserId = userId;
                ua.currentUsername = username;
            } else {
                System.out.println("❌ Невалиден избор.");
                return;
            }

            // Проверки и продължение
            System.out.println("Проверка: username = " + ua.currentUsername);
            System.out.println("Проверка: userId = " + ua.currentUserId);

            if (ua.currentUserId != -1) {
                System.out.println("👤 Влязъл си като " + ua.currentUsername + " (ID: " + ua.currentUserId + ")");
                myAgent.addBehaviour(new PlantInteractionBehaviour());
            } else {
                System.out.println("❌ Невалидни данни. Край.");
                myAgent.doDelete();
            }
        } catch (Exception e) {
            System.out.println("Грешка при базата: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

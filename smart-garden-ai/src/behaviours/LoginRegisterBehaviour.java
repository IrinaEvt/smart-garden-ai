package behaviours;

import agents.UserAgent;
import dao.UserDAO;
import jade.core.behaviours.OneShotBehaviour;

import javax.swing.*;
import java.util.Scanner;

public class LoginRegisterBehaviour extends OneShotBehaviour {




    @Override
    public void action() {
        UserAgent ua = (UserAgent) myAgent;

        String username = ua.loginUsername;
        String password = ua.loginPassword;
        String choice = ua.loginChoice;

        UserDAO userDAO = new UserDAO();

        try {
            if (choice.equals("2")) {
                if (userDAO.createUser(username, password)) {
                    System.out.println("✅ Регистрация успешна!");
                } else {
                    System.out.println("⚠ Потребител с това име вече съществува!");
                    return;
                }
            }

            int userId = userDAO.getUserId(username, password);
            ua.currentUserId = userId;
            ua.currentUsername = username;

            if (userId != -1) {
                System.out.println("👤 Влязъл си като " + username + " (ID: " + userId + ")");

                if (ua.loginGUI != null) {
                    SwingUtilities.invokeLater(() -> ua.loginGUI.dispose());
                }

                SwingUtilities.invokeLater(() -> new gui.PlantClientGUI(ua)); // старт на Plant GUI
            } else {
                System.out.println("❌ Невалидни данни.");
                myAgent.doDelete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package behaviours;

import agents.UserAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.UserDAO;
import jade.core.behaviours.OneShotBehaviour;
import models.Plant;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
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

                ua.requestPlantsForUser(plantsJson -> {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        System.out.println("JSON съдържание:");
                        System.out.println(plantsJson);
                        Plant[] plantArray = mapper.readValue(plantsJson, Plant[].class);
                        List<Plant> plants = Arrays.asList(plantArray);
                        SwingUtilities.invokeLater(() -> {
                            gui.PlantListGUI gui = new gui.PlantListGUI(ua, plants);
                            ua.setPlantListGUI(gui);  // ⬅️ това ти трябва!
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });// старт на Plant GUI
            } else {
                System.out.println("❌ Невалидни данни.");
                myAgent.doDelete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
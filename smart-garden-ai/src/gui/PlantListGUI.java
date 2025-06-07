package gui;

import agents.UserAgent;
import models.Plant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PlantListGUI extends JFrame {
    private UserAgent agent;
    private JComboBox<String> plantComboBox;
    private JTextArea plantDetailsArea;
    private JButton addSymptomButton, createPlantButton;

    private List<Plant> userPlants;

    public PlantListGUI(UserAgent agent, List<Plant> plants) {
        this.agent = agent;
        this.userPlants = plants;
        init();
    }

    private void init() {
        setTitle("Растения на " + agent.currentUsername);
        setSize(600, 400);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JLabel selectLabel = new JLabel("Избери растение:");
        selectLabel.setBounds(20, 20, 150, 25);
        add(selectLabel);

        plantComboBox = new JComboBox<>();
        for (Plant plant : userPlants) {
            plantComboBox.addItem(plant.getName());
        }
        plantComboBox.setBounds(150, 20, 200, 25);
        add(plantComboBox);

        JButton showDetailsBtn = new JButton("Покажи детайли");
        showDetailsBtn.setBounds(370, 20, 180, 25);
        showDetailsBtn.addActionListener(e -> showPlantDetails());
        add(showDetailsBtn);

        plantDetailsArea = new JTextArea();
        plantDetailsArea.setBounds(20, 60, 540, 180);
        plantDetailsArea.setEditable(false);
        add(plantDetailsArea);

        addSymptomButton = new JButton("➕ Добави симптом");
        addSymptomButton.setBounds(20, 260, 200, 30);
        addSymptomButton.addActionListener(onAddSymptom);

        createPlantButton = new JButton("🌱 Създай растение");
        createPlantButton.setBounds(240, 260, 200, 30);
        createPlantButton.addActionListener(onCreatePlant);

        add(addSymptomButton);
        add(createPlantButton);

        setVisible(true);
    }

    private void showPlantDetails() {
        String selectedName = (String) plantComboBox.getSelectedItem();
        if (selectedName != null) {
            agent.requestPlantDetails(selectedName, details -> plantDetailsArea.setText(details));
        }
    }

    private final ActionListener onAddSymptom = e -> {
        String selectedPlant = (String) plantComboBox.getSelectedItem();
        if (selectedPlant != null) {
            new SymptomAdderGUI(agent, selectedPlant);
        }
    };

    private final ActionListener onCreatePlant = e -> new NewPlantGUI(agent);
}

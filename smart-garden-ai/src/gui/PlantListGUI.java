package gui;

import agents.UserAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Plant;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PlantListGUI extends JFrame {
    private UserAgent agent;
    private JComboBox<String> plantComboBox;
    private JTextArea plantDetailsArea;
    private JTextArea symptomsArea;
    private JButton addSymptomButton, createPlantButton;

    private List<Plant> userPlants;

    public PlantListGUI(UserAgent agent, List<Plant> plants) {
        this.agent = agent;
        this.userPlants = plants;
        init();
    }

    private void init() {
        setTitle("Растения на " + agent.currentUsername);
        setSize(600, 450);
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
        plantDetailsArea.setEditable(false);
        JScrollPane detailsScroll = new JScrollPane(plantDetailsArea);
        detailsScroll.setBounds(20, 60, 540, 130);
        add(detailsScroll);

        JLabel symptomsLabel = new JLabel("Симптоми:");
        symptomsLabel.setBounds(20, 200, 100, 25);
        add(symptomsLabel);

        symptomsArea = new JTextArea();
        symptomsArea.setEditable(false);
        JScrollPane symptomsScroll = new JScrollPane(symptomsArea);
        symptomsScroll.setBounds(120, 200, 440, 60);
        add(symptomsScroll);

        addSymptomButton = new JButton("➕ Добави симптом");
        addSymptomButton.setBounds(20, 280, 200, 30);
        addSymptomButton.addActionListener(onAddSymptom);

        createPlantButton = new JButton("🌱 Създай растение");
        createPlantButton.setBounds(240, 280, 200, 30);
        createPlantButton.addActionListener(onCreatePlant);

        add(addSymptomButton);
        add(createPlantButton);

        setVisible(true);
    }

    private void showPlantDetails() {
        String selectedName = (String) plantComboBox.getSelectedItem();
        if (selectedName != null) {
            agent.requestPlantDetails(selectedName, details -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Plant plant = mapper.readValue(details, Plant.class);



                    System.out.println("✅ DEBUG: Получен JSON от агента: " + details);
                    System.out.println("✅ DEBUG: Plant обект:");
                    System.out.println("Име: " + plant.getName());
                    System.out.println("Тип: " + plant.getType());
                    System.out.println("Почвена влага: " + plant.getSoilMoisture());
                    System.out.println("Температура: " + plant.getTemperature());
                    System.out.println("Влажност: " + plant.getHumidity());
                    System.out.println("Светлина: " + plant.getLight());

                    StringBuilder info = new StringBuilder();
                    info.append("Име: ").append(plant.getName()).append("\n");
                    info.append("Тип: ").append(plant.getType()).append("\n");
                    info.append("Влага на почвата: ").append(plant.getSoilMoisture()).append("\n");
                    info.append("Температура: ").append(plant.getTemperature()).append("\n");
                    info.append("Влажност: ").append(plant.getHumidity()).append("\n");
                    info.append("Светлина: ").append(plant.getLight()).append("\n");

                    plantDetailsArea.setText(info.toString());

                    // Симптоми
                    List<String> symptoms = plant.getSymptoms();
                    if (symptoms == null || symptoms.isEmpty()) {
                        symptomsArea.setText("Няма записани симптоми.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (String s : symptoms) {
                            sb.append("• ").append(s).append("\n");
                        }
                        symptomsArea.setText(sb.toString());
                    }
                } catch (Exception e) {
                    plantDetailsArea.setText("Грешка при визуализация: " + e.getMessage());
                    symptomsArea.setText("");
                }
            });
        }
    }

    private final ActionListener onAddSymptom = e -> {
        String selectedPlant = (String) plantComboBox.getSelectedItem();
        if (selectedPlant != null) {
            new gui.SymptomAdderGUI(agent, selectedPlant) {
                @Override
                public void dispose() {
                    super.dispose();
                    // След затваряне на прозореца – обнови симптомите
                    SwingUtilities.invokeLater(() -> showPlantDetails());
                }
            };
        }
    };

    private final ActionListener onCreatePlant = e -> new NewPlantGUI(agent);
}
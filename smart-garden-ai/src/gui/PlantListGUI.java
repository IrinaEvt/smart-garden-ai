package gui;

import agents.UserAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Plant;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PlantListGUI extends JFrame {
    private  UserAgent agent;
    private JComboBox<String> plantComboBox;
    private JTextArea plantDetailsArea;
    private JButton addSymptomButton, createPlantButton;

    private JList<String> symptomList;
    private DefaultListModel<String> symptomListModel;

    private List<Plant> userPlants;

    public PlantListGUI(UserAgent agent, List<Plant> plants) {
        this.agent = agent;
        this.userPlants = plants;
        init();
    }

    public void updatePlantComboBox(List<Plant> newPlants) {
        this.userPlants = newPlants;
        plantComboBox.removeAllItems();
        for (Plant plant : newPlants) {
            plantComboBox.addItem(plant.getName());
        }

        // по желание: покажи детайли за първото
        if (!newPlants.isEmpty()) {
            plantComboBox.setSelectedIndex(0);
            showPlantDetails();
        }
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

        symptomListModel = new DefaultListModel<>();
        symptomList = new JList<>(symptomListModel);
        symptomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane symptomsScroll = new JScrollPane(symptomList);
        symptomsScroll.setBounds(120, 200, 440, 60);
        add(symptomsScroll);

        // reasoning при клик
        symptomList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedSymptom = symptomList.getSelectedValue();
                String selectedPlant = (String) plantComboBox.getSelectedItem();

                if (selectedSymptom != null && !selectedSymptom.equals("Няма записани симптоми.") && selectedPlant != null) {
                    agent.requestReasoningForPlant(selectedPlant, reasoning -> {
                        JOptionPane.showMessageDialog(this, reasoning, "Съвет за " + selectedPlant, JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
        });

        addSymptomButton = new JButton("➕ Добави симптом");
        addSymptomButton.setBounds(20, 280, 200, 30);
        addSymptomButton.addActionListener(onAddSymptom);
        add(addSymptomButton);

        createPlantButton = new JButton("🌱 Създай растение");
        createPlantButton.setBounds(240, 280, 200, 30);
        createPlantButton.addActionListener(onCreatePlant);
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

                    StringBuilder info = new StringBuilder();
                    info.append("Име: ").append(plant.getName()).append("\n");
                    info.append("Тип: ").append(plant.getType()).append("\n");
                    info.append("Влага на почвата: ").append(plant.getSoilMoisture()).append("\n");
                    info.append("Температура: ").append(plant.getTemperature()).append("\n");
                    info.append("Влажност: ").append(plant.getHumidity()).append("\n");
                    info.append("Светлина: ").append(plant.getLight()).append("\n");

                    plantDetailsArea.setText(info.toString());

                    List<String> symptoms = plant.getSymptoms();
                    symptomListModel.clear();
                    if (symptoms == null || symptoms.isEmpty()) {
                        symptomListModel.addElement("Няма записани симптоми.");
                    } else {
                        for (String s : symptoms) {
                            symptomListModel.addElement(s);
                        }
                    }
                } catch (Exception e) {
                    plantDetailsArea.setText("Грешка при визуализация: " + e.getMessage());
                    symptomListModel.clear();
                    symptomListModel.addElement("⚠️ Грешка при зареждане на симптоми.");
                }
            });
        }
    }

    private final ActionListener onAddSymptom = e -> {
        String selectedPlant = (String) plantComboBox.getSelectedItem();
        if (selectedPlant != null) {
            new SymptomAdderGUI(agent, selectedPlant) {
                @Override
                public void dispose() {
                    super.dispose();
                    SwingUtilities.invokeLater(() -> showPlantDetails());
                }
            };
        }
    };

    private final ActionListener onCreatePlant = e -> new NewPlantGUI(agent);
}

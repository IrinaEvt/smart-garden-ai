package gui;

import agents.UserAgent;
import models.Plant;

import javax.swing.*;

import java.awt.event.ActionListener;
import java.util.Arrays;

public class PlantClientGUI extends JFrame {

    private JTextField nameField, typeField, moistureField, tempField, humidityField, lightField, symptomsField, plantIdField;
    private JButton createPlantButton, getPlantsButton, getSymptomsButton;
    private JTextArea outputArea;

    private  UserAgent agent;

    public PlantClientGUI(UserAgent agent) {
        this.agent = agent;
        init();
    }

    private void init() {
        setTitle("Plant Manager");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        JLabel typeLabel = new JLabel("Type:");
        JLabel moistureLabel = new JLabel("Soil Moisture:");
        JLabel tempLabel = new JLabel("Temperature:");
        JLabel humidityLabel = new JLabel("Humidity:");
        JLabel lightLabel = new JLabel("Light:");
        JLabel symptomsLabel = new JLabel("Symptoms (comma-separated):");
        JLabel plantIdLabel = new JLabel("Plant ID (for symptoms):");

        nameField = new JTextField();
        typeField = new JTextField();
        moistureField = new JTextField();
        tempField = new JTextField();
        humidityField = new JTextField();
        lightField = new JTextField();
        symptomsField = new JTextField();
        plantIdField = new JTextField();

        createPlantButton = new JButton("Create & Analyze Plant");
        getPlantsButton = new JButton("Get My Plants");
        getSymptomsButton = new JButton("Get Symptoms by Plant ID");

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // position
        int y = 10;
        int labelWidth = 160;
        int fieldWidth = 180;

        nameLabel.setBounds(20, y, labelWidth, 25);
        nameField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        typeLabel.setBounds(20, y, labelWidth, 25);
        typeField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        moistureLabel.setBounds(20, y, labelWidth, 25);
        moistureField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        tempLabel.setBounds(20, y, labelWidth, 25);
        tempField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        humidityLabel.setBounds(20, y, labelWidth, 25);
        humidityField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        lightLabel.setBounds(20, y, labelWidth, 25);
        lightField.setBounds(200, y, fieldWidth, 25);
        y += 30;

        symptomsLabel.setBounds(20, y, 240, 25);
        symptomsField.setBounds(20, y + 25, 360, 25);
        y += 60;

        createPlantButton.setBounds(20, y, 200, 30);
        getPlantsButton.setBounds(230, y, 150, 30);
        y += 40;

        plantIdLabel.setBounds(20, y, 200, 25);
        plantIdField.setBounds(220, y, 80, 25);
        getSymptomsButton.setBounds(310, y, 200, 25);
        y += 35;

        scrollPane.setBounds(20, y, 540, 150);


        add(nameLabel); add(nameField);
        add(typeLabel); add(typeField);
        add(moistureLabel); add(moistureField);
        add(tempLabel); add(tempField);
        add(humidityLabel); add(humidityField);
        add(lightLabel); add(lightField);
        add(symptomsLabel); add(symptomsField);
        add(createPlantButton); add(getPlantsButton);
        add(plantIdLabel); add(plantIdField); add(getSymptomsButton);
        add(scrollPane);

        // actions
        createPlantButton.addActionListener(onCreatePlant);
        getPlantsButton.addActionListener(onGetPlants);
        getSymptomsButton.addActionListener(onGetSymptoms);

        setVisible(true);
    }

    private final ActionListener onCreatePlant = e -> {
        Plant plant = new Plant();
        plant.setName(nameField.getText());
        plant.setType(typeField.getText());
        plant.setSoilMoisture(moistureField.getText());
        plant.setTemperature(tempField.getText());
        plant.setHumidity(humidityField.getText());
        plant.setLight(lightField.getText());

        String[] symptoms = symptomsField.getText().split(",");
        plant.setSymptoms(Arrays.asList(symptoms));

        agent.sendPlantForAnalysis(plant);
        outputArea.append("✅ Изпратено растение за анализ: " + plant.getName() + "\n");
    };

    private final ActionListener onGetPlants = e -> {
        agent.requestPlantsForUser();
        outputArea.append("📦 Заявка за всички растения е изпратена.\n");
    };

    private final ActionListener onGetSymptoms = e -> {
        String plantId = plantIdField.getText();
        if (plantId != null && !plantId.trim().isEmpty()) {
            agent.requestSymptomsByPlantId(plantId);
            outputArea.append("📋 Заявка за симптоми на растение ID " + plantId + " е изпратена.\n");
        } else {
            outputArea.append("⚠ Моля въведете Plant ID.\n");
        }
    };
}

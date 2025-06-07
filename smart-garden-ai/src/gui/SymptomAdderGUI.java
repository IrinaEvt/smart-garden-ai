package gui;

import agents.UserAgent;

import javax.swing.*;

public class SymptomAdderGUI extends JFrame {
    private JTextField symptomField;
    private JTextArea reasoningArea;
    private JButton addButton;

    private String plantName;
    private UserAgent agent;

    public SymptomAdderGUI(UserAgent agent, String plantName) {
        this.agent = agent;
        this.plantName = plantName;
        init();
    }

    private void init() {
        setTitle("Добави симптом към " + plantName);
        setSize(500, 400);
        setLayout(null);

        JLabel symptomLabel = new JLabel("Симптом:");
        symptomLabel.setBounds(20, 20, 100, 25);
        add(symptomLabel);

        symptomField = new JTextField();
        symptomField.setBounds(120, 20, 200, 25);
        add(symptomField);

        addButton = new JButton("Добави");
        addButton.setBounds(340, 20, 100, 25);
        addButton.addActionListener(e -> {
            String symptom = symptomField.getText();
            agent.addSymptomToPlant(plantName, symptom, reasoning -> reasoningArea.setText(reasoning));
        });
        add(addButton);

        reasoningArea = new JTextArea();
        reasoningArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reasoningArea);
        scrollPane.setBounds(20, 60, 440, 280);
        add(scrollPane);

        setVisible(true);
    }
}

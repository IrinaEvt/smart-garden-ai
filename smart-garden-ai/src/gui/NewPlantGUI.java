package gui;

import agents.UserAgent;

import javax.swing.*;

public class NewPlantGUI extends JFrame {
    private JTextField nameField, typeField;
    private JButton createButton;

    private UserAgent agent;

    public NewPlantGUI(UserAgent agent) {
        this.agent = agent;
        init();
    }

    private void init() {
        setTitle("Създай ново растение");
        setSize(400, 200);
        setLayout(null);

        JLabel nameLabel = new JLabel("Име:");
        nameLabel.setBounds(20, 20, 100, 25);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(120, 20, 200, 25);
        add(nameField);

        JLabel typeLabel = new JLabel("Тип:");
        typeLabel.setBounds(20, 60, 100, 25);
        add(typeLabel);

        typeField = new JTextField();
        typeField.setBounds(120, 60, 200, 25);
        add(typeField);

        createButton = new JButton("Създай");
        createButton.setBounds(120, 100, 100, 30);
        createButton.addActionListener(e -> {
            agent.createNewPlant(nameField.getText(), typeField.getText());
            dispose();
        });
        add(createButton);

        setVisible(true);
    }
}

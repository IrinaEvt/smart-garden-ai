package gui;

import agents.UserAgent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginRegisterGUI  extends JFrame {

        private JTextField usernameField;
        private JPasswordField passwordField;
        private JRadioButton loginRadio;
        private JRadioButton registerRadio;
        private JButton confirmButton;

        private UserAgent agent;

        public LoginRegisterGUI(UserAgent agent) {
            this.agent = agent;
            agent.setLoginGUI(this);
            init();
        }

        private void init() {
            setTitle("Login / Register");
            setSize(400, 250);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            JPanel panel = new JPanel(null);

            JLabel userLabel = new JLabel("Username:");
            JLabel passLabel = new JLabel("Password:");
            usernameField = new JTextField();
            passwordField = new JPasswordField();

            loginRadio = new JRadioButton("Login");
            registerRadio = new JRadioButton("Register");
            ButtonGroup group = new ButtonGroup();
            group.add(loginRadio);
            group.add(registerRadio);
            loginRadio.setSelected(true);

            confirmButton = new JButton("Continue");

            userLabel.setBounds(30, 30, 100, 25);
            usernameField.setBounds(140, 30, 200, 25);
            passLabel.setBounds(30, 70, 100, 25);
            passwordField.setBounds(140, 70, 200, 25);
            loginRadio.setBounds(140, 110, 100, 25);
            registerRadio.setBounds(240, 110, 100, 25);
            confirmButton.setBounds(140, 150, 200, 30);

            panel.add(userLabel);
            panel.add(usernameField);
            panel.add(passLabel);
            panel.add(passwordField);
            panel.add(loginRadio);
            panel.add(registerRadio);
            panel.add(confirmButton);

            add(panel);
            setVisible(true);

            confirmButton.addActionListener(onConfirmClicked);
        }

        private final ActionListener onConfirmClicked = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String choice = registerRadio.isSelected() ? "2" : "1"; // 1 = login, 2 = register

                agent.setLoginInfo(username, password, choice);
            }
        };
}


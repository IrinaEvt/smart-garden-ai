package agents;

import behaviours.LoginRegisterBehaviour;
import behaviours.PlantInteractionBehaviour;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.LoginRegisterGUI;
import gui.PlantClientGUI;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import models.Plant;
import models.PlantRequestWrapper;

import javax.swing.*;


public class UserAgent extends Agent {
    public String loginUsername;
    public String loginPassword;
    public String loginChoice;

    public int currentUserId = -1;
    public String currentUsername;
    public LoginRegisterGUI loginGUI;





    @Override
    protected void setup() {
        System.out.println("Потребител: Влизане...");

        SwingUtilities.invokeLater(() -> new LoginRegisterGUI(this));

  //      addBehaviour(new LoginRegisterBehaviour()) ;
 //       addBehaviour(new PlantInteractionBehaviour()) ;


        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    System.out.println("UserAgent получи:\n" + msg.getContent());
                } else {
                    block();
                }
            }
        });
    }

    public void setLoginGUI(LoginRegisterGUI gui) {
        this.loginGUI = gui;
    }

    public void setLoginInfo(String username, String password, String choice) {
        this.loginUsername = username;
        this.loginPassword = password;
        this.loginChoice = choice;
        addBehaviour(new behaviours.LoginRegisterBehaviour()); // стартирай логин поведение
    }

    // send to CareAgent
    public void sendPlantForAnalysis(Plant plant) {
        try {
            PlantRequestWrapper wrapper = new PlantRequestWrapper();
            wrapper.setUserId(currentUserId);
            wrapper.setPlant(plant);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(wrapper);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(getAID("care"));
            msg.setContent("analyzePlantModel:" + json);
            send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // send request to CareAgent
    public void requestPlantsForUser() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getPlantsByUserId:" + currentUserId);
        send(msg);
    }


    public void requestSymptomsByPlantId(String plantId) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getSymptomsByPlantId:" + plantId);
        send(msg);
    }

    // Call from LoginRegisterBehaviour after sucsessful login
    public void launchPlantGUI() {
        SwingUtilities.invokeLater(() -> new PlantClientGUI(this));
    }
}

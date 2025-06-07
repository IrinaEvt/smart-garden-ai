package agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import gui.LoginRegisterGUI;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
    private gui.PlantListGUI plantListGUI;

    private final ConcurrentHashMap<String, Consumer<String>> pendingResponses = new ConcurrentHashMap<>();

    public void setPlantListGUI(gui.PlantListGUI gui) {
        this.plantListGUI = gui;
    }

    public void updatePlantList(List<Plant> updatedPlants) {
        this.plantListGUI.updatePlantComboBox(updatedPlants);
    }


    @Override
    protected void setup() {
        System.out.println("Потребител: Влизане...");

        SwingUtilities.invokeLater(() -> new LoginRegisterGUI(this));



        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("UserAgent получи:\n" + content);

                    // опитай да извикаш callback
                    Consumer<String> callback = pendingResponses.remove(msg.getConversationId());
                    if (callback != null) {
                        callback.accept(content);
                    } else {
                        System.out.println("⚠ Няма callback за: " + msg.getConversationId());
                    }
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
        addBehaviour(new behaviours.LoginRegisterBehaviour());
    }

    public void sendPlantForAnalysis(Plant plant,Consumer<String> callback) {
        try {
            PlantRequestWrapper wrapper = new PlantRequestWrapper();
            wrapper.setUserId(currentUserId);
            wrapper.setPlant(plant);

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(wrapper);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(getAID("care"));
            msg.setContent("analyzePlantModel:" + json);


            String convId = "analyze_" + plant.getName() + "_" + System.currentTimeMillis();
            msg.setConversationId(convId);

            pendingResponses.put(convId, callback);
            send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // send request to CareAgent
  /*  public void requestPlantsForUser() {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getPlantsByUserId:" + currentUserId);
        send(msg);
    }
*/
    public void requestPlantDetails(String plantName, Consumer<String> callback) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getPlant:" + plantName);

        String convId = "getPlant_" + plantName + "_" + System.currentTimeMillis();
        msg.setConversationId(convId);
        pendingResponses.put(convId, callback);
        send(msg);
    }

    public void requestPlantsForUser(Consumer<String> callback) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getPlantsByUserId:" + currentUserId);

        String convId = "getPlants_" + System.currentTimeMillis();
        msg.setConversationId(convId);

        pendingResponses.put(convId, callback);
        send(msg);
    }

    public void createNewPlant(String name, String type) {
       Plant plant = new Plant();
        plant.setName(name);
        plant.setType(type);
        plant.setSymptoms(new ArrayList<String>()); // без симптоми

        sendPlantForAnalysis(plant, response -> {

            requestPlantsForUser(json -> {
                try {
                    ObjectMapper mapper = new ObjectMapper();
                    Plant[] plantArray = mapper.readValue(json, Plant[].class);
                    List<Plant> updatedList = Arrays.asList(plantArray);
                    SwingUtilities.invokeLater(() -> updatePlantList(updatedList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void addSymptomToPlant(String plantName, String symptom, Consumer<String> reasoningCallback) {
        try {

            requestPlantDetails(plantName, plantStr -> {
                try {
                    System.out.println("📦 JSON от CareAgent: " + plantStr);
                    ObjectMapper mapper = new ObjectMapper();
                    Plant plant = mapper.readValue(plantStr, Plant.class);
                    if (!plant.getSymptoms().contains(symptom)) {
                        plant.getSymptoms().add(symptom);
                    }

                    PlantRequestWrapper wrapper = new PlantRequestWrapper();
                    wrapper.setPlant(plant);
                    wrapper.setUserId(currentUserId);

                    String json = mapper.writeValueAsString(wrapper);

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(getAID("care"));
                    msg.setContent("addSymptomReasoning:" + json);

                    String convId = "symptomReasoning_" + plantName + "_" + System.currentTimeMillis();
                    msg.setConversationId(convId);
                    pendingResponses.put(convId, reasoningCallback);
                    send(msg);
                } catch (Exception e) {
                    reasoningCallback.accept("Грешка при сериализация: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            reasoningCallback.accept("Грешка: " + e.getMessage());
        }
    }

    public void requestReasoningForPlant(String plantName, Consumer<String> callback) {
        requestPlantDetails(plantName, plantJson -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                Plant plant = mapper.readValue(plantJson, Plant.class);

                // Създай обвивка за reasoning
                PlantRequestWrapper wrapper = new PlantRequestWrapper();
                wrapper.setPlant(plant);
                wrapper.setUserId(currentUserId); // ако е нужно

                String json = mapper.writeValueAsString(wrapper);

                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("care"));
                msg.setContent("addSymptomReasoning:" + json);
                String convId = "reasoningOnly_" + plantName + "_" + System.currentTimeMillis();
                msg.setConversationId(convId);
                pendingResponses.put(convId, callback);
                send(msg);
            } catch (Exception e) {
                callback.accept("⚠ Грешка при reasoning заявка: " + e.getMessage());
            }
        });
    }


    public void requestPlantDetailsFromDB(String plantName, Consumer<String> callback) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getFullPlantDetails:" + plantName); // нова команда
        String convId = "plantFromDB_" + plantName + "_" + System.currentTimeMillis();
        msg.setConversationId(convId);
        pendingResponses.put(convId, callback);
        send(msg);
    }


    public void requestSymptomsByPlantId(String plantId) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID("care"));
        msg.setContent("getSymptomsByPlantId:" + plantId);
        send(msg);
    }

    // Call from LoginRegisterBehaviour after sucsessful login
  /*  public void launchPlantGUI() {
        SwingUtilities.invokeLater(() -> new PlantClientGUI(this));
    }*/
}
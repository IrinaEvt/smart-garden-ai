package agents;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import models.Plant;
import ontology.PlantOntology;

import java.util.ArrayList;
import java.util.List;

public class UserAgent extends Agent {
    private PlantOntology ontology;

    @Override
    protected void setup() {
        System.out.println("Потребител: Влизане...");

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {

                Plant plant = new Plant();
                plant.setName("orchid1");
                plant.setType("Orchid");
                List<String> symptoms = new ArrayList<>();
                symptoms.add("LeafYellowing");
                plant.setSymptoms(symptoms);


                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(plant);

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(getAID("care"));
                    msg.setContent("analyzePlantModel:" + json);
                    send(msg);
                } catch (Exception e) {
                    System.err.println("Неуспешна сериализация: " + e.getMessage());
                }


                ACLMessage getPlant = new ACLMessage(ACLMessage.REQUEST);
                getPlant.addReceiver(getAID("care"));
                getPlant.setContent("getPlant:orchid1");
                send(getPlant);

                try {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(plant); // използваме същия обект

                    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                    msg.addReceiver(getAID("care"));
                    msg.setContent("removePlant:" + json);
                    send(msg);
                } catch (Exception e) {
                    System.err.println("Неуспешна сериализация при removePlant: " + e.getMessage());
                }


            }


        });


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
}

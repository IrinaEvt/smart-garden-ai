package behaviours;

import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import models.Plant;

import java.util.ArrayList;
import java.util.List;

public class PlantInteractionBehaviour extends OneShotBehaviour {

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
            msg.addReceiver(myAgent.getAID("care"));
            msg.setContent("analyzePlantModel:" + json);
            myAgent.send(msg);
        } catch (Exception e) {
            System.err.println("Неуспешна сериализация: " + e.getMessage());
        }


        ACLMessage getPlant = new ACLMessage(ACLMessage.REQUEST);
        getPlant.addReceiver(myAgent.getAID("care"));
        getPlant.setContent("getPlant:orchid1");
        myAgent.send(getPlant);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(plant); // използваме същия обект

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(myAgent.getAID("care"));
            msg.setContent("removePlant:" + json);
            myAgent.send(msg);
        } catch (Exception e) {
            System.err.println("Неуспешна сериализация при removePlant: " + e.getMessage());
        }


    }

}

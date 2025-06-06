package behaviours;

import agents.UserAgent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import models.Plant;
import models.PlantRequestWrapper;

import java.util.ArrayList;
import java.util.List;

public class PlantInteractionBehaviour extends OneShotBehaviour {

    @Override
    public void action() {
        UserAgent ua = (UserAgent) myAgent;

        Plant plant = new Plant();
        plant.setName("orchid8");
        plant.setType("Orchid");
        List<String> symptoms = new ArrayList<>();
        symptoms.add("LeafYellowing");
        plant.setSymptoms(symptoms);

        PlantRequestWrapper wrapper = new PlantRequestWrapper();
        wrapper.setPlant(plant);
        wrapper.setUserId(ua.currentUserId);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(wrapper);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(myAgent.getAID("care"));
            msg.setContent("analyzePlantModel:" + json);
            myAgent.send(msg);
        } catch (Exception e) {
            System.err.println("Неуспешна сериализация: " + e.getMessage());
        }


        ACLMessage getPlant = new ACLMessage(ACLMessage.REQUEST);
        getPlant.addReceiver(myAgent.getAID("care"));
        getPlant.setContent("getPlant:orchid8");
        myAgent.send(getPlant);

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(plant);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(myAgent.getAID("care"));
            msg.setContent("removePlant:" + json);
            myAgent.send(msg);
        } catch (Exception e) {
            System.err.println("Неуспешна сериализация при removePlant: " + e.getMessage());
        }


    }

}

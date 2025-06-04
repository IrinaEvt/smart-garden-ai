package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.PlantOntology;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Plant;

import java.util.List;

public class CareAgent extends Agent {

    private PlantOntology ontology;

    @Override
    protected void setup() {
        ontology = new PlantOntology();
        System.out.println("OntologyAgent стартира.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String[] parts = msg.getContent().split(":");
                    String response = "";

                   switch (parts[0]) {


                        case "createPlantModel":
                            // Формат: createPlantModel:JSON
                            try {
                                String json = msg.getContent().substring("createPlantModel:".length());
                                ObjectMapper mapper = new ObjectMapper();
                                Plant plantModel = mapper.readValue(json, Plant.class);
                                ontology.createPlantIndividual(plantModel);
                                response = "Създадено растение чрез модел: " + plantModel.getName();
                            } catch (Exception e) {
                                response = "Грешка при обработка на Plant модел: " + e.getMessage();
                            }
                            break;

                       case "analyzePlantModel":
                           try {
                               String json = msg.getContent().substring("analyzePlantModel:".length());
                               ObjectMapper mapper = new ObjectMapper();
                               Plant plantModel = mapper.readValue(json, Plant.class);

                               ontology.createPlantIndividual(plantModel);
                               List<String> advice = ontology.getAdviceForPlantIndividual(plantModel.getName());
                               response = String.join("\n", advice);


                           } catch (Exception e) {
                               response = "Грешка при анализ: " + e.getMessage();
                           }
                           break;


                        case "getPlant":
                            // Формат: getPlant:orchid1
                            models.Plant plant = ontology.getPlantByIndividualName(parts[1]);
                            response = plant.toString();
                            break;

                         case "removePlant":
                            try {
                                String json = msg.getContent().substring("removePlant:".length());
                                 ObjectMapper mapper = new ObjectMapper();
                                 Plant plantModel = mapper.readValue(json, Plant.class);

                                 ontology.removePlantAndSymptoms(plantModel.getName(), plantModel.getSymptoms());
                                 response = "Растението и симптомите са премахнати: " + plantModel.getName();
                            } catch (Exception e) {
                                 response = "Грешка при изтриване: " + e.getMessage();
                                }
                            break;



                        default:
                            response = "Непозната команда.";
                    }

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(response);
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}

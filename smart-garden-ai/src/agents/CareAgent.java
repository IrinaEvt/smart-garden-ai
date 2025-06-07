package agents;

import dao.PlantDAO;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import models.PlantRequestWrapper;
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


              /*          case "createPlantModel":
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

               */

                        case "analyzePlantModel":
                            try {
                                String json = msg.getContent().substring("analyzePlantModel:".length());
                                ObjectMapper mapper = new ObjectMapper();
                                PlantRequestWrapper wrapper = mapper.readValue(json, PlantRequestWrapper.class);
                                Plant plantModel = wrapper.getPlant();

                                int userId = wrapper.getUserId();

                                ontology.createPlantIndividual(plantModel);

                                Plant fullPlant = ontology.getPlantByIndividualName(plantModel.getName());


                                //save in DB
                              PlantDAO plantDAO = new PlantDAO();
                               // int plantId = plantDAO.savePlant(fullPlant, userId);
                                int existingId = plantDAO.getPlantIdByName(plantModel.getName());

                                int plantId;
                                if (existingId != -1) {
                                    plantId = existingId;
                                    System.out.println("⚠️ Растение вече съществува: " + plantModel.getName());
                                    plantDAO.updatePlantNeedsAndType(fullPlant, plantId);


                                    plantDAO.deleteSymptomsByPlantId(plantId);
                                    if (plantModel.getSymptoms() != null) {
                                        for (String symptom : plantModel.getSymptoms()) {
                                            plantDAO.saveSymptom(symptom, plantId);
                                        }
                                    }
                                } else {
                                    plantId = plantDAO.savePlant(fullPlant, userId);
                                }


                                //съвети
                                List<String> advice = ontology.getAdviceForPlantIndividual(plantModel.getName());
                                response = String.join("\n", advice);


                            } catch (Exception e) {
                                response = "Грешка при анализ: " + e.getMessage();
                            }
                            break;

                        case "getFullPlantDetails":
                            try {
                                String plantName = parts[1];
                                PlantDAO dao = new PlantDAO();
                                int plantId = dao.getPlantIdByName(plantName);

                                Plant fullPlant = ontology.getPlantByIndividualName(plantName);
                                if (plantId != -1) {
                                    List<String> symptoms = dao.getSymptomsByPlantId(plantId);
                                    fullPlant.setSymptoms(symptoms);
                                }

                                ObjectMapper mapper = new ObjectMapper();
                                response = mapper.writeValueAsString(fullPlant);
                            } catch (Exception e) {
                                response = "Грешка при взимане на пълни детайли: " + e.getMessage();
                            }
                            break;

                        case "addSymptomReasoning":
                            try {
                                String json = msg.getContent().substring("addSymptomReasoning:".length());
                                ObjectMapper mapper = new ObjectMapper();
                                PlantRequestWrapper wrapper = mapper.readValue(json, PlantRequestWrapper.class);
                                Plant plant = wrapper.getPlant();


                                PlantDAO dao = new PlantDAO();
                                int plantId = dao.getPlantIdByName(plant.getName());
                                if (plantId != -1) {
                                    dao.deleteSymptomsByPlantId(plantId); //
                                    for (String symptom : plant.getSymptoms()) {
                                        dao.saveSymptom(symptom, plantId);
                                    }
                                }

                                ontology.createPlantIndividual(plant);
                                ontology.getAdviceForPlantIndividual(plant.getName());
                                List<String> reasoning = ontology.getAdviceForPlantIndividual(plant.getName());

                                System.out.println("📘 Reasoning резултат: " + reasoning);
                                response = String.join("\n", reasoning);

                            } catch (Exception e) {
                                response = "Грешка при reasoning: " + e.getMessage();
                            }
                            break;


                        case "getPlant":
                            try {
                                String plantName = parts[1];

                                // 1. Вземи данните от онтологията
                                models.Plant plant = ontology.getPlantByIndividualName(plantName);

                                // 2. Вземи симптомите от базата
                                PlantDAO plantDAO = new PlantDAO();
                                int plantId = plantDAO.getPlantIdByName(plantName);
                                Plant fullPlant = null;
                                if (plantId != -1) {
                                    fullPlant = plantDAO.getPlantById(plantId);
                                    List<String> symptoms = plantDAO.getSymptomsByPlantId(plantId);
                                    plant.setSymptoms(symptoms);
                                }

                                // 3. Върни комбинирания обект
                                ObjectMapper mapper = new ObjectMapper();
                                response = mapper.writeValueAsString(fullPlant);
                            } catch (Exception e) {
                                response = "Грешка при сериализация: " + e.getMessage();
                            }
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

                        case "getPlantsByUserId":
                            try {
                                int userId = Integer.parseInt(parts[1]);
                                PlantDAO plantDAO = new PlantDAO();
                                List<Plant> plants = plantDAO.getPlantsByUserId(userId);

                                ObjectMapper mapper = new ObjectMapper();
                                response = mapper.writeValueAsString(plants);

                            } catch (Exception e) {
                                response = "Грешка при взимане на растения: " + e.getMessage();
                            }
                            break;

                        case "getSymptomsByPlantId":
                            try {
                                int plantId = Integer.parseInt(parts[1]);
                                PlantDAO plantDAO = new PlantDAO();
                                List<String> symptoms = plantDAO.getSymptomsByPlantId(plantId);

                                ObjectMapper mapper = new ObjectMapper();
                                response = mapper.writeValueAsString(symptoms);

                            } catch (Exception e) {
                                response = "Грешка при взимане на симптоми: " + e.getMessage();
                            }
                            break;



                        default:
                            response = "Непозната команда.";
                    }

                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(response);


                    String convId = msg.getConversationId();
                    if (convId != null) {
                        reply.setConversationId(convId);
                    }
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}
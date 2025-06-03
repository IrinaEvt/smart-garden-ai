package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.PlantOntology;

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
                        case "createPlant":
                            // Формат: createPlant:orchid1:Orchid
                            ontology.createPlantIndividual(parts[1], parts[2]);
                            response = "Създадено растение (индивид): " + parts[1] + " от клас " + parts[2];
                            break;

                        case "addSymptom":
                            // Формат: addSymptom:orchid1:YellowLeaves:yellowLeaves1
                            ontology.addSymptomToPlantIndividual(parts[1], parts[2], parts[3]);
                            response = "Добавен симптом (индивид) " + parts[3] + " от клас " + parts[2] + " към растение " + parts[1];
                            break;

                        case "getAdvice":
                            // Формат: getAdvice:orchid1
                            List<String> advice = ontology.getAdviceForPlantIndividual(parts[1]);
                            response = String.join("\n", advice);
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

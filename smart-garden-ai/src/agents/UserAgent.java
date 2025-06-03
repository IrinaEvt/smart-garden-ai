package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ontology.PlantOntology;

import java.util.List;

public class UserAgent extends Agent {
    private PlantOntology ontology;

    @Override
    protected void setup() {
        System.out.println("Потребител: Влизане...");

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                // Създаване на индивид Lavender от клас Orchid
                ACLMessage createPlant = new ACLMessage(ACLMessage.REQUEST);
                createPlant.addReceiver(getAID("care"));
                createPlant.setContent("createPlant:orchid1:Orchid");
                send(createPlant);

                // Добавяне на симптом yellowLeaves1 от клас YellowLeaves към индивид orchid1
                ACLMessage addSymptom = new ACLMessage(ACLMessage.REQUEST);
                addSymptom.addReceiver(getAID("care"));
                addSymptom.setContent("addSymptom:orchid1:LeafYellowing:yellowLeaves1");
                send(addSymptom);

                // Извличане на препоръки
                ACLMessage getAdvice = new ACLMessage(ACLMessage.REQUEST);
                getAdvice.addReceiver(getAID("care"));
                getAdvice.setContent("getAdvice:orchid1");
                send(getAdvice);
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

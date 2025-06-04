package agents;

import behaviours.LoginRegisterBehaviour;
import behaviours.PlantInteractionBehaviour;
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
    public int currentUserId = -1;
    public String currentUsername;

    @Override
    protected void setup() {
        System.out.println("Потребител: Влизане...");

        addBehaviour(new LoginRegisterBehaviour()) ;
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
}

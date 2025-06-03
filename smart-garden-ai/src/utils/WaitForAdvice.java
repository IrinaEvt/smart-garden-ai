package utils;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class WaitForAdvice extends CyclicBehaviour {
    public WaitForAdvice(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        ACLMessage msg = myAgent.receive();
        if (msg != null) {
            if (msg.getPerformative() == ACLMessage.INFORM) {
                System.out.println("📥 Получена препоръка: " + msg.getContent());
                // GUI
            }
        } else {
            block();
        }
    }
}

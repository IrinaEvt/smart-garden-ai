import dao.DatabaseInitializer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jade.core.Runtime;



public class Main {
    public static void main(String[] args) {

        DatabaseInitializer.initialize();
        // Старт JADE runtime
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        AgentContainer container = rt.createMainContainer(profile);

        try {
            AgentController care = container.createNewAgent(
                    "care",
                    "agents.CareAgent",
                    null
            );

            AgentController user = container.createNewAgent(
                    "user",
                    "agents.UserAgent",
                    null
            );

            care.start();
            user.start();



        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
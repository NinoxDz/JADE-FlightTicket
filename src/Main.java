import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {

	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		Profile p= new ProfileImpl();
		p.setParameter(Profile.MAIN_HOST, "localhost");
		p.setParameter(Profile.GUI, "true");
		ContainerController cc = rt.createMainContainer(p);
		//for(int i=0; i<6;i++){
		AgentController ac;
		try {
			ac=cc.createNewAgent("Seller-1", "TicketTrading.Seller", null);
			ac.start();
			ac=cc.createNewAgent("Seller-2", "TicketTrading.Seller", null);
			ac.start();
			ac=cc.createNewAgent("Seller-3", "TicketTrading.Seller", null);
			ac.start();
			ac=cc.createNewAgent("Buyer-1", "TicketTrading.Buyer", null);
			ac.start();
			ac=cc.createNewAgent("Buyer-2", "TicketTrading.Buyer", null);
			ac.start();
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		//}
		}
	}

}

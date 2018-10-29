package TicketTrading;
import java.io.File;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;




public class Buyer extends Agent {
	
	private String from;
	private String to;
	private int price;
	private Ticket t1;
	private String t2;
	private AID[] sellerAgents;
	private AID[] BestSellerAgents;
	private BuyerGui myGui;
	
	protected void setup0() {
		HashMap <String, Object> ticket = new HashMap();
		ticket.put(Integer.toString(1), t1);
		
		Gson g = new Gson();
		t2 = g.toJson(t1);
		
		
		if (t1 != null) {
			
			from = t1.getFrom();
			to = t1.getTo();
			price = t1.getPrice();
			
			System.out.println("i want to buy ticket from :"+from+"  to: "+ to + "with price under: " +price);

			// Add a TickerBehaviour that schedules a request to seller agents every minute
			
			addBehaviour(new TickerBehaviour(this, 15000) {
				
				protected void onTick() {
					
					System.out.println("Trying to buy ticket");
					
					// Update the list of seller agents
					
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					
					sd.setType("Ticket-selling");
					template.addServices(sd);
					
					try {
						
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Found the following seller agents:" + result);
						sellerAgents = new AID[result.length];
						 BestSellerAgents = new AID[3];

						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer());
				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No target book title specified");
			doDelete();
		}
	}
	
	
	protected void setup() {
		
		myGui = new BuyerGui(this);
		myGui.showGui();
		
		
		System.out.println("Hallo! Buyer-agent "+getAID().getName()+" is ready.");
		//t1 = new Ticket("lyon","paris",500);
		
		
		
		
		
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
	}

	
	private class RequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer 
		private int bestPrice; 
		private ACLMessage reply2;
		private int nbestPrice; // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt;
		private MessageTemplate mt1;// The template to receive replies
		private int step = 0;
		int i=0;
		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent(t2);
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				System.out.println("Buyer: hello, i want to buy ticket "+t2+" do you have it?");

				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					
					

					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply.getContent());
						BestSellerAgents[i]=reply.getSender();
						System.out.println("---------------"+i+"----"+BestSellerAgents[i]);
						i++;
						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
							bestPrice = price;
							bestSeller = reply.getSender();
						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:			
				// Send the cfp to all sellers
				ACLMessage cfp1 = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < BestSellerAgents.length; ++i) {
					cfp1.addReceiver(BestSellerAgents[i]);
					//System.out.println("++++++++"+i+"+++++"+BestSellerAgents[i]);

				} 
				//cfp1.addReceiver(bestSeller);
				cfp1.setContent("sold");
				cfp1.setConversationId("book-trade");
				cfp1.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				System.out.println("Buyer: can i have a reduction on the book of "+t2+"?");
				bestSeller= null;
				repliesCnt=0;
				myAgent.send(cfp1);
				// Prepare the template to get proposals
				mt1 = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp1.getReplyWith()));
				step = 3;
				break;
			case 3:
				// Receive all proposals/refusals from seller agents
				reply2 = myAgent.receive(mt1);
				//System.out.println("+++++++reply2 buyer++"+reply2);
				//System.out.println("+++++++333333333333333333");

				if (reply2 != null) {
					// Reply received²
					//System.out.println("+++++++reply2 != null++"+reply2.getSender());

					if (reply2.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply2.getContent());
						//System.out.println("++++++++price+++++"+price);
						
						if (bestSeller == null || price < nbestPrice) {
							// This is the best offer at present
							nbestPrice = price;
							bestSeller = reply2.getSender();
							//System.out.println("+++++++bestSeller+++"+bestSeller);

						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 4; 
					}
				}
				else {
					block();
				}
				break;
				
			case 4:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(t2);
				order.setConversationId("book-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 5;
				break;
			case 5:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage. INFORM) {
						// Purchase successful. We can terminate
						System.out.println(t2+" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+nbestPrice);
						JOptionPane.showMessageDialog(null, "successfully purchased from agent : "+reply.getSender().getName().split("@")[0]+" with price of "+nbestPrice);
						myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = 6;
				}
				else {
					block();
					
				}
				break;
			}        
		}
		

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+t2+" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 6);
		}
	}
	
	public void updateCatalogues(final String From,final String To,final int Price) {
		
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				
				t1 = new Ticket(From,To,Price);
				System.out.println(getAID().getName().split("@")[0]+"   "+"New Ticket : from: "+From+"  To: "+To+"  price:"+Price+"   inserted into catalogues.");
				setup0();
				
				
				
			}
		} );
		
     
	}// End of inner class RequestPerformer
}

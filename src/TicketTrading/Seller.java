package TicketTrading;


import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class Seller extends Agent {
	
	private Hashtable catalogues;
	HashMap <String, Object>catalogue = new HashMap();
;   
	String Key;
	Ticket t2;
	private SellerGui myGui;
	Random rand = new Random();
	private int sold = rand.nextInt(100) + 1;;
	String title;
	Integer price;
	Gson json;
	Ticket t1;
	public int TicketIndex = 0;
		@Override
		protected void setup() {
			
			
			//catalogues = new Hashtable();
			//catalogue = new HashMap();
			Date t = new Date();
			
			
			//Ticket var = (Ticket) catalogue.get("001"); 
			//System.out.println("the price is ======"+ var.getPrice());
			
			
			myGui = new SellerGui(this);
			myGui.showGui();
			
			
			// Register the information of service in the pages
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			ServiceDescription sd = new ServiceDescription();
			sd.setType("Ticket-selling");
			sd.setName("JADE-trading");
			dfd.addServices(sd);
			try {
				DFService.register(this, dfd);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}

			// Add the behaviour serving queries from buyer agents
			addBehaviour(new OfferRequestsServer());

			// Add the behaviour serving purchase orders from buyer agents
			addBehaviour(new PurchaseOrdersServer());
		    System.out.println("Hello world, I'm " + this.getLocalName());
		}
		// Put agent clean-up operations here
		protected void takeDown() {
			// Deregister from the yellow pages
			try {
				DFService.deregister(this);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			// Close the GUI
			myGui.dispose();
			// Printout a dismissal message
			System.out.println("Seller-agent "+getAID().getName().split("@")[0]+" terminating.");
		}

		
		private class OfferRequestsServer extends CyclicBehaviour {
			public void action() {
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
				ACLMessage msg = myAgent.receive(mt);
				//System.out.println("getContent::00-"+msg);
				if (msg != null) {
					//System.out.println("getContent::"+msg.getContent());
									
					if(msg.getContent().equals("sold")){
						
						int p = (price.intValue()- sold);
						ACLMessage soldreply = msg.createReply();
						System.out.println(getAID().getName().split("@")[0]+": yes, we have a first customer promo on the ticket :"+title+" the final price will be "+p);
						soldreply.setPerformative(ACLMessage.PROPOSE);
						soldreply.setContent(String.valueOf(p));	
						//System.out.println("sent:"+soldreply);
						myAgent.send(soldreply);
						//System.out.println("donnnne");
						
					}
					//System.out.println("//////////////////////////////////////////////////////////////////");
					
					// CFP Message received. Process it
					ACLMessage reply = msg.createReply();
					title = msg.getContent();
					
					
					
					boolean x ;
					//System.out.println("title============"+title);
					if (!title.equals("sold")) {
					json = new Gson();
					t1 = json.fromJson(title, Ticket.class);
					SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
					Date date1 = null;
					try {
						date1=formatter.parse(t1.Departure_Date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					String from = t1.getFrom();
					String to = t1.getTo();
					String pricef =  Integer.toString(t1.getPrice());
					x = false;
			        
			        for (String key : catalogue.keySet()) {
			        	Ticket t = (Ticket) catalogue.get(key);
			        	//System.out.println("From====:"+ t.getFrom());
			        	  
			        	if(from.equals(t.getFrom())){
			        		if(to.equals(t.getTo())){
			        			try {

									if(date1.compareTo(formatter.parse(t.Departure_Date)) > 0){
						        		

									if(Integer.parseInt(pricef)>t.getPrice()){
										x = true;
										t2 = (Ticket) catalogue.get(key);

										Key = key;
										price=t.getPrice();
										reply.setPerformative(ACLMessage.PROPOSE);
										reply.setContent(String.valueOf(/*price.intValue()*/t.getPrice()));
										System.out.println(getAID().getName().split("@")[0]+": hello, the price of the ticket is="+price);
										myAgent.send(reply);
									}
									}
								} catch (NumberFormatException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			        		}
			        	}
			        	
			        }
			        //System.out.println("For Loop:"+x+"  agent="+this.getAgent());
			        if(x != true){
			        	System.out.println("false = :"+x);
			        	// The requested book is NOT available for sale.
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("not-available");
						myAgent.send(reply);
			        	
			        }}
					
				
					
						
						
					
				}
				else {
					block();
				}
			}
		}  // End of inner class OfferRequestsServer
		
		private class PurchaseOrdersServer extends CyclicBehaviour {
			public void action() {
				
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					System.out.println("ACCEPT_PROPOSAL");
					// ACCEPT_PROPOSAL Message received. Process it
					String title = msg.getContent();
					ACLMessage reply = msg.createReply();

					catalogue.remove(Key);
					
					
					if (t2 != null) {
						reply.setPerformative(ACLMessage.INFORM);
						System.out.println(t2.getFrom()+" sold to agent "+msg.getSender().getName());
					}
					else {
						// The requested book has been sold to another buyer in the meanwhile .
						reply.setPerformative(ACLMessage.FAILURE);
						reply.setContent("not-available");
					}
					myAgent.send(reply);
				}
				else {
					block();
				}
			}
		}
		
		public void updateCatalogues(final String From,final String To,final String Departure_Date, final int Price) {
			
			addBehaviour(new OneShotBehaviour() {
				public void action() {
					
					Ticket t1 = new Ticket(From,To,Departure_Date,Price);
					System.out.println("TicketIndex="+TicketIndex+"  from=="+t1.getFrom());
					catalogue.put(Integer.toString(TicketIndex), t1);
					TicketIndex+=1;
					
					System.out.println(getAID().getName().split("@")[0]+"   "+"New Ticket : from: "+From+"  To: "+To+"  Departure_Date: "+ Departure_Date+ "  price:"+Price+".");
				}
			} );
			
	     
		}
	}

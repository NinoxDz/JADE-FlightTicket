package TicketTrading;

import java.util.Date;

public class Ticket {
	String Departure_Date;
	String From;
	String To;
	int Price;

	public Ticket(String From,String To, String Departure_Date, int Price){
		
		this.From=From;
		this.To = To;
		this.Departure_Date = Departure_Date;
		this.Price = Price;
		
	}
	public Ticket(String From,String To, int Price){
		
		this.From=From;
		this.To = To;
		this.Price = Price;
		
	}
	String getFrom(){
		return this.From;
	}
	
	
	String getTo(){
		return this.To;
	}

	
	String getDeparture_Date(){
		return this.Departure_Date;
	}

	
	int getPrice(){
		return this.Price;
	}
	
	void setFrom(String from){
		this.From = from;
	}
	
	
	void setTo(String to){
		this.To = to;
	}

	
	void setDeparture_Date(String date){
		this.Departure_Date = date;
	}

	
	void setPrice(int price){
	 this.Price = price;
	}


}
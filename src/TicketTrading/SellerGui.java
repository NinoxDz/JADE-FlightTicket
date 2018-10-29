package TicketTrading;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;


public class SellerGui extends JFrame {	
	
	private Seller myAgent;

	private JTextField titleFieldFrom,titleFieldTo, priceField;
	
	SellerGui(Seller a) {
		
		super(a.getLocalName());
		
		myAgent = a;
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(4, 2));
		p.add(new JLabel("From:"));
		titleFieldFrom = new JTextField(15);
		p.add(titleFieldFrom);
		
		
		p.add(new JLabel("To:"));
		titleFieldTo = new JTextField(15);
		p.add(titleFieldTo);
		
		
		p.add(new JLabel("Departure Date:"));
		SpinnerModel model1 = new SpinnerDateModel();
		JSpinner spinner = new JSpinner(model1);
		p.add(spinner);
		
		
		p.add(new JLabel("Price:"));
		priceField = new JTextField(15);
		p.add(priceField);
		
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Add");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					String From = titleFieldFrom.getText().trim();
					String To = titleFieldTo.getText().trim();
					String Departure_Date = spinner.getValue().toString();
					String price = priceField.getText().trim();
					
					myAgent.updateCatalogues(From,To,Departure_Date, Integer.parseInt(price));
					//titleField.setText("");
					priceField.setText("");
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(SellerGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}

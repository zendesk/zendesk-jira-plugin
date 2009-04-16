package it.org.zendesk_jira_plugin;

import javax.swing.JFrame;

import org.agilos.zendesk_jira_plugin.integrationtest.fixtures.NotificationFixture;

/*
 * Small application for bootstrap testing of the <code>NotificationFixture</code> class.
 */
public class TestGUI {
	
	private void createGUI() {
    	JFrame frame = new JFrame("TestGUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
	}  
        
    public static void main(String[] args) {
    	System.out.println("Starting TestGUI");
    	TestGUI demo = new TestGUI();
    	
    	try {
			NotificationFixture nf = new NotificationFixture();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
    	// Compose the Demo 
    	demo.createGUI();
    }
}

package Chatserver;

import javax.swing.*;

//Class to precise who is connected : Client or Server
public class ClientServer {
/**
 * this fuction gets the jswing up and let the user to log in to the chat,
 * firts the user need to connect the server and than to the client
 * 
 */
	
	
	public static void main(String [] args){
		
		Object[] selectioValues = { "Server","Client"};
		String initialSection = "Server";
		
		Object selection = JOptionPane.showInputDialog(null, "Log to server: ", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);
		if(selection.equals("Server")){
                   String[] arguments = new String[] {};
			new MultiThreadChatServerSync().main(arguments);
			JOptionPane.showMessageDialog(null, "connected", "MyChatApp", JOptionPane.OK_OPTION);
		}
		Object selection2 = JOptionPane.showInputDialog(null, "Log to : client", "MyChatApp", JOptionPane.QUESTION_MESSAGE, null, selectioValues, initialSection);
		if(selection2.equals("Client")){
			String IPServer = JOptionPane.showInputDialog("Enter the Server localhost");
                        String[] arguments = new String[] {IPServer};
			new ChatClient().main(arguments);
		}
		
	}

}

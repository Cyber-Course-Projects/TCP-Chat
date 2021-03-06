/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Chatserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author Dolev
 */


// For every client's connection we call this class
public class clientThread extends Thread{
	private String clientName = null;
	private DataInputStream is = null;
	PrintStream os = null;
	private Socket clientSocket = null;
	private final clientThread[] threads;
	private int maxClientsCount;
/**
 * client thread function is a function that build the Threads that moving from the user to the server
 * @param clientSocket this socket get the user information
 * @param threads = this socket is the action that user doing, using as a pipe
 */
	public clientThread(Socket clientSocket, clientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}
/**
 * ths function is responsebole for all the action the user is doing in the chat
 * 
 */
	public void run() {
		int maxClientsCount = this.maxClientsCount;
		clientThread[] threads = this.threads;

		try {
			/*
			 * Create input and output streams for this client.
			 */
			is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
			while (true) {
				os.println("Enter your name.");
				name = is.readLine().trim();
				if (name.indexOf('@') == -1) {
					break;
				} else {
					os.println("The name should not contain '@' character.");
				}
			}

			/* Welcome the new the client. */
			os.println("Welcome " + name
					+ " to our chat room.\nTo leave enter <disconnect> in a new line.");
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println("*** A new user " + name
								+ " entered the chat room !!! ***");
					}
				}
			}
			
			/* Start the conversation. */
			while (true) {
				String line = is.readLine();
				if (line.startsWith("<disconnect>")) {
					break;
				}
				/* Get the user log to the chat */
				if (line.startsWith("<get_users>")) {
					for (int i = 0; i < maxClientsCount; i++) {
						if (threads[i] != null && threads[i] != this
								&& threads[i].clientName != null) {
							os.println("The "+ i + "user is:" + threads[i].clientName);
						}
					}
				}
				/* If the message is private sent it to the given client. */
				if (line.startsWith("@")) {
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null) {
						words[1] = words[1].trim();
						if (!words[1].isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < maxClientsCount; i++) {
									if (threads[i] != null && threads[i] != this
											&& threads[i].clientName != null
											&& threads[i].clientName.equals(words[0])) {
										threads[i].os.println("<" + name + "> " + words[1]);
										/*
										 * Echo this message to let the client know the private
										 * message was sent.
										 */
										this.os.println(">" + name + "> " + words[1]);
										break;
									}
								}
							}
						}
					}
				} else {
					/* The message is public, broadcast it to all other clients. */
					synchronized (this) {
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null && threads[i].clientName != null) {
								threads[i].os.println("<" + name + "> " + line);
							}
						}
					}
				}
			}
			/**
			 * this function get the other users know that someone leaves
			 */
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this
							&& threads[i].clientName != null) {
						threads[i].os.println("*** The user " + name
								+ " is leaving the chat room !!! ***");
					}
				}
			}
			os.println("*** Bye " + name + " ***");

			/*
			 * Clean up. Set the current thread variable to null so that a new client
			 * could be accepted by the server.
			 */
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}

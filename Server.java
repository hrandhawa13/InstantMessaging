package textMessaging;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Server extends JFrame{
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server(){
		super("Instant Messager");
		userText = new JTextField();
		userText.setEditable(false);//not allowed to type in anything unless connected
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add( new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	private void sendMessage(String str) {
		try{
			output.writeObject("SERVER - " + str);
			output.flush();
			showMessage("\n SERVER - " + str);
		}
		catch( IOException e ){
			chatWindow.append(" \n ERROR: DUDE I CANT SEND THAT MESSAGE");
		}
	}
	private void showMessage ( final String str ){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(str);
					}
				}	
		);
		
	}
	public void startRunning(){
		try{
			server = new ServerSocket(6789,100);
			while( true ){
				try{
					waitForConnection();
					setupStreams();
					whileChatting();
				}
				catch(EOFException e ){
					showMessage("\n Server ended the connection!");
				}
				finally{
					closeCrap();
				}
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect ...\n");
		connection = server.accept();
		showMessage("Now Connected to " + connection.getInetAddress().getHostName() + "/" +connection.getInetAddress().getHostAddress());
	}
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup \n");
	}
	private void whileChatting()throws IOException {
		String message = "You are now connected!";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String)input.readObject();
				showMessage("\n" + message);
			}
			catch( ClassNotFoundException e ){
				showMessage("\n idk wtf that user sent");
			}
		}
		while(! message.equals("CLIENT - END"));
	}
	private void ableToType(final boolean b) {
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(b);
				}
			}
		);
		
	}
	private void closeCrap() {
		showMessage("\n Closing connections \n");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}
		catch( IOException e ){
			e.printStackTrace();
		}
		
	}
}

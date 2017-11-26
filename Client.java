package textMessaging;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message ="";
	private String serverIP;
	private Socket connection;
	
	public Client(String host ){
		super("Client messanger");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText,BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300,150);
		setVisible(true);
	}
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}
		catch( EOFException e ){
			showMessage("\n Client terminated the connection ");
		}
		catch (IOException e ){
			e.printStackTrace();
		}
		finally {
			closeCrap();
		}
	}
	private void connectToServer() throws IOException {
		showMessage(" Attempting connection.... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	private void setupStreams()throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are good to go\n");
	}
	private void whileChatting()throws IOException {
		ableToType(true);
		do{
			try{
				message =(String) input.readObject();
				showMessage("\n" + message);
			}
			catch(ClassNotFoundException e ){
				showMessage("\n Idk that bject type");
			}
		}
		while( !message.equals("SERVER - END"));
	}
	private void closeCrap(){
		showMessage("Closing the streams and socket");
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
	private void sendMessage( String str ){
		try{
			output.writeObject("CLIENT - " + str);
			output.flush();
			showMessage("\nCLIENT - " + str);
		}
		catch(IOException e){
			chatWindow.append("\n Something messed up sending message");
		}
	}
	private void showMessage(final String str){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append("\n " + str);
				}
			}	
		);
	}
	private void ableToType( final boolean b ){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(b);	
				}
			}	
		);
		
	}
}

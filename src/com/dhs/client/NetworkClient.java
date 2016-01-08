package com.dhs.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.dhs.dao.DataDao;
import com.dhs.dto.Data;
import com.dhs.dto.PersistenceConfiguration;

/**
 * A simple Swing-based client (Network Mode - Remote or Loopback) for the File
 * I/O server. It has a main frame window with a text field for entering
 * FileName and a textarea to see the Contents of the File
 */
public class NetworkClient {

	private BufferedReader in;
	private PrintWriter out;
	private JFrame frame = new JFrame("DHS Clinical System - Network Mode");
	private JTextField dataField = new JTextField(
			"Enter Absolute Path of File", 40);
	private JTextArea messageArea = new JTextArea(8, 60);
	private static String persistence;
	private static String fileName;

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Enter in the listener sends the
	 * textfield contents to the server.
	 */
	public NetworkClient() {

		// Layout GUI
		messageArea.setEditable(false);
		frame.getContentPane().add(dataField, "North");
		frame.getContentPane().add(new JScrollPane(messageArea), "Center");

		// Add Listeners
		dataField.addActionListener(new ActionListener() {
			/**
			 * Responds to pressing the enter key in the textfield by sending
			 * the contents of the text field to the server and displaying the
			 * response from the server in the text area.
			 */
			public void actionPerformed(ActionEvent e) {
				out.println(dataField.getText());
			}
		});
	}
	
	
	public static void startNetworkClient() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		PersistenceConfiguration persistenceConf = (PersistenceConfiguration) context
				.getBean("PersistenceConfiguration");
		initNetworkMode(persistenceConf.getPersistenceStrategy());
	}

	/**
	 * Implements the connection logic by prompting the end user for the
	 * server's IP address, connecting, setting up streams. The protocol says
	 * that the server sends Contents of specified File to the client
	 * immediately after establishing a connection.
	 */
	public void connectToServer() throws IOException {

		// Get the server address from a dialog box.
		String serverAddress = JOptionPane.showInputDialog(frame,
				"Enter IP Address of the Server:", "DHS Clinical System",
				JOptionPane.QUESTION_MESSAGE);

		// Make connection and initialize streams
		try {
			Socket socket = new Socket(serverAddress, 9898);
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (Exception e) {
			messageArea
					.append("Error in estabilishing Connection with Remote Server");
			System.out
					.println("Error in estabilishing Connection with Remote Server");
		}
		
		if (persistence.equalsIgnoreCase("console")) {
			String line = in.readLine();
			while (line != null) {
				messageArea.append(line + "\n");
				System.out.println(line + "\n");
				line = in.readLine();
			}
		} else if (persistence.equalsIgnoreCase("DB")) {
			Resource r = new ClassPathResource(
					"applicationContext.xml");
			BeanFactory factory = new XmlBeanFactory(r);
			DataDao dao = (DataDao) factory.getBean("d");
			String line = in.readLine();
			messageArea.append("Persisting contents of File in DB");
			Data data = new Data();
			data.setTimestamp((new Date()).toString());
			data.setFileName(dataField.getText());
			String content = "";
			while (line != null) {
				content += line;
				data.setContent(content);
				dao.saveOrUpdateData(data);
				line = in.readLine();
			}
		}
	}

	/**
	 * Runs the client application.
	 */
	public static void initNetworkMode(String strategy) throws Exception {
		persistence = strategy;
		NetworkClient client = new NetworkClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.pack();
		client.frame.setVisible(true);
		client.connectToServer();
	}

	/*****************Main Method for Testing********************/
	public static void main(String[] args) throws Exception {
		startNetworkClient();
	}
}
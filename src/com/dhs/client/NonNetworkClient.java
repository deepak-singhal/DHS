package com.dhs.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
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
 * A simple Swing-based client for the File I/O server. It has a main frame
 * window with a text field for entering FileName and a textarea to see the
 * Contents of the File.
 */
public class NonNetworkClient {

	private BufferedReader in;
	private PrintWriter out;
	private JFrame frame = new JFrame("DHS Clinical System - Non Network Mode");
	private JTextField dataField = new JTextField(
			"Enter Absolute Path of File", 40);
	private JTextArea messageArea = new JTextArea(8, 60);
	private static String persistence;

	private String fileName;

	/**
	 * Constructs the client by laying out the GUI and registering a listener
	 * with the textfield so that pressing Enter in the listener sends the
	 * textfield contents to the server.
	 */
	public NonNetworkClient() {

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
				try {
					setFileName(dataField.getText());
					File f = new File(fileName);
					if (f.exists() && !f.isDirectory()) {
						in = new BufferedReader(new FileReader(fileName));
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
							Data data = new Data();
							data.setTimestamp((new Date()).toString());
							data.setFileName(fileName);
							String content = "";
							String line = in.readLine();
							messageArea
									.append("Persisting contents of File in DB");
							while (line != null) {
								content += line;
								line = in.readLine();
							}
							data.setContent(content);
							dao.saveData(data);
						}

					} else {
						messageArea.append("File: " + fileName
								+ " does not exist on Local Machine");
						System.out.println("File: " + fileName
								+ " does not exist on Local Machine");
					}
				} catch (Exception ex) {
					System.out.println(ex.toString());
				}
			}
		});
	}
	
	public static void startNonNetworkClient() throws Exception{
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		PersistenceConfiguration persistenceConf = (PersistenceConfiguration) context
				.getBean("PersistenceConfiguration");
		initNonNetworkMode(persistenceConf.getPersistenceStrategy());
	}

	public void setFileName(String file) {
		fileName = file;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * Runs the client application.
	 */
	public static void initNonNetworkMode(String strategy) throws Exception {
		persistence = strategy;
		NonNetworkClient client = new NonNetworkClient();
		client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.frame.pack();
		client.frame.setVisible(true);
	}

	/*****************Main Method for Testing********************/
	public static void main(String[] args) throws Exception {
		startNonNetworkClient();
		}
}
package com.dhs.client;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dhs.client.NetworkClient;
import com.dhs.client.NonNetworkClient;
import com.dhs.dto.PersistenceConfiguration;

public class Client {
	JFrame myFrame = null;
	PersistenceConfiguration persistenceConf;

	public static void startClient(){
		(new Client()).modeSelect();
	}
	private void modeSelect() {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		persistenceConf = (PersistenceConfiguration) context
				.getBean("PersistenceConfiguration");

		int messageType = JOptionPane.QUESTION_MESSAGE;

		String[] options = { "Network Mode", "Non-Network Mode" };

		ImageIcon icon = new ImageIcon("images\\DHS.png");

		int mode = JOptionPane.showOptionDialog(myFrame,
				"Select the Mode to launch the DHS Clinical System",
				"DHS Clinical System", 0, messageType, icon, options, "");

		if (mode == 0) {
			try {
				NetworkClient.initNetworkMode(persistenceConf
						.getPersistenceStrategy());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (mode == 1) {
			try {
				NonNetworkClient.initNonNetworkMode(persistenceConf
						.getPersistenceStrategy());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/*****************Main Method********************/
	public static void main(String[] a) {
		startClient();
	}

}
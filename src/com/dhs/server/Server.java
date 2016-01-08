package com.dhs.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server program which accepts requests from clients to send File Contents.
 * When clients connect, a new thread is started to handle an interactive dialog
 * in which the client sends in a string and the server thread sends back the
 * contents of specified file.
 * 
 * The program runs in an infinite loop, so shutdown is platform dependent. If
 * you ran it from a console window with the "java" interpreter, Ctrl+C
 * generally will shut it down.
 */
public class Server {
	/**
	 * Application method to run the server runs in an infinite loop listening
	 * on port 9898. When a connection is requested, it spawns a new thread to
	 * do the servicing and immediately returns to listening. The server keeps a
	 * unique client number for each client that connects just to show
	 * interesting logging messages. It is certainly not necessary to do this.
	 */
	public static void startServer() throws IOException{
		System.out.println("DHS Server Started ...");
		int clientNumber = 0;
		ServerSocket listener = new ServerSocket(9898);
		try {
			while (true) {
				new Filer(listener.accept(), clientNumber++).start();
			}
		} finally {
			listener.close();
		}
	}

	/**
	 * A private thread to handle Client's requests on a particular socket.
	 */
	private static class Filer extends Thread {
		private Socket socket;
		private int clientNumber;

		public Filer(Socket socket, int clientNumber) {
			this.socket = socket;
			this.clientNumber = clientNumber;
			log("New connection with DHS client# " + clientNumber + " at "
					+ socket);
		}

		/**
		 * Services this thread's client by repeatedly reading strings and
		 * sending back the contents of the specified file.
		 */
		public void run() {
			BufferedReader readBuf = null;
			try {

				/**
				 * Decorate the streams so we can send characters and not just
				 * bytes. Ensure output is flushed after every newline.
				 */
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);

				log("Connection Estabilished with Client #" + clientNumber
						+ ".");

				/**
				 * Get messages from the client, line by line return them File
				 * Contents
				 */
				while (true) {
					String input = in.readLine();
					File f = new File(input);
					if (f.exists() && !f.isDirectory()) {
						readBuf = new BufferedReader(new FileReader(input));
						String line = readBuf.readLine();
						while (line != null) {
							out.println(line);
							line = readBuf.readLine();
						}
					} else {
						out.println("File: " + input
								+ " does not exist on Server");
					}
				}
			} catch (FileNotFoundException e) {
				log("" + e);
			} catch (IOException e) {
				log("Connection Closed");
			} finally {
				try {
					readBuf.close();
					socket.close();
				} catch (IOException e) {
					log("Couldn't close a socket, what's going on?");
				}
				log("Connection with client# " + clientNumber + " closed");
			}
		}

		/**
		 * Logs a simple message. In this case we just write the message to the
		 * server applications standard output.
		 */
		private void log(String message) {
			System.out.println(message);
		}
	}
	
	/*****************Main Method********************/
	public static void main(String[] args) throws Exception {
		startServer();
	}
}
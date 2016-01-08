DHS Medical Services
======================
Overall Architecture
====================
 Client sends a request with a path to any file on the server 
 Server reads the file and responds back with the content 
 Client outputs the content to the console

Major Components
================
The main architecture of the application must be a traditional client-server system. There are three key parts: the server-side, the client-side, and the network connection between the two. 

Non-Network Mode 
The program must be able to work in a non-networked mode. In this mode, the server and client must run in the same VM and must perform no networking, must not use loopback networking i.e: no “localhost” or “127.0.0.1”, and must not involve the serialization of anyobjects when communicating between the client and server components. The operating mode is selected using the single command line argument that is permitted. 

Network Communication Approach 
You must use a socket connection and define a protocol. Keep in mind that networking must be entirely bypassed in the non-network mode.

Package and Submission
======================

DHS Clinical System consists of these components: 

1. DHS-Client.jar - An executable JAR file, which will run both the stand-alone client and the network connected client. 
Furthermore, depends on User’s selection to connect either one of the modes thus 
popped up.

2. DHS-Server.jar - An executable server-specific JAR file, which will run the networked server.

3. DHS-1.0.jar - A common JAR file, which will contain code common (Complete Code) to both client and server applications: 
- src directory containing the source files for the project 
- Any original data file(s) the candidate used to test the program
- A very simple and basic document file summarizing the design choices called design.docx

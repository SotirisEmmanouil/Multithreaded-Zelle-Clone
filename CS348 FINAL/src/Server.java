import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.*;
import java.io.*; 

public class Server{ 
	
	//initialize socket and input stream 
	private SSLServerSocket server;
	private SSLSocket socket;
	private volatile boolean isRunning;
    private final int port;
    String userName;
    private static Map<String, Double> balances = new HashMap<>();		//hash map to hold user balances and user names
	private DataOutputStream out;
    
	//keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 365 -keystore server_keystore.jks
	//keytool -exportcert -alias server -file server_certificate.cer -keystore server_keystore.jks
	//keytool -importcert -alias server -file server_certificate.cer -keystore client_truststore.jks
	
	
	// creates a server and connects it to the given port 
	public Server(int port) { 
        this.port = port;
        this.isRunning = true;
		// starts server and waits for a connection 
		try{ 
			// Here we load the server keystore and truststore
			char[] keyStorePass = "summer2023".toCharArray();
			KeyStore serverKeyStore =KeyStore.getInstance("JKS");
			FileInputStream serverKeyStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/server_keystore.jks");
			serverKeyStore.load(serverKeyStoreFile, keyStorePass);

            char[] trustStorePass = "summer2023".toCharArray();
			KeyStore trustStore = KeyStore.getInstance("JKS");
			FileInputStream trustStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/server_truststore.jks");
            trustStore.load(trustStoreFile, trustStorePass);

			//Here we create a KeyManagerFactory and a TrustManagerFactory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(serverKeyStore, keyStorePass);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

			// Create an SSL context with the server keystore and TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Create an SSL server socket
            SSLServerSocketFactory factory = context.getServerSocketFactory();
            server = (SSLServerSocket) factory.createServerSocket(port);
            server.setNeedClientAuth(true); // require client authentication

            System.out.println("Zelle Server started");
            System.out.println("Waiting for a client to join...\n\n");

            // Start listening for client connections in a separate thread
            Thread connectionListenerThread = new Thread(this::listenForConnections);
            connectionListenerThread.start();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException | 
            KeyStoreException | CertificateException | UnrecoverableKeyException e) {
            System.out.println(e);
            
            }
        }

        private void listenForConnections() {
            while (isRunning) {
                try {
                    // Accept the client connection
                	   
                    socket = (SSLSocket) server.accept();
                    DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                  
                    double balance;
                    
                    if((userName = in.readUTF()) != null && (balance = in.readDouble())!= 0.0) {
                    	 System.out.println(userName + " connected to the Zelle server");
                    	 balances.put(userName, balance);
                    	 System.out.println(userName+" has a balance of $"+balances.get(userName)+"\n\n"); 
                    }
    
                    // Start a new thread to handle the client communication
                    Thread clientThread = new Thread(() -> handleClient(socket));
                    clientThread.start();
    
                } catch (IOException e) {		//if unsuccesful, catch the error 
                    System.out.println("Error accepting client connection: " + e);
                }
            }
        }
    
        private void handleClient(SSLSocket clientSocket) {
            try {
                // Initialize input stream
                DataInputStream in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
    
                String reason = in.readUTF();
                double amount = in.readDouble();		//read input
                String receiver = in.readUTF(); 
                
        		out = new DataOutputStream(socket.getOutputStream()); 
     
                	
                System.out.println("Transaction reason: "+ reason);
                System.out.println("Amount being sent from user: "+userName+" to user: "+receiver+"is "+amount+"\n\n");
                    
                balances.put(userName, balances.get(userName) - amount);
                balances.put(receiver, balances.get(receiver) + amount);
                System.out.println("Transfer successful, sender: "+userName+", your new balance: $" + balances.get(userName)+"\n\n");
                System.out.println("Transfer successful, receiver: "+receiver+", your new balance: $" + balances.get(receiver)+"\n\n");
                   
                System.out.println("Transaction complete");
           
               
                balances.put(userName, 0.0);
                balances.put(receiver, 0.0);		//withdraw funds
              
                System.out.println("Funds withdrawn from "+ userName+" now has: "+balances.get(userName)+" on the Zelle Server");
                System.out.println("Funds withdrawn from "+ receiver+" now has: "+balances.get(receiver)+" on the Zelle Server");
                System.out.println("Clients disconnected"); 
                
                clientSocket.close();
                in.close();
    
            } catch (IOException e) {
                System.out.println("Error handling client: " + e);
            }
        }

        public void stop() {
            isRunning = false;
            try {
                server.close();
            } catch (IOException e) {
                System.out.println("Error while stopping server: " + e);
            }
        }
    
        public static void main(String args[]) {
            Server server = new Server(63426);		//start the server at the decided port number
        }
    }

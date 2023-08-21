import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;

import javax.net.ssl.*;
import java.io.*; 


public class Client{ 
	// We initialize our socket( tunnel )
	// and our input reader and output stream
	// we will take the input from the user
	// and send it to the socket using output stream
	private SSLSocket socket;
	private BufferedReader input;
	private DataOutputStream out;
	private DataInputStream in;
	//keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 365 -keystore client_keystore.jks
	//keytool -exportcert -alias server -file client_certificate.cer -keystore client_keystore.jks
	//keytool -importcert -alias server -file client_certificate.cer -keystore client_truststore.jks

	//keytool -exportcert -alias server -file server_certificate.cer -keystore client_keystore.jks
	//keytool -importcert -alias server -file server_certificate.cer -keystore client_truststore.jks
	//keytool -list -v -alias server -keystore server_keystore.jks


	// constructor that takes the IP Address and the Port for sender
	public Client(String address, int port, String user, String message, double amount, double balance, String senderUser) { 
		// we try to establish a connection 
		try { 
			// Load the client keystore (if required)
            char[] keystorePass = "summer2023".toCharArray();
            KeyStore clientKeyStore = KeyStore.getInstance("JKS");
            FileInputStream clientKeyStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/client_keystore.jks");
            clientKeyStore.load(clientKeyStoreFile, keystorePass);

			char[] trustStorePass = "summer2023".toCharArray();
            KeyStore trustStore = KeyStore.getInstance("JKS");
            FileInputStream trustStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/client_truststore.jks");
            trustStore.load(trustStoreFile, trustStorePass);

			// Create a KeyManagerFactory with the client keystore
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientKeyStore, keystorePass);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

			// Create an SSL context with the client keystore and TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			// Create an SSL socket and connect to the server
            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(address, port);
            
			System.out.println("Connected"); 
			System.out.println("Money will be sent!"); 
			// we 'ready' the input reader 
			input = new BufferedReader(new InputStreamReader(System.in));
			// and the output that is connected to the Socket
			out = new DataOutputStream(socket.getOutputStream()); 
			
			Transaction transaction = new Transaction(message, amount);		//transaction object instantiated with appropriate parameters
			
			out.writeUTF(user);
			out.writeDouble(balance);
			out.writeUTF(transaction.getMessage());
			out.writeDouble(transaction.getAmount());		
			out.writeUTF(senderUser);
		
		
		} 
	    
		catch(IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException i) { 
			System.out.println(i); 
		} 
		System.out.println("Closing connection");
		
		// close the connection 
		try { 
			input.close(); 
			out.close(); 
			socket.close(); 
		} 
		catch(IOException i) { 
			System.out.println(i); 
		} 
	} 
	
	public Client(String address, int port, String user, double balance) { //client constructor for receiving
		// we try to establish a connection 
		try { 
			// Load the client keystore (if required)
            char[] keystorePass = "summer2023".toCharArray();
            KeyStore clientKeyStore = KeyStore.getInstance("JKS");
            FileInputStream clientKeyStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/client_keystore.jks");
            clientKeyStore.load(clientKeyStoreFile, keystorePass);

			char[] trustStorePass = "summer2023".toCharArray();
            KeyStore trustStore = KeyStore.getInstance("JKS");
            FileInputStream trustStoreFile = new FileInputStream("/Users/sotirisemmanouil/eclipse-workspace/Democode/src/client_truststore.jks");
            trustStore.load(trustStoreFile, trustStorePass);

			// Create a KeyManagerFactory with the client keystore
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(clientKeyStore, keystorePass);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);

			// Create an SSL context with the client keystore and TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

			// Create an SSL socket and connect to the server
            SSLSocketFactory factory = context.getSocketFactory();
            socket = (SSLSocket) factory.createSocket(address, port);
            
			System.out.println("Connected"); 
			System.out.println("Money will be received as soon as its sent!"); 
			// we 'ready' the input reader 
			input = new BufferedReader(new InputStreamReader(System.in));
			// and the output that is connected to the Socket
			out = new DataOutputStream(socket.getOutputStream()); 
			out.writeUTF(user);
			out.writeDouble(balance);
		} 
	    
		catch(IOException | NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException i) { 
			System.out.println(i); 
		} 
		System.out.println("Closing connection");

		// close the connection 
		try { 
			input.close(); 
			out.close(); 
			socket.close(); 
		} 
		catch(IOException i) { 
			System.out.println(i); 
		} 
	} 
	
	
	public static void main(String args[]) { 
		
		Scanner scan = new Scanner(System.in);
		String passwordRe = "1223", passwordIn = null, user, senderUser, message = null, decider;
		int selection = 0;
		double balance = 0, amount;
		
		System.out.println("|-----------------------------|");
		System.out.println("|------Welcome to Zelle-------|");
		System.out.println("|-----------------------------|\n");
		
	    System.out.println("ENTER YOUR USERNAME: ");
		    user = scan.nextLine();
				   
		do {
			System.out.println("ENTER ZELLE SERVER PASSWORD: ");
		    passwordIn = scan.nextLine();
		   
		    if(!passwordIn.equals(passwordRe)) {
		      System.out.println("Incorrect password!");
		    	}
		    
			} while(!passwordIn.equals(passwordRe));
		
		System.out.println("ENTER YOUR BALANCE: ");
	    balance = scan.nextDouble();
	    scan.nextLine();
	   
	    System.out.println("ARE YOU SENDING MONEY? ENTER YES FOR SENDING OR NO FOR RECEIVING: ");
	    decider = scan.nextLine();
	   
	    if (decider.equalsIgnoreCase("Yes")) {
	    System.out.println("ENTER THE USERNAME OF THE RECEIVER: ");
	    senderUser = scan.nextLine();
	    
	    System.out.println("ENTER THE AMOUNT YOU WANT TO SEND: ");
	    amount = scan.nextDouble();
	    scan.nextLine();
	    
	    if(amount > balance) {
	    	
	     System.out.println("Payment exceeds your balance! Try new payment:");
	     amount = scan.nextDouble();
	     scan.nextLine();
	     
	         }
	   
        else {
        	
	    System.out.println("ENTER THE TRANSACTION DETAILS, THIS IS REQUIRED: ");	
	    message = scan.nextLine();
		//"172.20.10.2"
		Client client = new Client("127.0.0.1", 63423, user, message, amount, balance, senderUser); 
	   
        	}
	    		}  //end decider if for sending
	    else {			
	    //if receiving 
	    Client client = new Client("127.0.0.1", 63423, user, balance); 
	    	
	    	
	    }
	    	
	    }
	} 

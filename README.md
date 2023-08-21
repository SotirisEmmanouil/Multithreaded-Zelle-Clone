# Multithreaded-Zelle-Clone

A multithreaded Java server, which establishes a secure TCP connection with SSL/TLS encryption with clients, and allows users to send money
to each other. 

## How it works:

 `Server.java` must be running first. Clients then begin connecting, either acting as a receiver or a sender. The client code is located in the `Client.java` 
 class. Users will be asked to input their balance, username, the amount they are sending, the username of the receiver, and the reason for the transaction. Once they input this information, an SSL 
 handshaking process occurs by exchanging certificates between client and server. If the certificates match, a secure, encrypted connection is established with the server and the transaction occurs. 
 A hashmap holds the total balance of the users. The transaction is created via a custom object, found in the `transaction.java` class. 

## What I learned:

<ul>
  <li>Working with threads</li>
  <li>Computer Networking</li>
   <li>Creating a TCP server</li>
   <li>SSL encryption</li>
</ul>


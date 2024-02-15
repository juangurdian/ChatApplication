import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code ChatServer} class is responsible for setting up and managing the server-side 
 * functionality of a chat application. It handles client connections, message broadcasting, 
 * and private messaging.
 */

public class ChatServer {

    private ServerSocket serverSocket;
    private final Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
/**
     * Starts the server to listen for incoming client connections on the specified port.
     * Accepts new client connections, creates a handler for each, and starts a new thread for them.
     *
     * @param port The port number on which the server will listen for connections.
     */
    public void start(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
    
            while (true) { // Continuously listen for new client connections
                try {
                    Socket clientSocket = serverSocket.accept(); // Accept a new client connection
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clientHandlers.add(clientHandler);
                    new Thread(clientHandler).start(); // Start a new thread for each client
                } catch (IOException e) {
                    System.err.println("Error handling client connection: " + e.getMessage());
                    // Optionally, break or continue based on the nature of the error
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server on port " + port + ": " + e.getMessage());
        }
    }
    

    // Call this method after a client has connected or disconnected
    private String getUserListAsString() {
        return clientHandlers.stream()
                .map(handler -> handler.getClientName() + ":" + handler.getSocket().getInetAddress().getHostAddress())
                .collect(Collectors.joining(","));
    }

    /**
     * Sends the updated list of users to all connected clients.
     */
    // This method sends the updated user list
    private void sendUserListUpdate() {
        String userListMessage = "USERLIST:" + getUserListAsString();
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(userListMessage);
        }
    }

    /**
     * Broadcasts a message to all clients except the sender.
     *
     * @param message The message to be broadcasted.
     * @param sender  The sender of the message to avoid echoing the message back.
     */

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler handler : clientHandlers) {
            if (handler != sender) {
                handler.sendMessage(message);
            }
        }
    }
    
    /**
     * Removes a client handler from the set of active handlers and updates the user list.
     *
     * @param clientHandler The client handler to remove.
     */

    void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        sendUserListUpdate(); // Update user list when a client disconnects
    }

    /**
     * Sends a private message to a specific user.
     *
     * @param message       The message to be sent.
     * @param recipientNick The nickname of the recipient user.
     * @param sender        The sender's client handler.
     */

    public void sendPrivateMessage(String message, String recipientNick, ClientHandler sender) {
        boolean messageSent = false;
        for (ClientHandler handler : clientHandlers) {
            if (handler.getClientName().equals(recipientNick)) {
                // Prepare the private message
                String formattedMessage = "PRIVATE:" + sender.getClientName() + ":" + message;
                handler.sendMessage(formattedMessage);
                messageSent = true;
                break;
            }
        }
        // Optionally, you can send a confirmation or error back to the sender
        if (messageSent) {
            sender.sendMessage("Message sent to " + recipientNick);
        } else {
            sender.sendMessage("User " + recipientNick + " not found.");
        }
    }
    
    /**
     * The {@code ClientHandler} class handles interaction with an individual client, 
     * including sending and receiving messages.
     */

    private class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientName;
        private final ChatServer server;
    /**
         * Constructs a handler for an individual client.
         *
         * @param socket The socket connected to the client.
         * @param server The instance of {@code ChatServer}.
         */
        public ClientHandler(Socket socket, ChatServer server) {
            this.clientSocket = socket;
            this.server = server;
        }
    
    /**
         * Listens for messages from the client and processes them.
         */
        public void run() {
           // String inputMessage = " has joined";
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    
                // Handle the nickname message
                String nicknameMsg = in.readLine();
                if (nicknameMsg != null && nicknameMsg.startsWith("NICKNAME:")) {
                    clientName = nicknameMsg.substring(9);
                    server.broadcastMessage(clientName + " has joined", null);
                    sendUserListUpdate(); // Send updated user list when a new client connects
                }
    
                String inputLine;
    while ((inputLine = in.readLine()) != null) {
        if ("DISCONNECT".equalsIgnoreCase(inputLine)) {
            break; // Exit the loop and proceed to closing connections
        }
        
        // Handling private messages
        if (inputLine.startsWith("PRIVATE:")) {
            String[] parts = inputLine.split(":", 3); // Split into "PRIVATE", recipient, and message
            if (parts.length == 3) {
                String recipient = parts[1];
                String privateMessage = parts[2];
                server.sendPrivateMessage(privateMessage, recipient, this);
            }
            // Do not broadcast this message, go to the next iteration of the loop
            continue;
        }
        
        // Handling public messages
        // Decrypt and broadcast the message to all clients including the sender
        if (inputLine.startsWith("MSG:")) {
            String encryptedMessage = inputLine.substring(4); // Remove the "MSG:" prefix
            inputLine = EncryptionUtil.decrypt(encryptedMessage);
        }
        
        server.broadcastMessage(clientName + ": " + inputLine, null); // Broadcast to all, including sender
    }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnections(); // Ensure this is called when the client disconnects
            }
        }

        // Add this method to the ChatServer class



        /**
         * Closes the connection to the client and cleans up resources.
         */
    
        private void closeConnections() {
            //String outputMessage = " has left";
            server.broadcastMessage(clientName + " has left", null);
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                server.removeClient(this); // This must be called after closing connections
            }
        }
    
        void sendMessage(String message) {
            out.println(message);
        }
    
        String getClientName() {
            return clientName;
        }
    
        Socket getSocket() {
            return clientSocket;
        }
    }

     /**
     * The main method to start the ChatServer.
     *
     * @param args Command-line arguments (unused).
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start(10100); // Use your port here
    }

    
}

import java.io.*;
import java.net.Socket;

/**
 * The {@code ChatModel} class manages the client-side network communication logic for the chat application.
 * It handles connecting to the server, sending messages, listening for incoming messages, and disconnecting.
 */

public class ChatModel {

    private Socket socket; // The socket for communicating with the server
    private PrintWriter writer; // To write messages to the server
    private BufferedReader reader; // To read messages from the server
    private final ChatListener listener; // The listener for various events
    private boolean isConnected; // To keep track of the connection status

    /**
     * Defines the listener interface for chat-related events.
     */

    public interface ChatListener {
        void onMessageReceived(String message);
        void onConnectionStatusChanged(boolean isConnected);
        void onUserListReceived(String userList);
    }

    /**
     * Constructs a {@code ChatModel} with the specified chat listener.
     *
     * @param listener The listener to handle chat events.
     */
    public ChatModel(ChatListener listener) {
        this.listener = listener;
    }

    /**
     * Connects to the chat server using the provided hostname and port and registers the client with the given nickname.
     *
     * @param hostname The server's hostname.
     * @param port     The server's port number.
     * @param nickname The nickname for the client.
     * @throws IOException if an I/O error occurs when opening the connection.
     */
    public void connect(String hostname, int port, String nickname) throws IOException {
        if (isConnected) return;

        socket = new Socket(hostname, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send the nickname as a separate message prefixed with "NICKNAME:"
        writer.println("NICKNAME:" + nickname);

        isConnected = true;
        listener.onConnectionStatusChanged(isConnected);

        new Thread(this::listenForMessages).start();
    }

    /**
     * Sends a private message to a specified recipient.
     *
     * @param message   The message to be sent.
     * @param recipient The nickname of the message recipient.
     */
    public void sendPrivateMessage(String message, String recipient) {
        if (isConnected && writer != null) {
            writer.println("PRIVATE:" + recipient + ":" + message);
        }
    }    

    /**
     * Listens for incoming messages from the server and handles them accordingly.
     */
    private void listenForMessages() {
        try {
            String incomingMessage;
            while ((incomingMessage = reader.readLine()) != null) {
                if (incomingMessage.startsWith("USERLIST:")) {
                    listener.onUserListReceived(incomingMessage.substring(9));
                } else {
                    listener.onMessageReceived(incomingMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    /**
     * Sends a message to the server.
     *
     * @param message The message to be sent.
     */
    public void sendMessage(String message) {
        if (isConnected && writer != null) {
            writer.println(message);
        }
    }

    /**
     * Disconnects from the server and cleans up resources.
     */
    public void disconnect() {
        try {
            if (isConnected && writer != null) {
                sendMessage("DISCONNECT");
            }
            closeResources();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isConnected = false;
            listener.onConnectionStatusChanged(isConnected);
        }
    }

    /**
     * Closes the socket and associated streams.
     */
    private void closeResources() {
        try {
            if (socket != null) socket.close();
            if (writer != null) writer.close();
            if (reader != null) reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the client is currently connected to the server.
     *
     * @return {@code true} if the client is connected, {@code false} otherwise.
     */
    public boolean isConnected() {
        return isConnected;
    }
}

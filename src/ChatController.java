import javax.swing.*;

/**
 * The {@code ChatController} class handles the interaction logic between the {@code ChatClientView} 
 * and {@code ChatModel}. It manages the actions triggered by the user interface and updates the view 
 * based on changes from the model.
 */

public class ChatController {
    /**
     * Constructs a ChatController with the specified view.
     * It initializes the model and sets up the chat listeners to handle messages, 
     * connection status changes, and user list updates.
     *
     * @param view The {@code ChatClientView} that this controller will interact with.
     */

    private final ChatClientView view;
    private final ChatModel model;

    public ChatController(ChatClientView view) {

        /**
     * Initializes the action listeners for the various buttons and fields in the view.
     */
        this.view = view;
        this.model = new ChatModel(new ChatModel.ChatListener() {
            @Override
            public void onMessageReceived(String message) {
                // Decrypt the message before displaying it if it's prefixed with "MSG:"
                final String displayMessage;
                if (message.startsWith("MSG:")) {
                    String encryptedMessage = message.substring(4); // Remove the "MSG:" prefix
                    displayMessage = EncryptionUtil.decrypt(encryptedMessage);
                } else {
                    displayMessage = message;
                }
                SwingUtilities.invokeLater(() ->
                        view.getPublicChatArea().append(displayMessage + "\n"));
            }
            


            @Override
            public void onConnectionStatusChanged(boolean isConnected) {
                SwingUtilities.invokeLater(() -> {
                    view.getStatusLabel().setText(isConnected ? "Connected" : "Disconnected");
                    view.getConnectButton().setEnabled(!isConnected);
                    view.getDisconnectButton().setEnabled(isConnected);
                });
            }

            @Override
            public void onUserListReceived(String userList) {
                SwingUtilities.invokeLater(() -> {
                    String[] usersArray = userList.split(",");
                    view.updateUserList(usersArray);
                });
            }
        });
        initializeController();
    }

    private void initializeController() {
        view.getSendButton().addActionListener(e -> sendMessage());
        view.getConnectButton().addActionListener(e -> connect());
        view.getDisconnectButton().addActionListener(e -> disconnect());
        view.getDecryptButton().addActionListener(e -> decryptMessage());
        view.getClearDecryptButton().addActionListener(e -> clearDecryptedMessage());
    }
/**
     * Sends a message typed by the user to other clients. It checks if the message should be private 
     * and/or encrypted before sending it through the model.
     */
    private void sendMessage() {
        String message = view.getMessageField().getText();
        String recipient = view.getPrivateRecipientField().getText(); // Get the recipient from the privateRecipientField
        boolean shouldEncrypt = view.getEncryptCheckBox().isSelected();
        
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check for a private message recipient
        if (!recipient.isEmpty()) {
            if (shouldEncrypt) {
                message = EncryptionUtil.encrypt(message);
            }
            // Send the private message through the model
            model.sendPrivateMessage(message, recipient);
        } else {
            // For public messages, prepend with "MSG:"
            if (shouldEncrypt) {
                message = EncryptionUtil.encrypt(message);
                message = "" + message;
            }
            
            model.sendMessage(message);
        }
        
        view.getMessageField().setText(""); // Clear the message field
        view.getPrivateRecipientField().setText(""); // Clear the recipient field
    }
/**
     * Clears the decrypted message label and field in the view.
     */
    private void clearDecryptedMessage() {
        view.getDecryptedMessageLabel().setText("");
        view.getEncryptedMessageField().setText("");
    }
/**
     * Decrypts a message entered by the user in the encrypted message field and updates the view.
     */
    private void decryptMessage() {
        String encryptedMessage = view.getEncryptedMessageField().getText();
        if (!encryptedMessage.isEmpty()) {
            String decryptedMessage = EncryptionUtil.decrypt(encryptedMessage);
            view.getDecryptedMessageLabel().setText(decryptedMessage);
        } else {
            JOptionPane.showMessageDialog(view.getFrame(), "Encrypted message cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
/**
     * Initiates a connection to the server with the user-provided nickname, server IP, and port.
     */
    private void connect() {
        String nickname = view.getNicknameField().getText().trim();
        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Nickname cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int serverPort = Integer.parseInt(view.getServerPortField().getText().trim());
        String serverIP = "localhost"; // For now, using localhost. This can be changed to a dynamic IP if needed.

        try {
            model.connect(serverIP, serverPort, nickname);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view.getFrame(), "Unable to connect: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        view.getDisconnectButton().setEnabled(true);
        view.getConnectButton().setEnabled(false);
    }
/**
     * Disconnects the client from the server and updates the view to reflect the change in connection status.
     */
    private void disconnect() {
        model.disconnect();
        view.getDisconnectButton().setEnabled(false);
        view.getConnectButton().setEnabled(true);
    }
}

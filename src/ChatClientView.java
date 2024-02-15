import javax.swing.*;
import java.awt.*;
/**
 * The {@code ChatClientView} class is responsible for initializing and managing
 * the Graphical User Interface (GUI) components of the chat client.
 * It creates and lays out all the components required for the user to interact with the chat application.
 */
public class ChatClientView {
    // Various private member variables representing the GUI components are omitted for brevity.

    /**
     * Constructs a new ChatClientView and initializes the GUI.
     * The constructor calls {@code initializeGUI()} to set up all components.
     */

    private JFrame frame;
    private JTextArea publicChatArea;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextField messageField, nicknameField, serverIPField, serverPortField, privateRecipientField, encryptedMessageField;
    private JButton sendButton, connectButton, disconnectButton, decryptButton, clearDecryptButton;
    private JLabel statusLabel, decryptedMessageLabel;
    private JCheckBox encryptCheckBox;

    public ChatClientView() {
        initializeGUI();
    }

    private void initializeGUI() {
        /**
     * Initializes the GUI components and layout of the chat client.
     * This includes setting up the main frame, chat area, user list, and input fields.
     */
        frame = new JFrame("Chat Client");
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Public Chat Area
        publicChatArea = new JTextArea();
        publicChatArea.setEditable(false);
        JScrollPane publicChatScrollPane = new JScrollPane(publicChatArea);

        // User List Area
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userListScrollPane = new JScrollPane(userList);
        userListScrollPane.setPreferredSize(new Dimension(200, 0));

        // Setup the main chat and user list panels
        JSplitPane chatSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, publicChatScrollPane, userListScrollPane);
        chatSplitPane.setResizeWeight(0.75);
        frame.add(chatSplitPane, BorderLayout.CENTER);

        // Top Panel for connection settings
        JPanel topPanel = createTopPanel();
        frame.add(topPanel, BorderLayout.NORTH);

        // Bottom Panel for sending messages
        JPanel bottomPanel = createBottomPanel();
        frame.add(bottomPanel, BorderLayout.SOUTH);
        

        frame.setSize(1200, 600);
        frame.setVisible(true);
    }

    /**
     * Creates the top panel with connection settings including nickname, server IP, and server port fields.
     * It also contains the connect and disconnect buttons, and a status label indicating the connection status.
     * 
     * @return A {@code JPanel} that contains the connection settings components.
     */

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        nicknameField = new JTextField("Anonymous", 10);
        serverIPField = new JTextField("localhost", 10);
        serverPortField = new JTextField("10100", 5);
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        statusLabel = new JLabel("Disconnected");

        topPanel.add(new JLabel("Nickname:"));
        topPanel.add(nicknameField);
        topPanel.add(new JLabel("Server IP:"));
        topPanel.add(serverIPField);
        topPanel.add(new JLabel("Port:"));
        topPanel.add(serverPortField);
        topPanel.add(connectButton);
        topPanel.add(disconnectButton);
        topPanel.add(statusLabel);

        return topPanel;
    }
/**
     * Creates the bottom panel with message input fields, send button, encryption checkbox,
     * and fields for encrypted messages and decryption.
     * 
     * @return A {@code JPanel} that contains the components for message input and encryption.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        messageField = new JTextField(30);
        privateRecipientField = new JTextField(6); // Initialize this field
        sendButton = new JButton("Send");
        encryptCheckBox = new JCheckBox("Encrypt Message");
        encryptCheckBox.setSelected(false);
        encryptedMessageField = new JTextField(7);
        decryptButton = new JButton("Decrypt");
        clearDecryptButton = new JButton("Clear Decrypt");
        decryptedMessageLabel = new JLabel();
        bottomPanel.add(new JLabel("Message:"));
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);
        bottomPanel.add(new JLabel("Private To:")); // You may want to conditionally add this
        bottomPanel.add(privateRecipientField); // And this, based on whether private messaging is enabled
        bottomPanel.add(encryptCheckBox);
        bottomPanel.add(encryptedMessageField);
        bottomPanel.add(decryptButton);
        bottomPanel.add(clearDecryptButton);
        bottomPanel.add(decryptedMessageLabel);
        return bottomPanel;
    }
    
/**
     * Updates the user list display with the given array of user names.
     * 
     * @param users An array of user names to display in the user list.
     */
    public void updateUserList(String[] users) {
        userListModel.removeAllElements();
        for (String user : users) {
            userListModel.addElement(user);
        }
    }

    // Inside ChatClientView class, add this getter for the privateRecipientField
public JTextField getPrivateRecipientField() {
    return privateRecipientField;
}

    // Getters for the controller to use
    public JTextArea getPublicChatArea() { return publicChatArea; }
    public JTextField getMessageField() { return messageField; }
    public JTextField getNicknameField() { return nicknameField; }
    public JTextField getServerIPField() { return serverIPField; }
    public JTextField getServerPortField() { return serverPortField; }
    public JButton getSendButton() { return sendButton; }
    public JButton getConnectButton() { return connectButton; }
    public JButton getDisconnectButton() { return disconnectButton; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JFrame getFrame() { return frame; }
    public JTextField getEncryptedMessageField() { return encryptedMessageField; }
    public JButton getDecryptButton() { return decryptButton; }
    public JButton getClearDecryptButton() { return clearDecryptButton; }
    public JLabel getDecryptedMessageLabel() { return decryptedMessageLabel; }

    public AbstractButton getEncryptCheckBox() {
        return encryptCheckBox;
    }
}

import javax.swing.SwingUtilities;

/**
 * The mainApp class serves as the entry point for the Chat Client Application.
 * It uses SwingUtilities.invokeLater to ensure that the chat client's user interface
 * is created and updated on the Event Dispatch Thread, which is the proper practice
 * for Swing applications to maintain thread safety.
 */

public class mainApp {

    /**
     * The main method is the entry point of the application.
     * It creates the chat client's view and controller on the Event Dispatch Thread.
     * 
     * @param args command-line arguments passed to the program (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClientView view = new ChatClientView();
            ChatController controller = new ChatController(view);
            // No need to pass the model to the controller, as it's instantiated within the controller
        });
    }
}

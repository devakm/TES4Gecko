package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Display a dialog from a worker thread
 */
public class WorkerDialog implements Runnable {

    /** Dialog closed */
    public static final int CLOSED_OPTION = -1;
    
    /** 'OK' response */
    public static final int OK_OPTION = 0;
    
    /** 'Yes' response */
    public static final int YES_OPTION = 0;
    
    /** 'No' response */
    public static final int NO_OPTION = 1;
    
    /** 'Yes to All' response */
    public static final int YES_TO_ALL_OPTION = 2;
    
    /** Confirmation dialog */
    private boolean confirmDialog;
    
    /** Parent window */
    private Component parent;
    
    /** Confirmation message */
    private String message;
    
    /** Dialog title */
    private String title;
    
    /** Option type */
    private int optionType;
    
    /** Message type */
    private int messageType;
    
    /** User selection */
    private int selection;
    
    /** Flag to include 'Yes to All' button */
    private boolean yesToAll;
    
    /**
     * Create a new message dialog instance
     *
     * @param       parent          Parent window
     * @param       message         Message
     * @param       title           Dialog title
     * @param       messageType     Message type
     */
    public WorkerDialog(Component parent, String message, String title, int messageType) {
        this.parent = parent;
        this.message = message;
        this.title = title;
        this.messageType = messageType;
        confirmDialog = false;
    }
    
    /**
     * Create a new confirmation dialog instance
     *
     * @param       parent          Parent window
     * @param       message         Confirmation message
     * @param       title           Dialog title
     * @param       optionType      Option type
     * @param       messageType     Message type
     * @param       yesToAll        TRUE if yes-to-all is allowed
     */
    public WorkerDialog(Component parent, String message, String title, int optionType, int messageType, boolean yesToAll) {
        this.parent = parent;
        this.message = message;
        this.title = title;
        this.optionType = optionType;
        this.messageType = messageType;
        this.yesToAll = yesToAll;
        confirmDialog = true;
    }
    
    /**
     * Display the confirmation dialog and get the selection
     */
    public void run() {
        if (confirmDialog) {
            if (yesToAll) {
                Object[] options = {"Yes", "No", "Yes to All"};
                selection = JOptionPane.showOptionDialog(parent, message, title, optionType, messageType, 
                                                         null, options, options[2]);
            } else {
                selection = JOptionPane.showConfirmDialog(parent, message, title, optionType, messageType);
            }
        } else {
            JOptionPane.showMessageDialog(parent, message, title, messageType);
            selection = OK_OPTION;
        }
    }
    
    /**
     * Return the user selection
     *
     * @return                      The selected option
     */
    public int getSelection() {
        return selection;
    }
    
    /**
     * Display the message dialog
     *
     * @param       parent          Parent window
     * @param       message         Message
     * @param       title           Dialog title
     * @param       messageType     Message type
     */
    public static void showMessageDialog(Component parent, String message, String title, int messageType) {
        int selection = CLOSED_OPTION;
        try {
            WorkerDialog messageDialog = new WorkerDialog(parent, message, title, messageType);
            SwingUtilities.invokeAndWait(messageDialog);
        } catch (InterruptedException exc) {
            Main.logException("Message dialog interrupted", exc);
        } catch (Throwable exc) {
            Main.logException("Exception while displaying message dialog", exc);
        }
    }
    
    /**
     * Display the confirmation dialog
     *
     * @param       parent          Parent window
     * @param       message         Confirmation message
     * @param       title           Dialog title
     * @param       optionType      Option type
     * @param       messageType     Message type
     * @param       yesToAll        TRUE if yes-to-all is allowed
     * @return                      The selected option
     */
    public static int showConfirmDialog(Component parent, String message, String title, int optionType, 
                                        int messageType, boolean yesToAll) {
        int selection = CLOSED_OPTION;
        try {
            WorkerDialog confirmDialog = new WorkerDialog(parent, message, title, optionType, messageType, yesToAll);
            SwingUtilities.invokeAndWait(confirmDialog);
            selection = confirmDialog.getSelection();
        } catch (InterruptedException exc) {
            Main.logException("Confirmation dialog interrupted", exc);
        } catch (Throwable exc) {
            Main.logException("Exception while displaying confirmation dialog", exc);
        }
        
        return selection;
    }
}

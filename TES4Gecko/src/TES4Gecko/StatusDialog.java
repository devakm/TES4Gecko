package TES4Gecko;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Informational panel displayed while processing an asynchronous function.
 */
public class StatusDialog extends JDialog implements ActionListener {
    
    /** Parent frame */
    private Component parent;
    
    /** Worker task */
    private Thread worker;
    
    /** Message text */
    private JLabel messageText;
    
    /** Progress bar */
    private JProgressBar progressBar;
    
    /** Function status */
    private int status = -1;
    
    /** Deferred status message */
    private String deferredText;
    
    /** Deferred progress */
    private int deferredProgress;
    
    /** 
     * Create a new instance of StatusDialog
     *
     * @param       parent          Parent frame
     * @param       text            Status message
     * @param       title           Dialog title
     */
    public StatusDialog(JFrame parent, String text, String title) {
        super(parent, title, true);
        this.parent = parent;
        initFields(text);
    }
    
    /**
     * Create a new instance of StatusDialog
     *
     * @param       parent          Parent dialog
     * @param       text            Status message
     * @param       title           Dialog title
     */
    public StatusDialog(JDialog parent, String text, String title) {
        super(parent, title, true);
        this.parent = parent;
        initFields(text);
    }
    
    /**
     * Set up the dialog fields
     *
     * @param       text            Status message
     */
    private void initFields(String text) {
        
        //
        // Create the progress bar using a scale of 0 to 100
        //
        JPanel progressPane = new JPanel();
        progressPane.setLayout(new BoxLayout(progressPane, BoxLayout.Y_AXIS));
        progressPane.add(Box.createVerticalStrut(15));
        messageText = new JLabel(text);
        progressPane.add(messageText);
        progressPane.add(Box.createVerticalStrut(15));
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressPane.add(progressBar);
        progressPane.add(Box.createVerticalStrut(15));

        //
        // Create the Cancel button
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setOpaque(false);
        JButton button = new JButton("Cancel");
        button.setActionCommand("cancel");
        button.addActionListener(this);
        buttonPane.add(button);
        
        //
        // Set the dialog content pane
        //
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPane.add(progressPane);
        contentPane.add(Box.createVerticalStrut(10));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
    }
    
    /**
     * Set the worker task.  The worker task will be interrupted if the user
     * presses the 'Cancel' button.
     *
     * @param       worker          The worker task
     */
    public void setWorker(Thread worker) {
        this.worker = worker;
    }

    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            if (action.equals("cancel") && worker != null) {
                worker.interrupt();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);            
        }
    }
    
    /**
     * Show the dialog.  Control will not return to the caller until the closeDialog()
     * method is called.  This method must be called on the event dispatch thread.
     *
     * @return                      1 if the function completed successfully and 0 if the
     *                              function completed unsuccessfully
     */
    public int showDialog() {
        pack();
        setLocationRelativeTo(parent);
        
        while (status == -1)
            setVisible(true);
        
        return status;
    }
    
    /**
     * Get the worker status
     *
     * @return                      1 if the function completed successfully, 0 if the
     *                              function completed unsuccessfully, and -1 if the
     *                              worker is still active.
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Close the dialog.  This method can be called on the event dispatch
     * thread or on a worker thread.
     *
     * @param       completed       TRUE if the function completed successfully
     */
    public void closeDialog(boolean completed) {
        status = (completed ? 1 : 0);
        if (SwingUtilities.isEventDispatchThread()) {
            setVisible(false);
            dispose();
        } else {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    setVisible(false);
                    dispose();
                }
            });
        }
    }
    
    /**
     * Update the status message and reset the progress bar.  
     * This method can be called on the event dispatch thread or on a worker thread.
     *
     * @param       text            Status message
     */
    public void updateMessage(String text) {
        if (SwingUtilities.isEventDispatchThread()) {
            messageText.setText(text);
            progressBar.setValue(0);
            pack();
            setLocationRelativeTo(parent);
        } else {
            deferredText = text;
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    messageText.setText(deferredText);
                    progressBar.setValue(0);
                    pack();
                    setLocationRelativeTo(parent);
                }
            });
        }
    }
    
    /**
     * Update the progress bar.  This method can be called on the
     * event dispatch thread or on a worker thread.
     *
     * @param       progress        The current progress (0-100)
     */
    public void updateProgress(int progress) {
        if (SwingUtilities.isEventDispatchThread()) {
            progressBar.setValue(progress);
        } else {
            deferredProgress = progress;
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(deferredProgress);
                }
            });
        }
    }
}

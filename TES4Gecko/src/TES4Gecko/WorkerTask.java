package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Worker task to perform a long-running function
 */
public class WorkerTask extends Thread {
    
    /** Status dialog */
    private StatusDialog statusDialog;
    
    /**
     * Create a new worker task
     *
     * @param       statusDialog    The status dialog for the asynchronous task
     */
    public WorkerTask(StatusDialog statusDialog) {
        super();
        this.statusDialog = statusDialog;
    }
    
    /**
     * Return the status dialog for the worker task
     *
     * @return                      The status dialog
     */
    public StatusDialog getStatusDialog() {
        return statusDialog;
    }

    /**
     * Return the parent dialog for the worker task.
     *
     * @return                      The parent dialog or null
     */
    public Component getParent() {
        return statusDialog;
    }
}

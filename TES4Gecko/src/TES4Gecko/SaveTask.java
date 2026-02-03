package TES4Gecko;

import java.io.*;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to save a plugin.
 */
public class SaveTask extends WorkerTask {
    
    /** Plugin file */
    private File pluginFile;
    
    /** Plugin */
    private Plugin plugin;

    /**
     * Create an instance of the save task
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     * @param       plugin          The plugin
     */
    public SaveTask(StatusDialog statusDialog, File pluginFile, Plugin plugin) {
        super(statusDialog);
        this.pluginFile = pluginFile;
        this.plugin = plugin;
        this.plugin.setPluginFile(this.pluginFile);
    }
    
    /**
     * Save a plugin
     *
     * @param       parent          The parent frame or dialog
     * @param       pluginFile      The plugin file
     * @param       plugin          The plugin
     * @return                      TRUE if the plugin was saved
     */
    public static boolean savePlugin(Component parent, File pluginFile, Plugin plugin) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog;
        if (parent instanceof JFrame)
            statusDialog = new StatusDialog((JFrame)parent, "Saving plugin", "Save Plugin");
        else
            statusDialog = new StatusDialog((JDialog)parent, "Saving plugin", "Save Plugin");
        
        //
        // Create the worker task
        //
        SaveTask worker = new SaveTask(statusDialog, pluginFile, plugin);
        statusDialog.setWorker(worker);
        
        //
        // Start the worker thread and wait for completion
        //
        worker.start();
        statusDialog.showDialog();
        
        //
        // Display an error message if the save failed
        //
        boolean saved = (statusDialog.getStatus()==1 ? true : false);
        if (!saved) {
            JOptionPane.showMessageDialog(parent, "Unable to save "+pluginFile.getName(),
                                          "Save Plugin", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return saved;
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        try {
            plugin.store(this);
            completed = true;
        } catch (DataFormatException exc) {
            Main.logException("Compression Error", exc);
        } catch (IOException exc) {
            Main.logException("I/O Error", exc);
        } catch (InterruptedException exc) {
            WorkerDialog.showMessageDialog(getStatusDialog(), "Request canceled", "Interrupted", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Exception while saving plugin", exc);
        }                

        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

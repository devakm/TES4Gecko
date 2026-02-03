package TES4Gecko;

import java.io.*;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to load a plugin.
 */
public class LoadTask extends WorkerTask {
    
    /** Plugin file */
    private File pluginFile;
    
    /** Plugin */
    private Plugin plugin;

    /**
     * Create an instance of the load task
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     */
    public LoadTask(StatusDialog statusDialog, File pluginFile) {
        super(statusDialog);
        this.pluginFile = pluginFile;
    }
    
    /**
     * Load a plugin
     *
     * @param       parent          The parent frame or dialog
     * @param       pluginFile      The plugin file
     * @return                      The plugin or null if an error occurred
     */
    public static Plugin loadPlugin(Component parent, File pluginFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog;
        if (parent instanceof JFrame)
            statusDialog = new StatusDialog((JFrame)parent, "Loading plugin", "Load Plugin");
        else
            statusDialog = new StatusDialog((JDialog)parent, "Loading plugin", "Load Plugin");
        
        //
        // Create the worker task
        //
        LoadTask worker = new LoadTask(statusDialog, pluginFile);
        statusDialog.setWorker(worker);
        
        //
        // Start the worker thread and wait for completion
        //
        worker.start();
        statusDialog.showDialog();
        
        //
        // Display an error message if the load failed
        //
        if (statusDialog.getStatus() != 1) {
            worker.plugin = null;
            JOptionPane.showMessageDialog(parent, "Unable to load "+pluginFile.getName(),
                                          "Load Plugin", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return worker.plugin;
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        try {
            plugin = new Plugin(pluginFile);
            plugin.load(this);
            completed = true;
        } catch (PluginException exc) {
            Main.logException("Plugin Error", exc);
        } catch (DataFormatException exc) {
            Main.logException("Compression Error", exc);
        } catch (IOException exc) {
            Main.logException("I/O Error", exc);
        } catch (InterruptedException exc) {
            WorkerDialog.showMessageDialog(getStatusDialog(), "Request canceled", "Interrupted", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Exception while loading plugin", exc);
        }

        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

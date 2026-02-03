package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Create the record tree for a plugin
 */
public class CreateTreeTask extends WorkerTask {

    /** Plugin file */
    private File pluginFile;
    
    /** Plugin */
    private Plugin plugin;
    
    /** Plugin node */
    private PluginNode pluginNode;
    
    /** 
     * Create a new instance of CreateTreeTask 
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     */
    public CreateTreeTask(StatusDialog statusDialog, File pluginFile) {
        super(statusDialog);
        this.pluginFile = pluginFile;
    }

    /**
     * Create the plugin tree
     *
     * @param       parent          The parent frame or dialog
     * @param       pluginFile      The plugin file
     * @return                      The plugin node or null if unable to build tree
     */
    public static PluginNode createTree(Component parent, File pluginFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog;
        if (parent instanceof JFrame)
            statusDialog = new StatusDialog((JFrame)parent, "Creating tree", "Create Tree");
        else
            statusDialog = new StatusDialog((JDialog)parent, "Creating tree", "Create Tree");
        
        //
        // Create the worker task
        //
        CreateTreeTask worker = new CreateTreeTask(statusDialog, pluginFile);
        statusDialog.setWorker(worker);
        
        //
        // Start the worker thread and wait for completion
        //
        worker.start();
        statusDialog.showDialog();
        
        //
        // Display the completion message
        //
        if (statusDialog.getStatus() != 1) {
            worker.pluginNode = null;
            JOptionPane.showMessageDialog(parent, "Unable to create tree for "+pluginFile.getName(),
                                          "Create Patch", JOptionPane.INFORMATION_MESSAGE);
        }
        
        return worker.pluginNode;
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        try {
            
            //
            // Load the plugin
            //
            plugin = new Plugin(pluginFile);
            plugin.load(this);
        
            //
            // Create the tree
            //
            pluginNode = new PluginNode(plugin);
            pluginNode.buildNodes(this);

            //
            // All done
            //
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
            Main.logException("Exception while creating tree", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

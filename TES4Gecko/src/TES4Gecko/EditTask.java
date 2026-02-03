package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to rewrite a plugin file with a new description in the header record
 */
public class EditTask extends WorkerTask {
    
    /** Plugin file */
    private File inFile;
    
    /** Plugin information */
    private PluginInfo pluginInfo;

    /**
     * Create an edit instance
     *
     * @param       statusDialog    The status dialog
     * @param       inFile          The plugin file
     * @param       pluginInfo      The plugin information
     */
    public EditTask(StatusDialog statusDialog, File inFile, PluginInfo pluginInfo) {
        super(statusDialog);
        this.inFile = inFile;
        this.pluginInfo = pluginInfo;
    }

    /**
     * Rewrite the plugin file with the updated description
     *
     * @param       parent          The parent frame
     * @param       inFile          The plugin file
     * @param       pluginInfo      The plugin information
     */
    public static void editFile(JFrame parent, File inFile, PluginInfo pluginInfo) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Updating "+inFile.getName(), "Update Plugin");
        
        //
        // Create the worker task
        //
        EditTask worker = new EditTask(statusDialog, inFile, pluginInfo);
        statusDialog.setWorker(worker);
        
        //
        // Start the worker thread and wait for completion
        //
        worker.start();
        statusDialog.showDialog();
        
        //
        // Display the completion message
        //
        if (statusDialog.getStatus() == 1)
            JOptionPane.showMessageDialog(parent, "Updated "+inFile.getName(),
                                          "Update Plugin", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to update "+inFile.getName(),
                                          "Update Plugin", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        File outFile = new File(inFile.getParent()+Main.fileSeparator+"Gecko.tmp");
        RandomAccessFile in = null;
        FileOutputStream out = null;
        byte[] buffer = new byte[4096];
        boolean completed = false;

        try {

            //
            // Open the input and output files
            //
            if (!inFile.exists() || !inFile.isFile())
                throw new IOException("'"+inFile.getName()+"' does not exist");

            if (outFile.exists())
                outFile.delete();

            in = new RandomAccessFile(inFile, "r");
            out = new FileOutputStream(outFile);
            long fileSize = inFile.length();
            long processedCount = 0;
            int currentProgress = 0;

            //
            // Write the updated header to the output file
            //
            PluginHeader inHeader = new PluginHeader(inFile);
            inHeader.read(in);

            PluginHeader outHeader = new PluginHeader(outFile);
            outHeader.setRecordCount(inHeader.getRecordCount());
            outHeader.setMaster(inHeader.isMaster());
            outHeader.setMasterList(inHeader.getMasterList());
            outHeader.setVersion(pluginInfo.getVersion());
            outHeader.setCreator(pluginInfo.getCreator());
            outHeader.setSummary(pluginInfo.getSummary());
            outHeader.write(out);

            //
            // Copy the input file to the output file
            //
            while (true) {
                int count = in.read(buffer, 0, 4096);
                if (count < 0)
                    break;

                if (count > 0)
                    out.write(buffer, 0, count);

                if (interrupted())
                    throw new InterruptedException("Request canceled");
                
                processedCount += count;
                int newProgress = (int)((processedCount*100L)/fileSize);
                if (newProgress >= currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }

            //
            // Close the files and rename the temporary file
            //
            out.close();
            out = null;
            
            in.close();
            in = null;
            
            inFile.delete();
            outFile.renameTo(inFile);
            
            completed = true;
        } catch (PluginException exc) {
            Main.logException("Plugin Error", exc);
        } catch (IOException exc) {
            Main.logException("I/O Error", exc);
        } catch (InterruptedException exc) {
            WorkerDialog.showMessageDialog(getStatusDialog(), "Request canceled", "Interrupted", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Exception while updating plugin", exc);
        }

        //
        // Delete the temporary file if the copy failed
        //
        if (!completed) {
            try {
                if (out != null)
                    out.close();
                
                if (in != null)
                    in.close();
                
                if (outFile.exists())
                    outFile.delete();
            } catch (IOException exc) {
                Main.logException("I/O Error", exc);
            }
        }

        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

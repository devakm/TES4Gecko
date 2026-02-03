package TES4Gecko;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to convert a plugin file to a master file or a master file to a plugin file
 */
public class ConvertTask extends WorkerTask {
    
    /** Plugin file */
    private File inFile;
    
    /** Master file */
    private File outFile;

    /**
     * Create a conversion instance
     *
     * @param       statusDialog    The status dialog
     * @param       inFile          The input file
     * @param       outFile         The output file
     */
    public ConvertTask(StatusDialog statusDialog, File inFile, File outFile) {
        super(statusDialog);
        this.inFile = inFile;
        this.outFile = outFile;
    }

    /**
     * Copy the input file to the output file and set the master/plugin flag based on the
     * file extension
     *
     * @param       parent          The parent frame
     * @param       inFile          The input file
     * @param       outFile         The output file
     */
    public static void convertFile(JFrame parent, File inFile, File outFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, 
                                                     "Converting '"+inFile.getName()+"' to '"+outFile.getName()+"'", 
                                                     "Convert File");
        
        //
        // Create the worker task
        //
        ConvertTask worker = new ConvertTask(statusDialog, inFile, outFile);
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
            JOptionPane.showMessageDialog(parent, "'"+inFile.getName()+"' converted to '"+outFile.getName()+"'",
                                          "Convert File", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to convert "+inFile.getName(),
                                          "Convert File", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] buffer = new byte[4096];
        boolean completed = false;

        //
        // Create the output file
        //
        // We just need to copy the input file to the output file and set the first
        // flag byte in the TES4 record to 0x01 for a master file or to 0x00 for a
        // plugin file (as determined by the file extension)
        //
        try {
            int count;
            int flagValue;
            boolean headerSet = false;

            if (!inFile.exists() || !inFile.isFile())
                throw new IOException("'"+inFile.getName()+"' does not exist");

            if (outFile.exists())
                outFile.delete();

            String name = outFile.getName();
            int sep = name.lastIndexOf('.');
            if (name.substring(sep).equalsIgnoreCase(".esm"))
                flagValue = 1;
            else
                flagValue = 0;

            in = new FileInputStream(inFile);
            out = new FileOutputStream(outFile);
            long fileSize = inFile.length();
            long processedCount = 0;
            int currentProgress = 0;

            while (true) {
                count = in.read(buffer, 0, 4096);
                if (count < 0)
                    break;

                if (count > 0) {
                    if (!headerSet) {
                        if (count < 20)
                            throw new PluginException("'"+inFile.getName()+"' is not a TES4 file");

                        String type = new String(buffer, 0, 4);
                        if (!type.equals("TES4"))
                            throw new PluginException("'"+inFile.getName()+"' is not a TES4 file");

                        buffer[8] = (byte)flagValue;
                        headerSet = true;
                    }

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
            }

            //
            // Close the files
            //
            out.close();
            out = null;
            in.close();
            in = null;
            completed = true;
        } catch (PluginException exc) {
            Main.logException("Plugin Error", exc);
        } catch (IOException exc) {
            Main.logException("I/O Error", exc);
        } catch (InterruptedException exc) {
            WorkerDialog.showMessageDialog(getStatusDialog(), "Request canceled", "Interrupted", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Exception while converting file", exc);
        }

        //
        // Delete the output file if the copy failed
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

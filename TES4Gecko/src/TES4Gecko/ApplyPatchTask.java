package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to apply a patch file created by TES4Gecko
 */
public class ApplyPatchTask extends WorkerTask {
    
    /** Plugin file */
    private File pluginFile;
        
    /** Plugin */
    private Plugin plugin;
    
    /** Plugin form map */
    private Map<Integer, FormInfo> pluginMap;
    
    /** Plugin master list */
    private List<String> pluginMasterList;
    
    /** Plugin master count */
    private int pluginMasterCount;
    
    /** Patch file */
    private File patchFile;
        
    /** Patch */
    private Plugin patch;
    
    /** Patch form list */
    private List<FormInfo> patchList;
    
    /** Patch master list */
    private List<String> patchMasterList;
    
    /** Patch master count */
    private int patchMasterCount;
    
    /** Patch form adjustment */
    private FormAdjust patchFormAdjust;
    
    /**
     * Create a patch instance
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     * @param       patchFile       The patch file
     */
    public ApplyPatchTask(StatusDialog statusDialog, File pluginFile, File patchFile) {
        super(statusDialog);
        this.pluginFile = pluginFile;
        this.patchFile = patchFile;
    }

    /**
     * Apply a patch file
     *
     * @param       parent          The parent frame
     * @param       pluginFile      The plugin file
     * @param       patchFile       The patch file
     */
    public static void applyPatch(JFrame parent, File pluginFile, File patchFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Applying patch", "Apply Patch");
        
        //
        // Create the worker task
        //
        ApplyPatchTask worker = new ApplyPatchTask(statusDialog, pluginFile, patchFile);
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
            JOptionPane.showMessageDialog(parent, "Patch applied to "+pluginFile.getName(),
                                          "Apply Patch", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to apply patch to "+pluginFile.getName(),
                                          "Apply Patch", JOptionPane.INFORMATION_MESSAGE);
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
            pluginMap = plugin.getFormMap();
            pluginMasterList = plugin.getMasterList();
            pluginMasterCount = pluginMasterList.size();

            //
            // Load the patch
            //
            patch = new Plugin(patchFile);
            patch.load(this);
            patchList = patch.getFormList();
            patchMasterList = patch.getMasterList();
            patchMasterCount = patchMasterList.size();

            //
            // The plugin master list must match the patch master list
            //
            if (patchMasterCount != pluginMasterCount)
                throw new PluginException("The plugin master list does not match the patch master list");

            for (int i=0; i<patchMasterCount; i++)
                if (!pluginMasterList.get(i).equals(patchMasterList.get(i)))
                    throw new PluginException("The plugin master list does not match the patch master list");
            
            //
            // Build a null form adjustment
            //
            patchFormAdjust = new FormAdjust();

            //
            // Set the plugin creator and summary to the values from the patch
            //
            plugin.setCreator(patch.getCreator());
            plugin.setSummary(patch.getSummary());

            //
            // Set the plugin version to the higher version number
            //
            plugin.setVersion(Math.max(plugin.getVersion(), patch.getVersion()));

            //
            // Apply the patch
            //
            int patchCount = patchList.size();
            int processedCount = 0;
            int currentProgress = 0;
            getStatusDialog().updateMessage("Applying patch to "+pluginFile.getName());

            //
            // Process each record in the patch plugin
            //
            for (FormInfo patchInfo : patchList) {
                PluginRecord patchRecord = (PluginRecord)patchInfo.getSource();
                int formID = patchRecord.getFormID();
                Integer mapFormID = new Integer(formID);
                FormInfo pluginInfo = pluginMap.get(mapFormID);
                if (pluginInfo == null) {
                    
                    //
                    // The patch record doesn't exist in the plugin, so we will add it
                    //
                    plugin.copyRecord(patchRecord, patchFormAdjust);
                    if (Main.debugMode)
                        System.out.printf("Added %s record %s (%08X)\n", patchRecord.getRecordType(), 
                                          patchRecord.getEditorID(), formID);
                } else {
                    PluginRecord pluginRecord = (PluginRecord)pluginInfo.getSource();
                    String recordType = pluginRecord.getRecordType();
                    PluginGroup parentGroup = (PluginGroup)pluginRecord.getParent();
                    List<PluginRecord> recordList = parentGroup.getRecordList();
                    int index = recordList.indexOf(pluginRecord);
                    if (index >= 0) {
                        if (patchRecord.isDeleted()) {
                        
                            //
                            // The patch record is deleted, so we will remove the plugin record
                            //
                            plugin.removeRecord(pluginRecord);
                            if (Main.debugMode)
                                System.out.printf("Deleted %s record %s (%08X)\n", recordType, 
                                                  pluginRecord.getEditorID(), formID);
                        } else {
                            
                            //
                            // The plugin contains a matching record, so we will replace it with
                            // the patch record
                            //
                            pluginRecord = (PluginRecord)patchRecord.clone();
                            pluginRecord.setParent(parentGroup);
                            recordList.set(index, pluginRecord);
                            pluginInfo.setSource(pluginRecord);
                            if (Main.debugMode)
                                System.out.printf("Updated %s record %s (%08X)\n", recordType, 
                                                  pluginRecord.getEditorID(), formID);
                        }
                    }
                }
                
                //
                // Update the progress bar
                //
                processedCount++;
                int newProgress = (processedCount*100)/patchCount;
                if (newProgress >= currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }

            //
            // Save the patched plugin
            //
            plugin.store(this);
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
            Main.logException("Exception while applying patch", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

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
 * Task to clean a plugin
 */
public class CleanTask extends WorkerTask {
    
    /** Plugin file */
    private File pluginFile;
    
    /** Plugin */
    private Plugin plugin;
    
    /** Plugin group list */
    private List<PluginGroup> pluginGroupList;
    
    /** Plugin master list */
    private List<String> pluginMasterList;
    
    /** Current master */
    private Plugin master;
    
    /** Current master form map */
    private Map<Integer, FormInfo> masterFormMap;
    
    /** Current master count */
    private int masterCount;
    
    /** Current master index */
    private int masterIndex;
    
    /** Plugin has been modified */
    private boolean pluginModified = false;

    /** 
     * Create a new instance of CleanTask
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     */
    public CleanTask(StatusDialog statusDialog, File pluginFile) {
        super(statusDialog);
        this.pluginFile = pluginFile;
    }
    
    /**
     * Clean a plugin
     *
     * @param       parent          The parent frame
     * @param       pluginFile      The plugin file
     */
    public static void cleanPlugin(JFrame parent, File pluginFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Cleaning "+pluginFile.getName(), "Clean Plugin");
        
        //
        // Create the worker task
        //
        CleanTask worker = new CleanTask(statusDialog, pluginFile);
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
            JOptionPane.showMessageDialog(parent, pluginFile.getName()+" cleaned",
                                          "Clean Plugin", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to clean "+pluginFile.getName(),
                                          "Clean Plugin", JOptionPane.INFORMATION_MESSAGE);
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
            pluginGroupList = plugin.getGroupList();
            pluginMasterList = plugin.getMasterList();

            //
            // Compare the plugin to each master in the plugin master list
            //
            int pluginMasterCount = pluginMasterList.size();
            for (masterIndex=0; masterIndex<pluginMasterCount; masterIndex++) {
                String masterName = pluginMasterList.get(masterIndex);

                //
                // Load the master file as a plugin.  We need to do this because we need
                // the WRLD, CELL and DIAL records which are not included in the master
                // index file.
                //
                File masterFile = new File(Main.pluginDirectory+Main.fileSeparator+masterName);
                master = new Plugin(masterFile);
                master.load(this);
                masterFormMap = master.getFormMap();
                masterCount = master.getMasterList().size();
                
                //
                // Update the status message
                //
                getStatusDialog().updateMessage("Comparing '"+plugin.getName()+"' to '"+masterName+"'");
                int processedCount = 0;
                int currentProgress = 0;
                
                //
                // Process each top-level group in the plugin
                //
                for (PluginGroup pluginGroup : pluginGroupList) {
                    compareGroup(pluginGroup);
                    processedCount++;
                    int newProgress = (processedCount*100)/pluginGroupList.size();
                    if (newProgress >= currentProgress+5) {
                        currentProgress = newProgress;
                        getStatusDialog().updateProgress(currentProgress);
                    }
                }
            }

            //
            // Save the updated plugin if we made any changes
            //
            if (pluginModified)
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
            Main.logException("Exception while cleaning plugin", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
    
    /**
     * Compare the records in a group
     *
     * @param       group           The group to compare
     */
    private void compareGroup(PluginGroup pluginGroup) {
        List<PluginRecord> recordList = pluginGroup.getRecordList();
        
        //
        // Process subgroups first so we can successfully detect empty groups
        // when we process WRLD, CELL and DIAL records
        //
        for (PluginRecord pluginRecord : recordList) {
            if (pluginRecord instanceof PluginGroup)
                compareGroup((PluginGroup)pluginRecord);
        }
        
        //
        // Now process the records
        //
        int recordCount = recordList.size();
        for (int recordIndex=0; recordIndex<recordCount; recordIndex++) {
            PluginRecord pluginRecord = recordList.get(recordIndex);
            if (pluginRecord instanceof PluginGroup)
                continue;
                
            String recordType = pluginRecord.getRecordType();
            int formID = pluginRecord.getFormID();
            int modIndex = formID>>>24;
                
            //
            // Compare the record to the master record if it references the
            // current master.  A deleted record is not checked because it is
            // deleting the master record.
            //
            if (modIndex == masterIndex && !pluginRecord.isDeleted()) {
                int masterFormID = (formID&0x00ffffff) | (masterCount<<24);
                Integer formIndex = new Integer(masterFormID);
                FormInfo masterFormInfo = masterFormMap.get(formIndex);
                if (masterFormInfo == null) {
                    if (Main.debugMode)
                        System.out.printf("%s: Record %08X not found\n", master.getName(), masterFormID);
                } else {
                    PluginRecord masterRecord = (PluginRecord)masterFormInfo.getSource();
                    if (pluginRecord.isIdentical(masterRecord)) {
                        boolean ignoreRecord = true;

                        //
                        // The record is the same as the corresponding master record.  However,
                        // WRLD, CELL and DIAL records cannot be deleted if the associated group
                        // is non-empty.  If the group is empty, then we need to delete both
                        // the record and the group.  Otherwise, we need to keep the WRLD, CELL or
                        // DIAL record.
                        //
                        if (recordType.equals("WRLD") || recordType.equals("CELL") || recordType.equals("DIAL")) {
                            int groupIndex = recordIndex+1;
                            if (groupIndex < recordCount) {
                                PluginRecord cmpRecord = recordList.get(groupIndex);
                                if (cmpRecord instanceof PluginGroup) {
                                    PluginGroup cmpGroup = (PluginGroup)cmpRecord;
                                    if (cmpGroup.getGroupParentID() == pluginRecord.getFormID()) {
                                        cmpGroup.removeIgnoredRecords();
                                        if (!cmpGroup.isEmpty()) {
                                            ignoreRecord = false;
                                            if (Main.debugMode)
                                                System.out.printf("Keeping %s record %s (%08X)\n", recordType, 
                                                                  pluginRecord.getEditorID(), pluginRecord.getFormID());
                                        }
                                    }
                                }
                            }
                        }

                        //
                        // Ignore the plugin record since it is the same as the master record
                        //
                        if (ignoreRecord) {
                            pluginRecord.setIgnore(true);
                            pluginModified = true;
                            if (Main.debugMode)
                                System.out.printf("Ignoring %s record %s (%08X)\n",
                                                  recordType, pluginRecord.getEditorID(), pluginRecord.getFormID());
                        }
                    }
                }
            }
        }
    }
}

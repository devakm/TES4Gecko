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
 * Task to create a patch file
 */
public class CreatePatchTask extends WorkerTask {
    
    /** Base plugin file */
    private File baseFile;
    
    /** Base plugin form adjustment */
    private FormAdjust baseFormAdjust;
    
    /** Base plugin */
    private Plugin basePlugin;
    
    /** Modified plugin file */
    private File modifiedFile;
    
    /** Modified plugin form adjustment */
    private FormAdjust modifiedFormAdjust;
    
    /** Modified plugin */
    private Plugin modifiedPlugin;
        
    /** Patch file */
    private File patchFile;
        
    /** Patch plugin */
    private Plugin patchPlugin;
    
    /**
     * Create a patch instance
     *
     * @param       statusDialog    The status dialog
     * @param       baseFile        The base plugin file
     * @param       modifiedFile    The modified plugin file
     * @param       patchFile       The patch plugin file
     */
    public CreatePatchTask(StatusDialog statusDialog, File baseFile, File modifiedFile, File patchFile) {
        super(statusDialog);
        this.baseFile = baseFile;
        this.modifiedFile = modifiedFile;
        this.patchFile = patchFile;
    }

    /**
     * Create a patch file
     *
     * @param       parent          The parent frame
     * @param       baseFile        The base plugin file
     * @param       modifiedFile    The modified plugin file
     * @param       patchFile       The patch plugin file
     */
    public static void createPatch(JFrame parent, File baseFile, File modifiedFile, File patchFile) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Creating patch", "Create Patch");
        
        //
        // Create the worker task
        //
        CreatePatchTask worker = new CreatePatchTask(statusDialog, baseFile, modifiedFile, patchFile);
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
            JOptionPane.showMessageDialog(parent, "Patch created for "+baseFile.getName(),
                                          "Create Patch", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to create patch for "+baseFile.getName(),
                                          "Create Patch", JOptionPane.INFORMATION_MESSAGE);        
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        try {

            //
            // Load the base plugin
            //
            basePlugin = new Plugin(baseFile);
            basePlugin.load(this);
            List<FormInfo> baseList = basePlugin.getFormList();
            Map<Integer, FormInfo> baseMap = basePlugin.getFormMap();
            List<String> baseMasterList = basePlugin.getMasterList();
            baseFormAdjust = new FormAdjust();

            //
            // Load the modified plugin
            //
            modifiedPlugin = new Plugin(modifiedFile);
            modifiedPlugin.load(this);
            List<FormInfo> modifiedList = modifiedPlugin.getFormList();
            Map<Integer, FormInfo> modifiedMap = modifiedPlugin.getFormMap();
            List<String> modifiedMasterList = modifiedPlugin.getMasterList();
            modifiedFormAdjust = new FormAdjust();

            //
            // Both plugins must have the same master list
            //
            int masterCount = baseMasterList.size();
            if (masterCount != modifiedMasterList.size())
                throw new PluginException("The master list is not the same for both plugins");

            for (int i=0; i<masterCount; i++)
                if (!baseMasterList.get(i).equals(modifiedMasterList.get(i)))
                    throw new PluginException("The master list is not the same for both plugins");

            //
            // Initialize the patch plugin.  The patch version will be set the higher version
            // number from the base plugin or the modified plugin.
            //
            getStatusDialog().updateMessage("Creating patch for "+baseFile.getName());
            patchPlugin = new Plugin(patchFile, modifiedPlugin.getCreator(), modifiedPlugin.getSummary(), 
                                     modifiedMasterList);
            patchPlugin.setVersion(Math.max(basePlugin.getVersion(), modifiedPlugin.getVersion()));
            patchPlugin.createInitialGroups();

            //
            // Scan the modified plugin and copy new and changed records to the patch plugin
            //
            int formCount = modifiedList.size();
            int processedCount = 0;
            int currentProgress = 0;
            for (FormInfo formInfo : modifiedList) {
                PluginRecord record = (PluginRecord)formInfo.getSource();
                if (record != null) {
                    int formID = record.getFormID();
                    FormInfo baseInfo = baseMap.get(new Integer(formID));
                    if (baseInfo == null) {
                        patchPlugin.copyRecord(record, modifiedFormAdjust);
                    } else {
                        PluginRecord baseRecord = (PluginRecord)baseInfo.getSource();
                        if (!baseRecord.isIdentical(record))
                            patchPlugin.copyRecord(record, modifiedFormAdjust);
                    }
                }
                
                processedCount++;
                int newProgress = (processedCount*50)/formCount;
                if (newProgress >= currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }

            //
            // Scan the base plugin and create a delete record for each record that is not
            // present in the modified plugin
            //
            formCount = baseList.size();
            processedCount = 0;
            for (FormInfo formInfo : baseList) {
                PluginRecord record = (PluginRecord)formInfo.getSource();
                if (record != null) {
                    int formID = record.getFormID();
                    FormInfo modifiedInfo = modifiedMap.get(new Integer(formID));
                    if (modifiedInfo == null) {
                        PluginGroup patchGroup = patchPlugin.createHierarchy(record, baseFormAdjust);
                        int deletedFormID = baseFormAdjust.adjustFormID(formID);
                        String recordType = record.getRecordType();
                        String editorID = record.getEditorID();
                        PluginRecord deletedRecord = new PluginRecord(recordType, deletedFormID);
                        deletedRecord.setDelete(true);
                        deletedRecord.setParent(patchGroup);
                        patchGroup.getRecordList().add(deletedRecord);
                        FormInfo deletedFormInfo = new FormInfo(deletedRecord, recordType, deletedFormID, editorID);
                        deletedFormInfo.setParentFormID(patchGroup.getGroupParentID());
                        patchPlugin.getFormList().add(deletedFormInfo);
                        patchPlugin.getFormMap().put(new Integer(deletedFormID), deletedFormInfo);
                        if (Main.debugMode)
                            System.out.printf("%s: Deleted %s record %s (%08X)\n",
                                              patchFile.getName(), editorID, recordType, deletedFormID);
                    }

                    processedCount++;
                    int newProgress = (processedCount*50)/formCount+50;
                    if (newProgress >= currentProgress+5) {
                        currentProgress = newProgress;
                        getStatusDialog().updateProgress(currentProgress);
                    }
                }
            }

            //
            // Save the patch plugin
            //
            patchPlugin.store(this);
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
            Main.logException("Exception while creating patch", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
}

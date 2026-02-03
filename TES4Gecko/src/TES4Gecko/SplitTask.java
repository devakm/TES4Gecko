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
 * Task to split a plugin into an ESM/ESP pair
 */
public class SplitTask extends WorkerTask {
    
    /** Source plugin node */
    private PluginNode pluginNode;

    /** Source plugin file */
    private File pluginFile;
    
    /** Source plugin */
    private Plugin plugin;
    
    /** Source plugin form list */
    private List<FormInfo> formList;
    
    /** Source plugin form map */
    private Map<Integer, FormInfo> formMap;
    
    /** Source plugin master count */
    private int masterCount;
    
    /** Masters */
    private Master[] masters;
    
    /** Create independent master */
    private boolean independentMaster;
    
    /** Output master node */
    private PluginNode outputMasterNode;
    
    /** Output master */
    private Plugin outputMaster;
    
    /** Output plugin node */
    private PluginNode outputPluginNode;
    
    /** Output plugin */
    private Plugin outputPlugin;
    
    /** Output status map */
    private Map<Integer, Boolean> outputMap;
    
    /** Pending status list */
    private List<PluginRecord> pendingList;
    
    /** Reference check map */
    private Map<Integer, PluginRecord> checkMap;
    
    /** Output form adjustment */
    private FormAdjust formAdjust;
    
    /** 
     * Create a new instance of SplitTask 
     *
     * @param       statusDialog        The status dialog
     * @param       pluginFile          The source plugin file
     * @param       pluginNode          The source plugin node
     * @param       independentMaster   TRUE if creating an independent master
     * @param       outputMasterNode    The output master node
     * @param       outputPluginNode    The output plugin node
     */
    public SplitTask(StatusDialog statusDialog, File pluginFile, PluginNode pluginNode, boolean independentMaster,
                     PluginNode outputMasterNode, PluginNode outputPluginNode) {
        super(statusDialog);
        
        this.pluginFile = pluginFile;
        this.pluginNode = pluginNode;
        plugin = pluginNode.getPlugin();
        formList = plugin.getFormList();
        formMap = plugin.getFormMap();
        masterCount = plugin.getMasterList().size();
        
        this.outputMasterNode = outputMasterNode;
        outputMaster = outputMasterNode.getPlugin();
        
        this.outputPluginNode = outputPluginNode;
        outputPlugin = outputPluginNode.getPlugin();
        
        this.independentMaster = independentMaster;
        outputMap = new HashMap<Integer, Boolean>(formList.size());
        checkMap = new HashMap<Integer, PluginRecord>(formList.size());
        pendingList = new ArrayList<PluginRecord>(25);
    }

    /**
     * Split a plugin into an ESM/ESP pair
     *
     * @param       parent              The parent dialog
     * @param       pluginFile          The source plugin file
     * @param       pluginNode          The source plugin node
     * @param       independentMaster   TRUE if creating an independent master
     * @param       outputMasterNode    The output master node
     * @param       outputPluginNode    The output plugin node
     * @return                          TRUE if the plugin was split successfully
     */
    public static boolean splitPlugin(JDialog parent, File pluginFile, PluginNode pluginNode, boolean independentMaster,
                                      PluginNode outputMasterNode, PluginNode outputPluginNode) {
        boolean completed = false;
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Splitting "+pluginNode.getPlugin().getName(),
                                                     "Split Plugin");
        
        //
        // Create the worker task
        //
        SplitTask worker = new SplitTask(statusDialog, pluginFile, pluginNode, independentMaster, 
                                         outputMasterNode, outputPluginNode);
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
            completed = true;
        else
            JOptionPane.showMessageDialog(parent, "Unable to split "+pluginNode.getPlugin().getName(),
                                          "Split Plugin", JOptionPane.INFORMATION_MESSAGE);
        
        return completed;
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        
        try {
            int recordCount = formList.size();
            int processedCount = 0;
            int currentProgress = 0;
            
            //
            // Build the form adjustment for the plugin records
            //
            int[] masterMap = new int[masterCount];
            for (int i=0; i<masterCount; i++)
                masterMap[i] = i;
            
            formAdjust = new FormAdjust(masterMap, masterCount, formMap);
            
            //
            // Load the master files
            //
            masters = new Master[masterCount];
            List<String> masterList = plugin.getMasterList();
            int index = 0;
            for (String masterName : masterList) {
                File masterFile = new File(pluginFile.getParent()+Main.fileSeparator+masterName);
                Master master = new Master(masterFile);
                master.load(this);
                masters[index++] = master;
            }
            
            //
            // Update the status message
            //
            getStatusDialog().updateMessage("Splitting "+plugin.getName());
            
            //
            // Map the records in the source plugin
            //
            for (FormInfo formInfo : formList) {
                PluginRecord record = (PluginRecord)formInfo.getSource();
                if (Main.debugMode)
                    System.out.printf("Mapping %s record %s (%08X)\n",
                                      record.getRecordType(), record.getEditorID(), record.getFormID());
                
                boolean addMaster = checkMaster(record, false);
                int formID = record.getFormID();
                int masterID = formID>>>24;
                int splitFormID;
                if (masterID < masterCount)
                    splitFormID = formID;
                else if (addMaster || !independentMaster)
                    splitFormID = (formID&0x00ffffff) | (masterCount<<24);
                 else
                    splitFormID = (formID&0x00ffffff) | ((masterCount+1)<<24);
                
                formInfo.setMergedFormID(splitFormID);
                pendingList.clear();
                outputMap.put(new Integer(formID), new Boolean(addMaster));
                if (Main.debugMode)
                    System.out.printf("%s record %s (%08X) master status set to %s\n",
                                      record.getRecordType(), record.getEditorID(), record.getFormID(), addMaster);
                
                //
                // Update our progress
                //
                if (interrupted())
                    throw new InterruptedException("Request canceled");
                
                processedCount++;
                int newProgress = (processedCount*50)/recordCount;
                if (newProgress >= currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }                
            }
            
            //
            // Copy the records in the source plugin
            //
            // Special handling is required for a DOOR reference that is copied to the new 
            // master but teleports to a location in a cell that is in another master.  The
            // game engine can crash if the teleport marker is placed from a master, so we
            // need to clone the reference and remove the XTEL subrecord from the master copy.
            //
            processedCount = 0;
            for (FormInfo formInfo : formList) {
                PluginRecord record = (PluginRecord)formInfo.getSource();
                if (outputMap.get(new Integer(record.getFormID())).booleanValue()) {
                    outputMaster.copyRecord(record, formAdjust);
                    if (record.getRecordType().equals("REFR"))
                        cloneDoorReference(record, formInfo);
                } else {
                    outputPlugin.copyRecord(record, formAdjust);
                }
                
                //
                // Update our progress
                //
                if (interrupted())
                    throw new InterruptedException("Request canceled");
                
                processedCount++;
                int newProgress = (processedCount*50)/recordCount+50;
                if (newProgress >= currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }
            
            //
            // Save the ESM/ESP pair
            //
            outputMaster.store(this);
            outputPlugin.store(this);
            
            //
            // Build the tree nodes
            //
            outputMasterNode.buildNodes(this);
            outputPluginNode.buildNodes(this);
            
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
            Main.logException("Exception while splitting plugin", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
    
    /**
     * Determine if a record should be placed in the new master or the new plugin.  Records that
     * are checked recursively will be added to the output map to improve performance on
     * subsequent reference checking.
     *
     * @param       record                  The record to check
     * @param       recursive               TRUE if this is a recursive call
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private boolean checkMaster(PluginRecord record, boolean recursive) 
                                            throws DataFormatException, IOException, PluginException {
        //
        // See if we have already determined the status of this record (from an earlier
        // recursive status check)
        //
        int formID = record.getFormID();
        Integer objFormID = new Integer(formID);
        Boolean objAddMaster = outputMap.get(objFormID);
        if (objAddMaster != null)
            return objAddMaster.booleanValue();
        
        //
        // Determine the record status
        //
        boolean addMaster = true;
        boolean addStatus = true;
        String recordType = record.getRecordType();
        int masterID = formID>>>24;

        PluginGroup group = (PluginGroup)record.getParent();
        int groupType = group.getGroupType();
        int parentFormID = group.getGroupParentID();
        int parentMasterID = parentFormID>>>24;

        if (masterID < masterCount) {
            //
            // The record modifies a master record
            //
            // If the record is in a top-level group, we will check to see if the record also exists in
            // the master.  The record will be copied to the new plugin if it exists in the master.
            // Otherwise, it will be copied to the new master.  Note that CELL and DIAL records are
            // always copied to the new plugin.
            //
            // The record is always copied to the new plugin if it is in a subgroup.
            //
            if (groupType == PluginGroup.TOP) {
                if (recordType.equals("CELL") || recordType.equals("DIAL")) {
                    addMaster = false;
                } else {
                    Master master = masters[masterID];
                    int masterFormID = (formID&0x00ffffff) | (master.getMasterList().size()<<24);
                    if (master.getFormMap().get(new Integer(masterFormID)) != null)
                        addMaster = false;
                }
            } else {
                addMaster = false;
            }

        } else {

            if (recordType.equals("GMST") || recordType.equals("MGEF")) {
                //
                // GMST and MGEF records are always added to the new plugin
                //
                addMaster = false;

            } else if (groupType == PluginGroup.WORLDSPACE) {
                //
                // Records that modify worldspaces defined in a master are always added to the
                // new plugin.  Otherwise, they will be added to the new master.
                //
                if (parentMasterID < masterCount) {
                    Master master = masters[parentMasterID];
                    int masterFormID = (parentFormID&0x00ffffff) | (master.getMasterList().size()<<24);
                    if (master.getFormMap().get(new Integer(masterFormID)) != null)
                        addMaster = false;
                }
                
            } else if (groupType == PluginGroup.TOPIC) {
                //
                // Records that modify topics defined in a master are always added to the
                // new plugin.  Otherwise, they will be added to the new master.
                //
                if (parentMasterID < masterCount)
                    addMaster = false;

            } else if (groupType == PluginGroup.CELL_DISTANT) {
                //
                // The Oblivion game engine has a problem with disappearing land when visible-when-distant
                // references are found in a non-00 master.  So we will always add them to the new plugin.
                //
                addMaster = false;
                
            } else if (groupType == PluginGroup.CELL || groupType == PluginGroup.CELL_PERSISTENT ||
                       groupType == PluginGroup.CELL_TEMPORARY) {
                //
                // Records that modify cells defined in a master are always added to the new plugin.
                // Otherwise, they will be added to the new master if the cell is also in the master.
                //
                if (parentMasterID < masterCount) {
                    addMaster = false;
                } else {
                    FormInfo cellInfo = formMap.get(new Integer(parentFormID));
                    if (cellInfo != null) {
                        int cellFormID = cellInfo.getMergedFormID();
                        int cellMasterID = cellFormID>>>24;
                        if (cellMasterID != masterCount)
                            addMaster = false;
                    }
                }
            }

            //
            // Check the record references if we are creating an independent ESM/ESP pair
            // and we are adding the record to the master (we don't care about the
            // references if we are going to add the record to the plugin).
            //
            if (independentMaster && addMaster) {
                int status = checkReferences(record);
                if (status < 0)
                    addStatus = false;
                else if (status == 0)
                    addMaster = false;
            }
        }

        //
        // Add the record status to the output map if this is a recursive call (this will
        // improve performance when processing subsequent request).  We don't want to record
        // the status if we encountered a recursive reference (the status of this record will 
        // depend on the status of the recursive reference).  We will maintain a list of
        // pending records so that we don't waste time continually checking them while
        // following subrecord references.
        //
        if (addStatus) {
            if (recursive) {
                outputMap.put(objFormID, new Boolean(addMaster));
                if (Main.debugMode)
                    System.out.printf("%s record %s (%08X) master status set to %s\n",
                                      record.getRecordType(), record.getEditorID(), objFormID, addMaster);
            }
        } else if (!pendingList.contains(record)) {
            pendingList.add(record);
        }
        
        return addMaster;
    }
    
    /**
     * Check the subrecord references to determine if the record should be placed
     * in the new master or the new plugin.  This method is called only if we are
     * creating an independent master.
     *
     * @param       record                  The record to check
     * @return                              -1 if recursive reference, 0 if unclean, 1 if clean
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private int checkReferences(PluginRecord record) throws DataFormatException, IOException, PluginException {
        boolean clean = true;
        boolean recursiveCheck = false;
        if (Main.debugMode)
            System.out.printf("Checking references for %s record %s (%08X)\n",
                              record.getRecordType(), record.getEditorID(), record.getFormID());
        
        //
        // Add the current record to the check map.  We will return immediately if the record
        // is being checked recursively (we cannot determine the record status until the
        // status of the recursive items has been determined)
        //
        int recordFormID = record.getFormID();
        Integer objFormID = new Integer(recordFormID);
        if (checkMap.get(objFormID) != null) {
            if (Main.debugMode)
                System.out.printf("Recursive reference check for %s record %s (%08X)\n",
                                  record.getRecordType(), record.getEditorID(), record.getFormID());
            
            return -1;
        }
        
        checkMap.put(objFormID, record);
        
        //
        // Check each subrecord
        //
        List<PluginSubrecord> subrecords = record.getSubrecords();
        for (PluginSubrecord subrecord : subrecords) {
            int[][] references = subrecord.getReferences();
            if (references == null || references.length == 0)
                continue;
            
            //
            // Check each reference in the subrecord
            //
            for (int i=0; i<references.length; i++) {
                int formID = references[i][1];
                int masterID = formID>>>24;
                if (formID == 0)
                    continue;
                
                //
                // A master reference is clean unless the item is modified by
                // a record in the plugin.  In that case, we have to assume that
                // this record might be dependent on that modification.
                //
                // The referenced record has already been processed if it is in the
                // output map.  In this case, the merged form ID tells us whether
                // the record is in the output master or the output plugin.
                //
                // If the referenced record is not in the output map, we will need
                // to check its references to determine whether it will be placed in
                // the output master or the output plugin.
                //
                Integer checkFormID = new Integer(formID);
                if (masterID < masterCount) {
                    if (formMap.get(checkFormID) != null)
                        clean = false;
                } else {
                    Boolean addMaster = outputMap.get(checkFormID);
                    if (addMaster != null) {
                        if (!addMaster.booleanValue())
                            clean = false;
                    } else if (checkMap.get(checkFormID) == null) {
                        FormInfo formInfo = formMap.get(checkFormID);
                        if (formInfo != null) {
                            PluginRecord checkRecord = (PluginRecord)formInfo.getSource();
                            if (!pendingList.contains(checkRecord))
                                clean = checkMaster((PluginRecord)formInfo.getSource(), true);
                        } else {
                            clean = false;
                        }
                    } else {
                        recursiveCheck = true;
                    }
                }
                
                if (!clean) {
                    if (Main.debugMode)
                        System.out.printf("Unclean reference %08X in %s subrecord of record %08X\n", 
                                          checkFormID, subrecord.getSubrecordType(), recordFormID);
                    
                    break;
                }
            }
            
            if (!clean)
                break;
        }
        
        //
        // Remove the current record from the check map
        //
        checkMap.remove(objFormID);
        return (clean ? (recursiveCheck ? -1 : 1) : 0);
    }
    
    /**
     * Clone a door reference if the door is placed in the new master but it teleports
     * to a location in another master.
     *
     * @param       record                  The reference record
     * @param       formInfo                The record form information
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private void cloneDoorReference(PluginRecord record, FormInfo formInfo) 
                                            throws DataFormatException, IOException, PluginException {
        boolean cloneReference = false;
        PluginSubrecord subrecord;
        String subrecordType;
        byte[] subrecordData;
        int index;
        
        //
        // Get the new master record
        //
        int masterFormID = formInfo.getMergedFormID();
        FormInfo masterFormInfo = outputMaster.getFormMap().get(new Integer(masterFormID));
        if (masterFormInfo == null)
            throw new PluginException(String.format("Unable to locate output master record %08X", masterFormID));
        
        PluginRecord masterRecord = (PluginRecord)masterFormInfo.getSource();
        
        //
        // Look for an XTEL subrecord
        //
        List<PluginSubrecord> subrecords = masterRecord.getSubrecords();
        int count = subrecords.size();
        for (index=0; index<count; index++) {
            subrecord = subrecords.get(index);
            subrecordType = subrecord.getSubrecordType();
            if (subrecordType.equals("XTEL")) {
                subrecordData = subrecord.getSubrecordData();
                int formID = SerializedElement.getInteger(subrecordData, 0);
                int masterID = formID>>>24;
                if (masterID != masterCount)
                    cloneReference = true;
                
                break;
            }
        }
        
        //
        // Clone the reference if it teleports to a cell in another master
        //
        if (cloneReference) {
            
            //
            // Remove the XTEL subrecord from the new master record
            //
            subrecords.remove(index);
            masterRecord.setSubrecords(subrecords);
            
            //
            // Copy the door reference to the new plugin
            //
            outputPlugin.copyRecord(record, formAdjust);
        }
    }
}

package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Task to move worldspace records between master and plugin
 */
public class WorldspaceTask extends WorkerTask {
    
    /** Insert placeholders */
    private boolean insertPlaceholders = false;
    
    /** Plugin modified */
    private boolean pluginModified = false;
    
    /** Plugin file */
    private File pluginFile;
    
    /** Plugin */
    private Plugin plugin;
    
    /** Plugin module index */
    private int pluginIndex;
    
    /** Masters */
    private Master[] masters;
    
    /** Worldspaces and statics that need to be relocated */
    private List<FormInfo> worldspaceList;
    
    /** Worldspace names */
    private List<String> worldspaceNames;
    
    /** Mapping from original form ID to relocated form ID */
    private Map<Integer, Integer> worldspaceMap;
    
    /** Distant LOD form ID values */
    private List<Integer> distantList;

    /** Next index 00 worldspace form ID */
    private int baseFormID;
    
    /** 
     * Create a new instance of WorldspaceTask 
     *
     * @param       statusDialog    The status dialog
     * @param       pluginFile      The plugin file
     * @param       options         Options
     */
    public WorldspaceTask(StatusDialog statusDialog, File pluginFile, int options) {
        super(statusDialog);
        this.pluginFile = pluginFile;
        if ((options&0x01) == 1)
            insertPlaceholders = true;
    }
    
    /**
     * Move worldspace records for a plugin
     *
     * @param       parent          The parent frame
     * @param       pluginFile      The plugin file
     * @param       options         Options
     */
    public static void moveWorldspaces(JFrame parent, File pluginFile, int options) {
        
        //
        // Create the status dialog
        //
        StatusDialog statusDialog = new StatusDialog(parent, "Moving worldspaces for "+pluginFile.getName(), "Move Worldspaces");
        
        //
        // Create the worker task
        //
        WorldspaceTask worker = new WorldspaceTask(statusDialog, pluginFile, options);
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
            JOptionPane.showMessageDialog(parent, pluginFile.getName()+" updated",
                                          "Move Worldspaces", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(parent, "Unable to move worldspaces for "+pluginFile.getName(),
                                          "Move Worldspaces", JOptionPane.INFORMATION_MESSAGE);        
    }
    
    /**
     * Run the executable code for this thread
     */
    public void run() {
        boolean completed = false;
        worldspaceList = new ArrayList<FormInfo>(50);
        worldspaceNames = new ArrayList<String>(50);
        worldspaceMap = new HashMap<Integer, Integer>(50);
        distantList = new ArrayList<Integer>(50);
        
        try {
            
            //
            // Load the plugin
            //
            plugin = new Plugin(pluginFile);
            plugin.load(this);
            
            //
            // Load the masters for the plugin
            //
            List<String> masterList = plugin.getMasterList();
            pluginIndex = masterList.size();
            if (pluginIndex > 0) {
                masters = new Master[pluginIndex];
                for (int i=0; i<pluginIndex; i++) {
                    String masterName = masterList.get(i);
                    File masterFile = new File(Main.pluginDirectory+Main.fileSeparator+masterName);
                    masters[i] = new Master(masterFile);
                    masters[i].load(this);
                }
            }
            
            //
            // Get the initial worldspace form ID based on the current time.  We will use the number
            // of seconds since April 8, 2007 (1176047100) and a base form ID of 0x00200000.  The form 
            // ID will be incremented every 18 seconds and will roll over when it reaches 0x01000000.
            //
            Date date = new Date();
            baseFormID = ((int)(date.getTime()/1000L-1176047100L)%0x00E00000)+0x00200000;
            if (Main.debugMode)
                System.out.printf("Base worldspace form ID is %08X\n", baseFormID);

            //
            // Update the status message
            //
            getStatusDialog().updateMessage("Moving worldspaces for "+plugin.getName());
            
            //
            // Relocate plugin worldspaces and distant statics
            //
            if (pluginIndex > 0) {
                
                //
                // Map the worldspaces and distant statics found in the plugin
                //
                mapWorldspaces();
                
                //
                // Map the distant statics referenced by .lod files
                //
                String lodPath = String.format("%s%sDistantLOD", Main.pluginDirectory, Main.fileSeparator);
                File lodDir = new File(lodPath);
                if (lodDir.exists() && lodDir.isDirectory())
                    mapDistantStatics(lodDir);
                
                //
                // Relocate worldspaces and distant statics found in the plugin
                //
                relocateWorldspaces();
                
                //
                // Update worldspace references and distant statics
                //
                updateReferences();
                
                //
                // Update the .lod files
                //
                if (lodDir.exists() && lodDir.isDirectory())
                    updateDistantStatics(lodDir);
                
                //
                // Create placeholders for the moved worldspaces and distant statics
                //
                if (insertPlaceholders)
                    createPlaceholders();
                
                //
                // Rename landscape meshes
                //
                String dirPath = String.format("%s%sMeshes%sLandscape%sLOD",
                                               Main.pluginDirectory, Main.fileSeparator, Main.fileSeparator,
                                               Main.fileSeparator);
                File dirFile = new File(dirPath);
                if (dirFile.exists() && dirFile.isDirectory())
                    renameFiles(dirFile);
                
                //
                // Rename landscape textures
                //
                dirPath = String.format("%s%sTextures%sLandscapeLOD%sGenerated",
                                        Main.pluginDirectory, Main.fileSeparator, Main.fileSeparator,
                                        Main.fileSeparator);
                dirFile = new File(dirPath);
                if (dirFile.exists() && dirFile.isDirectory())
                    renameFiles(dirFile);
            }
            
            //
            // Save the plugin if it has been modified
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
            Main.logException("Exception while moving worldspaces", exc);            
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);        
    }
    
    /**
     * Map the worldspaces and distant statics in the plugin
     *
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    private void mapWorldspaces() throws DataFormatException, IOException, PluginException {
        List<FormInfo> formList = plugin.getFormList();
        Map<Integer, FormInfo> formMap = plugin.getFormMap();
        Map<Integer, FormInfo> masterFormMap = masters[0].getFormMap();

        //
        // Scan the plugin records
        //
        for (FormInfo formInfo : formList) {
            String recordType = formInfo.getRecordType();
            int formID = formInfo.getFormID();
            int modIndex = formID>>>24;

            if (recordType.equals("WRLD")) {
                
                //
                // Process a WRLD record
                //
                // If the worldspace is in the plugin index, we will move it to the 00 index.
                //
                // If the worldspace is in a non-00 master index, we will check for a placeholder.
                // If found, we will move the plugin worldspace to match the master worldspace.
                //
                if (modIndex >= pluginIndex) {
                    worldspaceList.add(formInfo);
                } else if (modIndex > 0) {
                    Master checkMaster = masters[modIndex];
                    int checkFormID = (formID&0x00ffffff) | (checkMaster.getMasterList().size()<<24);
                    FormInfo checkFormInfo = checkMaster.getFormMap().get(new Integer(checkFormID));
                    if (checkFormInfo != null) {
                        PluginRecord checkRecord = checkMaster.getRecord(checkFormID);
                        if (checkRecord.getRecordType().equals("BOOK")) {
                            String editorID = checkRecord.getEditorID();
                            if (editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko"))
                                worldspaceList.add(formInfo);
                        }
                    }
                }
                
                //
                // Add the lowercased worldspace name to the list
                //
                worldspaceNames.add(formInfo.getEditorID().toLowerCase());
                
            } else if (recordType.equals("REFR") || recordType.equals("ACHR") || recordType.equals("ACRE")) {
                
                //
                // Process an object reference in a cell
                //
                // If the base item is in a non-00 master index, we will check for a placeholder.
                // If found, we will change the plugin reference to the relocated item.  If there
                // is no placeholder but this is a visible-when-distant reference, we will need to
                // clone the item in the plugin and then relocate it.
                //
                // If the base item is in the plugin index and this is a visible-when-distant reference, 
                // we will move it to the 00 index.
                //
                PluginRecord record = (PluginRecord)formInfo.getSource();
                int recordFlags = record.getRecordFlags();
                int baseFormID = 0;
                List<PluginSubrecord> subrecords = record.getSubrecords();
                for (PluginSubrecord subrecord : subrecords) {
                    if (subrecord.getSubrecordType().equals("NAME")) {
                        byte[] subrecordData = subrecord.getSubrecordData();
                        baseFormID = SerializedElement.getInteger(subrecordData, 0);
                        break;
                    }
                }

                int baseModIndex = baseFormID>>>24;
                if (baseFormID != 0 && baseModIndex > 0) {
                    Integer lookupFormID = new Integer(baseFormID);
                    FormInfo baseFormInfo = formMap.get(lookupFormID);
                    if (baseFormInfo == null) {
                        if (baseModIndex < pluginIndex) {
                            Master checkMaster = masters[baseModIndex];
                            int checkFormID = (baseFormID&0x00ffffff) | (checkMaster.getMasterList().size()<<24);
                            baseFormInfo = checkMaster.getFormMap().get(new Integer(checkFormID));
                            if (baseFormInfo != null) {
                                PluginRecord checkRecord = checkMaster.getRecord(checkFormID);
                                boolean relocated = false;
                                if (checkRecord.getRecordType().equals("BOOK")) {
                                    String editorID = checkRecord.getEditorID();
                                    if (editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko"))
                                        relocated = true;
                                }
                                
                                if (relocated || (recordFlags&0x00008000) != 0) {
                                    FormInfo relocFormInfo = new FormInfo(baseFormInfo.getSource(),
                                                                          baseFormInfo.getRecordType(),
                                                                          baseFormID,
                                                                          baseFormInfo.getEditorID());
                                    relocFormInfo.setPlugin(baseFormInfo.getPlugin());
                                    if (!worldspaceList.contains(relocFormInfo)) {
                                        worldspaceList.add(relocFormInfo);
                                        if (Main.debugMode)
                                            System.out.printf("Relocation required for reference %08X to %s base item %s (%08X)\n",
                                                              formInfo.getFormID(), baseFormInfo.getRecordType(),
                                                              baseFormInfo.getEditorID(), baseFormID);
                                    }
                                }
                            }
                        }
                    } else if ((recordFlags&0x00008000) != 0 && !worldspaceList.contains(baseFormInfo)) {
                        worldspaceList.add(baseFormInfo);
                        if (Main.debugMode)
                            System.out.printf("Relocation required for reference %08X to %s base item %s (%08X)\n",
                                              formInfo.getFormID(), baseFormInfo.getRecordType(),
                                              baseFormInfo.getEditorID(), baseFormID);
                    } else if (baseModIndex < pluginIndex && !worldspaceList.contains(baseFormInfo)) {
                        Master checkMaster = masters[baseModIndex];
                        int checkFormID = (baseFormID&0x00ffffff) | (checkMaster.getMasterList().size()<<24);
                        FormInfo checkFormInfo = checkMaster.getFormMap().get(new Integer(checkFormID));
                        if (checkFormInfo != null) {
                            PluginRecord checkRecord = checkMaster.getRecord(checkFormID);
                            if (checkRecord.getRecordType().equals("BOOK")) {
                                String editorID = checkRecord.getEditorID();
                                if (editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko")) {
                                    worldspaceList.add(baseFormInfo);
                                    if (Main.debugMode)
                                        System.out.printf("Relocation required for reference %08X to %s base item %s (%08X)\n",
                                                          formInfo.getFormID(), baseFormInfo.getRecordType(),
                                                          baseFormInfo.getEditorID(), baseFormID);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Map the distant statics referenced by .lod files corresponding to worldspaces referenced
     * in the plugin
     *
     * @param       dirFile                 The directory containing the files
     *
     * @exception   IOException             An I/O error occurred
     */
    private void mapDistantStatics(File dirFile) throws IOException {
        File[] files = dirFile.listFiles();
        
        //
        // Process each .lod file in the directory and get the static references.
        //
        // The .lod file has the following format:
        //   Bytes 0-3: Number of static references (integer)
        //   Bytes 4-n: Static references
        //
        // Each static reference has the following format:
        //   Bytes 0-3: Form ID (integer)
        //   Bytes 4-7: Number of occurrences (integer)
        //   Bytes 8-n: Static occurrences
        //
        // Each static occurrent has the following format:
        //   Bytes 0-11:  X, Y, Z coordinates (float)
        //   Bytes 12-23: X, Y, Z rotation (float)
        //   Bytes 24-27: Scaling (float)
        //
        for (File file : files) {
            String name = file.getName();
            int length = (int)file.length();
            int pos = name.lastIndexOf('.');
            if (pos > 0 && name.substring(pos).equalsIgnoreCase(".lod") && length > 0) {
                pos = name.indexOf('_');
                if (pos > 0 && worldspaceNames.contains(name.substring(0, pos).toLowerCase())) {
                    FileInputStream in = null;
                    try {
                        
                        //
                        // Read the .lod file
                        //
                        in = new FileInputStream(file);
                        byte[] buffer = new byte[length];
                        int count = in.read(buffer);
                        if (count != length)
                            throw new EOFException("Unexpected end-of-file on "+name);

                        in.close();
                        in = null;
                        
                        //
                        // Get the base item references that apply to the current move operation
                        //
                        int referenceCount = SerializedElement.getInteger(buffer, 0);
                        int offset = 4;
                        for (int i=0; i<referenceCount; i++) {
                            int formID = SerializedElement.getInteger(buffer, offset);
                            int modIndex = formID>>>24;
                            count = SerializedElement.getInteger(buffer, offset+4);
                            offset += 8 + 28*count;
                            if (modIndex > 0) {
                                Integer objFormID = new Integer(formID);
                                if (!distantList.contains(objFormID))
                                    distantList.add(new Integer(objFormID));
                            }
                        }
                    } finally {
                        if (in != null)
                            in.close();
                    }
                }
            }
        }
        
        //
        // Scan the plugin records and map the .lod values to plugin values
        //
        List<FormInfo> formList = plugin.getFormList();
        Map<Integer, FormInfo> formMap = plugin.getFormMap();
        Map<Integer, FormInfo> masterFormMap = masters[0].getFormMap();
        for (FormInfo formInfo : formList) {
            int formID = formInfo.getFormID();
            int modIndex = formID>>>24;
            if (modIndex >= pluginIndex) {
                for (Integer distantFormID : distantList) {
                    if (distantFormID.intValue() == formID) {
                        if (!worldspaceList.contains(formInfo)) {
                            worldspaceList.add(formInfo);
                            if (Main.debugMode)
                                System.out.printf("DistantLOD entry maps to %s record %s (%08X)\n",
                                                  formInfo.getRecordType(), formInfo.getEditorID(),
                                                  formID);
                        }
                        
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Relocate worldspaces and distant statics
     *
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    private void relocateWorldspaces() throws DataFormatException, IOException, PluginException {
        for (FormInfo formInfo : worldspaceList) {
            int oldFormID = formInfo.getFormID();
            int modIndex = oldFormID>>>24;
            int formID;
            if (modIndex >= pluginIndex) {
                String recordType = formInfo.getRecordType();
                String editorID = formInfo.getEditorID();
                if (recordType.equals("BOOK") && editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko"))
                    formID = Integer.parseInt(editorID.substring(9), 16);
                else
                    formID = getBaseFormID();
            } else {
                PluginRecord checkRecord = masters[modIndex].getRecord(oldFormID);
                String recordType = checkRecord.getRecordType();
                String editorID = checkRecord.getEditorID();
                if (recordType.equals("BOOK") && editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko")) {
                    formID = Integer.parseInt(editorID.substring(9), 16);
                } else if (formInfo.getPlugin() instanceof Master) {
                    formID = getBaseFormID();
                    PluginRecord record = (PluginRecord)checkRecord.clone();
                    editorID = String.format("TES4Gecko%08X", formID);
                    PluginGroup group = plugin.createTopGroup(recordType);
                    record.setFormID(formID);
                    record.setEditorID(editorID);
                    record.setParent(group);
                    group.getRecordList().add(record);
                    FormInfo cloneFormInfo = new FormInfo(record, recordType, formID, editorID);
                    cloneFormInfo.setPlugin(plugin);
                    plugin.getFormList().add(cloneFormInfo);
                    plugin.getFormMap().put(new Integer(formID), cloneFormInfo);
                    pluginModified = true;
                } else {
                    formID = getBaseFormID();
                }
            }

            worldspaceMap.put(new Integer(oldFormID), new Integer(formID));
            if (Main.debugMode)
                System.out.printf("Relocating %s record %s from %08X to %08X\n", 
                                  formInfo.getRecordType(), formInfo.getEditorID(), oldFormID, formID);
        }
    }
    
    /**
     * Get a new relocated form ID
     *
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    private int getBaseFormID() throws DataFormatException, IOException, PluginException {
        int formID = 0;
        boolean haveFormID = false;
        while (!haveFormID) {
            
            //
            // Get the next base form ID.  The form ID will roll-over from 0x01000000 to
            // 0x00200000.
            //
            if (baseFormID >= 0x01000000)
                baseFormID = 0x00200000;
            
            formID = baseFormID++;
            haveFormID = true;
            
            //
            // Check each master to make sure the base form ID is not already in use
            //
            Integer checkObj = new Integer(formID);
            for (int i=0; i<masters.length; i++) {
                if (masters[i].getFormMap().get(checkObj) != null) {
                    haveFormID = false;
                    break;
                }
            }
        }
        
        return formID;
    }

    /**
     * Update references
     *
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    private void updateReferences() throws DataFormatException, IOException, PluginException {
        List<PluginGroup> groupList = plugin.getGroupList();
        int totalCount = groupList.size();
        int processedCount = 0;
        int currentProgress = 0;
        
        //
        // Process each top-level group
        //
        for (PluginGroup group : groupList) {
            updateGroupReferences(group);
            processedCount++;
            int newProgress = (processedCount*100)/totalCount;
            if (newProgress > currentProgress+5) {
                currentProgress = newProgress;
                getStatusDialog().updateProgress(currentProgress);
            }
        }
    }
    
    /**
     * Update the references for a group
     *
     * @param       group                   The plugin group
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    void updateGroupReferences(PluginGroup group) throws DataFormatException, IOException, PluginException {
        List<PluginRecord> recordList = group.getRecordList();
        
        //
        // Process each record in the group
        //
        for (PluginRecord record : recordList) {
            if (record instanceof PluginGroup) {
                
                //
                // Update the subgroup references
                //
                PluginGroup subgroup = (PluginGroup)record;
                updateGroupReferences(subgroup);
                
                //
                // Update the group parent ID for a worldspace group
                //
                if (subgroup.getGroupType() == PluginGroup.WORLDSPACE) {
                    int groupParentID = subgroup.getGroupParentID();
                    Integer movedFormID = worldspaceMap.get(new Integer(groupParentID));
                    if (movedFormID != null) {
                        subgroup.setGroupParentID(movedFormID.intValue());
                        pluginModified = true;
                    }
                }
                
            } else {
            
                //
                // Process the subrecords for the current record
                //
                List<PluginSubrecord> subrecords = record.getSubrecords();
                boolean recordModified = false;
                for (PluginSubrecord subrecord : subrecords) {
                    boolean subrecordModified = false;
                    byte[] subrecordData = subrecord.getSubrecordData();
                    int[][] references = subrecord.getReferences();
                    if (references == null || references.length == 0)
                        continue;

                    //
                    // Process each reference in the current subrecord
                    //
                    for (int i=0; i<references.length; i++) {
                        int offset = references[i][0];
                        int formID = references[i][1];
                        if (formID == 0)
                            continue;

                        Integer movedFormID = worldspaceMap.get(new Integer(formID));
                        if (movedFormID != null) {
                            SerializedElement.setInteger(movedFormID.intValue(), subrecordData, offset);
                            subrecordModified = true;
                        }
                    }

                    //
                    // Store the updated the subrecord data
                    //
                    if (subrecordModified) {
                        subrecord.setSubrecordData(subrecordData);
                        recordModified = true;
                    }
                }

                //
                // Store the updated subrecords
                //
                if (recordModified) {
                    record.setSubrecords(subrecords);
                    pluginModified = true;
                }

                //
                // Adjust the record form ID (setFormID() is used instead of changeFormID()
                // because we will adjust the worldspace groups ourself).  Note that we don't
                // want to adjust one of our placeholders (this can happen if Move Worldspaces
                // is done on a relocated plugin).
                //
                String recordType = record.getRecordType();
                String editorID = record.getEditorID();
                if (!recordType.equals("BOOK") || editorID.length() != 17 || !editorID.substring(0, 9).equals("TES4Gecko")) {
                    int formID = record.getFormID();
                    Integer movedFormID = worldspaceMap.get(new Integer(formID));
                    if (movedFormID != null) {
                        record.setFormID(movedFormID.intValue());
                        pluginModified = true;
                    }
                }
            }
        }
    }
    
    /**
     * Update the .lod files
     *
     * @param       dirFile         The directory containing the files
     *
     * @exception   IOException     An I/O error occurred
     */
    private void updateDistantStatics(File dirFile) throws IOException {
        File[] files = dirFile.listFiles();
        
        //
        // Process each .lod file in the directory and update the static references.
        //
        // The .lod file has the following format:
        //   Bytes 0-3: Number of static references (integer)
        //   Bytes 4-n: Static references
        //
        // Each static reference has the following format:
        //   Bytes 0-3: Form ID (integer)
        //   Bytes 4-7: Number of occurrences (integer)
        //   Bytes 8-n: Static occurrences
        //
        // Each static occurrent has the following format:
        //   Bytes 0-11:  X, Y, Z coordinates (float)
        //   Bytes 12-23: X, Y, Z rotation (float)
        //   Bytes 24-27: Scaling (float)
        //
        for (File file : files) {
            String name = file.getName();
            int length = (int)file.length();
            int pos = name.lastIndexOf('.');
            if (pos > 0 && name.substring(pos).equalsIgnoreCase(".lod") && length > 0) {
                pos = name.indexOf('_');
                if (pos > 0 && worldspaceNames.contains(name.substring(0, pos).toLowerCase())) {
                    FileInputStream in = null;
                    FileOutputStream out = null;
                    try {
                        
                        //
                        // Read the .lod file
                        //
                        in = new FileInputStream(file);
                        byte[] buffer = new byte[length];
                        int count = in.read(buffer);
                        if (count != length)
                            throw new EOFException("Unexpected end-of-file on "+name);

                        in.close();
                        in = null;

                        //
                        // Update references to relocated items
                        //
                        boolean fileUpdated = false;
                        int referenceCount = SerializedElement.getInteger(buffer, 0);
                        int offset = 4;
                        for (int i=0; i<referenceCount; i++) {
                            int formID = SerializedElement.getInteger(buffer, offset);
                            Integer movedFormID = worldspaceMap.get(new Integer(formID));
                            if (movedFormID != null) {
                                SerializedElement.setInteger(movedFormID.intValue(), buffer, offset);
                                fileUpdated = true;
                            }
                            
                            count = SerializedElement.getInteger(buffer, offset+4);
                            offset += 8 + 28*count;
                        }
                        
                        //
                        // Rewrite the updated .lod file if we made any changes
                        //
                        if (fileUpdated) {
                            out = new FileOutputStream(file);
                            out.write(buffer);
                            out.close();
                            out = null;
                        }
                    } finally {
                        if (out != null)
                            out.close();
                        
                        if (in != null)
                            in.close();
                    }
                }
            }
        }        
    }
    
    /**
     * Create placeholder records for moved worldspaces and statics
     *
     * @exception   DataFormatException     Compression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         Plugin format error detected
     */
    private void createPlaceholders() throws DataFormatException, IOException, PluginException {
        Map<Integer, FormInfo> formMap = plugin.getFormMap();
        PluginGroup group = plugin.createTopGroup("BOOK");
        List<PluginRecord> groupList = group.getRecordList();
        Set<Map.Entry<Integer, Integer>> mapSet = worldspaceMap.entrySet();
        
        //
        // Create a BOOK record for each moved worldspace or static defined in the plugin
        //
        for (Map.Entry<Integer, Integer> entry : mapSet) {
            int oldFormID = entry.getKey().intValue();
            int newFormID = entry.getValue().intValue();
            if ((oldFormID>>>24) < pluginIndex)
                continue;
            
            FormInfo formInfo = formMap.get(entry.getKey());
            if (formInfo != null) {
                String recordType = formInfo.getRecordType();
                String editorID = formInfo.getEditorID();
                if (recordType.equals("BOOK") && editorID.length() == 17 && editorID.substring(0, 9).equals("TES4Gecko"))
                    continue;
            }
        
            //
            // Create the subrecords
            //
            List<PluginSubrecord> subrecords = new ArrayList<PluginSubrecord>(10);

            byte[] fullData = String.format("Moved %08X\0", oldFormID).getBytes();
            PluginSubrecord fullSubrecord = new PluginSubrecord("BOOK", "FULL", fullData);
            subrecords.add(fullSubrecord);

            byte[] descData = String.format("Moved %08X to %08X\0", oldFormID, newFormID).getBytes();
            PluginSubrecord descSubrecord = new PluginSubrecord("BOOK", "DESC", descData);
            subrecords.add(descSubrecord);

            byte[] dataData = new byte[10];
            dataData[0] = (byte)0x01;
            dataData[1] = (byte)0xff;
            dataData[2] = (byte)0x00;
            dataData[3] = (byte)0x00;
            dataData[4] = (byte)0x00;
            dataData[5] = (byte)0x00;
            dataData[6] = (byte)0x00;
            dataData[7] = (byte)0x00;
            dataData[8] = (byte)0x00;
            dataData[9] = (byte)0x00;
            PluginSubrecord dataSubrecord = new PluginSubrecord("BOOK", "DATA", dataData);
            subrecords.add(dataSubrecord);

            byte[] modlData = "Clutter\\Books\\Scroll01.nif\0".getBytes();
            PluginSubrecord modlSubrecord = new PluginSubrecord("BOOK", "MODL", modlData);
            subrecords.add(modlSubrecord);

            byte[] modbData = new byte[4];
            modbData[0] = (byte)0xad;
            modbData[1] = (byte)0x3c;
            modbData[2] = (byte)0xdb;
            modbData[3] = (byte)0x41;
            PluginSubrecord modbSubrecord = new PluginSubrecord("BOOK", "MODB", modbData);
            subrecords.add(modbSubrecord);

            byte[] iconData = "Clutter\\IconScroll1.dds\0".getBytes();
            PluginSubrecord iconSubrecord = new PluginSubrecord("BOOK", "ICON", iconData);
            subrecords.add(iconSubrecord);

            //
            // Create the BOOK record with the same form ID as the original worldspace.
            // We don't update the form list or the form map since they still point to
            // the original worldspace.
            //
            PluginRecord record = new PluginRecord("BOOK", oldFormID);
            record.setSubrecords(subrecords);
            record.setEditorID(String.format("TES4Gecko%08X", newFormID));
            groupList.add(record);
            pluginModified = true;
            if (Main.debugMode)
                System.out.printf("Added %s record %s (%08X)\n",
                                  record.getRecordType(), record.getEditorID(), record.getFormID());
        }
    }
    
    /**
     * Rename meshes and textures with the new worldspace form ID values
     *
     * @param       dirFile         The directory containing the files
     */
    private void renameFiles(File dirFile) {
        File[] files = dirFile.listFiles();
        
        //
        // Rename a file if the filename starts with a form ID
        //
        for (File file : files) {
            if (file.isFile()) {
                String fileName = file.getName();
                if (Character.isDigit(fileName.charAt(0))) {
                    int sep = fileName.indexOf('.');
                    if (sep > 0) {
                        String prefix = fileName.substring(0, sep);
                        try {
                            Integer checkID = new Integer(prefix);
                            Integer mappedID = worldspaceMap.get(checkID);
                            if (mappedID != null) {
                                String mappedFileName = mappedID.toString()+fileName.substring(sep);
                                File mappedFile = new File(dirFile.getPath()+Main.fileSeparator+mappedFileName);
                                if (!mappedFile.exists()) {
                                    file.renameTo(mappedFile);
                                    if (Main.debugMode)
                                        System.out.printf("Renamed %s to %s\n", file.getPath(), mappedFile.getPath());
                                }
                            }
                        } catch (NumberFormatException exc) {
                        }
                    }
                }
            }
        }
    }
}

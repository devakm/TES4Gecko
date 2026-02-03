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
 * Abstract class to display a plugin with support for ignoring plugin records and 
 * for copying plugin records to the clipboard plugin.  The subclass is responsible
 * for initializing the clipboard variables.
 */
public abstract class DisplayPlugin extends JDialog {
    
    /** Clipboard */
    protected Plugin clipboard;

    /** Clipboard file */
    protected File clipboardFile;
    
    /** Clipboard tree */
    protected JTree clipboardTree;
    
    /** Clipboard tree model */
    protected DefaultTreeModel clipboardTreeModel;
    
    /** Clipboard modified */
    protected boolean clipboardModified = false;
    
    /** Clipboard cleared */
    protected boolean clipboardCleared = true;
    
    /** Copy referenced items */
    protected boolean copyReferences = false;
    
    /** 
     * Create a new instance of DisplayPlugin
     *
     * @param       parent          The parent frame
     * @param       title           The dialog title
     */
    public DisplayPlugin(JFrame parent, String title) {
        super(parent, title, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    /**
     * Create the children for a record node
     *
     * @param       recordNode              The record node
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    protected void createRecordChildren(RecordNode recordNode) throws DataFormatException, IOException, PluginException {
        
        //
        // Build the subrecord nodes
        //
        List<PluginSubrecord> subrecordList = recordNode.getRecord().getSubrecords();
        for (PluginSubrecord subrecord : subrecordList) {
            subrecord.setSpillMode(true);
            DefaultMutableTreeNode subrecordNode = new DefaultMutableTreeNode(subrecord);
            recordNode.add(subrecordNode);
        }
    }
    
    /**
     * Toggle the ignore flag for the selected records.  The ignore flags for all records in a
     * group will be toggled when a group is selected.  Subrecords will be skipped since they
     * cannot be marked as ignored.
     *
     * @param       tree            The tree being processed
     * @return                      TRUE if the plugin was modified
     */
    protected boolean toggleIgnore(JTree tree) {
        boolean pluginModified = false;
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths == null) {
            JOptionPane.showMessageDialog(this, "You must select at least one record.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            for (TreePath treePath : treePaths) {
                TreeNode node = (TreeNode)treePath.getLastPathComponent();
                if (node instanceof PluginNode) {
                    JOptionPane.showMessageDialog(this, 
                                    "The entire plugin can not be selected.  The selection will be ignored.",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
                } else if (node instanceof GroupNode) {
                    if (toggleGroupIgnore(tree, treePath, (GroupNode)node))
                        pluginModified = true;
                } else if (node instanceof RecordNode) {
                    if (toggleRecordIgnore(tree, treePath, (RecordNode)node)) {
                        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
                        treeModel.nodeChanged(node);
                        pluginModified = true;
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                                    "A subrecord can not be ignored.  The selection will be ignored.",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
        
        return pluginModified;
    }
    
    /**
     * Toggle the ignore flag for all records in a group.
     *
     * @param       tree            The tree being processed
     * @param       groupPath       Tree path for the group
     * @param       groupNode       Tree node for the group
     * @return                      TRUE if the plugin was modified
     */
    private boolean toggleGroupIgnore(JTree tree, TreePath groupPath, GroupNode groupNode) {
        boolean pluginModified = false;
        
        //
        // Ignore each subgroup and record in the current group
        //
        int childCount = groupNode.getChildCount();
        List<Integer> childList = new ArrayList<Integer>(childCount);
        for (int i=0; i<childCount; i++) {
            TreeNode node = (TreeNode)groupNode.getChildAt(i);
            TreePath treePath = groupPath.pathByAddingChild(node);
            if (node instanceof GroupNode) {
                if (toggleGroupIgnore(tree, treePath, (GroupNode)node))
                    pluginModified = true;
            } else if (node instanceof RecordNode) {
                if (toggleRecordIgnore(tree, treePath, (RecordNode)node)) {
                    childList.add(new Integer(i));
                    pluginModified = true;
                }
            }
        }
        
        //
        // Notify the tree model that nodes have been changed
        //
        childCount = childList.size();
        if (childCount > 0) {
            int[] childIndices = new int[childCount];
            for (int i=0; i<childCount; i++)
                childIndices[i] = childList.get(i).intValue();
            
            DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
            treeModel.nodesChanged(groupNode, childIndices);
        }
        
        return pluginModified;
    }
    
    /**
     * Sends an update event for the RecordNode and all children.
     *
     * @param       tree            The tree being processed
     * @param       groupPath       Tree path for the group
     * @param       recordNode      Tree node for the record
     * @return                      TRUE if the plugin was modified
     */
    protected void updateRecordNode(JTree tree, RecordNode recordNode)
    {        
        //
        // Ignore each subgroup and record in the current group
        //
        int childCount = recordNode.getChildCount();
        if (childCount > 0)
        {
        	int[] childIndices = new int[childCount];
        	for (int i=0; i<childCount; i++)
        		childIndices[i] = i;        
        //
	        // Notify the tree model that nodes have been changed
	        //
	        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
	        treeModel.nodesChanged(recordNode, childIndices);
        }
    }
    
    /**
     * Toggle the ignore flag for a record
     *
     * @param       tree            The tree being processed
     * @param       treePath        Tree path for the record
     * @param       recordNode      Tree node for the record
     * @return                      TRUE if the plugin was modified
     */
    protected boolean toggleRecordIgnore(JTree tree, TreePath treePath, RecordNode recordNode) {
        Plugin plugin = ((PluginNode)treePath.getPathComponent(0)).getPlugin();
        PluginRecord record = recordNode.getRecord();
        record.setIgnore(record.isIgnored() ? false : true);
        if (!record.isIgnored()) {
            Map<Integer, FormInfo> formMap = plugin.getFormMap();
            Integer objFormID = new Integer(record.getFormID());
            if (formMap.get(objFormID) == null) {
                GroupNode parentNode = (GroupNode)recordNode.getParent();
                PluginGroup parentGroup = parentNode.getGroup();
                FormInfo formInfo = new FormInfo(record, record.getRecordType(), record.getFormID(), record.getEditorID());
                formInfo.setRecordNode(recordNode);
                formInfo.setParentFormID(parentGroup.getGroupParentID());
                plugin.getFormList().add(formInfo);
                formMap.put(objFormID, formInfo);
            }
        }
        
        return true;
    }

    /**
     * Copy the selected records to the clipboard.  A record will not be copied if it has
     * already been copied to the clipboard.  Records referenced by the selected records
     * will also be copied to the clipboard.
     *
     * @param       pluginTree              The plugin tree being processed
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    protected void copyRecords(JTree pluginTree) throws DataFormatException, IOException, PluginException {
        PluginNode pluginNode = (PluginNode)pluginTree.getModel().getRoot();
        Plugin plugin = pluginNode.getPlugin();
        FormAdjust formAdjust = pluginNode.getFormAdjust();
        
        //
        // At least one record must be selected
        //
        TreePath[] treePaths = pluginTree.getSelectionPaths();
        if (treePaths == null) {
            JOptionPane.showMessageDialog(this, "You must select at least one record to copy.", 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //
        // Copy the selected records to the clipboard
        //
        for (TreePath treePath : treePaths) {
            DefaultMutableTreeNode pathNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            if (pathNode instanceof PluginNode) {
                JOptionPane.showMessageDialog(this,  
                                        "The entire plugin can not be selected.  The selection will be ignored.",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
            } else if (pathNode instanceof GroupNode) {
                PluginGroup group = ((GroupNode)pathNode).getGroup();
                if (group.getGroupType() == PluginGroup.TOP) {
                    PluginNode rootNode = (PluginNode)clipboardTreeModel.getRoot();
                    int childCount = rootNode.getChildCount();
                    String groupRecordType = group.getGroupRecordType();
                    for (int i=0; i<childCount; i++) {
                        GroupNode parentNode = (GroupNode)rootNode.getChildAt(i);
                        if (parentNode.getGroup().getGroupRecordType().equals(groupRecordType)) {
                            List<PluginRecord> recordList = group.getRecordList();
                            for (PluginRecord record : recordList) {
                                if (record instanceof PluginGroup)
                                    copyGroup(plugin, formAdjust, (PluginGroup)record, parentNode);
                                else
                                    copyRecord(plugin, formAdjust, record, parentNode);
                            }
                            
                            break;
                        }
                    }
                } else {
                    GroupNode parentNode = createHierarchy(plugin, formAdjust, group);
                    copyGroup(plugin, formAdjust, group, parentNode);
                }
            } else if (pathNode instanceof RecordNode) {
                PluginRecord record = ((RecordNode)pathNode).getRecord();
                GroupNode parentNode = createHierarchy(plugin, formAdjust, record);
                copyRecord(plugin, formAdjust, record, parentNode);
            } else {
                JOptionPane.showMessageDialog(this, 
                                        "An individual subrecord can not be copied.  The selection will be ignored.",
                                        "Warning", JOptionPane.WARNING_MESSAGE);
            }
        }        
    }
 
    /**
     * Create the clipboard group hierarchy for a record or group
     *
     * @param       plugin                  Source plugin
     * @param       formAdjust              Plugin form adjustment
     * @param       record                  Plugin record or group
     * @return                              The parent clipboard tree node
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private GroupNode createHierarchy(Plugin plugin, FormAdjust formAdjust, PluginRecord record)
                                            throws DataFormatException, IOException, PluginException {
        GroupNode parentNode = null;
        GroupNode groupNode;
        RecordNode recordNode;
        PluginGroup parentGroup;
        DefaultMutableTreeNode node;
        List<PluginRecord> recordList;
        int childCount, recordCount;
        
        //
        // Create the clipboard group hierarchy for the new rcord or group
        //
        parentGroup = clipboard.createHierarchy(record, formAdjust);
        
        //
        // Locate the top group for the parent group
        //
        List<PluginGroup> groupList = new ArrayList<PluginGroup>(10);
        PluginGroup topGroup = parentGroup;
        while (topGroup.getGroupType() != PluginGroup.TOP) {
            groupList.add(topGroup);
            topGroup = (PluginGroup)topGroup.getParent();
        }
        
        //
        // Locate the clipboard tree node for the top group
        //
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)clipboardTreeModel.getRoot();
        childCount = rootNode.getChildCount();
        for (int i=0; i<childCount; i++) {
            parentNode = (GroupNode)rootNode.getChildAt(i);
            if (parentNode.getGroup() == topGroup)
                break;
        }
        
        //
        // Create clipboard tree nodes for each level in the group hierarchy
        //
        for (int i=groupList.size()-1; i>=0; i--) {
            PluginGroup group = groupList.get(i);
            groupNode = null;
            
            //
            // Check for an existing tree node for the current group
            //
            childCount = parentNode.getChildCount();
            boolean foundGroup = false;
            for (int j=0; j<childCount; j++) {
                node = (DefaultMutableTreeNode)parentNode.getChildAt(j);
                if (node.getUserObject() == group) {
                    groupNode = (GroupNode)node;
                    foundGroup = true;
                    break;
                }
            }
            
            //
            // Create a new tree node if we didn't find the group.  For a Worldspace, Cell or Topic
            // group, we also need to create the WRLD, CELL or DIAL record.
            //
            if (!foundGroup) {
                int groupType = group.getGroupType();
                if (groupType == PluginGroup.WORLDSPACE || groupType == PluginGroup.CELL || groupType == PluginGroup.TOPIC) {
                    int formID = group.getGroupParentID();
                    parentGroup = parentNode.getGroup();
                    recordList = parentGroup.getRecordList();
                    int index = recordList.indexOf(group);
                    if (index < 1)
                        throw new PluginException("Type "+groupType+" subgroup not preceded by WRLD/CELL/DIAL record");
                    
                    PluginRecord prevRecord = recordList.get(index-1);
                    String recordType = prevRecord.getRecordType();
                    if (!recordType.equals("WRLD") && !recordType.equals("CELL") && !recordType.equals("DIAL"))
                        throw new PluginException("Type "+groupType+" subgroup not preceded by WRLD/CELL/DIAL record");
                    
                    if (prevRecord.getFormID() != formID)
                        throw new PluginException("WRLD/CELL/DIAL record form ID mismatch");
                    
                    recordNode = new RecordNode(prevRecord);
                    createRecordChildren(recordNode);
                    parentNode.add(recordNode);
                    
                    groupNode = new GroupNode(group);
                    parentNode.add(groupNode);
                    
                    int[] childIndices = new int[2];
                    childIndices[0] = childCount;
                    childIndices[1] = childCount+1;
                    clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
                } else {
                    groupNode = new GroupNode(group);
                    int[] childIndices = new int[1];
                    if (groupType == PluginGroup.CELL_DISTANT || groupType == PluginGroup.CELL_PERSISTENT) {
                        childIndices[0] = 0;
                        parentNode.insert(groupNode, 0);
                    } else {
                        parentNode.add(groupNode);
                        childIndices[0] = childCount;
                    }
                    
                    clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
                }
                
                clipboardModified = true;
            }
            
            //
            // The current group node becomes the new parent node
            //
            parentNode = groupNode;
        }
        
        //
        // The WRLD, CELL or DIAL record was copied if we are creating the hierarchy for a
        // Worldspace, Cell or Topic subgroup.  So we need to create the corresponding
        // clipboard tree node if it doesn't already exist.
        //
        if (record instanceof PluginGroup) {
            PluginGroup group = (PluginGroup)record;
            int groupType = group.getGroupType();
            if (groupType == PluginGroup.WORLDSPACE || groupType == PluginGroup.CELL || groupType == PluginGroup.TOPIC) {
                
                //
                // See if the WRLD/CELL/DIAL record node already exists in the clipboard tree
                //
                int formID = formAdjust.adjustFormID(group.getGroupParentID());
                childCount = parentNode.getChildCount();
                boolean foundRecord = false;
                for (int i=0; i<childCount; i++) {
                    node = (DefaultMutableTreeNode)parentNode.getChildAt(i);
                    if (node instanceof RecordNode) {
                        if (((RecordNode)node).getRecord().getFormID() == formID) {
                            foundRecord = true;
                            break;
                        }
                    }
                }
                
                //
                // Create new record and group nodes if they don't exist yet
                //
                if (!foundRecord) {
                    recordList = parentNode.getGroup().getRecordList();
                    recordCount = recordList.size();
                    for (int i=0; i<recordCount; i++) {
                        PluginRecord checkRecord = recordList.get(i);
                        if (!(checkRecord instanceof PluginGroup)) {
                            if (checkRecord.getFormID() == formID) {
                                if (i > recordCount-2)
                                    throw new PluginException("WRLD/CELL/DIAL record not followed by subgroup");
                                
                                recordNode = new RecordNode(checkRecord);
                                if (checkRecord.getRecordLength() != 0)
                                    recordNode.add(new DefaultMutableTreeNode(null));
                                
                                parentNode.add(recordNode);
                                groupNode = new GroupNode((PluginGroup)recordList.get(i+1));
                                parentNode.add(groupNode);
                                int[] childIndices = new int[2];
                                childIndices[0] = childCount;
                                childIndices[1] = childCount+1;
                                clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
                                clipboardModified = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        return parentNode;
    }
    
    /**
     * Copy a group to the clipboard
     *
     * @param       plugin                  Source plugin
     * @param       formAdjust              Plugin form adjustment
     * @param       group                   Plugin group to be copied
     * @param       parentNode              Clipboard parent node
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private void copyGroup(Plugin plugin, FormAdjust formAdjust, PluginGroup group, GroupNode parentNode)
                                            throws DataFormatException, IOException, PluginException {
        PluginGroup parentGroup = parentNode.getGroup();
        
        //
        // Adjust the group label for Worldspace, Cell and Topic groups since they use the
        // associated record form ID for the group label
        //
        int groupType = group.getGroupType();
        byte[] groupLabel = group.getGroupLabel();
        switch (groupType) {
            case PluginGroup.WORLDSPACE:
            case PluginGroup.CELL:
            case PluginGroup.CELL_PERSISTENT:
            case PluginGroup.CELL_TEMPORARY:
            case PluginGroup.CELL_DISTANT:
            case PluginGroup.TOPIC:
                int oldFormID = SerializedElement.getInteger(groupLabel, 0);
                int newFormID = formAdjust.adjustFormID(oldFormID);
                SerializedElement.setInteger(newFormID, groupLabel, 0);
        }
        
        //
        // Create the clipboard group if it doesn't exist yet
        //
        int childCount = parentNode.getChildCount();
        GroupNode clipboardNode = null;
        boolean foundGroup = false;
        for (int index=0; index<childCount; index++) {
            TreeNode node = (TreeNode)parentNode.getChildAt(index);
            if (node instanceof GroupNode) {
                clipboardNode = (GroupNode)node;
                PluginGroup checkGroup = clipboardNode.getGroup();
                if (checkGroup.getGroupType() == groupType) {
                    byte[] checkLabel = checkGroup.getGroupLabel();
                    if (groupLabel[0] == checkLabel[0] && groupLabel[1] == checkLabel[1] &&
                                                          groupLabel[2] == checkLabel[2] && 
                                                          groupLabel[3] == checkLabel[3]) {
                        foundGroup = true;
                        break;
                    }
                }
            }
        }
        
        if (!foundGroup) {
            PluginGroup clipboardGroup = new PluginGroup(groupType, groupLabel);
            clipboardGroup.setParent(parentGroup);
            if (groupType == PluginGroup.CELL_DISTANT || groupType == PluginGroup.CELL_PERSISTENT)
                parentGroup.getRecordList().add(0, clipboardGroup);
            else
                parentGroup.getRecordList().add(clipboardGroup);
            
            clipboardNode = new GroupNode(clipboardGroup);
            int[] childIndices = new int[1];
            if (groupType == PluginGroup.CELL_DISTANT || groupType == PluginGroup.CELL_PERSISTENT) {
                childIndices[0] = 0;
                parentNode.insert(clipboardNode, 0);
            } else {
                childIndices[0] = childCount;
                parentNode.add(clipboardNode);
            }
            
            clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
            clipboardModified = true;            
        }
        
        //
        // Copy each record in the group to the clipboard
        //
        List<PluginRecord> recordList = group.getRecordList();
        for (PluginRecord record : recordList) {
            if (record instanceof PluginGroup)
                copyGroup(plugin, formAdjust, (PluginGroup)record, clipboardNode);
            else
                copyRecord(plugin, formAdjust, record, clipboardNode);
        }
    }
    
    /**
     * Copy a record to the clipboard
     *
     * @param       plugin                  Source plugin
     * @param       formAdjust              Plugin form adjustment
     * @param       record                  Plugin record to be copied
     * @param       parentNode              Clipboard parent node
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private void copyRecord(Plugin plugin, FormAdjust formAdjust, PluginRecord record, GroupNode parentNode) 
                                            throws DataFormatException, IOException, PluginException {
        PluginGroup parentGroup = parentNode.getGroup();
        String recordType = record.getRecordType();

        //
        // Nothing to do if the record has already been copied to the clipboard.  
        //
        int formID = formAdjust.adjustFormID(record.getFormID());
        Integer mapID = new Integer(formID);
        Map<Integer, FormInfo> formMap = clipboard.getFormMap();
        if (formMap.get(mapID) != null)
            return;
        
        //
        // Copy the record to the clipboard
        //
        clipboard.copyRecord(record, formAdjust);
        PluginRecord clipboardRecord = (PluginRecord)formMap.get(mapID).getSource();
        clipboardModified = true;
        
        //
        // Update the clipboard tree
        //
        List<PluginRecord> recordList = parentGroup.getRecordList();
        int recordCount = recordList.size();
        int childCount = parentNode.getChildCount();
        for (int i=0; i<recordCount; i++) {
            PluginRecord checkRecord = recordList.get(i);
            if (checkRecord == clipboardRecord) {
                if (recordType.equals("WRLD") || recordType.equals("CELL") || recordType.equals("DIAL")) {
                    if (i > recordCount-2)
                        throw new PluginException("WRLD/CELL/DIAL record not followed by subgroup");
                    
                    checkRecord = recordList.get(i+1);
                    if (!(checkRecord instanceof PluginGroup))
                        throw new PluginException("WRLD/CELL/DIAL record not followed by subgroup");
                    
                    PluginGroup clipboardGroup = (PluginGroup)checkRecord;
                    if (clipboardGroup.getGroupParentID() != formID)
                        throw new PluginException("WRLD/CELL/DIAL record not followed by subgroup");
                    
                    RecordNode recordNode = new RecordNode(clipboardRecord);
                    createRecordChildren(recordNode);
                    parentNode.add(recordNode);
                    
                    GroupNode groupNode = new GroupNode(clipboardGroup);
                    parentNode.add(groupNode);
                    
                    int[] childIndices = new int[2];
                    childIndices[0] = childCount;
                    childIndices[1] = childCount+1;
                    clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
                } else {
                    RecordNode recordNode = new RecordNode(clipboardRecord);
                    createRecordChildren(recordNode);
                    
                    int index;
                    for (index=0; index<childCount; index++) {
                        TreeNode node = (TreeNode)parentNode.getChildAt(index);
                        if (node instanceof RecordNode)
                            if (recordNode.compareTo((RecordNode)node) < 0)
                                break;
                    }
                    
                    parentNode.insert(recordNode, index);
                    
                    int[] childIndices = new int[1];
                    childIndices[0] = index;
                    clipboardTreeModel.nodesWereInserted(parentNode, childIndices);
                }
            }
        }
        
        //
        // Copy referenced records
        //
        if (copyReferences)
            copyRecordReferences(plugin, formAdjust, record);
    }
    
    /**
     * Copy record references
     *
     * @param       plugin                  The source plugin
     * @param       formAdjust              The plugin form adjustment
     * @param       record                  The plugin record being copied
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private void copyRecordReferences(Plugin plugin, FormAdjust formAdjust, PluginRecord record) 
                                            throws DataFormatException, IOException, PluginException {
        Map<Integer, FormInfo> formMap = plugin.getFormMap();
        int masterCount = plugin.getMasterList().size();
        Map<Integer, FormInfo> clipboardMap = clipboard.getFormMap();
        
        //
        // Process the subrecords
        //
        List<PluginSubrecord> subrecordList = record.getSubrecords();
        for (PluginSubrecord subrecord : subrecordList) {
            int[][] references = subrecord.getReferences();
            if (references == null)
                continue;
          
            //
            // Process each reference
            //
            for (int i=0; i<references.length; i++) {
                int formID = references[i][1];
                if (formID == 0)
                    continue;
        
                int masterID = formID>>>24;
                if (masterID < masterCount)
                    continue;
                
                Integer objFormID = new Integer(formID);
                if (clipboardMap.get(objFormID) != null)
                    continue;
        
                FormInfo formInfo = formMap.get(objFormID);
                if (formInfo == null)
                    continue;
                
                PluginRecord refRecord = (PluginRecord)formInfo.getSource();
                if (refRecord == null)
                    continue;
                
                //
                // Copy the referenced record
                //
                GroupNode parentNode = createHierarchy(plugin, formAdjust, refRecord);
                copyRecord(plugin, formAdjust, refRecord, parentNode);
            }
        }
    }
    
    /**
     * Validate a plugin tree by removing nodes for ignored records and empty groups that are 
     * no longer part of the plugin.  The plugin tree must be validated whenever the plugin 
     * is saved because the save process removes ignored records and empty groups.  Note that
     * the top-level groups are never removed from the plugin group list even though they are
     * not written to the plugin file if they are empty.
     *
     * @param       tree                The plugin tree
     */
    protected void validateTree(JTree tree) {
        DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();
        PluginNode root = (PluginNode)treeModel.getRoot();
        Enumeration nodes = root.children();
        while (nodes.hasMoreElements()) {
            GroupNode groupNode = (GroupNode)nodes.nextElement();
            boolean subtreeChanged = validateTreeGroup(treeModel, groupNode);
            if (subtreeChanged)
                treeModel.nodeStructureChanged(groupNode);
        }
    }
    
    /**
     * Validate a group tree node by removing ignored records and empty subgroups if they
     * are no longer part of the plugin.
     *
     * @param       treeModel           The tree model
     * @param       groupNode           The group node being validated
     * @return                          TRUE if the node structure has changed
     */
    private boolean validateTreeGroup(DefaultTreeModel treeModel, GroupNode parentNode) {
        boolean nodeStructureChanged = false;
        PluginGroup parentGroup = parentNode.getGroup();
        int index = 0;
        while (index < parentNode.getChildCount()) {
            boolean removeNode = false;
            TreeNode node = (TreeNode)parentNode.getChildAt(index);
            if (node instanceof GroupNode) {
                GroupNode groupNode = (GroupNode)node;
                PluginGroup group = groupNode.getGroup();
                boolean subtreeChanged = validateTreeGroup(treeModel, groupNode);
                if (subtreeChanged)
                    nodeStructureChanged = true;
                
                if (group.isEmpty()) {
                    List<PluginRecord> recordList = parentGroup.getRecordList();
                    removeNode = true;
                    for (PluginRecord groupRecord : recordList) {
                        if (groupRecord == group) {
                            removeNode = false;
                            break;
                        }
                    }
                }
            } else if (node instanceof RecordNode) {
                RecordNode recordNode = (RecordNode)node;
                PluginRecord record = recordNode.getRecord();
                if (record.isIgnored())
                    removeNode = true;
                else // Validate subrecords
                {
                    boolean subtreeChanged = validateTreeRecord(treeModel, recordNode);
                    if (subtreeChanged)
                        nodeStructureChanged = true;
                }
            }
            
            if (removeNode) {
                parentNode.remove(index);
                nodeStructureChanged = true;
            } else {
                index++;
            }
        }
        
        return nodeStructureChanged;
    }

    /**
     * Validate a record tree node by removing leaf nodes related to removed subrecords.
     *
     * @param       treeModel           The tree model
     * @param       recordNode          The record node being validated
     * @return                          TRUE if the node structure has changed
     */
    private boolean validateTreeRecord(DefaultTreeModel treeModel, RecordNode parentRecNode) {
        boolean nodeStructureChanged = false;
        PluginRecord parentRec = (PluginRecord)parentRecNode.getUserObject();
        int index = 0;
        List<PluginSubrecord> subrecs = new ArrayList<PluginSubrecord>();
        try
        {
            subrecs = parentRec.getSubrecords();
        } 
        catch (Exception ex)
        {
        	return false;
        }
        while (index < parentRecNode.getChildCount()) {
            boolean removeNode = false;
            DefaultMutableTreeNode subNode = (DefaultMutableTreeNode)parentRecNode.getChildAt(index);
            PluginSubrecord subrec = (PluginSubrecord)subNode.getUserObject();
            if (subrec == null && Main.debugMode)
            {
            	String errStr = "Subrec is null at index " + index + " with parent "
            	              + parentRec.getEditorID() + "(" + parentRec.getFormID() + ")" +" with " + subrecs.size() + " subrecords.\n";
            	System.out.printf(errStr);
            }
            boolean removeSub = true;
			for (PluginSubrecord sub : subrecs)
			{
				if (subrec != null && subrec.equals(sub)) // Looking at IDs, not content
				{
					removeSub = false;
					break;
				}
			}
            if (removeSub) {
            	parentRecNode.remove(index);
                nodeStructureChanged = true;
            } else {
                index++;
            }
        }
        
        return nodeStructureChanged;
    }
}

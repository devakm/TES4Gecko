package TES4Gecko;

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
import javax.swing.tree.*;

/**
 * The PluginNode is the root node for a plugin tree
 */
public class PluginNode extends DefaultMutableTreeNode {
    
    /** Distinct tree paths */
    private List<TreePath> distinctPaths;
    
    /** Plugin form adjustment */
    private FormAdjust formAdjust;
    
    /** 
     * Create a new instance of PluginNode 
     *
     * @param       plugin          The plugin
     */
    public PluginNode(Plugin plugin) {
        super(plugin);
    }
    
    /**
     * Get the plugin
     *
     * @return                      The plugin
     */
    public Plugin getPlugin() {
        return (Plugin)getUserObject();
    }
    
    /**
     * Get the distinct path list or null if the path list has not been set
     *
     * @return                      The distinct path list
     */
    public List<TreePath> getDistinctPaths() {
        return distinctPaths;
    }
    
    /**
     * Set the distinct path list
     *
     * @param       distinctPaths   The distinct path list
     */
    public void setDistinctPaths(List<TreePath> distinctPaths) {
        this.distinctPaths = distinctPaths;
    }
    
    /**
     * Get the plugin form adjustment
     *
     * @return                      The plugin form adjustment or null
     */
    public FormAdjust getFormAdjust() {
        return formAdjust;
    }
    
    /**
     * Set the plugin form adjustment
     *
     * @param       formAdjust      The plugin form adjustment
     */
    public void setFormAdjust(FormAdjust formAdjust) {
        this.formAdjust = formAdjust;
    }
    
    /**
     * Insert a group node in sorted order.  Note that a plugin node has only
     * plugin groups as children.
     *
     * @param       groupNode       The group node
     */
    public void insert(GroupNode groupNode) {
        int count = getChildCount();
        int index;
        for (index=0; index<count; index++)
            if (groupNode.compareTo((GroupNode)getChildAt(index)) < 0)
                break;
        
        insert(groupNode, index);
    }
    
    /**
     * Build the tree nodes for this plugin
     *
     * @param       task                    Worker task or null
     * @exception   InterruptedException    The worker thread was interrupted
     */
    public void buildNodes(WorkerTask task) throws InterruptedException {
        StatusDialog statusDialog = (task!=null ? task.getStatusDialog() : null);
        List<PluginGroup> groupList = getPlugin().getGroupList();
        int groupCount = groupList.size();
        int processedCount = 0;
        int currentProgress = 0;
        if (statusDialog != null)
            statusDialog.updateMessage("Creating tree for "+getPlugin().getName());

        //
        // Remove all of the children
        //
        removeAllChildren();
        
        //
        // Process the top-level groups
        //
        for (PluginGroup group : groupList) {
            GroupNode groupNode = new GroupNode(group);
            createGroupChildren(groupNode, group);
            insert(groupNode);
            if (task != null && task.interrupted())
                throw new InterruptedException("Request canceled");
                
            processedCount++;
            int newProgress = (processedCount*100)/groupCount;
            if (newProgress >= currentProgress+5) {
                currentProgress = newProgress;
                if (statusDialog != null)
                    statusDialog.updateProgress(currentProgress);
            }
        }        
    }
    
    /**
     * Create the children for a group node.  The records for a top-level group will be
     * sorted based on the editor ID (WRLD, CELL and DIAL groups will not be sorted
     * since they contain subgroups).  
     * 
     * Subrecord nodes are not created at this point in order to reduce storage use and 
     * execution time.  Instead, the subrecord nodes will be created the first time that
     * the record node is expanded.
     *
     * @param       groupNode               The group node
     * @param       group                   The plugin group
     */
    private void createGroupChildren(GroupNode groupNode, PluginGroup group) {
        Map<Integer, FormInfo> formMap = getPlugin().getFormMap();
        
        //
        // Process each record in the list
        //
        List<PluginRecord> recordList = group.getRecordList();
        for (PluginRecord record : recordList) {
            if (record instanceof PluginGroup) {
                
                //
                // Process a subgroup
                //
                PluginGroup subgroup = (PluginGroup)record;
                GroupNode subgroupNode = new GroupNode(subgroup);
                createGroupChildren(subgroupNode, subgroup);
                groupNode.add(subgroupNode);
                
            } else {
                
                RecordNode recordNode = new RecordNode(record);
                
                //
                // Create a dummy subrecord node if the record has any data
                //
                if (record.getRecordLength() != 0)
                    recordNode.add(new DefaultMutableTreeNode(null));

                //
                // Set the record node in the associated record form information
                //
                FormInfo formInfo = formMap.get(new Integer(record.getFormID()));
                if (formInfo != null)
                    formInfo.setRecordNode(recordNode);
                
                //
                // Sort the records based on the record editor ID.  We will not sort
                // the records for the CELL, DIAL or WRLD groups since the record
                // order is important.
                //
                int groupType = group.getGroupType();
                if (groupType == PluginGroup.TOP) {
                    String recordType = group.getGroupRecordType();
                    if (recordType.equals("CELL") || recordType.equals("DIAL") || recordType.equals("WRLD"))
                        groupNode.add(recordNode);
                    else
                        groupNode.insert(recordNode);
                } else if (groupType == PluginGroup.CELL_DISTANT || groupType == PluginGroup.CELL_PERSISTENT ||
                                                                    groupType == PluginGroup.CELL_TEMPORARY) {
                    groupNode.insert(recordNode);
                } else {
                    groupNode.add(recordNode);
                }
            }
        }
    }    
}

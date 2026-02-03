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
 * Task to compare two plugins and record the differences
 */
public class CompareTask extends WorkerTask {
    
    /** Root node for plugin A */
    private PluginNode pluginNodeA;
    
    /** Record map for plugin A */
    private Map<Integer, RecordNode> recordMapA;
    
    /** Root node for plugin B */
    private PluginNode pluginNodeB;
    
    /** Record map for plugin B */
    private Map<Integer, RecordNode> recordMapB;
    
    /** 
     * Create a new instance of CompareTask 
     *
     * @param       statusDialog    The status dialog
     * @param       pluginNodeA     The first plugin node
     * @param       pluginNodeB     The second plugin node
     */
    public CompareTask(StatusDialog statusDialog, PluginNode pluginNodeA, PluginNode pluginNodeB) {
        super(statusDialog);
        this.pluginNodeA = pluginNodeA;
        this.pluginNodeB = pluginNodeB;
    }

    /**
     * Compare two plugins
     *
     * @param       parent          The parent frame
     * @param       pluginNodeA     The first plugin node
     * @param       pluginNodeB     The second plugin node
     * @return                      TRUE if the comparison was completed
     */
    public static boolean comparePlugins(JFrame parent, PluginNode pluginNodeA, PluginNode pluginNodeB) {
        boolean completed = false;
        
        //
        // Create the status dialog
        //
        String text = "Comparing '"+pluginNodeA.getPlugin().getName()+"' and '"+pluginNodeB.getPlugin().getName()+"'";
        StatusDialog statusDialog = new StatusDialog(parent, text, "Compare Plugins");
        
        //
        // Create the worker task
        //
        CompareTask worker = new CompareTask(statusDialog, pluginNodeA, pluginNodeB);
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
            JOptionPane.showMessageDialog(parent, "Unable to compare plugins",
                                          "Compare Plugins", JOptionPane.INFORMATION_MESSAGE);
        
        return completed;
    }

    /**
     * Run the executable code for the thread
     */
    public void run() {
        boolean completed = false;
        int currentProgress = 0, processedCount = 0, newProgress;
        try {
            
            //
            // Build the record map for plugin A
            //
            recordMapA = buildRecordMap(pluginNodeA);
            int totalCount = pluginNodeA.getChildCount();
            
            //
            // Build the record map for plugin B
            //
            recordMapB = buildRecordMap(pluginNodeB);
            totalCount += pluginNodeB.getChildCount();
            
            //
            // Compare the first plugin to the second plugin
            //
            pluginNodeA.setDistinctPaths(new ArrayList<TreePath>(100));
            int groupCount = pluginNodeA.getChildCount();
            for (int i=0; i<groupCount; i++) {
                GroupNode groupNode = (GroupNode)pluginNodeA.getChildAt(i);
                compareGroupChildren(pluginNodeA, groupNode, recordMapB);
                
                if (interrupted())
                    throw new InterruptedException("Request canceled");
                
                processedCount++;
                newProgress = (processedCount*100)/totalCount;
                if (newProgress > currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }
            
            //
            // Compare the second plugin to the first plugin
            //
            pluginNodeB.setDistinctPaths(new ArrayList<TreePath>(100));
            groupCount = pluginNodeB.getChildCount();
            for (int i=0; i<groupCount; i++) {
                GroupNode groupNode = (GroupNode)pluginNodeB.getChildAt(i);
                compareGroupChildren(pluginNodeB, groupNode, recordMapA);
                
                if (interrupted())
                    throw new InterruptedException("Request canceled");
                
                processedCount++;
                newProgress = (processedCount*100)/totalCount;
                if (newProgress > currentProgress+5) {
                    currentProgress = newProgress;
                    getStatusDialog().updateProgress(currentProgress);
                }
            }
            
            //
            // Comparison completed
            //
            completed = true;
        } catch (InterruptedException exc) {
            WorkerDialog.showMessageDialog(getStatusDialog(), "Request canceled", "Interrupted", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Exception while comparing plugins", exc);
        }
        
        //
        // All done
        //
        getStatusDialog().closeDialog(completed);
    }
    
    /**
     * Build the record map for a plugin
     *
     * @param       pluginNode      The root node for the plugin
     * @return                      The record map
     */
    private Map<Integer, RecordNode> buildRecordMap(PluginNode pluginNode) {
        Plugin plugin = pluginNode.getPlugin();
        Map<Integer, RecordNode> recordMap = new HashMap<Integer, RecordNode>(plugin.getRecordCount());
        int count = pluginNode.getChildCount();
        
        //
        // Process each top-level group for the plugin
        //
        for (int i=0; i<count; i++) {
            TreeNode node = pluginNode.getChildAt(i);
            if (node instanceof GroupNode)
                mapGroupRecords((GroupNode)node, recordMap);
            else
                throw new UnsupportedOperationException("Top-level node is not a group");
        }
        
        return recordMap;
    }
    
    /**
     * Map the records for a group
     *
     * @param       groupNode       The group node
     * @param       recordMap       The record map
     */
    private void mapGroupRecords(GroupNode groupNode, Map<Integer, RecordNode> recordMap) {
        int count = groupNode.getChildCount();
        
        //
        // Process each record for the group
        //
        for (int i=0; i<count; i++) {
            TreeNode node = groupNode.getChildAt(i);
            if (node instanceof GroupNode) {
                mapGroupRecords((GroupNode)node, recordMap);
            } else if (node instanceof RecordNode) {
                RecordNode recordNode = (RecordNode)node;
                PluginRecord record = recordNode.getRecord();
                if (!record.isIgnored())
                    recordMap.put(new Integer(record.getFormID()), recordNode);
            } else {
                throw new UnsupportedOperationException("Child node is not a group or record node");
            }
        }
    }
    
    /**
     * Compare the group children.  A record will be distinct if it is not found in the
     * comparision plugin or if the group ancestors are not the same.  When a distinct
     * record is found, each ancestor group will also be marked as distinct.
     *
     * @param       pluginNode      The plugin node
     * @param       groupNode       The group node
     * @param       recordMap       The comparison record map
     */
    private void compareGroupChildren(PluginNode pluginNode, GroupNode groupNode, Map<Integer, RecordNode> recordMap) {
        boolean expandGroup = false;
        List<TreePath> distinctPaths = pluginNode.getDistinctPaths();
        int distinctCount = distinctPaths.size();
        int childCount = groupNode.getChildCount();
        
        //
        // Compare each record for the group
        //
        for (int i=0; i<childCount; i++) {
            TreeNode node = groupNode.getChildAt(i);
            if (node instanceof GroupNode) {
                compareGroupChildren(pluginNode, (GroupNode)node, recordMap);
            } else if (node instanceof RecordNode) {
                RecordNode recordNode = (RecordNode)node;
                PluginRecord record = recordNode.getRecord();
                if (record.isIgnored()) {
                    recordNode.setDistinct(true);
                } else {
                    RecordNode cmpNode = recordMap.get(new Integer(record.getFormID()));
                    if (cmpNode == null || !recordNode.equals(cmpNode)) {
                        recordNode.setDistinct(true);
                    } else {
                        TreeNode[] path = recordNode.getPath();
                        TreeNode[] cmpPath = cmpNode.getPath();
                        if (path.length != cmpPath.length) {
                            recordNode.setDistinct(true);
                        } else {
                            for (int j=0; j<path.length; j++) {
                                if (!(path[j] instanceof GroupNode))
                                    break;
                                
                                PluginGroup group = ((GroupNode)path[j]).getGroup();
                                byte[] groupLabel = group.getGroupLabel();
                                PluginGroup cmpGroup = ((GroupNode)cmpPath[j]).getGroup();
                                byte[] cmpGroupLabel = cmpGroup.getGroupLabel();
                                if (group.getGroupType() != cmpGroup.getGroupType() ||
                                                            groupLabel[0] != cmpGroupLabel[0] ||
                                                            groupLabel[1] != cmpGroupLabel[1] ||
                                                            groupLabel[2] != cmpGroupLabel[2] ||
                                                            groupLabel[3] != cmpGroupLabel[3]) {
                                    recordNode.setDistinct(true);
                                    break;
                                }
                            }
                        }
                    }
                }
          
                //
                // Mark each ancestor group as distinct if the record is distinct
                //
                if (recordNode.isDistinct()) {
                    expandGroup = true;
                    TreeNode parentNode = recordNode;
                    while ((parentNode=parentNode.getParent()) != null) {
                        if (!(parentNode instanceof GroupNode))
                            break;
                        
                        GroupNode parentGroup = (GroupNode)parentNode;
                        if (parentGroup.isDistinct())
                            break;
                        
                        parentGroup.setDistinct(true);
                    }
                }
            }
        }
        
        //
        // Save the tree path if the current group contains one or more distinct records.
        // We need to add the path only if a descendent node has not already added its own
        // path.
        //
        if (expandGroup && distinctPaths.size() == distinctCount) {
            TreeNode[] pathNodes = groupNode.getPath();
            TreePath treePath = new TreePath(pathNodes);
            distinctPaths.add(treePath);
        }
    }
}

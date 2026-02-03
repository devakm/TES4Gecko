package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * A GroupNode in a tree represents a plugin group
 */
public class GroupNode extends DefaultMutableTreeNode implements Comparable<GroupNode> {
    
    /** The group is distinct */
    private boolean distinct = false;
    
    /** 
     * Create a new instance of GroupNode 
     *
     * @param       group           The plugin group
     */
    public GroupNode(PluginGroup group) {
        super(group);
    }
    
    /**
     * Get the plugin group
     *
     * @return                      The plugin group
     */
    public PluginGroup getGroup() {
        return (PluginGroup)getUserObject();
    }
    
    /**
     * Check if the group is distinct.
     *
     * @return                      TRUE if the group is distinct
     */
    public boolean isDistinct() {
        return distinct;
    }
    
    /**
     * Set the group as distinct or shared
     *
     * @param       distinct        TRUE if the group is distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    
    /**
     * Insert a group node in sorted order.  Group nodes are inserted before any
     * record nodes.
     *
     * @param       groupNode       The group node
     */
    public void insert(GroupNode groupNode) {
        int count = getChildCount();
        int index;
        for (index=0; index<count; index++) {
            TreeNode node = getChildAt(index);
            if (!(node instanceof GroupNode))
                break;
            
            if (groupNode.compareTo((GroupNode)node) < 0)
                break;
        }
        
        insert(groupNode, index);
    }
    
    /**
     * Insert a record node in sorted order.  Record nodes will be inserted after
     * any group nodes.
     *
     * @param       recordNode      The record node
     */
    public void insert(RecordNode recordNode) {
        int count = getChildCount();
        int index;
        for (index=0; index<count; index++) {
            TreeNode node = getChildAt(index);
            if (!(node instanceof RecordNode))
                continue;
            
            if (recordNode.compareTo((RecordNode)node) < 0)
                break;
        }
        
        insert(recordNode, index);
    }
    
    /**
     * Return the hash code for this group node.  The hash code is the
     * hash code for the plugin group.
     *
     * @return                      The hash code
     */
    public int hashCode() {
        return getGroup().hashCode();
    }
    
    /**
     * Determine if two group nodes are equal.  Two group nodes are equal if
     * the plugin groups are identical.
     *
     * @param       obj             The comparison node
     * @return                      TRUE if the group nodes are equal
     */
    public boolean equals(Object obj) {
        boolean areEqual = false;
        if (obj instanceof GroupNode)
            areEqual = getGroup().isIdentical(((GroupNode)obj).getGroup());
        
        return areEqual;
    }
    
    /**
     * Compare two group nodes.  The comparison is performed using the
     * string representation of the group.
     *
     * @param       obj             The comparison node
     * @return                      -1, 0, 1 depending on the comparison result
     */
    public int compareTo(GroupNode node) {
        return getGroup().toString().compareTo(node.getGroup().toString());
    }
}

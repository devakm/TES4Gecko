package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * A RecordNode in a tree represents a plugin record
 */
public class RecordNode extends DefaultMutableTreeNode implements Comparable<RecordNode> {
    
    /** The record is distinct */
    private boolean distinct = false;
    
    /** 
     * Create a new instance of RecordNode 
     *
     * @param       record          The plugin record
     */
    public RecordNode(PluginRecord record) {
        super(record);
    }
    
    /**
     * Get the plugin record
     *
     * @return                      The plugin record
     */
    public PluginRecord getRecord() {
        return (PluginRecord)getUserObject();
    }
    
    /**
     * Check if the record is distinct.
     *
     * @return                      TRUE if the record is distinct
     */
    public boolean isDistinct() {
        return distinct;
    }
    
    /**
     * Set the record as distinct or shared
     *
     * @param       distinct        TRUE if the record is distinct
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    
    /**
     * Return the hash code for this record node.  The hash code is the
     * hash code for the plugin record.
     *
     * @return                      The hash code
     */
    public int hashCode() {
        return getRecord().hashCode();
    }
    
    /**
     * Determine if two record nodes are equal.  Two record nodes are equal if
     * the plugin record are identical.
     *
     * @param       obj             The comparison node
     * @return                      TRUE if the record nodes are equal
     */
    public boolean equals(Object obj) {
        boolean areEqual = false;
        if (obj instanceof RecordNode)
            areEqual = getRecord().isIdentical(((RecordNode)obj).getRecord());
        
        return areEqual;
    }
    
    /**
     * Compare two record nodes.  The comparison is performed using the record
     * type, editor ID and the form ID.
     *
     * @param       obj             The comparison node
     * @return                      -1, 0, 1 depending on the comparison result
     */
    public int compareTo(RecordNode node) {
        PluginRecord record = getRecord();
        PluginRecord cmpRecord = node.getRecord();
        int diff = record.getRecordType().compareTo(cmpRecord.getRecordType());
        if (diff == 0) {
            diff = record.getEditorID().compareTo(cmpRecord.getEditorID());
            if (diff == 0) {
                int formID = record.getFormID();
                int cmpFormID = cmpRecord.getFormID();
                if (formID < cmpFormID)
                    diff = -1;
                else if (formID > cmpFormID)
                    diff = 1;
            }
        }

        return diff;
    }
}

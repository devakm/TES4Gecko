package TES4Gecko;

import java.util.*;

/**
 * Form ID adjustments
 */
public class FormAdjust {
    
    /** The master map */
    private int[] masterMap;
    
    /** The new master count */
    private int masterCount;
    
    /** The form information map */
    private Map<Integer, FormInfo> formMap;
    
    /** 
     * Create a new instance of FormAdjust which performs no adjustments
     */
    public FormAdjust() {
    }
    
    /**
     * Create a new instance of FormAdjust to adjust the master index in form ID
     * references.  The master map must be the same size as the old master list.
     *
     * @param       masterMap       Mapping between old and new master lists
     * @param       masterCount     Number of entries in the new master list
     */
    public FormAdjust(int[] masterMap, int masterCount) {
        this.masterMap = masterMap;
        this.masterCount = masterCount;
    }
    
    /**
     * Create a new instance of FormAdjust to adjust master and plugin form ID references.
     * The master map must be the same size as the old master list.
     *
     * @param       masterMap       Mapping between old and new master lists
     * @param       masterCount     Number of entries in the new master list
     * @param       formMap         The form information map
     */
    public FormAdjust(int[] masterMap, int masterCount, Map<Integer, FormInfo> formMap) {
        this.masterMap = masterMap;
        this.masterCount = masterCount;
        this.formMap = formMap;
    }
    
    /**
     * Get the master map array
     *
     * @return                      The master map array
     */
    public int[] getMasterMap() {
        return masterMap;
    }
    
    /**
     * Get the master count
     *
     * @return                      The master count
     */
    public int getMasterCount() {
        return masterCount;
    }
    
    /**
     * Get the form map
     *
     * @return                      The form map
     */
    public Map<Integer, FormInfo> getFormMap() {
        return formMap;
    }
    
    /**
     * Adjust a form ID
     *
     * @param       formID          The record form ID
     * @return                      The adjusted form ID
     */
    public int adjustFormID(int formID) {
        
        //
        // Return the original form ID if no mapping is being done or there is no form ID
        //
        if (masterMap == null || formID == 0)
            return formID;
        
        //
        // Adjust a master reference using the master map
        //
        int masterID = formID>>>24;
        if (masterID < masterMap.length) {
            masterID = masterMap[masterID];
            return (formID&0x00ffffff) | (masterID<<24);
        }
        
        //
        // Adjust a plugin reference using the form list.  If there is no form list,
        // set the plugin index to the new master list count.
        //
        int newFormID = formID&0x00ffffff;
        if (formMap == null) {
            newFormID |= (masterCount<<24);
        } else {
            FormInfo formInfo = formMap.get(new Integer(formID));
            if (formInfo == null)
                newFormID |= (masterCount<<24);
            else
                newFormID = formInfo.getMergedFormID();
        }
        
        return newFormID;
    }
    
    /**
     * Adjust an editor ID
     *
     * @param       formID          The record form ID
     * @return                      The adjusted editor ID or null
     */
    public String adjustEditorID(int formID) {
        String editorID = null;
        if (formMap != null) {
            FormInfo formInfo = formMap.get(new Integer(formID));
            if (formInfo != null)
                editorID = formInfo.getMergedEditorID();
        }
        
        return editorID;
    }
}

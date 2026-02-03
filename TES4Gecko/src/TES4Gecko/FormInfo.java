package TES4Gecko;

/**
 * The FormInfo class maps the original Form ID and Editor ID to the merged Form ID and Editor ID
 */
public class FormInfo {
    
    /** Plugin */
    private Object plugin;
    
    /** Source object */
    private Object source;
    
    /** Record node */
    private RecordNode recordNode;
    
    /** Record type */
    private String recordType;
    
    /** Form ID */
    private int formID;
    
    /** Parent form ID */
    private int parentFormID;
    
    /** Editor ID */
    private String editorID;
    
    /** Voice file name */
    private String voiceName;
    
    /** Merged Form ID */
    private int mergedFormID;
    
    /** Merged Editor ID */
    private String mergedEditorID;
    
    /**
     * Create a new mapping
     *
     * @param       source          Source object
     * @param       recordType      The record type
     * @param       formID          The original Form ID
     * @param       editorID        The original Editor ID
     */
    public FormInfo(Object source, String recordType, int formID, String editorID) {
        this.source = source;
        this.recordType = recordType;
        this.formID = formID;
        this.editorID = editorID;
        this.mergedFormID = formID;
        this.mergedEditorID = editorID;
    }
    
    /**
     * Return the associated plugin
     *
     * @return                      The plugin object
     */
    public Object getPlugin() {
        return plugin;
    }
    
    /**
     * Set the associated plugin
     *
     * @param       plugin          The plugin object
     */
    public void setPlugin(Object plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Return the source object
     *
     * @return                      The source object
     */
    public Object getSource() {
        return source;
    }
    
    /**
     * Set the source object
     *
     * @param       source          The source object
     */
    public void setSource(Object source) {
        this.source = source;
    }
    
    /**
     * Return the tree node associated with the source record
     *
     * @return                      The record node
     */
    public RecordNode getRecordNode() {
        return recordNode;
    }
    
    /**
     * Set the tree node associated with the source record
     *
     * @param       recordNode      The record node
     */
    public void setRecordNode(RecordNode recordNode) {
        this.recordNode = recordNode;
    }
    
    /**
     * Return the record type
     *
     * @return                      The record type
     */
    public String getRecordType() {
        return recordType;
    }
    
    /**
     * Return the Form ID
     *
     * @return                      The Form ID
     */
    public int getFormID() {
        return formID;
    }
    
    /**
     * Return the parent form ID of the group containing the record
     *
     * @return                      The parent form ID or zero
     */
    public int getParentFormID() {
        return parentFormID;
    }
    
    /**
     * Set the parent form ID
     *
     * @param       formID          The parent form ID
     */
    public void setParentFormID(int formID) {
        parentFormID = formID;
    }
    
    /**
     * Return the Editor ID
     *
     * @return                      The Editor ID
     */
    public String getEditorID() {
        return editorID;
    }
    
    /**
     * Return the voice file name
     *
     * @return                      The voice file name or null if there is no voice file
     */
    public String getVoiceName() {
        return voiceName;
    }
    
    /**
     * Set the voice file name
     *
     * @param       fileName        The voice file name
     */
    public void setVoiceName(String fileName) {
        voiceName = fileName;
    }
    
    /**
     * Return the merged Form ID
     *
     * @return                      The merged Form ID
     */
    public int getMergedFormID() {
        return mergedFormID;
    }
    
    /**
     * Set the new Form ID
     *
     * @param                       The new Form ID
     */
    public void setFormID(int newFormID) {
        formID = newFormID;
    }
    
    /**
     * Set the merged Form ID
     *
     * @param                       The merged Form ID
     */
    public void setMergedFormID(int formID) {
        mergedFormID = formID;
    }
    
    /**
     * Return the merged Editor ID
     *
     * @return                      The merged Editor ID
     */
    public String getMergedEditorID() {
        return mergedEditorID;
    }
    
    /**
     * Set the Editor ID
     *
     * @param                       The new editor ID
     */
    public void setEditorID(String newEditorID) {
        editorID = newEditorID;
    }

    /**
     * Set the merged Editor ID
     *
     * @param                       The merged Editor ID
     */
    public void setMergedEditorID(String editorID) {
        mergedEditorID = editorID;
    }

    /**
     * Determine if this FormInfo is equal to another FormInfo.  Two objects are considered to be
     * equal if they have the same source, record type and form ID.
     *
     * @param       object          The object to be compared
     */
    public boolean equals(Object object) {
        boolean areEqual = false;
        if (object instanceof FormInfo) {
            FormInfo objInfo = (FormInfo)object;
            if (objInfo.getPlugin() == plugin &&
                                    objInfo.getSource() == source && 
                                    objInfo.getRecordType().equals(recordType) && 
                                    objInfo.getFormID() == formID)
                areEqual = true;
        }
        
        return areEqual;
    }    
}

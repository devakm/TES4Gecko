package TES4Gecko;

/**
 * The PluginTopic represents a dialog topic defined in a plugin or master file.
 */
public class PluginTopic {
    
    /** Form ID */
    private int formID;
    
    /** Editor ID */
    private String editorID;
    
    /** Deleted item */
    private boolean deleted;
    
    /**
     * Create a new topic
     *
     * @param       formID          The form ID for the topic
     */
    public PluginTopic(int formID) {
        this(formID, new String());
    }
    
    /**
     * Create a new topic
     *
     * @param       formID          The form ID for the topic
     * @param       editorID        The editor ID for the topic
     */
    public PluginTopic(int formID, String editorID) {
        this.formID = formID;
        this.editorID = editorID;
        this.deleted = false;
    }
    
    /**
     * Return the topic form ID
     *
     * @return                      The topic form ID
     */
    public int getFormID() {
        return formID;
    }
    
    /**
     * Return the topic editor ID
     *
     * @return                      The topic editor ID
     */
    public String getEditorID() {
        return editorID;
    }
    
    /**
     * Check if the topic is deleted
     *
     * @return                      TRUE if the topic is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Set the delete status for the topic
     *
     * @param       deleted         TRUE if the topic is deleted
     */
    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
}

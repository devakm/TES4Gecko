package TES4Gecko;

/**
 * The PluginQuest represents a quest defined in a plugin or master file.
 */
public class PluginQuest {
    
    /** Form ID */
    private int formID;
    
    /** Editor ID */
    private String editorID;
    
    /** Deleted item */
    private boolean deleted;
    
    /**
     * Create a new quest
     *
     * @param       formID          The form ID for the quest
     */
    public PluginQuest(int formID) {
        this(formID, new String());
    }
    
    /**
     * Create a new quest
     *
     * @param       formID          The form ID for the quest
     * @param       editorID        The editor ID for the quest
     */
    public PluginQuest(int formID, String editorID) {
        this.formID = formID;
        this.editorID = editorID;
        this.deleted = false;
    }
    
    /**
     * Return the quest form ID
     *
     * @return                      The quest form ID
     */
    public int getFormID() {
        return formID;
    }
    
    /**
     * Return the quest editor ID
     *
     * @return                      The quest editor ID
     */
    public String getEditorID() {
        return editorID;
    }
    
    /**
     * Check if the quest is deleted
     *
     * @return                      TRUE if the quest is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Set the delete status for the quest
     *
     * @param       deleted         TRUE if the quest is deleted
     */
    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
}

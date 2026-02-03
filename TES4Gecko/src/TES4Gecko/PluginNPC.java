package TES4Gecko;

/**
 * The PluginNPC represents an NPC (non-player character) defined in a plugin or master file.
 */
public class PluginNPC {
    
    /** Form ID */
    private int formID;
    
    /** Editor ID */
    private String editorID;
    
    /** Race ID */
    private int raceID;
    
    /** Female */
    private boolean female;
    
    /** Deleted */
    private boolean deleted;
    
    /**
     * Create a new NPC
     *
     * @param       formID          The form ID for the NPC
     */
    public PluginNPC(int formID) {
        this(formID, new String(), 0, false);
    }

    /**
     * Create a new NPC
     *
     * @param       formID          The form ID for the NPC
     * @param       editorID        The editor ID for the NPC
     * @param       raceID          The form ID of the NPC race
     * @param       female          TRUE if the NPC is female
     */
    public PluginNPC(int formID, String editorID, int raceID, boolean female) {
        this.formID = formID;
        this.editorID = editorID;
        this.raceID = raceID;
        this.female = female;
        this.deleted = false;
    }
    
    /**
     * Return the NPC form ID
     *
     * @return                      The NPC form ID
     */
    public int getFormID() {
        return formID;
    }
    
    /**
     * Return the NPC editor ID
     *
     * @return                      The NPC editor ID
     */
    public String getEditorID() {
        return editorID;
    }
    
    /**
     * Return the NPC race
     *
     * @return                      The form ID of the NPC race
     */
    public int getRaceID() {
        return raceID;
    }
    
    /**
     * Check if the NPC is female
     *
     * @return                      TRUE if the NPC is female
     */
    public boolean isFemale() {
        return female;
    }
    
    /**
     * Check if the NPC is deleted
     *
     * @return                      TRUE if the NPC is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Set the delete status for the NPC
     *
     * @param       deleted         TRUE if the NPC is deleted
     */
    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
}

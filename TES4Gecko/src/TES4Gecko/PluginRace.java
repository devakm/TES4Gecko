package TES4Gecko;

/**
 * The PluginRace represents a race defined in a plugin or master file.
 */
public class PluginRace {
    
    /** Form ID */
    private int formID;
    
    /** Editor ID */
    private String editorID;
    
    /** Full name */
    private String fullName;
    
    /** Deleted item */
    private boolean deleted;
    
    /** Playable race */
    private boolean playableRace;
    
    /** Male voice form ID */
    private int maleVoiceID;
    
    /** Female voice form ID */
    private int femaleVoiceID;
    
    /**
     * Create a new race
     *
     * @param       formID          The form ID for the race
     */
    public PluginRace(int formID) {
        this(formID, new String(), new String(), false, formID, formID);
    }
    
    /**
     * Create a new race
     *
     * @param       formID          The form ID for the race
     * @param       editorID        The editor ID for the race
     * @param       fullName        The full name
     * @param       playableRace    TRUE if this is a playable race
     * @param       maleVoiceID     The male voice race ID
     * @param       femaleVoiceID   The female voice race ID
     */
    public PluginRace(int formID, String editorID, String fullName, boolean playableRace, 
                      int maleVoiceID, int femaleVoiceID) {
        this.formID = formID;
        this.editorID = editorID;
        this.fullName = fullName;
        this.playableRace = playableRace;
        this.maleVoiceID = maleVoiceID;
        this.femaleVoiceID = femaleVoiceID;
        this.deleted = false;
    }
    
    /**
     * Return the race form ID
     *
     * @return                      The race form ID
     */
    public int getFormID() {
        return formID;
    }
    
    /**
     * Return the race editor ID
     *
     * @return                      The race editor ID
     */
    public String getEditorID() {
        return editorID;
    }
    
    /**
     * Return the race name
     *
     * @return                      The race name
     */
    public String getName() {
        return fullName;
    }
    
    /**
     * Check if the race is deleted
     *
     * @return                      TRUE if the race is deleted
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Set the delete status for the race
     *
     * @param       deleted         TRUE if the race is deleted
     */
    public void setDelete(boolean deleted) {
        this.deleted = deleted;
    }
    
    /**
     * Check if the race is playable
     *
     * @return                      TRUE if the race is playable
     */
    public boolean isPlayableRace() {
        return playableRace;
    }
    
    /**
     * Return the male voice form ID
     *
     * @return                      The race form ID for the male voice
     */
    public int getMaleVoiceID() {
        return maleVoiceID;
    }
    
    /**
     * Return the female voice form ID
     *
     * @return                      The race form ID for the female voice
     */
    public int getFemaleVoiceID() {
        return femaleVoiceID;
    }
}

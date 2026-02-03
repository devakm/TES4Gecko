package TES4Gecko;

/**
 * The PluginInfo record contains information about a plugin.
 */
public class PluginInfo {
    
    /** The plugin name */
    private String pluginName;

    /** The plugin version */
    private float pluginVersion;
    
    /** The plugin creator */
    private String pluginCreator;
    
    /** The plugin summary */
    private String pluginSummary;
    
    /** Delete last conflict */
    private boolean deleteLastConflict = false;
    
    /** Edit conflicts */
    private boolean editConflicts = false;
    
    /**
     * Create a new plugin information.  The plugin version will be set to 0.8.
     *
     * @param       name            The plugin name
     * @param       creator         The plugin creator
     * @param       summary         The plugin summary
     */
    public PluginInfo(String name, String creator, String summary) {
        this(name, creator, summary, 0.8f);
    }
    
    /**
     * Create a new plugin information
     *
     * @param       name            The plugin name
     * @param       creator         The plugin creator
     * @param       summary         The plugin summary
     * @param       version         The plugin version
     */
    public PluginInfo(String name, String creator, String summary, float version) {
        pluginName = name;
        pluginCreator = (creator!=null ? creator : new String());
        pluginSummary = (summary!=null ? summary : new String());
        pluginVersion = version;
    }
    
    /**
     * Return the plugin name
     *
     * @return                      The plugin name
     */
    public String getName() {
        return pluginName;
    }
    
    /**
     * Return the plugin version
     *
     * @return                      The plugin version
     */
    public float getVersion() {
        return pluginVersion;
    }
    
    /**
     * Set the plugin version
     *
     * @param       version         The plugin version
     */
    public void setVersion(float version) {
        pluginVersion = version;
    }
    
    /**
     * Return the plugin creator
     *
     * @return                      The plugin creator
     */
    public String getCreator() {
        return pluginCreator;
    }
    
    /**
     * Set the plugin creator
     *
     * @param       creator         The plugin creator
     */
    public void setCreator(String creator) {
        pluginCreator = creator;
    }
    
    /**
     * Return the plugin summary
     *
     * @return                      The plugin summary
     */
    public String getSummary() {
        return pluginSummary;
    }
    
    /**
     * Set the plugin summary
     *
     * @param       summary         The plugin summary
     */
    public void setSummary(String summary) {
        pluginSummary = summary;
    }
    
    /**
     * Determine if the first or last conflict should be deleted
     *
     * @return                      TRUE if the last conflict should be deleted
     */
    public boolean shouldDeleteLastConflict() {
        return deleteLastConflict;
    }
    
    /**
     * Indicate if the last conflict should be deleted
     *
     * @param       deleteLastConflict  TRUE if the last conflict should be deleted
     */
    public void setDeleteLastConflict(boolean deleteLastConflict) {
        this.deleteLastConflict = deleteLastConflict;
    }
    
    /**
     * Determine if the user can edit conflicts
     *
     * @return                      TRUE if the user can edit conflicts
     */
    public boolean shouldEditConflicts() {
        return editConflicts;
    }
    
    /**
     * Indicate if the user can edit conflicts
     *
     * @param       editConflicts   TRUE if the user can edit conflicts
     */
    public void setEditConflicts(boolean editConflicts) {
        this.editConflicts = editConflicts;
    }
}

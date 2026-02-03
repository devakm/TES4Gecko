package TES4Gecko;

import java.io.*;
import javax.swing.*;

/**
 * This is a file chooser filter that selects TES4 files (*.esm or *.esp)
 */
public class PluginFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
    
    /** Select master files */
    private boolean selectMasterFiles;
    
    /** Select plugin files */
    private boolean selectPluginFiles;
    
    /** Select patch files */
    private boolean selectPatchFiles;
    
    /**
     * Create a new file filter to select all file types
     */
    public PluginFileFilter() {
        super();
        this.selectMasterFiles = true;
        this.selectPluginFiles = true;
        this.selectPatchFiles = true;
    }
    
    /**
     * Create a new file filter to select either master files or plugin files
     *
     * @param       selectMasterFiles   TRUE to select master files, FALSE to select plugin files
     */
    public PluginFileFilter(boolean selectMasterFiles) {
        super();
        this.selectMasterFiles = selectMasterFiles;
        this.selectPluginFiles = !selectMasterFiles;
        this.selectPatchFiles = false;
    }
    
    /**
     * Create a new file filter to select individual file types
     *
     * @param       selectMasterFiles   TRUE to select master files
     * @param       selectPluginFiles   TRUE to select plugin files
     * @param       selectPatchFiles    TRUE to select patch files
     */
    public PluginFileFilter(boolean selectMasterFiles, boolean selectPluginFiles, boolean selectPatchFiles) {
        super();
        this.selectMasterFiles = selectMasterFiles;
        this.selectPluginFiles = selectPluginFiles;
        this.selectPatchFiles = selectPatchFiles;
    }

    /**
     * Return the filter description
     *
     * @return                  String describing the file filter
     */
    public String getDescription() {
        String text = null;
        if (selectMasterFiles)
            text = "TES Files (*.esm";
        
        if (selectPluginFiles)
            if (text == null)
                text = "TES Files (*.esp";
            else
                text = text.concat(", *.esp");
        
        if (selectPatchFiles)
            if (text == null)
                text = "TES Files (*.esu";
            else
                text = text.concat(", *.esu");
        
        return text.concat(")");
    }
    
    /**
     * Accept or reject a file.  A directory is always accepted so the user can
     * navigate down the hierarchy by clicking on the directory name.
     *
     * @param       file            Current file
     * @return                      TRUE to accept the file
     */
    public boolean accept(File file) {
        boolean accept = false;
        
        if (!file.isFile()) {
            accept = true;
        } else {
            String name = file.getName();
            int sep = name.lastIndexOf('.');
            if (sep > 0) {
                if (name.substring(sep).equalsIgnoreCase(".esm")) {
                    if (selectMasterFiles)
                        accept = true;
                } else if (name.substring(sep).equalsIgnoreCase(".esp")) {
                    if (selectPluginFiles)
                        accept = true;
                } else if (name.substring(sep).equalsIgnoreCase(".esu")) {
                    if (selectPatchFiles)
                        accept = true;
                }
            }
        }
        
        return accept;
    }
}

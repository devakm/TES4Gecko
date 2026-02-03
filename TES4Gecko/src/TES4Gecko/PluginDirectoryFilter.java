package TES4Gecko;

import java.io.*;
import javax.swing.*;

/**
 * This is a file chooser filter that selects directories
 */
public class PluginDirectoryFilter extends javax.swing.filechooser.FileFilter {
    
    /**
     * Create a new directory filter
     */
    public PluginDirectoryFilter() {
        super();
    }

    /**
     * Return the filter description
     *
     * @return                  String describing the file filter
     */
    public String getDescription() {
        return "File Directories";
    }
    
    /**
     * Accept or reject a path.  We will accept a directory and reject a file.
     *
     * @param       file            Current file
     * @return                      TRUE to accept the path
     */
    public boolean accept(File file) {
        return file.isDirectory();
    }
}


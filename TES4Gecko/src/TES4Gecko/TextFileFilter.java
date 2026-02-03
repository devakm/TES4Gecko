package TES4Gecko;

import java.io.*;

/**
 * This is a file chooser filter that selects text files (*.txt)
 */
public class TextFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter
{
    
    /**
     * Create a new file filter to select all file types
     */
    public TextFileFilter() {
        super();
    }
    
    /**
     * Return the filter description
     *
     * @return                  String describing the file filter
     */
    public String getDescription()
    {
        return "Text Files (*.txt)";
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
        
        if (!file.isFile())
        {
            accept = true;
        } else
        {
            String name = file.getName();
            int sep = name.lastIndexOf('.');
            if (sep > 0)
            {
                if (name.substring(sep).equalsIgnoreCase(".txt")) accept = true;
            }
        }
        
        return accept;
    }
}

package TES4Gecko;

import java.io.*;

/**
 * This is a file chooser filter that selects text files (*.txt)
 */
public class ESPFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter
{
    
    /**
     * Create a new file filter to select all file types
     */
    public ESPFileFilter() {
        super();
    }
    
    /**
     * Return the filter description
     *
     * @return                  String describing the file filter
     */
    public String getDescription()
    {
        return "Oblivion Plugin Files (*.esp)";
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
                if (name.substring(sep).equalsIgnoreCase(".esp")) accept = true;
            }
            else // Accepts entries w/o extensions, which means choosers using this will have to check.
            {
            	accept = true;
            }
        }
        
        return accept;
    }
}

package TES4Gecko;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Dialog to get the list of plugin files to be merged
 */
public class PluginDialog extends JDialog implements ActionListener {
    
    /** Selected plugin names */
    private String[] pluginNames;
    
    /** Plugin table column names */
    private String[] columnNames = {"Priority", "Plugin"};
    
    /** Plugin table column classes */
    private Class<?>[] columnClasses = {Integer.class, String.class};
    
    /** Plugin table model */
    private PluginTableModel tableModel;
    
    /** Plugin table */
    private JTable table;
    
    /** Plugin table scroll pane */
    private JScrollPane scrollPane;

    /**
     * Create the dialog
     *
     * @param       parent          Parent frame
     */
    public PluginDialog(JFrame parent) {
        super(parent, "Select Plugins", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //
        // Create the plugin table
        //
        String pluginDirectory = Main.properties.getProperty("plugin.directory");
        tableModel = new PluginTableModel(pluginDirectory);
        table = new JTable(tableModel);
        table.setCellSelectionEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        
        //
        // Set the column sizes
        //
        TableCellRenderer headRenderer = table.getTableHeader().getDefaultRenderer();
        if (headRenderer instanceof DefaultTableCellRenderer)
            ((DefaultTableCellRenderer)headRenderer).setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(350);
        
        //
        // Set the background color for non-editable columns
        //
        TableCellRenderer renderer = (DefaultTableCellRenderer)columnModel.getColumn(1).getCellRenderer();
        if (renderer == null)
            renderer = table.getDefaultRenderer(columnClasses[1]);
        ((DefaultTableCellRenderer)renderer).setBackground(Main.backgroundColor);
        
        //
        // Create the table scroll pane
        //
        scrollPane = new JScrollPane(table);

        //
        // Create the buttons
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Main.backgroundColor);

        JButton button = new JButton("OK");
        button.setActionCommand("done");
        button.addActionListener(this);
        buttonPane.add(button);

        button = new JButton("Cancel");
        button.setActionCommand("cancel");
        button.addActionListener(this);
        buttonPane.add(button);

        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPane.add(new JLabel("<html><b>Set the merge priority for two or more plugins.  "+
                                   "Plugins with a blank or zero priority will not merged.</b><br><br></html>"), 
                                   BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        setContentPane(contentPane);
    }

    /**
     * Return the selected plugin names
     *
     * @return                      Array of plugin names or null if no plugins selected
     */
    public String[] getPluginNames() {
        return pluginNames;
    }

    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @return                      Array of plugin names or null
     */
    public static String[] showDialog(JFrame parent) {
        PluginDialog dialog = new PluginDialog(parent);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.getPluginNames();
    }
    
    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            boolean doAction = true;
                
            //
            // Stop editing if a cell editor is active.  Don't do the action if
            // the edited value is not valid.
            //
            if (table.isEditing())
                if (!table.getCellEditor().stopCellEditing())
                    doAction = false;
            
            //
            // Process the action command
            //
            if (doAction) {
                String action = ae.getActionCommand();
                if (action.equals("done")) {
                    int rows = tableModel.getRowCount();
                    List<PluginTableEntry> entryList = new ArrayList<PluginTableEntry>(rows);
                    for (int row=0; row<rows; row++) {
                        PluginTableEntry entry = tableModel.getPluginTableEntry(row);
                        int priority = entry.getMergePriority();
                        if (priority > 0) {
                            int index = 0;
                            for (PluginTableEntry checkEntry : entryList) {
                                if (priority < checkEntry.getMergePriority())
                                    break;

                                index++;
                            }

                            entryList.add(index, entry);
                        }
                    }

                    int count = entryList.size();
                    if (count < 2) {
                        JOptionPane.showMessageDialog(this, "You must select at least two plugins to merge",
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        pluginNames = new String[count];
                        int index = 0;
                        for (PluginTableEntry entry : entryList)
                            pluginNames[index++] = entry.getPluginName();

                        setVisible(false);
                        dispose();
                    }
                } else if (action.equals("cancel")) {
                    setVisible(false);
                    dispose();
                }
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }   
    }
    
    /**
     * Plugin table entry
     */
    private class PluginTableEntry {
        
        /** Plugin name */
        private String pluginName;
        
        /** Last modified timestamp */
        private long lastModified;
        
        /** Merge priority */
        private int mergePriority;
        
        /**
         * Create a new plugin table entry
         *
         * @param       pluginName      Plugin name
         * @param       lastModified    Plugin last modified timestamp
         */
        public PluginTableEntry(String pluginName, long lastModified) {
            this.pluginName = pluginName;
            this.lastModified = lastModified;
            this.mergePriority = 0;
        }
        
        /**
         * Return the plugin name
         *
         * @return                      The plugin name
         */
        public String getPluginName() {
            return pluginName;
        }
        
        /**
         * Return the last modified timestamp
         *
         * @return                      The last modified timestamp
         */
        public long getLastModified() {
            return lastModified;
        }
        
        /**
         * Return the merge priority
         *
         * @return                      The merge priority
         */
        public int getMergePriority() {
            return mergePriority;
        }
        
        /**
         * Set the merge priority
         *
         * @param       priority        The merge priority
         */
        public void setMergePriority(int priority) {
            mergePriority = priority;
        }
    }
    
    /**
     * Plugin file filter
     */
    private class PluginMergeFilter implements FileFilter {
        
        /**
         * Create a new filter
         */
        public PluginMergeFilter() {
            super();
        }
        
        /**
         * Accept or reject a file
         *
         * @param       file        The current file
         */
        public boolean accept(File file) {
            boolean acceptFile = false;
            if (file.isFile()) {
                String fileName = file.getName();
                int sep = fileName.lastIndexOf('.');
                if (sep > 0 && fileName.substring(sep).equalsIgnoreCase(".esp"))
                    acceptFile = true;
            }
            
            return acceptFile;
        }
    }
    
    /**
     * Plugin table model
     */
    private class PluginTableModel extends AbstractTableModel {
        
        /** Table data */
        private List<PluginTableEntry> tableData;
        
        /**
         * Create the table model
         *
         * @param       pluginDirectory The plugin directory
         */
        public PluginTableModel(String pluginDirectory) {
            
            //
            // Create the table data
            //
            tableData = new ArrayList<PluginTableEntry>();
            
            //
            // Build the list of plugin files
            //
            if (pluginDirectory != null && pluginDirectory.length() != 0) {
                File directory = new File(pluginDirectory);
                if (directory.isDirectory()) {
                    File[] fileList = directory.listFiles(new PluginMergeFilter());
                    if (fileList != null && fileList.length != 0) {
                        for (File file : fileList) {
                            long lastModified = file.lastModified();
                            int index = 0;
                            for (PluginTableEntry entry : tableData) {
                                if (lastModified < entry.getLastModified())
                                    break;
                                
                                index++;
                            }
                            
                            tableData.add(index, new PluginTableEntry(file.getName(), lastModified));
                        }
                    }
                }
            }
        }

        /**
         * Get the number of columns in the table
         *
         * @return                      The number of columns
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Get the column class
         *
         * @param       column          Column number
         * @return                      The column class
         */
        public Class<?> getColumnClass(int column) {
            return columnClasses[column];
        }

        /**
         * Get the column name
         *
         * @param       column          Column number
         * @return                      Column name
         */
        public String getColumnName(int column) {
            return columnNames[column];
        }

        /**
         * Get the number of rows in the table
         *
         * @return                      The number of rows
         */
        public int getRowCount() {
            return tableData.size();
        }

        /**
         * Check if the specified cell is editable
         *
         * @param       row             Row number
         * @param       column          Column number
         * @return                      TRUE if the cell is editable
         */
        public boolean isCellEditable(int row, int column) {
            return (column==0);
        }

        /**
         * Get the value for a cell
         *
         * @param       row             Row number
         * @param       column          Column number
         * @return                      Returns the object associated with the cell
         */
        public Object getValueAt(int row, int column) {
            if (row >= tableData.size())
                throw new IndexOutOfBoundsException("Table row "+row+" is not valid");

            Object value = null;
            PluginTableEntry entry = tableData.get(row);
            switch (column) {
                case 0:                             // Merge priority
                    int priority = entry.getMergePriority();
                    if (priority > 0)
                        value = new Integer(priority);
                    break;
                    
                case 1:                             // Plugin name
                    value = entry.getPluginName();
                    break;
                    
                default:
                    throw new IndexOutOfBoundsException("Table column "+column+" is not valid");
            }
            
            return value;
        }

        /**
         * Set the value for a cell
         *
         * @param       value           Cell value
         * @param       row             Row number
         * @param       column          Column number
         */
        public void setValueAt(Object value, int row, int column) {
            if (row >= tableData.size())
                throw new IndexOutOfBoundsException("Table row "+row+" is not valid");
            
            if (column == 0) {
                int priority;
                if (value == null) {
                    priority = 0;
                } else {
                    priority = ((Integer)value).intValue();
                    if (priority < 1)
                        priority = 0;
                }
                
                tableData.get(row).setMergePriority(priority);
                fireTableCellUpdated(row, column);
            }
        }
        
        /**
         * Get the plugin table entry for a table row
         *
         * @param       row             The table row
         * @return                      The plugin table entry
         */
        public PluginTableEntry getPluginTableEntry(int row) {
            if (row >= tableData.size())
                throw new IndexOutOfBoundsException("Table row "+row+" is not valid");
            
            return tableData.get(row);
        }
    }
}

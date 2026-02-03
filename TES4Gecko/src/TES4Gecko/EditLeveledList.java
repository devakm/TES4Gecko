package TES4Gecko;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

/**
 * Edit a merged leveled list and allow list items to be copied from a plugin leveled list or
 * deleted from the merged leveled list.  The level and count for items in the merged leveled
 * list can be modified as well.
 */
public class EditLeveledList implements Runnable {
    
    /** Parent dialog */
    private JDialog parent;
    
    /** Master files */
    private Master[] masters;
    
    /** Merged leveled list record */
    private PluginRecord mergedRecord;
    
    /** Plugin leveled list record */
    private PluginRecord pluginRecord;
    
    /** Merged form map */
    private Map<Integer, FormInfo> mergedFormMap;
    
    /**
     * Create the edit task
     *
     * @param       parent          The parent dialog
     * @param       mergedRecord    The merged record
     * @param       pluginRecord    The plugin record
     * @param       mergedFormMap   The merged form map
     * @param       masters         The master files
     */
    public EditLeveledList(JDialog parent, PluginRecord mergedRecord, PluginRecord pluginRecord,
                           Map<Integer, FormInfo>mergedFormMap, Master[] masters) {
        this.parent = parent;
        this.masters = masters;
        this.mergedRecord = mergedRecord;
        this.pluginRecord = pluginRecord;
        this.mergedFormMap = mergedFormMap;
    }
    
    /**
     * Show the leveled list edit dialog.  This method will switch to the event dispatch thread
     * and control will not return to the caller until the dialog is closed.
     *
     * @param       parent          The parent dialog
     * @param       mergedRecord    The merged record
     * @param       pluginRecord    The plugin record
     * @param       mergedFormMap   The merged form map
     * @param       masters         The master files
     */
    public static void showWorkerDialog(JDialog parent, PluginRecord mergedRecord, PluginRecord pluginRecord,
                                        Map<Integer, FormInfo>mergedFormMap, Master[] masters) {
        try {
            EditLeveledList task = new EditLeveledList(parent, mergedRecord, pluginRecord, mergedFormMap, masters);
            SwingUtilities.invokeAndWait(task);
        } catch (InterruptedException exc) {
            Main.logException("Edit dialog interrupted", exc);
        } catch (Throwable exc) {
            Main.logException("Exception while displaying edit dialog", exc);
        }                    
    }
        
    /**
     * Display the leveled list edit dialog.  This method runs on the event dispatch thread.
     */
    public void run() {
        EditLeveledListDialog dialog = new EditLeveledListDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Leveled list edit dialog
     */
    private class EditLeveledListDialog extends JDialog implements ActionListener, ListSelectionListener {
        
        /** Leveled list column names */
        private String[] columnNames = {"Level", "Count", "Editor ID"};
        
        /** Leveled list column classes */
        private Class<?>[] columnClasses = {Integer.class, Integer.class, String.class};
        
        /** Merged leveled list subrecords */
        List<PluginSubrecord> mergedSubrecordList;
        
        /** Plugin leveled list subrecords */
        List<PluginSubrecord> pluginSubrecordList;
    
        /** Merged leveled list table */
        private JTable mergedTable;
    
        /** Merged leveled list table model */
        private LeveledListTableModel mergedTableModel;
        
        /** Merged leveled list table selection model */
        private ListSelectionModel mergedSelectionModel;
    
        /** Plugin leveled list table */
        private JTable pluginTable;
    
        /** Plugin leveled list table model */
        private LeveledListTableModel pluginTableModel;
        
        /** Plugin leveled list table selection model */
        private ListSelectionModel pluginSelectionModel;
        
        /** Calculate from all levels */
        private JCheckBox allLevelsField;
        
        /** Calculate for each item in count */
        private JCheckBox allItemsField;
        
        /** Use all spells */
        private JCheckBox allSpellsField;
        
        /** Chance none */
        private JFormattedTextField chanceField;
    
        /**
         * Create the dialog
         */
        public EditLeveledListDialog() {
            super(parent, "Edit leveled list: "+mergedRecord.getEditorID(), true);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            
            try {
                JLabel title;
                Color backgroundColor = Main.backgroundColor;
                boolean allLevels = false;
                boolean allItems = false;
                boolean allSpells = false;
                int chanceNone = 0;
                
                //
                // Get the subrecord lists
                //
                mergedSubrecordList = mergedRecord.getSubrecords();
                pluginSubrecordList = pluginRecord.getSubrecords();
                
                //
                // Get the level list attributes
                //
                for (PluginSubrecord subrecord : mergedSubrecordList) {
                    String subrecordType = subrecord.getSubrecordType();
                    if (subrecordType.equals("LVLD")) {
                        byte[] subrecordData = subrecord.getSubrecordData();
                        chanceNone = (int)subrecordData[0]&255;
                    } else if (subrecordType.equals("LVLF")) {
                        byte[] subrecordData = subrecord.getSubrecordData();
                        if ((subrecordData[0]&0x01) != 0)
                            allLevels = true;
                        if ((subrecordData[0]&0x02) != 0)
                            allItems = true;
                        if ((subrecordData[0]&0x04) != 0)
                            allSpells = true;
                    }
                }
                
                //
                // Create the leveled list attributes pane
                //
                JPanel attributesPane = new JPanel(new GridLayout(0,1));
                attributesPane.setBackground(backgroundColor);
                
                allLevelsField = new JCheckBox("Calculate from all levels <= PC's level", allLevels);
                allLevelsField.setBackground(backgroundColor);
                attributesPane.add(allLevelsField);
                
                allItemsField = new JCheckBox("Calculate for each item in count", allItems);
                allItemsField.setBackground(backgroundColor);
                attributesPane.add(allItemsField);
                
                if (mergedRecord.getRecordType().equals("LVSP")) {
                    allSpellsField = new JCheckBox("Use all spells", allSpells);
                    allSpellsField.setBackground(backgroundColor);
                    attributesPane.add(allSpellsField);
                }
                
                title = new JLabel("Chance none  ", JLabel.LEFT);
                title.setOpaque(false);
                
                chanceField = new JFormattedTextField(new EditNumber(true, false));
                chanceField.setInputVerifier(new EditInputVerifier(false));
                chanceField.setColumns(3);
                chanceField.setValue(new Integer(chanceNone));
                
                JPanel chancePane = new JPanel();
                chancePane.setBackground(backgroundColor);
                chancePane.add(title);
                chancePane.add(chanceField);
                chancePane.add(Box.createGlue());
                attributesPane.add(chancePane);

                //
                // Create the top pane for the dialog with the attributes on the left
                //
                JPanel topPane = new JPanel(new BorderLayout());
                topPane.setBackground(backgroundColor);
                topPane.add(attributesPane, BorderLayout.WEST);
                topPane.add(Box.createGlue(), BorderLayout.CENTER);
                
                //
                // Build the merged leveled list table.  The 'level' and 'count' columns
                // can be edited.
                //
                mergedTableModel = new LeveledListTableModel(mergedSubrecordList, true);
                mergedTable = new JTable(mergedTableModel);
                mergedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                mergedTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                mergedTable.setCellSelectionEnabled(true);
                mergedSelectionModel = mergedTable.getSelectionModel();
                mergedSelectionModel.addListSelectionListener(this);
                
                TableCellRenderer headRenderer = mergedTable.getTableHeader().getDefaultRenderer();
                if (headRenderer instanceof DefaultTableCellRenderer)
                    ((DefaultTableCellRenderer)headRenderer).setHorizontalAlignment(JLabel.CENTER);

                TableColumnModel columnModel = mergedTable.getColumnModel();
                columnModel.getColumn(0).setPreferredWidth(50);
                columnModel.getColumn(1).setPreferredWidth(50);
                columnModel.getColumn(2).setPreferredWidth(200);
                
                JScrollPane mergedScrollPane = new JScrollPane(mergedTable);
                mergedScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                Dimension preferredSize = new Dimension(50+50+200, mergedScrollPane.getPreferredSize().height);
                mergedScrollPane.setPreferredSize(preferredSize);
                
                JPanel mergedPane = new JPanel();
                mergedPane.setLayout(new BoxLayout(mergedPane, BoxLayout.Y_AXIS));
                mergedPane.setBackground(backgroundColor);
                title = new JLabel("Merged Leveled List", JLabel.CENTER);
                title.setOpaque(false);
                title.setFont(title.getFont().deriveFont(Font.BOLD));
                mergedPane.add(title);
                mergedPane.add(mergedScrollPane);
            
                //
                // Build the plugin leveled list table.  No columns can be edited.
                //
                pluginTableModel = new LeveledListTableModel(pluginSubrecordList, false);
                pluginTable = new JTable(pluginTableModel);
                pluginTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                pluginTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                pluginSelectionModel = pluginTable.getSelectionModel();
                pluginSelectionModel.addListSelectionListener(this);
                
                headRenderer = pluginTable.getTableHeader().getDefaultRenderer();
                if (headRenderer instanceof DefaultTableCellRenderer)
                    ((DefaultTableCellRenderer)headRenderer).setHorizontalAlignment(JLabel.CENTER);

                columnModel = pluginTable.getColumnModel();
                columnModel.getColumn(0).setPreferredWidth(50);
                columnModel.getColumn(1).setPreferredWidth(50);
                columnModel.getColumn(2).setPreferredWidth(200);
                
                JScrollPane pluginScrollPane = new JScrollPane(pluginTable);
                pluginScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                pluginScrollPane.setPreferredSize(preferredSize);
                
                JPanel pluginPane = new JPanel();
                pluginPane.setLayout(new BoxLayout(pluginPane, BoxLayout.Y_AXIS));
                pluginPane.setBackground(backgroundColor);
                title = new JLabel("Plugin Leveled List", JLabel.CENTER);
                title.setOpaque(false);
                title.setFont(title.getFont().deriveFont(Font.BOLD));
                pluginPane.add(title);
                pluginPane.add(pluginScrollPane);
                
                //
                // Create the leveled list table pane
                //
                JPanel tablePane = new JPanel();
                tablePane.setBackground(backgroundColor);
                tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.X_AXIS));
                tablePane.add(mergedPane);
                tablePane.add(Box.createHorizontalStrut(15));
                tablePane.add(pluginPane);

                //
                // Create the buttons
                //
                JPanel buttonPane = new JPanel();
                buttonPane.setBackground(backgroundColor);

                JButton button = new JButton("Copy Plugin Item");
                button.setActionCommand("copy");
                button.addActionListener(this);
                buttonPane.add(button);
                
                buttonPane.add(Box.createHorizontalStrut(10));
                
                button = new JButton("Delete Merged Item");
                button.setActionCommand("delete");
                button.addActionListener(this);
                buttonPane.add(button);
                
                buttonPane.add(Box.createHorizontalStrut(10));
                
                button = new JButton("Done");
                button.setActionCommand("done");
                button.addActionListener(this);
                buttonPane.add(button);
                
                //
                // Build the dialog content pane
                //
                JPanel contentPane = new JPanel();
                contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
                contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                contentPane.setBackground(backgroundColor);
                contentPane.add(topPane);
                contentPane.add(Box.createVerticalStrut(20));
                contentPane.add(tablePane);
                contentPane.add(Box.createVerticalStrut(20));
                contentPane.add(buttonPane);
                setContentPane(contentPane);
            } catch (Throwable exc) {
                Main.logException("Exception while constructing edit dialog", exc);
            }
        }
        
        /**
         * Action performed (ActionListener interface)
         *
         * @param       ae              Action event
         */
        public void actionPerformed(ActionEvent ae) {
            try {
                boolean doAction = true;
                String action = ae.getActionCommand();
                
                //
                // Ignore the action if the chance field is not valid
                //
                if (!chanceField.isEditValid())
                    doAction = false;
                
                //
                // Stop editing if a cell editor is active.  Don't do the action if
                // the edited value is not valid.
                //
                if (mergedTable.isEditing())
                    if (!mergedTable.getCellEditor().stopCellEditing())
                        doAction = false;
                
                //
                // Perform the requested action
                //
                if (doAction) {
                    if (action.equals("copy")) {
                        copyListItem();
                    } else if (action.equals("delete")) {
                        deleteListItem();
                    } else if (action.equals("done")) {
                        if (updateMergedRecord()) {
                            setVisible(false);
                            dispose();
                        }
                    }
                }
            } catch (Throwable exc) {
                Main.logException("Exception while processing action event", exc);
            }   
        }

        /**
         * Copy an item from the plugin leveled list to the merged leveled list
         */
        private void copyListItem() {
            int row = pluginTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "You must select a plugin list item to copy",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            PluginSubrecord subrecord = pluginTableModel.getSubrecord(row);
            pluginTableModel.deleteRow(row);
            mergedTableModel.addRow(subrecord);
        }
        
        /**
         * Delete an item from the merged leveled list
         */
        private void deleteListItem() {
            int row = mergedTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "You must select a merged list item to delete",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            mergedTableModel.deleteRow(row);
        }
        
        /**
         * Update the merged record
         *
         * @return                              TRUE if the entered values are valid
         * @exception   DataFormatException     Error while compressing data
         * @exception   IOException             An I/O error occurred
         */
        private boolean updateMergedRecord() throws DataFormatException, IOException {

            //
            // Get the chance of none being selected
            //
            int chanceNone = ((Integer)chanceField.getValue()).intValue();
            if (chanceNone < 0 || chanceNone > 100) {
                JOptionPane.showMessageDialog(this, "You must enter a chance between 0 and 100",
                                              "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            //
            // Get the flags
            //
            boolean allLevels = allLevelsField.isSelected();
            boolean allItems = allItemsField.isSelected();
            boolean allSpells = false;
            if (allSpellsField != null)
                allSpells = allSpellsField.isSelected();
            
            //
            // Update the merged subrecords
            //
            for (PluginSubrecord subrecord : mergedSubrecordList) {
                if (subrecord.getSubrecordType().equals("LVLD")) {
                    byte[] subrecordData = subrecord.getSubrecordData();
                    subrecordData[0] = (byte)chanceNone;
                } else if (subrecord.getSubrecordType().equals("LVLF")) {
                    byte[] subrecordData = subrecord.getSubrecordData();
                    subrecordData[0] &= (byte)0xf8;
                    if (allLevels)
                        subrecordData[0] |= (byte)0x01;
                    
                    if (allItems)
                        subrecordData[0] |= (byte)0x02;
                    
                    if (allSpells)
                        subrecordData[0] |= (byte)0x04;
                }
            }
            
            //
            // Update the merged record
            //
            mergedRecord.setSubrecords(mergedSubrecordList);
            return true;
        }

        /**
         * Value changed (ListSelectionListener interface)
         *
         * @param   se                  List selection event
         */
        public void valueChanged(ListSelectionEvent se) {
            ListSelectionModel lsm = (ListSelectionModel)se.getSource();
            
            //
            // When a row is selected, we will clear the selection in the other table.
            // This means only one table will be active at a time and should reduce
            // confusion when selecting a pushbutton.
            //
            if (!lsm.getValueIsAdjusting() && !lsm.isSelectionEmpty()) {
                if (lsm == mergedSelectionModel) {
                    if (!pluginSelectionModel.isSelectionEmpty())
                        pluginSelectionModel.clearSelection();
                } else {
                    if (!mergedSelectionModel.isSelectionEmpty())
                        mergedSelectionModel.clearSelection();
                }
            }
        }
        
        /**
         * Leveled list entry
         */
        private class LeveledListEntry {
            
            /** Subrecord */
            private PluginSubrecord subrecord;
            
            /** Editor ID */
            private String editorID;
            
            /** Level */
            private int level;
            
            /** Count */
            private int count;
            
            /**
             * Create a new leveled list entry
             *
             * @param       subrecord       The subrecord
             * @param       editorID        The editor ID
             * @param       level           The item level
             * @param       count           The item count
             */
            public LeveledListEntry(PluginSubrecord subrecord, String editorID, int level, int count) {
                this.subrecord = subrecord;
                this.editorID = editorID;
                this.level = level;
                this.count = count;
            }
            
            /**
             * Return the subrecord
             *
             * @return                      The subrecord
             */
            public PluginSubrecord getSubrecord() {
                return subrecord;
            }
            
            /**
             * Return the editor ID
             *
             * @return                      The editor ID
             */
            public String getEditorID() {
                return editorID;
            }
            
            /**
             * Return the item level
             *
             * @return                      The item level
             */
            public int getItemLevel() {
                return level;
            }
            
            /**
             * Set the item level
             *
             * @param       level           The item level
             */
            public void setItemLevel(int level) {
                this.level = level;
                try {
                    byte[] subrecordData = subrecord.getSubrecordData();
                    subrecordData[0] = (byte)level;
                    subrecordData[1] = (byte)(level>>>8);
                    subrecord.setSubrecordData(subrecordData);
                } catch (IOException exc) {
                    Main.logException("Exception while setting subrecord data", exc);
                }
            }
            
            /**
             * Return the item count
             *
             * @return                      The item count
             */
            public int getItemCount() {
                return count;
            }
            
            /**
             * Set the item count
             *
             * @param       count           The item count
             */
            public void setItemCount(int count) {
                this.count = count;
                try {
                    byte[] subrecordData = subrecord.getSubrecordData();
                    subrecordData[8] = (byte)count;
                    subrecordData[9] = (byte)(count>>>8);
                    subrecord.setSubrecordData(subrecordData);
                } catch (IOException exc) {
                    Main.logException("Exception while getting subrecord data", exc);
                }
            }
        }
    
        /**
         * Leveled list table model
         */
        private class LeveledListTableModel extends AbstractTableModel {
            
            /** Subrecord list */
            private List<PluginSubrecord> subrecordList;
            
            /** Level list items */
            private List<LeveledListEntry> tableData;
            
            /** Table cells can be edited */
            private boolean isEditable;
        
            /**
             * Create the table model
             *
             * @param       subrecordList   Leveled list subrecords
             * @param       isEditable      TRUE if the table is editable
             */
            public LeveledListTableModel(List<PluginSubrecord> subrecordList, boolean isEditable) {
                this.subrecordList = subrecordList;
                this.isEditable = isEditable;
                
                //
                // Allocate the table data list
                //
                tableData = new ArrayList<LeveledListEntry>(subrecordList.size());
                
                //
                // Build the leveled list entries
                //
                int level, count, formID, masterID;
                String editorID;
                Integer objFormID;
                FormInfo formInfo;
                for (PluginSubrecord subrecord : subrecordList) {
                    if (subrecord.getSubrecordType().equals("LVLO")) {
                        byte[] subrecordData;
                        try {
                            subrecordData = subrecord.getSubrecordData();
                        } catch (IOException exc) {
                            Main.logException("Exception while getting subrecord data", exc);
                            subrecordData = new byte[0];
                        }
                        
                        if (subrecordData.length >= 12) {
                            level = ((int)subrecordData[0]&255) | (((int)subrecordData[1]&255)<<8);
                            count = ((int)subrecordData[8]&255) | (((int)subrecordData[9]&255)<<8);
                            formID = ((int)subrecordData[4]&255) | (((int)subrecordData[5]&255)<<8) |
                                            (((int)subrecordData[6]&255)<<16) | (((int)subrecordData[7]&255)<<24);
                            masterID = formID>>>24;
                            if (masterID < masters.length) {
                                Master master = masters[masterID];
                                masterID = master.getMasterList().size();
                                formID = (formID&0x00ffffff) | (masterID<<24);
                                objFormID = new Integer(formID);
                                formInfo = master.getFormMap().get(objFormID);
                            } else {
                                objFormID = new Integer(formID);
                                formInfo = mergedFormMap.get(objFormID);
                            }
                            
                            if (formInfo != null)
                                editorID = formInfo.getMergedEditorID();
                            else
                                editorID = String.format("(%08X)",  objFormID);
                            
                            tableData.add(new LeveledListEntry(subrecord, editorID, level, count));
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
                return (isEditable && column<2);
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
                LeveledListEntry entry = tableData.get(row);
                switch (column) {
                    case 0:                             // Item level
                        value = new Integer(entry.getItemLevel());
                        break;
                    
                    case 1:                             // Item count
                        value = new Integer(entry.getItemCount());
                        break;
                    
                    case 2:                             // Editor ID
                        value = entry.getEditorID();
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

                LeveledListEntry entry = tableData.get(row);
                switch (column) {
                    
                        //
                        // The item level has been changed.  We need to sort both the
                        // table entries and the subrecord list based on the new level.
                        //
                    case 0:
                        int oldLevel = entry.getItemLevel();
                        int newLevel = ((Integer)value).intValue();
                        if (oldLevel != newLevel) {
                            PluginSubrecord subrecord = entry.getSubrecord();
                            
                            //
                            // Remove the existing list entry
                            //
                            tableData.remove(row);
                            subrecordList.remove(subrecord);
                            fireTableRowsDeleted(row, row);
                            
                            //
                            // Set the new item level
                            //
                            entry.setItemLevel(newLevel);
                            
                            //
                            // Insert the updated row in the table data
                            //
                            boolean insert = false;
                            int index = 0;
                            for (LeveledListEntry checkEntry : tableData) {
                                if (newLevel < checkEntry.getItemLevel()) {
                                    insert = true;
                                    break;
                                }
                                
                                index++;
                            }
                            
                            if (insert)
                                tableData.add(index, entry);
                            else
                                tableData.add(entry);
                            
                            fireTableRowsInserted(index, index);
                            
                            //
                            // Insert the updated subrecord in the subrecord list
                            //
                            insert = false;
                            index = 0;
                            for (PluginSubrecord checkSubrecord : subrecordList) {
                                if (checkSubrecord.getSubrecordType().equals("LVLO")) {
                                    try {
                                        byte[] checkSubrecordData = checkSubrecord.getSubrecordData();
                                        int checkLevel = ((int)checkSubrecordData[0]&255) | 
                                                            (((int)checkSubrecordData[1]&255)<<8);
                                        if (newLevel < checkLevel) {
                                            insert = true;
                                            break;
                                        }
                                    } catch (IOException exc) {
                                        Main.logException("Exception while getting subrecord data", exc);
                                    }
                                }
                                
                                index++;
                            }
                            
                            if (insert)
                                subrecordList.add(index, subrecord);
                            else
                                subrecordList.add(subrecord);
                        }
                        break;
                      
                        //
                        // The item count has been changed
                        //
                    case 1:
                        int oldCount = entry.getItemCount();
                        int newCount = ((Integer)value).intValue();
                        if (oldCount != newCount) {
                            entry.setItemCount(newCount);
                            fireTableCellUpdated(row, column);
                        }
                        break;
                }
            }
            
            /**
             * Return the subrecord for a table row
             *
             * @return                  The subrecord
             */
            public PluginSubrecord getSubrecord(int row) {
                if (row >= tableData.size())
                    throw new IndexOutOfBoundsException("Table row "+row+" is not valid");
                
                return tableData.get(row).getSubrecord();
            }
            
            /**
             * Add a new row to the table
             *
             * @param       subrecord   The subrecord for the row
             */
            public void addRow(PluginSubrecord subrecord) {
                byte[] subrecordData;
                try {
                    subrecordData = subrecord.getSubrecordData();
                } catch (IOException exc) {
                    Main.logException("Exception while getting subrecord data", exc);
                    subrecordData = new byte[0];
                }
                
                if (subrecordData.length < 12)
                    return;
                
                Integer objFormID;
                FormInfo formInfo;
                String editorID;
                int level = ((int)subrecordData[0]&255) | (((int)subrecordData[1]&255)<<8);
                int count = ((int)subrecordData[8]&255) | (((int)subrecordData[9]&255)<<8);
                int formID = ((int)subrecordData[4]&255) | (((int)subrecordData[5]&255)<<8) |
                                (((int)subrecordData[6]&255)<<16) | (((int)subrecordData[7]&255)<<24);
                int masterID = formID>>>24;
                if (masterID < masters.length) {
                    Master master = masters[masterID];
                    masterID = master.getMasterList().size();
                    formID = (formID&0x00ffffff) | (masterID<<24);
                    objFormID = new Integer(formID);
                    formInfo = master.getFormMap().get(objFormID);
                } else {
                    objFormID = new Integer(formID);
                    formInfo = mergedFormMap.get(objFormID);
                }

                if (formInfo != null)
                    editorID = formInfo.getMergedEditorID();
                else
                    editorID = String.format("(%08X)",  objFormID);

                LeveledListEntry entry = new LeveledListEntry(subrecord, editorID, level, count);

                //
                // Insert the updated row in the table data
                //
                boolean insert = false;
                int index = 0;
                for (LeveledListEntry checkEntry : tableData) {
                    if (level < checkEntry.getItemLevel()) {
                        insert = true;
                        break;
                    }

                    index++;
                }

                if (insert)
                    tableData.add(index, entry);
                else
                    tableData.add(entry);

                fireTableRowsInserted(index, index);

                //
                // Insert the updated subrecord in the subrecord list
                //
                insert = false;
                index = 0;
                for (PluginSubrecord checkSubrecord : subrecordList) {
                    if (checkSubrecord.getSubrecordType().equals("LVLO")) {
                        try {
                            byte[] checkSubrecordData = checkSubrecord.getSubrecordData();
                            int checkLevel = ((int)checkSubrecordData[0]&255) | 
                                                (((int)checkSubrecordData[1]&255)<<8);
                            if (level < checkLevel) {
                                insert = true;
                                break;
                            }
                        } catch (IOException exc) {
                            Main.logException("Exception while getting subrecord data", exc);
                        }
                    }

                    index++;
                }

                if (insert)
                    subrecordList.add(index, subrecord);
                else
                    subrecordList.add(subrecord);                
            }
            
            /**
             * Delete a table row
             *
             * @param       row         The row to be deleted
             */
            public void deleteRow(int row) {
                if (row >= tableData.size())
                    throw new IndexOutOfBoundsException("Table row "+row+" is not valid");
                
                PluginSubrecord subrecord = tableData.get(row).getSubrecord();
                tableData.remove(row);
                subrecordList.remove(subrecord);
                fireTableRowsDeleted(row, row);
            }
        }
    }
}

package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Dialog to get the information for the merged plugin
 */
public class MergeDialog extends JDialog implements ActionListener {
    
    /** Plugin information */
    private PluginInfo pluginInfo;
    
    /** Name field */
    private JTextField nameField;
    
    /** Creator field */
    private JTextField creatorField;
    
    /** Summary field */
    private JTextArea summaryField;
    
    /** Delete last conflict */
    private JCheckBox deleteLastConflictField;
    
    /** Edit conflicts */
    private JCheckBox editConflictsField;

    /**
     * Create the dialog
     *
     * @param       parent          Parent frame
     * @param       creator         Initial creator string
     * @param       summary         Initial summary string
     */
    public MergeDialog(JFrame parent, String creator, String summary) {
        super(parent, "Merged Plugin", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        Color backgroundColor = Main.backgroundColor;

        //
        // Create the edit pane
        //
        Dimension labelSize = new Dimension(70, 12);
        JPanel namePane = new JPanel();
        namePane.setOpaque(false);
        JLabel label = new JLabel("Name:", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        namePane.add(label);
        nameField = new JTextField("Merged Plugin.esp", 32);
        namePane.add(nameField);

        JPanel creatorPane = new JPanel();
        creatorPane.setOpaque(false);
        label = new JLabel("Creator:", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        creatorPane.add(label);
        creatorField = new JTextField(creator, 32);
        creatorPane.add(creatorField);
        
        JPanel summaryPane = new JPanel();
        summaryPane.setOpaque(false);
        label = new JLabel("Summary:", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        summaryPane.add(label);
        summaryField = new JTextArea(summary, 8, 32);
        summaryField.setLineWrap(true);
        summaryField.setWrapStyleWord(true);
        summaryField.setFont(creatorField.getFont());
        JScrollPane scrollPane = new JScrollPane(summaryField);
        summaryPane.add(scrollPane);
        
        //
        // Create the check boxes
        //
        JPanel checkBoxPane = new JPanel();
        checkBoxPane.setLayout(new BoxLayout(checkBoxPane, BoxLayout.Y_AXIS));
        checkBoxPane.setBackground(backgroundColor);
        
        deleteLastConflictField = new JCheckBox("Delete last master record conflict", false);
        deleteLastConflictField.setOpaque(false);
        checkBoxPane.add(deleteLastConflictField);
        
        editConflictsField = new JCheckBox("Edit master leveled list conflicts", false);
        editConflictsField.setOpaque(false);
        checkBoxPane.add(editConflictsField);
        
        //
        // Create the buttons
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(backgroundColor);

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
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBackground(backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPane.add(namePane);
        contentPane.add(creatorPane);
        contentPane.add(summaryPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(checkBoxPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);            
    }
    
    /**
     * Return the plugin information
     *
     * @return                      Plugin information
     */
    public PluginInfo getInfo() {
        return pluginInfo;
    }

    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       creator         Initial creator string
     * @param       summary         Initial summary string
     * @return                      Plugin information or null
     */
    public static PluginInfo showDialog(JFrame parent, String creator, String summary) {
        MergeDialog dialog = new MergeDialog(parent, creator, summary);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.getInfo();
    }
    
    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            if (action.equals("done")) {
                String name = nameField.getText();
                if (name.length() == 0) {
                    JOptionPane.showMessageDialog(this, "You must specify a name for the merged plugin",
                                                  "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    String creator = creatorField.getText();
                    if (creator.length() == 0)
                        creator = new String("DEFAULT");
                    
                    StringBuilder summary = new StringBuilder(summaryField.getText());
                    int index = 0;
                    while (true) {
                        index = summary.indexOf("\n",  index);
                        if (index < 0)
                            break;

                        if (index == 0 || summary.charAt(index-1) != '\r') {
                            summary.insert(index, "\r");
                            index += 2;
                        } else {
                            index++;
                        }
                    }

                    pluginInfo = new PluginInfo(name, creator, summary.toString());
                    pluginInfo.setDeleteLastConflict(deleteLastConflictField.isSelected());
                    pluginInfo.setEditConflicts(editConflictsField.isSelected());
                    setVisible(false);
                    dispose();
                }
            } else if (action.equals("cancel")) {
                setVisible(false);
                dispose();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }   
    }
}

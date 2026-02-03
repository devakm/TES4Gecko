package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableColumn;

/**
 * Dialog to edit the plugin description
 */
public class EditDialog extends JDialog implements ActionListener {
    
    /** Plugin information */
    private PluginInfo pluginInfo;
    
    /** Plugin versions */
    private Float[] versions;
    
    /** Version field */
    private JComboBox versionField;
    
    /** Creator field */
    private JTextField creatorField;
    
    /** Summary field */
    private JTextArea summaryField;
    
    /** Description updated */
    private boolean descriptionUpdated = false;
    
    // Only here to allow for explicit listener removal.
    private JButton updateButton;
    private JButton cancelButton;

    /**
     * Create the dialog
     *
     * @param       parent          Parent frame
     * @param       pluginInfo      The plugin information
     */
    public EditDialog(JFrame parent, PluginInfo pluginInfo) {
        super(parent, pluginInfo.getName(), true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.pluginInfo = pluginInfo;
    
        //
        // Build the plugin version list.  Base Oblivion is 0.8 while the Shivering Isles
        // expansion is 1.0.
        //
        versions = new Float[2];
        versions[0] = new Float(0.8f);
        versions[1] = new Float(1.0f);
        
        //
        // Get the summary with "\r\n" replaced with "\n"
        //
        StringBuilder summary = new StringBuilder(pluginInfo.getSummary());
        int index = 0;
        while (true) {
            index = summary.indexOf("\r\n", index);
            if (index < 0)
                break;
            
            summary.delete(index, index+1);
            index++;
        }

        //
        // Create the edit pane
        //
        Dimension labelSize = new Dimension(70, 12);
        
        JPanel versionPane = new JPanel();
        versionPane.setOpaque(false);
        JLabel label = new JLabel("Version: ", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        versionField = new JComboBox(versions);
        versionField.setSelectedItem(new Float(pluginInfo.getVersion()));
        versionPane.add(label);
        versionPane.add(versionField);
        
        JPanel creatorPane = new JPanel();
        creatorPane.setOpaque(false);
        label = new JLabel("Creator: ", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        creatorField = new JTextField(pluginInfo.getCreator(), 32);
        creatorPane.add(label);
        creatorPane.add(creatorField);
        
        JPanel summaryPane = new JPanel();
        summaryPane.setOpaque(false);
        label = new JLabel("Summary :", JLabel.LEADING);
        label.setPreferredSize(labelSize);
        summaryField = new JTextArea(summary.toString(), 8, 32);
        summaryField.setLineWrap(true);
        summaryField.setWrapStyleWord(true);
        summaryField.setFont(creatorField.getFont());
        JScrollPane scrollPane = new JScrollPane(summaryField);
        summaryPane.add(label);
        summaryPane.add(scrollPane);
        
        //
        // Create the buttons
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Main.backgroundColor);

        updateButton = new JButton("Update");
        updateButton.setActionCommand("update");
        updateButton.addActionListener(this);
        buttonPane.add(updateButton);

        cancelButton = new JButton("Cancel");
        cancelButton.setActionCommand("cancel");
        cancelButton.addActionListener(this);
        buttonPane.add(cancelButton);

        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPane.add(creatorPane);
        contentPane.add(summaryPane);
        contentPane.add(versionPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);            
    }
    
    /**
     * Return the description update status
     *
     * @return                      TRUE if the description was updated
     */
    public boolean isUpdated() {
        return descriptionUpdated;
    }

    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       pluginInfo      Plugin information
     * @return      updated         TRUE if the description was updated
     */
    public static boolean showDialog(JFrame parent, PluginInfo pluginInfo) {
        EditDialog dialog = new EditDialog(parent, pluginInfo);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.isUpdated();
    }
    
    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            if (action.equals("update")) {
                //
                // Get the updated fields
                //
                Float version = (Float)versionField.getSelectedItem();
                String creator = creatorField.getText();
                StringBuilder summary = new StringBuilder(summaryField.getText());
                
                if (version == null)
                    version = new Float(0.8f);
                
                if (creator.length() == 0)
                    creator = new String("DEFAULT");
                
                //
                // Replace "\n" with "\r\n" in the summary
                //
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
                
                //
                // Update the plugin
                //
                pluginInfo.setVersion(version.floatValue());
                pluginInfo.setCreator(creator);
                pluginInfo.setSummary(summary.toString());
                descriptionUpdated = true;
                setVisible(false);
                cancelButton.removeActionListener(this);
                updateButton.removeActionListener(this);
                EditDialog.removeAllComponents(this);
                dispose();
            } else if (action.equals("cancel")) {
                setVisible(false);
//                cancelButton.removeActionListener(this);
//                updateButton.removeActionListener(this);
                EditDialog.removeAllComponents(this);
                dispose();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }
    }

    public static void removeAllComponents(Container cont)
    {
    	Component[] components = cont.getComponents();
    	Component comp;
    	for (int i = 0; i < components.length; i++)
    	{
    		comp = components[i];
    		if (comp != null)    			
			{
    			if (comp instanceof JTree) ((JTree)comp).setCellRenderer(null);
    			else if (comp instanceof JTable)
    			{
    				((JTable)comp).setDefaultRenderer(Object.class, null);
    				removeTableColumnRenderers((JTable)comp);
				}
    			cont.remove(comp);
    			if (comp instanceof Container) removeAllComponents((Container)comp);
    		}
		} 
    	if (cont instanceof Window)	((Window)cont).dispose();
	}
    
    private static void removeTableColumnRenderers(JTable table)
    {
    	for (Enumeration<TableColumn> cols=table.getColumnModel().getColumns();
    	cols.hasMoreElements(); )
		{
    		cols.nextElement().setCellRenderer(null);
		}
	}
	
}


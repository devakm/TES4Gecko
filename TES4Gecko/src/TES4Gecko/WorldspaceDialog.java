package TES4Gecko;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Get the Move Worldspace option
 */
public class WorldspaceDialog extends JDialog implements ActionListener {
    
    /** Option **/
    private int option = -1;
    
    /** Insert worldspace placeholders */
    private boolean insertPlaceholders = false;
    
    /** Placeholders field */
    private JCheckBox placeholdersField;
    
    /** 
     * Create a new instance of WorldspaceDialog
     *
     * @param       parent          The parent frame
     */
    public WorldspaceDialog(JFrame parent) {
        super(parent, "Move Worldspaces", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        JPanel buttonPane = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPane.setOpaque(true);
        buttonPane.setBackground(Main.backgroundColor);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        placeholdersField = new JCheckBox("Insert worldspace placeholders", insertPlaceholders);
        placeholdersField.setBackground(Main.backgroundColor);
        buttonPane.add(placeholdersField);
        
        buttonPane.add(Box.createGlue());
        
        JButton button = new JButton("Move to master index");
        button.setActionCommand("move to master index");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Cancel");
        button.setActionCommand("cancel");
        button.addActionListener(this);
        buttonPane.add(button);
        
        setContentPane(buttonPane);
    }
    
    /**
     * Get the Move Worldspace option
     *
     * @param       parent          The parent frame
     * @return                      0 = Do not insert placeholders
     *                              1 = Insert placeholders
     *                             -1 = Request canceled
     */
    public static int showDialog(JFrame parent) {
        WorldspaceDialog dialog = new WorldspaceDialog(parent);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        int result = dialog.option;
        if (result == 0 && dialog.insertPlaceholders)
            result = 1;
        
        return result;
    }

    
    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {
            String action = ae.getActionCommand();
            insertPlaceholders = placeholdersField.isSelected();
            if (action.equals("move to master index")) {
                option = 0;
                setVisible(false);
                dispose();
            } else if (action.equals("cancel")) {
                setVisible(false);
                dispose();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }
    }
}

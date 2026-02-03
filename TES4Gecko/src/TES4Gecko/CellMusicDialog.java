package TES4Gecko;

import java.io.*;

import java.util.Iterator;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Dialog to change cell music type
 */
public class CellMusicDialog extends JDialog implements ActionListener {
    
    private JButton doneButton;
    
    public String cellMusicType = Default;
    
    public static final String Default = "Default";
    public static final String Public = "Public";
    public static final String Dungeon = "Dungeon";
    public static final String Cancel = "Cancel";
    private static final String Title = "Exterior Cell Music"; 
    
    /**
     * Create the dialog
     *
     * @param       parent          Parent frame
     */
    public CellMusicDialog(JDialog parent) {
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //
        // Create the buttons
        //
        JRadioButton defaultButton   = new JRadioButton(Default , true);
        JRadioButton publicButton    = new JRadioButton(Public, false);
        JRadioButton dungeonButton = new JRadioButton(Dungeon, false);
        defaultButton.setBackground(Main.backgroundColor);
        publicButton.setBackground(Main.backgroundColor);
        dungeonButton.setBackground(Main.backgroundColor);
        ButtonGroup bgroup = new ButtonGroup();
        defaultButton.setActionCommand("set default music");
        defaultButton.addActionListener(this);
        bgroup.add(defaultButton);
        publicButton.setActionCommand("set public music");
        publicButton.addActionListener(this);
        bgroup.add(publicButton);
        dungeonButton.setActionCommand("set dungeon music");
        dungeonButton.addActionListener(this);
        bgroup.add(dungeonButton);
        JPanel radioPane = new JPanel(new GridLayout(3,1));
        radioPane.setBackground(Main.backgroundColor);
        radioPane.add(defaultButton);
        radioPane.add(publicButton);
        radioPane.add(dungeonButton);

        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBackground(Main.backgroundColor);

        doneButton = new JButton("Done");
        doneButton.setActionCommand("done");
        doneButton.addActionListener(this);
        buttonPane.add(doneButton);

        buttonPane.add(Box.createHorizontalStrut(45));
        JButton button = new JButton("Cancel");
        button.setActionCommand("cancel");
        button.addActionListener(this);
        buttonPane.add(button);

        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(radioPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
        setTitle(Title);
        
        // Add listener for window closing events; set 
        addWindowListener(new WindowAdapter()
        {
        	public void windowClosing(WindowEvent e)
        	{
        		// Close-dialog icon was clicked, so set cancel music change.
        		cellMusicType = Cancel;
                setVisible(false);
        		dispose();
            }
        });
    }

    /**
     * Show the dialog
     *
     * @param       parent          Parent frame
     * @param       pluginFile      Plugin file
     * @param       plugin          Plugin
     */
    public static String showDialog(JDialog parent)
    {
    	CellMusicDialog dialog = new CellMusicDialog(parent);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.cellMusicType;
    }
    
    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae)
    {
        String action = ae.getActionCommand();
        try
        {
            if (action.equals("set default music"))
            {
            	cellMusicType = Default;
            }
            else if (action.equals("set public music"))
            {
            	cellMusicType = Public;
            }
            else if (action.equals("set dungeon music"))
            {
            	cellMusicType = Dungeon;
            }
            else if (action.equals("cancel")) // By default, export all exterior cells.
            {
            	cellMusicType = Cancel;
                setVisible(false);
                dispose();
            }
            else if (action.equals("done"))
            {
                setVisible(false);
                dispose();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event " + action, exc);
        }   
    }
}

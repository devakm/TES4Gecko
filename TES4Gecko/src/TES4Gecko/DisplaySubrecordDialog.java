package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Dialog to display the data for a subrecord
 */
public class DisplaySubrecordDialog extends JDialog implements ActionListener {

    /**
     * Create the display dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       subrecord       The subrecord to display
     */
    public DisplaySubrecordDialog(JDialog parent, PluginSubrecord subrecord) {
        super(parent, "Subrecord Data: " + subrecord.getSubrecordType(), true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Set the label showing how he data is displayed.
        JLabel displayTypeLabel = new JLabel("<html><b>Subrecord data displayed as " +
        		subrecord.getDisplayDataTypeLabel() + "</b></html>");
        displayTypeLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPanel displayTypePane = new JPanel();
        displayTypePane.setBackground(Main.backgroundColor);
        displayTypePane.add(displayTypeLabel);

        //
        // Create the text area containing the record data
        // Very hackish way to deal with the horizontal scrollbar 
        // encroaching into the text area.
        String displayData = subrecord.getDisplayData();
        int numRows = 0;
        if (displayData.contains("\n") || displayData.contains("\r"))
        {
        	String[] dummyArray = displayData.split("[\n\r]");
        	boolean horizScroll = false;
        	for (int i = 0; i < dummyArray.length; i++)
        	{
        		if (!dummyArray[i].equals("")) numRows++;
        		else if (dummyArray[i].length() > 79) horizScroll = true;
        	}
        	if (horizScroll) numRows++;
        }
        else
        {
        	numRows = (displayData.length() > 79) ? 2: 1;
        }
        JTextArea textArea = new JTextArea(displayData);
        textArea.setRows(numRows);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //
        // Create the buttons
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Main.backgroundColor);

        JButton button = new JButton("Done");
        button.setActionCommand("done");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        buttonPane.add(button);

        contentPane.add(displayTypePane);
        contentPane.add(scrollPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);  

    }

    /**
     * Create the display dialog, but always with byte array subrec type. The boolean arg
     * is bogus; its mere presence sends us into this method.
     *
     * @param       parent          Parent window for the dialog
     * @param       subrecord       The subrecord to display
     * @param       alwaysByte      Marker arg
     */
    public DisplaySubrecordDialog(JDialog parent, PluginSubrecord subrecord, boolean alwaysByte) {
        super(parent, "Subrecord Data: Byte Array", true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Set the label showing how he data is displayed.
        JLabel displayTypeLabel = new JLabel("<html><b>Subrecord data displayed as " +
        		"Byte Array" + "</b></html>");
        displayTypeLabel.setAlignmentX(LEFT_ALIGNMENT);
        JPanel displayTypePane = new JPanel();
        displayTypePane.setBackground(Main.backgroundColor);
        displayTypePane.add(displayTypeLabel);

        //
        // Create the text area containing the record data
        // Very hackish way to deal with the horizontal scrollbar 
        // encroaching into the text area.
        String displayData = subrecord.getDisplayDataAsBytes();
        int numRows = 0;
        if (displayData.contains("\n") || displayData.contains("\r"))
        {
        	String[] dummyArray = displayData.split("[\n\r]");
        	boolean horizScroll = false;
        	for (int i = 0; i < dummyArray.length; i++)
        	{
        		if (!dummyArray[i].equals("")) numRows++;
        		else if (dummyArray[i].length() > 79) horizScroll = true;
        	}
        	if (horizScroll) numRows++;
        }
        else
        {
        	numRows = (displayData.length() > 79) ? 2: 1;
        }
        JTextArea textArea = new JTextArea(displayData);
        textArea.setRows(numRows);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //
        // Create the buttons
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Main.backgroundColor);

        JButton button = new JButton("Done");
        button.setActionCommand("done");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        buttonPane.add(button);

        contentPane.add(displayTypePane);
        contentPane.add(scrollPane);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);  

    }

    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       subrecord       The subrecord to display
     */
    public static void showDialog(JDialog parent, PluginSubrecord subrecord) {
        DisplaySubrecordDialog dialog = new DisplaySubrecordDialog(parent, subrecord);
        dialog.pack();
        Dimension resizeDim = new Dimension(Math.min(dialog.getSize().width, parent.getSize().width * 2/3),
        		Math.min(dialog.getSize().height, parent.getSize().height * 2/3));
        dialog.setPreferredSize(resizeDim);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
    
    /**
     * Show the dialog, but always with byte array subrec type. The boolean arg
     * is bogus; its mere presence sends us into this method.
     *
     * @param       parent          Parent window for the dialog
     * @param       subrecord       The subrecord to display
     * @param       alwaysByte      Marker arg
     **/
    public static void showDialog(JDialog parent, PluginSubrecord subrecord, boolean alwaysByte) {
        DisplaySubrecordDialog dialog = new DisplaySubrecordDialog(parent, subrecord, alwaysByte);
        dialog.pack();
        Dimension resizeDim = new Dimension(Math.min(dialog.getSize().width, parent.getSize().width * 2/3),
        		Math.min(dialog.getSize().height, parent.getSize().height * 2/3));
        dialog.setPreferredSize(resizeDim);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
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
                setVisible(false);
                dispose();                
            }            
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }       
    }
}

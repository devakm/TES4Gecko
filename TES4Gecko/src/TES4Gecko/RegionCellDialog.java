package TES4Gecko;

import java.io.*;

import java.util.Iterator;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Dialog to edit the plugin master list
 */
public class RegionCellDialog extends JDialog implements ActionListener {
    
    /** Region Data list box */
    private JList regionList;
    
    private JButton doneButton;
    
    private String regionsToExport = All;
    
    public static final String None = "None";
    public static final String All = "All";
    public static final String CancelMerge = "CancelMerge";
    public static final String Some = "Some";
    public static final String Except = "Except";
    public static final String Separator = ":";
    private static final String Title = "Exterior Cells to be Merged"; 
    private static final String Header = 
    	"<html>There are exterior cells in the plugin to be merged. Please select whether to merge all<br>" 
      + "such cells, none of those cells, or select the regions where the cells to be merged are located.<br>"
      + " Please note the following:<br><br>"
      + "&#8226 Persistent references are unaffected.<br>"
      + "&#8226 New regions and worldspaces are still merged; this only affects exterior cells.<br>"
      + "&#8226 If this window is closed by any method except the <i>Done</i> button, all exterior cells are merged.<br></html>";
    private static final String Unassigned = "<html><b><i>There are exterior cells not assigned to any region in this plugin!<br>" +
    										 "Only the first option will merge these cells!</i></b></html>";
    
    class cellRenderer extends JPanel implements ListCellRenderer
    {
    	JPanel testPanel = null;
    	//JPanel thisPanel = null;
    	JLabel plugin = null;
    	JLabel worldspace = null;
    	JLabel region = null;
     
    	cellRenderer()
    	{
    		plugin = new JLabel("");
    		worldspace = new JLabel("");
    		region = new JLabel("");
    		testPanel = new JPanel();
    		testPanel.setLayout(new GridLayout(1,3));
    		testPanel.add(plugin);
    		testPanel.add(worldspace);
    		testPanel.add(region);
    	}
    	public Component getListCellRendererComponent(JList list, Object value, int idx, boolean isSel, boolean hasFocus)
    	{
    		if (value!=null)
    		{
    			// Object has to be an array of length 4of strings:
    			// index 0 - Form ID (not displayed)
    			// index 1 - Plugin file name
    			// index 2 - Worldspace name
    			// index 3 - Region name
    			String[] regionInfo = (String[]) value;
    			plugin.setText(regionInfo[1]); 
    			worldspace.setText(regionInfo[2]); 
    			region.setText(regionInfo[3]); 
    		}
    		testPanel.setBackground((isSel)?list.getSelectionBackground():list.getBackground());
    		testPanel.setForeground((isSel)?list.getSelectionForeground():list.getForeground());
    		//testPanel.setSize(30,30);
    		return testPanel;
    	}
    }


    /**
     * Create the dialog
     *
     * @param       parent          Parent frame
     * @param       pluginFile      Plugin file
     * @param       plugin          Plugin
     */
    public RegionCellDialog(JFrame parent, Vector<String[]> regionData) {
        super(parent, true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //
        // First check to see  if there are unassigned exterior cells and if there
        // are ONLY unassigned exterior cells
        //
        boolean hasUnassignedCells = false;
        boolean hasOnlyUnassignedCells = false;
		for (Iterator<String[]> i = regionData.iterator(); i.hasNext(); )
		{
			String[] regionArray = i.next();
			if (Integer.parseInt(regionArray[0], 16) == Plugin.NoRegionAssignedToCell)
			{
				hasUnassignedCells = true;
				i.remove();
				break;
			}
		}
		if (hasUnassignedCells && regionData.size() == 0) // Only unassigned cells.
		{
			hasOnlyUnassignedCells = true;
		}

        regionList = new JList(regionData);
        // The following listener disables "done" if there is no selection made.
        ListSelectionListener listListener = new ListSelectionListener() {
        	public void valueChanged(ListSelectionEvent e) {
        	    if (e.getValueIsAdjusting() == false) {

        	        if (regionList.getSelectedIndices().length == 0) {
        	        //No selection, disable done button.
        	            doneButton.setEnabled(false);

        	        } else {
        	        //Selection, enable the fire button.
        	            doneButton.setEnabled(true);
        	        }
        	    }
        	}        
        };
        regionList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        regionList.addListSelectionListener(listListener);
        regionList.setCellRenderer(new cellRenderer());
        JScrollPane listPane = new JScrollPane(regionList);
        regionList.setEnabled(false); // Initially the ALL option is enabled.
        //
        // Create the buttons
        //
        JRadioButton allButton   = new JRadioButton("Merge all exterior cells; includes cells not assigned to regions" , true);
        JRadioButton noneButton    = new JRadioButton("Merge no exterior cells", false);
        JRadioButton cancelMergeButton = new JRadioButton("Cancel entire merge", false);
        JRadioButton selectButton = new JRadioButton("Select regions with exterior cells to be merged:", false);
        JRadioButton exceptButton = new JRadioButton("Select regions with exterior cells NOT to be merged:", false);
        allButton.setBackground(Main.backgroundColor);
        noneButton.setBackground(Main.backgroundColor);
        cancelMergeButton.setBackground(Main.backgroundColor);
        selectButton.setBackground(Main.backgroundColor);
        exceptButton.setBackground(Main.backgroundColor);
        ButtonGroup bgroup = new ButtonGroup();
        allButton.setActionCommand("merge all");
        allButton.addActionListener(this);
        bgroup.add(allButton);
        noneButton.setActionCommand("merge none");
        noneButton.addActionListener(this);
        bgroup.add(noneButton);
        cancelMergeButton.setActionCommand("cancel merge");
        cancelMergeButton.addActionListener(this);
        bgroup.add(cancelMergeButton);
        selectButton.setActionCommand("merge some");
        selectButton.addActionListener(this);
        if (!hasOnlyUnassignedCells)
        {
            bgroup.add(selectButton);
        }
        exceptButton.setActionCommand("merge some except");
        exceptButton.addActionListener(this);
        if (!hasOnlyUnassignedCells)
        {
            bgroup.add(exceptButton);
        }

        JPanel radioPane = new JPanel(new GridLayout(5,1));
        radioPane.setBackground(Main.backgroundColor);
        radioPane.add(allButton);
        radioPane.add(noneButton);
        radioPane.add(cancelMergeButton);
        if (!hasOnlyUnassignedCells)
        {
        	radioPane.add(selectButton);
        }
        if (!hasOnlyUnassignedCells)
        {
        	radioPane.add(exceptButton);
        }

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
        JLabel headerLabel = new JLabel(Header, JLabel.LEFT);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPane.add(headerLabel);
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(radioPane);
        if (hasUnassignedCells)
        {
            JLabel unassignedLabel = new JLabel(Unassigned, JLabel.LEFT);
            unassignedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPane.add(unassignedLabel);
            contentPane.add(Box.createVerticalStrut(5));
        }
        if (!hasOnlyUnassignedCells)
        {
        	contentPane.add(listPane);
        }
        contentPane.add(Box.createVerticalStrut(15));
        contentPane.add(buttonPane);
        setContentPane(contentPane);
        setTitle(Title);
        
        // Add listener for window closing events; set 
        addWindowListener(new WindowAdapter()
        {
        	public void windowClosing(WindowEvent e)
        	{
        		// Close-dialog icon was clicked, so set export to ALL.
        		regionsToExport = All;
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
    public static String showDialog(JFrame parent, Vector<String[]> regionData)
    {
    	RegionCellDialog dialog = new RegionCellDialog(parent, regionData);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.regionsToExport;
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
            if (action.equals("merge all"))
            {
                regionList.clearSelection();
                regionList.setEnabled(false);
                doneButton.setEnabled(true);
                regionsToExport = All;
            }
            else if (action.equals("merge none"))
            {
                regionList.clearSelection();
                regionList.setEnabled(false);
                doneButton.setEnabled(true);
                regionsToExport = None;
            }
            else if (action.equals("cancel merge"))
            {
                regionList.clearSelection();
                regionList.setEnabled(false);
                doneButton.setEnabled(true);
                regionsToExport = CancelMerge;
            }
            else if (action.equals("merge some"))
            {
                regionList.setEnabled(true);
                if (regionList.getSelectedIndex() == -1) // Need at least one region selected.
                	doneButton.setEnabled(false);
                regionsToExport = Some;
            }
            else if (action.equals("merge some except"))
            {
                regionList.setEnabled(true);
                if (regionList.getSelectedIndex() == -1) // Need at least one region selected.
                	doneButton.setEnabled(false);
                regionsToExport = Except;
            }
            else if (action.equals("cancel")) // By default, export all exterior cells.
            {
                regionsToExport = All;
                setVisible(false);
                dispose();
            }
            else if (action.equals("done"))
            {
            	if (regionsToExport.equals(Some) || regionsToExport.equals(Except))
            	{
            		Object[] valueArray = regionList.getSelectedValues();
            		for (int i = 0; i < valueArray.length; i++)
            		{
            			String[] regionValues = (String[]) (valueArray[i]);
            			regionsToExport += Separator + regionValues[0]; // First values is Form ID.
            		}
            	}
                setVisible(false);
                dispose();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event " + action, exc);
        }   
    }
}

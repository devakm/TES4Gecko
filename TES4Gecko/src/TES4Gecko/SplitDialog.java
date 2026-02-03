package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.zip.DataFormatException;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Dialog to split a plugin into an ESM/ESP pair
 */
public class SplitDialog extends JDialog implements ActionListener, TreeExpansionListener {
    
    /** Input plugin file */
    private File pluginFile;
    
    /** Input plugin */
    private Plugin plugin;
    
    /** Input plugin node */
    private PluginNode pluginNode;
    
    /** Input plugin tree model */
    private DefaultTreeModel pluginTreeModel;
    
    /** Input plugin tree */
    private JTree pluginTree;
    
    /** Output master file */
    private File outputMasterFile;
    
    /** Output master */
    private Plugin outputMaster;
    
    /** Output master node */
    private PluginNode outputMasterNode;
    
    /** Output master tree model */
    private DefaultTreeModel outputMasterTreeModel;
    
    /** Output master tree */
    private JTree outputMasterTree;
    
    /** Output plugin file */
    private File outputPluginFile;
    
    /** Output plugin */
    private Plugin outputPlugin;
    
    /** Output plugin node */
    private PluginNode outputPluginNode;
    
    /** Output plugin tree model */
    private DefaultTreeModel outputPluginTreeModel;
    
    /** Output plugin tree */
    private JTree outputPluginTree;
    
    /** Independent ESM/ESP field */
    private JCheckBox independentField;
    
    /** Split button */
    private JButton splitButton;
    
    /** 
     * Create a new instance of SplitDialog 
     *
     * @param       parent          Parent window for the dialog
     * @param       pluginFile      Plugin file
     * @param       pluginNode      Plugin node
     */
    public SplitDialog(JFrame parent, File pluginFile, PluginNode pluginNode) {
        super(parent, "Split Plugin", true);
        
        //
        // Save the plugin information
        //
        this.pluginFile = pluginFile;
        this.pluginNode = pluginNode;
        this.plugin = pluginNode.getPlugin();
        
        List<PluginGroup> pluginGroupList = plugin.getGroupList();
        List<String> pluginMasterList = plugin.getMasterList();
        String parentDirectory = pluginFile.getParent();
        String baseName = pluginFile.getName();
        int index = baseName.lastIndexOf('.');
        if (index > 0)
            baseName = baseName.substring(0, index);
        
        //
        // Create the output master
        //
        outputMasterFile = new File(parentDirectory+Main.fileSeparator+"OUTPUT_"+baseName+".esm");
        outputMaster = new Plugin(outputMasterFile, plugin.getCreator(), plugin.getSummary(), pluginMasterList);
        outputMaster.setMaster(true);
        outputMaster.setVersion(plugin.getVersion());
        outputMasterNode = new PluginNode(outputMaster);
        
        //
        // Create a top-level group for each top-level group in the original plugin
        //
        List<PluginGroup> outputGroupList = outputMaster.getGroupList();
        for (PluginGroup group : pluginGroupList) {
            PluginGroup outputGroup = new PluginGroup(group.getGroupRecordType());
            outputGroupList.add(outputGroup);
            GroupNode groupNode = new GroupNode(outputGroup);
            outputMasterNode.insert(groupNode);
        }
        
        //
        // Create the master list for the output plugin.  Note that the output files will need to be
        // renamed before they are used, so the new master list will contain the base name for the
        // new master and not the output name.
        //
        List<String> outputMasterList = new ArrayList<String>(5);
        for (String masterName : pluginMasterList)
            outputMasterList.add(masterName);
        
        outputMasterList.add(baseName+".esm");
        
        //
        // Create the output plugin
        //
        outputPluginFile = new File(parentDirectory+Main.fileSeparator+"OUTPUT_"+baseName+".esp");
        outputPlugin = new Plugin(outputPluginFile, plugin.getCreator(), plugin.getSummary(), outputMasterList);
        outputPlugin.setVersion(plugin.getVersion());
        outputPluginNode = new PluginNode(outputPlugin);
        
        //
        // Create a top-level group for each top-level group in the original plugin
        //
        outputGroupList = outputPlugin.getGroupList();
        for (PluginGroup group : pluginGroupList) {
            PluginGroup outputGroup = new PluginGroup(group.getGroupRecordType());
            outputGroupList.add(outputGroup);
            GroupNode groupNode = new GroupNode(outputGroup);
            outputPluginNode.insert(groupNode);
        }
        
        //
        // Create the plugin tree
        //
        pluginTreeModel = new DefaultTreeModel(pluginNode);
        pluginTree = new JTree(pluginTreeModel);
        pluginTree.setScrollsOnExpand(true);
        pluginTree.addTreeExpansionListener(this);

        JScrollPane pluginScrollPane = new JScrollPane(pluginTree);
        pluginScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        pluginScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        pluginScrollPane.setPreferredSize(new Dimension(380, 380));
        
        JLabel label = new JLabel("Source plugin: "+plugin.getName());
        label.setBackground(Main.backgroundColor);
        
        JPanel pluginPane = new JPanel();
        pluginPane.setLayout(new BoxLayout(pluginPane, BoxLayout.Y_AXIS));
        pluginPane.setBackground(Main.backgroundColor);
        pluginPane.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        pluginPane.add(label);
        pluginPane.add(pluginScrollPane);
        
        //
        // Create the output master tree
        //
        outputMasterTreeModel = new DefaultTreeModel(outputMasterNode);
        outputMasterTree = new JTree(outputMasterTreeModel);
        outputMasterTree.setScrollsOnExpand(true);
        outputMasterTree.addTreeExpansionListener(this);

        JScrollPane outputMasterScrollPane = new JScrollPane(outputMasterTree);
        outputMasterScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputMasterScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputMasterScrollPane.setPreferredSize(new Dimension(380, 380));
        
        label = new JLabel("Output master: "+outputMaster.getName());
        label.setBackground(Main.backgroundColor);
        
        JPanel outputMasterPane = new JPanel();
        outputMasterPane.setLayout(new BoxLayout(outputMasterPane, BoxLayout.Y_AXIS));
        outputMasterPane.setBackground(Main.backgroundColor);
        outputMasterPane.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        outputMasterPane.add(label);
        outputMasterPane.add(outputMasterScrollPane);
        
        //
        // Create the output plugin tree
        //
        outputPluginTreeModel = new DefaultTreeModel(outputPluginNode);
        outputPluginTree = new JTree(outputPluginTreeModel);
        outputPluginTree.setScrollsOnExpand(true);
        outputPluginTree.addTreeExpansionListener(this);

        JScrollPane outputPluginScrollPane = new JScrollPane(outputPluginTree);
        outputPluginScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        outputPluginScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        outputPluginScrollPane.setPreferredSize(new Dimension(380, 380));
        
        label = new JLabel("Output plugin: "+outputPlugin.getName());
        label.setBackground(Main.backgroundColor);
        
        JPanel outputPluginPane = new JPanel();
        outputPluginPane.setLayout(new BoxLayout(outputPluginPane, BoxLayout.Y_AXIS));
        outputPluginPane.setBackground(Main.backgroundColor);
        outputPluginPane.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        outputPluginPane.add(label);
        outputPluginPane.add(outputPluginScrollPane);
        
        //
        // Create the tree pane
        //
        JPanel treePane = new JPanel();
        treePane.setLayout(new BoxLayout(treePane, BoxLayout.X_AXIS));
        treePane.setBackground(Main.backgroundColor);
        treePane.add(pluginPane);
        treePane.add(Box.createHorizontalStrut(10));
        treePane.add(outputMasterPane);
        treePane.add(Box.createHorizontalStrut(10));
        treePane.add(outputPluginPane);
        
        //
        // Create the button pane
        //
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Main.backgroundColor);
        
        independentField = new JCheckBox("Independent ESM/ESP", false);
        independentField.setBackground(Main.backgroundColor);
        buttonPane.add(independentField);

        buttonPane.add(Box.createHorizontalStrut(15));
        
        splitButton = new JButton("Split Plugin");
        splitButton.setActionCommand("split plugin");
        splitButton.addActionListener(this);
        buttonPane.add(splitButton);

        buttonPane.add(Box.createHorizontalStrut(15));

        JButton button = new JButton("Done");
        button.setActionCommand("done");
        button.addActionListener(this);
        buttonPane.add(button);
        
        //
        // Create the content pane
        //
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.add(treePane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        contentPane.setPreferredSize(new Dimension(975, 600));
        setContentPane(contentPane);
    }
    
    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       pluginFile      Plugin file
     * @param       pluginNode      Plugin node
     */
    public static void showDialog(JFrame parent, File pluginFile, PluginNode pluginNode) {
        SplitDialog dialog = new SplitDialog(parent, pluginFile, pluginNode);
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
            } else if (action.equals("split plugin")) {
                splitPlugin();
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }
    }
    
    /**
     * Tree node has been expanded (TreeExpansionListener interface)
     *
     * @param       event               Tree expansion event
     */
    public void treeExpanded(TreeExpansionEvent event) {
        JTree tree = (JTree)event.getSource();
        TreePath treePath = event.getPath();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
        
        //
        // Get the subrecords for a record if we haven't created them yet
        //
        if (node instanceof RecordNode) {
            RecordNode recordNode = (RecordNode)node;
            DefaultMutableTreeNode subrecordNode = (DefaultMutableTreeNode)recordNode.getFirstChild();
            if (subrecordNode.getUserObject() == null) {
                try {
                    recordNode.removeAllChildren();
                    createRecordChildren(recordNode);
                    DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
                    model.nodeStructureChanged(recordNode);
                } catch (Throwable exc) {
                    Main.logException("Exception while creating subrecords", exc);
                }
            }
        }
    }
    
    /**
     * Tree node has been collapsed (TreeExpansionListener interface)
     *
     * @param       event               Tree expansion event
     */
    public void treeCollapsed(TreeExpansionEvent event) {
    }
    
    /**
     * Create the children for a record node
     *
     * @param       recordNode              The record node
     * @exception   DataFormatException     Error while expanding the record data
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The record data is not valid
     */
    private void createRecordChildren(RecordNode recordNode) throws DataFormatException, IOException, PluginException {
        List<PluginSubrecord> subrecordList = recordNode.getRecord().getSubrecords();
        for (PluginSubrecord subrecord : subrecordList) {
            subrecord.setSpillMode(true);
            DefaultMutableTreeNode subrecordNode = new DefaultMutableTreeNode(subrecord);
            recordNode.add(subrecordNode);
        }
    }
    
    /**
     * Split the plugin into an ESM/ESP pair
     */
    private void splitPlugin() {
        
        //
        // Split the plugin
        //
        boolean completed = SplitTask.splitPlugin(this, pluginFile, pluginNode, independentField.isSelected(), 
                                                  outputMasterNode, outputPluginNode);
        if (completed) {
            
            //
            // Disable the 'Split Plugin' button
            //
            splitButton.setEnabled(false);
            
            //
            // Display the new master tree
            //
            outputMasterTreeModel = new DefaultTreeModel(outputMasterNode);
            outputMasterTree.setModel(outputMasterTreeModel);
            
            //
            // Display the new plugin tree
            //
            outputPluginTreeModel = new DefaultTreeModel(outputPluginNode);
            outputPluginTree.setModel(outputPluginTreeModel);
        }
    }
}

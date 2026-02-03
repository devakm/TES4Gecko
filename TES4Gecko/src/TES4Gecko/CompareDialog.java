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
 * Dialog to compare two plugins and display the differences.
 */
public class CompareDialog extends DisplayPlugin implements ActionListener, TreeExpansionListener {
    
    /** Plugin file A */
    private File pluginFileA;
    
    /** Plugin A */
    private Plugin pluginA;
    
    /** Plugin A form adjust */
    private FormAdjust pluginFormAdjustA;
    
    /** Root node for plugin A */
    private PluginNode pluginNodeA;
    
    /** Plugin tree A */
    private JTree pluginTreeA;
    
    /** Plugin tree model A */
    private DefaultTreeModel pluginTreeModelA;
    
    /** Scroll pane for plugin tree A */
    private JScrollPane scrollPaneA;
    
    /** Expand nodes button for plugin tree A */
    private JButton expandButtonA;
    
    /** Progress bar for plugin tree A */
    private JProgressBar progressBarA;
    
    /** Plugin A modified */
    private boolean pluginAModified = false;
    
    /** Expanding plugin A nodes */
    private boolean expandingA = false;
    
    /** Plugin file B */
    private File pluginFileB;
    
    /** Plugin B form adjust */
    private FormAdjust pluginFormAdjustB;
    
    /** Plugin B */
    private Plugin pluginB;
    
    /** Root node for plugin B */
    private PluginNode pluginNodeB;
    
    /** Plugin tree B */
    private JTree pluginTreeB;
    
    /** Plugin tree model B */
    private DefaultTreeModel pluginTreeModelB;
    
    /** Scroll pane for plugin tree B */
    private JScrollPane scrollPaneB;
    
    /** Expand nodes button for plugin tree B */
    private JButton expandButtonB;
    
    /** Progress bar for plugin tree B */
    private JProgressBar progressBarB;
    
    /** Plugin B modified */
    private boolean pluginBModified = false;
    
    /** Expanding plugin B nodes */
    private boolean expandingB = false;
    
    /** Clipboard scroll pane */
    private JScrollPane clipboardScrollPane;
    
    /** Synchronized expansion */
    private boolean synchronizedExpansion = true;
    
    /**
     * Create the display dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       pluginFileA     First plugin file
     * @param       pluginFileB     Second plugin file
     * @param       pluginNodeA     First plugin node
     * @param       pluginNodeB     Second plugin node
     */
    public CompareDialog(JFrame parent, File pluginFileA, File pluginFileB, PluginNode pluginNodeA, PluginNode pluginNodeB) {
        super(parent, "Compare Plugins");
        
        //
        // Save plugin A values
        //
        this.pluginFileA = pluginFileA;
        this.pluginNodeA = pluginNodeA;
        pluginA = pluginNodeA.getPlugin();
        
        //
        // Save plugin B values
        //
        this.pluginFileB = pluginFileB;
        this.pluginNodeB = pluginNodeB;
        pluginB = pluginNodeB.getPlugin();
        
        //
        // Create the tree for plugin A
        //
        pluginTreeModelA = new DefaultTreeModel(pluginNodeA);
        pluginTreeA = new JTree(pluginTreeModelA);
        pluginTreeA.setScrollsOnExpand(true);
        pluginTreeA.setCellRenderer(new CompareCellRenderer());
        pluginTreeA.addTreeExpansionListener(this);
        
        //
        // Create the tree for plugin B
        //
        pluginTreeModelB = new DefaultTreeModel(pluginNodeB);
        pluginTreeB = new JTree(pluginTreeModelB);
        pluginTreeB.setScrollsOnExpand(true);
        pluginTreeB.setCellRenderer(new CompareCellRenderer());
        pluginTreeB.addTreeExpansionListener(this);
        
        //
        // Create the clipboard master list by combining the master lists from the two plugins
        //
        List<String> pluginMasterListA = pluginA.getMasterList();
        List<String> pluginMasterListB = pluginB.getMasterList();
        List<String> clipboardMasterList = new ArrayList<String>(pluginMasterListA.size()+pluginMasterListB.size());
        
        for (String master : pluginMasterListA)
            clipboardMasterList.add(master);
        
        for (String master : pluginMasterListB)
            if (!clipboardMasterList.contains(master))
                clipboardMasterList.add(master);
        
        //
        // Create the form adjustment for plugin A
        //
        int masterCount = pluginMasterListA.size();
        int[] masterMap = new int[masterCount];
        for (int i=0; i<masterCount; i++) {
            String masterName = pluginMasterListA.get(i);
            masterMap[i] = clipboardMasterList.indexOf(masterName);
        }
        
        pluginFormAdjustA = new FormAdjust(masterMap, clipboardMasterList.size());
        pluginNodeA.setFormAdjust(pluginFormAdjustA);

        //
        // Create the form adjustment for plugin B
        //
        masterCount = pluginMasterListB.size();
        masterMap = new int[masterCount];
        for (int i=0; i<masterCount; i++) {
            String masterName = pluginMasterListB.get(i);
            masterMap[i] = clipboardMasterList.indexOf(masterName);
        }
        
        pluginFormAdjustB = new FormAdjust(masterMap, clipboardMasterList.size());
        pluginNodeB.setFormAdjust(pluginFormAdjustB);
        
        //
        // Create the clipboard plugin
        //
        clipboardFile = new File(pluginFileA.getParent()+Main.fileSeparator+"Gecko Clipboard.esp");
        clipboard = new Plugin(clipboardFile,  pluginA.getCreator(), pluginA.getSummary(), clipboardMasterList);
        clipboard.setVersion(Math.max(pluginA.getVersion(), pluginB.getVersion()));
        clipboard.createInitialGroups();
        
        //
        // Create the clipboard tree
        //
        PluginNode rootNode = new PluginNode(clipboard);
        List<PluginGroup> groupList = clipboard.getGroupList();
        for (PluginGroup group : groupList) {
            GroupNode groupNode = new GroupNode(group);
            rootNode.insert(groupNode);
        }
        
        clipboardTreeModel = new DefaultTreeModel(rootNode);
        clipboardTree = new JTree(clipboardTreeModel);
        clipboardTree.setScrollsOnExpand(true);
        clipboardTree.setSelectionModel(null);
        clipboardTree.addTreeExpansionListener(this);
        
        //
        // Create the first plugin pane
        //
        JPanel labelPane = new JPanel();
        labelPane.setBackground(Main.backgroundColor);
        labelPane.add(new JLabel(pluginFileA.getName()));
        
        scrollPaneA = new JScrollPane(pluginTreeA);
        scrollPaneA.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneA.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneA.setPreferredSize(new Dimension(250, 500));
        
        JPanel buttonPane = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPane.setBackground(Main.backgroundColor);
        
        expandButtonA = new JButton("Expand Distinct Nodes");
        expandButtonA.setActionCommand("expand distinct A");
        expandButtonA.addActionListener(this);
        buttonPane.add(expandButtonA);
        
        JButton button = new JButton("Collapse Top Nodes");
        button.setActionCommand("collapse all A");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Toggle Ignore");
        button.setActionCommand("toggle ignore A");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Copy to Clipboard");
        button.setActionCommand("copy to clipboard A");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Save Plugin");
        button.setActionCommand("save plugin A");
        button.addActionListener(this);
        buttonPane.add(button);
        
        progressBarA = new JProgressBar(0, 100);
        progressBarA.setString("Idle");
        progressBarA.setStringPainted(true);
        buttonPane.add(progressBarA);
        
        JPanel filePaneA = new JPanel();
        filePaneA.setLayout(new BoxLayout(filePaneA, BoxLayout.Y_AXIS));
        filePaneA.setBackground(Main.backgroundColor);
        filePaneA.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        filePaneA.add(labelPane);
        filePaneA.add(scrollPaneA);
        filePaneA.add(Box.createVerticalStrut(10));
        filePaneA.add(buttonPane);
        
        //
        // Create the second plugin pane
        //
        labelPane = new JPanel();
        labelPane.setBackground(Main.backgroundColor);
        labelPane.add(new JLabel(pluginFileB.getName()));
        
        scrollPaneB = new JScrollPane(pluginTreeB);
        scrollPaneB.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPaneB.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneB.setPreferredSize(new Dimension(250, 500));
        
        buttonPane = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPane.setBackground(Main.backgroundColor);
        
        expandButtonB = new JButton("Expand Distinct Nodes");
        expandButtonB.setActionCommand("expand distinct B");
        expandButtonB.addActionListener(this);
        buttonPane.add(expandButtonB);
        
        button = new JButton("Collapse Top Nodes");
        button.setActionCommand("collapse all B");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Toggle Ignore");
        button.setActionCommand("toggle ignore B");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Copy to Clipboard");
        button.setActionCommand("copy to clipboard B");
        button.addActionListener(this);
        buttonPane.add(button);
        
        button = new JButton("Save Plugin");
        button.setActionCommand("save plugin B");
        button.addActionListener(this);
        buttonPane.add(button);
        
        progressBarB = new JProgressBar(0, 100);
        progressBarB.setString("Idle");
        progressBarB.setStringPainted(true);
        buttonPane.add(progressBarB);
        
        JPanel filePaneB = new JPanel();
        filePaneB.setLayout(new BoxLayout(filePaneB, BoxLayout.Y_AXIS));
        filePaneB.setBackground(Main.backgroundColor);
        filePaneB.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        filePaneB.add(labelPane);
        filePaneB.add(scrollPaneB);
        filePaneB.add(Box.createVerticalStrut(10));
        filePaneB.add(buttonPane);
        
        //
        // Create the clipboard pane
        //
        labelPane = new JPanel();
        labelPane.setBackground(Main.backgroundColor);
        labelPane.add(new JLabel(clipboardFile.getName()));
        
        clipboardScrollPane = new JScrollPane(clipboardTree);
        clipboardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        clipboardScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        clipboardScrollPane.setPreferredSize(new Dimension(250, 500));
        
        buttonPane = new JPanel(new GridLayout(3, 2, 5, 5));
        buttonPane.setBackground(Main.backgroundColor);
        
        button = new JButton("Save Clipboard");
        button.setActionCommand("save clipboard");
        button.addActionListener(this);
        buttonPane.add(button);
        
        buttonPane.add(Box.createGlue());
        buttonPane.add(Box.createGlue());
        buttonPane.add(Box.createGlue());
        buttonPane.add(Box.createGlue());
        
        JPanel clipboardPane = new JPanel();
        clipboardPane.setLayout(new BoxLayout(clipboardPane, BoxLayout.Y_AXIS));
        clipboardPane.setBackground(Main.backgroundColor);
        clipboardPane.setBorder(BorderFactory.createEtchedBorder(Color.WHITE, Color.BLACK));
        clipboardPane.add(labelPane);
        clipboardPane.add(clipboardScrollPane);
        clipboardPane.add(Box.createVerticalStrut(10));
        clipboardPane.add(buttonPane);

        //
        // Set up the top pane with the tree panes arranged horizontally
        //
        JPanel topPane = new JPanel();
        topPane.setLayout(new BoxLayout(topPane, BoxLayout.X_AXIS));
        topPane.setBackground(Main.backgroundColor);
        topPane.add(filePaneA);
        topPane.add(Box.createHorizontalStrut(15));
        topPane.add(filePaneB);
        topPane.add(Box.createHorizontalStrut(15));
        topPane.add(clipboardPane);
        
        //
        // Set up the bottom pane
        //
        JPanel bottomPane = new JPanel();
        bottomPane.setBackground(Main.backgroundColor);
        
        JCheckBox checkBox = new JCheckBox("Synchronized Expansion", synchronizedExpansion);
        checkBox.setBackground(Main.backgroundColor);
        checkBox.setActionCommand("toggle synchronized expansion");
        checkBox.addActionListener(this);
        bottomPane.add(checkBox);
        
        button = new JButton("Done");
        button.setActionCommand("done");
        button.addActionListener(this);
        bottomPane.add(button);
        
        //
        // Set up the content pane
        //
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPane.add(topPane, BorderLayout.CENTER);
        contentPane.add(bottomPane, BorderLayout.SOUTH);
        contentPane.setPreferredSize(new Dimension(975, 650));
        setContentPane(contentPane);
        
        //
        // Receive WindowListener events
        //
        addWindowListener(new DialogWindowListener());
    }
    
    /**
     * Show the dialog
     *
     * @param       parent          Parent window for the dialog
     * @param       pluginFileA     First plugin file
     * @param       pluginFileB     Second plugin file
     * @param       pluginNodeA     First plugin node
     * @param       pluginNodeB     Second plugin node
     */
    public static void showDialog(JFrame parent, File pluginFileA, File pluginFileB, 
                                  PluginNode pluginNodeA, PluginNode pluginNodeB) {
        CompareDialog dialog = new CompareDialog(parent, pluginFileA, pluginFileB, pluginNodeA, pluginNodeB);
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
                closeDialog();
                setVisible(false);
                dispose();
            } else if (action.equals("toggle synchronized expansion")) {
                synchronizedExpansion = (synchronizedExpansion ? false : true);
            } else if (action.equals("expand distinct A")) {
                if (expandingA) {
                    progressBarA.setValue(0);
                    progressBarA.setString("Idle");
                    expandButtonA.setText("Expand Distinct Nodes");
                    expandingA = false;
                } else {
                    expandButtonA.setText("Stop Expanding Nodes");
                    progressBarA.setString("Expanding");
                    expandingA = true;
                    expandDistinctNodes(pluginTreeA, null);
                }
            } else if (action.equals("continue expand A")) {
                if (expandingA)
                    expandDistinctNodes(pluginTreeA, (Integer)ae.getSource());
            } else if (action.equals("expand distinct B")) {
                if (expandingB) {
                    progressBarB.setValue(0);
                    progressBarB.setString("Idle");
                    expandButtonB.setText("Expand Distinct Nodes");
                    expandingB = false;
                } else {
                    expandButtonB.setText("Stop Expanding Nodes");
                    progressBarB.setString("Expanding");
                    expandingB = true;
                    expandDistinctNodes(pluginTreeB, null);
                }
            } else if (action.equals("continue expand B")) {
                if (expandingB)
                    expandDistinctNodes(pluginTreeB, (Integer)ae.getSource());
            } else if (action.equals("collapse all A")) {
                collapseTopNodes(pluginTreeA);
            } else if (action.equals("collapse all B")) {
                collapseTopNodes(pluginTreeB);
            } else if (action.equals("toggle ignore A")) {
                if (toggleIgnore(pluginTreeA))
                    pluginAModified = true;
            } else if (action.equals("toggle ignore B")) {
                if (toggleIgnore(pluginTreeB))
                    pluginBModified = true;
            } else if (action.equals("copy to clipboard A")) {
                copyRecords(pluginTreeA);
            } else if (action.equals("copy to clipboard B")) {
                copyRecords(pluginTreeB);
            } else if (action.equals("save plugin A")) {
                if (pluginAModified) {
                    if (SaveTask.savePlugin(this, pluginFileA, pluginA)) {
                        pluginAModified = false;
                        validateTree(pluginTreeA);
                    }
                }
            } else if (action.equals("save plugin B")) {
                if (pluginBModified) {
                    if (SaveTask.savePlugin(this, pluginFileB, pluginB)) {
                        pluginBModified = false;
                        validateTree(pluginTreeB);
                    }
                }
            } else if (action.equals("save clipboard")) {
                if (clipboardModified) {
                    if (SaveTask.savePlugin(this, clipboardFile, clipboard)) {
                        clipboardModified = false;
                        validateTree(clipboardTree);
                    }
                }
            }
        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }
    }
    
    /**
     * Tree node has been expanded (TreeExpansionListener interface)
     *
     * @param       event           Tree expansion event
     */
    public void treeExpanded(TreeExpansionEvent event) {
        JTree tree = (JTree)event.getSource();
        TreePath path = event.getPath();
        TreeNode node = (TreeNode)path.getLastPathComponent();
        
        //
        // Add subrecords if we are expanding a record node
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
        
        //
        // Expand the corresponding group or record node in the companion tree.  We will
        // adjust the vertical scroll to display as many of the expanded children as possible.
        //
        if (synchronizedExpansion && (tree == pluginTreeA || tree == pluginTreeB) &&
                                     (node instanceof GroupNode || node instanceof RecordNode)) {
            JTree cmpTree = (tree==pluginTreeA ? pluginTreeB : pluginTreeA);
            JScrollPane scrollPane = (tree==pluginTreeA ? scrollPaneB : scrollPaneA);
            int scrollValue = scrollPane.getHorizontalScrollBar().getValue();
            TreePath cmpPath = findMatchingPath(cmpTree, path);
            if (cmpPath != null && !cmpTree.isExpanded(cmpPath)) {
                cmpTree.expandPath(cmpPath);
                TreeNode cmpNode = (TreeNode)cmpPath.getLastPathComponent();
                int childCount = cmpNode.getChildCount();
                if (childCount > 0) {
                    int visibleRows = scrollPane.getViewport().getExtentSize().height/cmpTree.getRowHeight();
                    int index = Math.min(childCount, visibleRows-1);
                    cmpPath = cmpPath.pathByAddingChild(cmpNode.getChildAt(index-1));
                }
          
                cmpTree.scrollPathToVisible(cmpPath);
                scrollPane.getHorizontalScrollBar().setValue(scrollValue);
            }
        }
    }
    
    /**
     * Tree node has been collapse (TreeExpansionListener interface)
     *
     * @param       event           Tree expansion event
     */
    public void treeCollapsed(TreeExpansionEvent event) {
        JTree tree = (JTree)event.getSource();
        TreePath path = event.getPath();
        TreeNode node = (TreeNode)path.getLastPathComponent();
        
        //
        // Collapse the corresponding group or record node in the companion tree.
        // We will adjust the vertical scroll to display the collapsed node.
        //
        if (synchronizedExpansion && (tree == pluginTreeA || tree == pluginTreeB) &&
                                     (node instanceof GroupNode || node instanceof RecordNode)) {
            JTree cmpTree = (tree==pluginTreeA ? pluginTreeB : pluginTreeA);
            JScrollPane scrollPane = (tree==pluginTreeA ? scrollPaneB : scrollPaneA);
            int scrollValue = scrollPane.getHorizontalScrollBar().getValue();
            TreePath cmpPath = findMatchingPath(cmpTree, path);
            if (cmpPath != null && !cmpTree.isCollapsed(cmpPath)) {
                cmpTree.collapsePath(cmpPath);
                cmpTree.scrollPathToVisible(cmpPath);
                scrollPane.getHorizontalScrollBar().setValue(scrollValue);
            }
        }
    }
    
    /**
     * Close the dialog window
     */
    private void closeDialog() {
        
        //
        // Stop expanding nodes
        //
        expandingA = false;
        expandingB = false;
        
        //
        // Save the first plugin if it has been modified
        //
        if (pluginAModified) {
            int selection = JOptionPane.showConfirmDialog(this, 
                    "The first plugin has been modified. Do you want to save the changes?", 
                    "Plugin Modified", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.YES_OPTION)
                SaveTask.savePlugin(this, pluginFileA, pluginA);
        }
        
        //
        // Save the second plugin if it has been modified
        //
        if (pluginBModified) {
            int selection = JOptionPane.showConfirmDialog(this, 
                    "The second plugin has been modified. Do you want to save the changes?", 
                    "Plugin Modified", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.YES_OPTION)
                SaveTask.savePlugin(this, pluginFileB, pluginB);
        }

        //
        // Save the clipboard if it has been modified
        //
        if (clipboardModified) {
            int selection = JOptionPane.showConfirmDialog(this, 
                    "The clipboard has been modified. Do you want to save the changes?", 
                    "Clipboard Modified", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (selection == JOptionPane.YES_OPTION)
                SaveTask.savePlugin(this, clipboardFile, clipboard);
        } 
    }
    
    /**
     * Collapse the top-level nodes for the specified tree
     *
     * @param       tree            The tree to collapse
     */
    private void collapseTopNodes(JTree tree) {
        PluginNode pluginNode = (tree==pluginTreeA ? pluginNodeA : pluginNodeB);
        TreeNode[] pathNodes = new TreeNode[2];
        pathNodes[0] = pluginNode;
        int count = pluginNode.getChildCount();
        for (int i=0; i<count; i++) {
            GroupNode groupNode = (GroupNode)pluginNode.getChildAt(i);
            pathNodes[1] = groupNode;
            tree.collapsePath(new TreePath(pathNodes));
        }
    }
    
    /**
     * Expand all distinct nodes for the specified tree
     *
     * @param       tree            The tree to expand
     * @param       pathIndex       The current path index
     */
    private void expandDistinctNodes(JTree tree, Integer pathIndex) {
        PluginNode pluginNode;
        JProgressBar progressBar;
        if (tree == pluginTreeA) {
            pluginNode = pluginNodeA;
            progressBar = progressBarA;
        } else {
            pluginNode = pluginNodeB;
            progressBar = progressBarB;
        }
        
        int index = (pathIndex!=null ? pathIndex.intValue() : 0);
        List<TreePath> pathList = pluginNode.getDistinctPaths();
        int count = pathList.size();

        //
        // Stop now if we have expanded all of the paths
        //
        if (index >= count) {
            progressBar.setValue(0);
            progressBar.setString("Idle");
            if (tree == pluginTreeA) {
                expandButtonA.setText("Expand Distinct Nodes");
                expandingA = false;
            } else {
                expandButtonB.setText("Expand Distinct Nodes");
                expandingB = false;
            }
            
            return;
        }
        
        //
        // Expand the next distinct path and update the progress bar
        //
        tree.expandPath(pathList.get(index));
        index++;
        progressBar.setValue((index*100)/count);
        
        //
        // Dispatch a new event after expanding the path in order to avoid locking up the
        // user interface.
        //
        String actionCommand = (tree==pluginTreeA ? "continue expand A" : "continue expand B");
        ActionEvent actionEvent = new ActionEvent(new Integer(index), ActionEvent.ACTION_PERFORMED, actionCommand);
        SwingUtilities.invokeLater(new DeferredActionEvent(this, actionEvent));
    }
    
    /**
     * Find a matching path in one tree based on a path for the companion tree
     *
     * @param       tree            The tree to search
     * @param       path            The tree path to locate
     * @return                      The matching path or null if no match found
     */
    private TreePath findMatchingPath(JTree tree, TreePath path) {
        TreePath cmpPath = new TreePath(tree.getModel().getRoot());
        int count = path.getPathCount();
        boolean foundMatch = true;
        
        //
        // Search for a matching path
        //
        // Group nodes match if they have the same group type and label.  Record nodes
        // match if they have the same record type and form ID.
        //
        for (int i=1; i<count&&foundMatch; i++) {
            TreeNode node = (TreeNode)path.getPathComponent(i);
            TreeNode cmpParentNode = (TreeNode)cmpPath.getPathComponent(i-1);
            int cmpCount = cmpParentNode.getChildCount();
            if (node instanceof GroupNode) {
                foundMatch = false;
                GroupNode groupNode = (GroupNode)node;
                PluginGroup group = groupNode.getGroup();
                int groupType = group.getGroupType();
                byte[] groupLabel = group.getGroupLabel();
                for (int j=0; j<cmpCount; j++) {
                    TreeNode cmpNode = (TreeNode)cmpParentNode.getChildAt(j);
                    if (cmpNode instanceof GroupNode) {
                        GroupNode cmpGroupNode = (GroupNode)cmpNode;
                        PluginGroup cmpGroup = cmpGroupNode.getGroup();
                        int cmpGroupType = cmpGroup.getGroupType();
                        byte[] cmpGroupLabel = cmpGroup.getGroupLabel();
                        if (groupType == cmpGroupType && groupLabel[0] == cmpGroupLabel[0] &&
                                                         groupLabel[1] == cmpGroupLabel[1] &&
                                                         groupLabel[2] == cmpGroupLabel[2] &&
                                                         groupLabel[3] == cmpGroupLabel[3]) {
                            foundMatch = true;
                            cmpPath = cmpPath.pathByAddingChild(cmpGroupNode);
                            break;
                        }
                    }
                }
            } else if (node instanceof RecordNode) {
                foundMatch = false;
                RecordNode recordNode = (RecordNode)node;
                PluginRecord record = recordNode.getRecord();
                for (int j=0; j<cmpCount; j++) {
                    TreeNode cmpNode = (TreeNode)cmpParentNode.getChildAt(j);
                    if (cmpNode instanceof RecordNode) {
                        RecordNode cmpRecordNode = (RecordNode)cmpNode;
                        PluginRecord cmpRecord = cmpRecordNode.getRecord();
                        if (record.equals(cmpRecord)) {
                            foundMatch = true;
                            cmpPath = cmpPath.pathByAddingChild(cmpRecordNode);
                            break;
                        }
                    }
                }
            }
        }
        
        return (foundMatch ? cmpPath : null);
    }
    
    /**
     * Process window events
     */
    private class DialogWindowListener extends WindowAdapter {
        
        /**
         * Create a new window listener
         */
        public DialogWindowListener() {
            super();
        }
        
        /**
         * Window is closing (WindowListener interface)
         *
         * @param   we              Window event
         */
        public void windowClosing(WindowEvent we) {
            closeDialog();
        }
    }
    
    /**
     * Tree cell renderer for the compare trees
     */
    private class CompareCellRenderer extends DefaultTreeCellRenderer {
        
        /**
         * Create a new tree cell renderer
         */
        public CompareCellRenderer() {
            super();
            
            //
            // Set our color scheme
            //
            setTextSelectionColor(Color.WHITE);
            setTextNonSelectionColor(Color.BLACK);
            setBackgroundSelectionColor(Color.BLUE);
            setBackgroundNonSelectionColor(Color.WHITE);
        }
        
        /**
         * Get the tree cell renderer component
         *
         * @param       tree        The tree
         * @param       value       The tree node
         * @param       isSelected  TRUE if the node is selected
         * @param       isExpanded  TRUE if the node is expanded
         * @param       isLeaf      TRUE if the node is a leaf
         * @param       row         The tree row
         * @param       hasFocus    TRUE if the tree has the focus
         * @return                  The component used to render the cell
         */
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded,
                                                      boolean isLeaf, int row, boolean hasFocus) {
            
            //
            // Get the cell renderer component (JLabel)
            //
            Component component = super.getTreeCellRendererComponent(tree, value, isSelected, isExpanded, isLeaf, 
                                                                     row, hasFocus);
            
            //
            // Set the non-selected background color depending on whether the node is shared or distinct
            //
            if (value instanceof GroupNode)
                setBackgroundNonSelectionColor(((GroupNode)value).isDistinct() ? Color.YELLOW : Color.WHITE);
            else if (value instanceof RecordNode)
                setBackgroundNonSelectionColor(((RecordNode)value).isDistinct() ? Color.YELLOW : Color.WHITE);
            else
                setBackgroundNonSelectionColor(Color.WHITE);
            
            return component;
        }
    }
    
    /**
     * Deferred action event
     */
    private class DeferredActionEvent implements Runnable {
        
        /** The action listener */
        private ActionListener listener;
        
        /** The action event */
        private ActionEvent event;
        
        /**
         * Create a new instance of DeferredActionEvent
         *
         * @param   listener        The action listener
         * @param   event           The action event
         */
        public DeferredActionEvent(ActionListener listener, ActionEvent event) {
            this.listener = listener;
            this.event = event;
        }
        
        /**
         * Perform the action
         */
        public void run() {
            listener.actionPerformed(event);
        }
    }
}

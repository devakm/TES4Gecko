package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * Main application window
 */
public class MainWindow extends JFrame implements ActionListener {

    /** Application window is minimized */
    private boolean windowMinimized = false;

    /**
     * Create the application window
     */
    public MainWindow() {

        //
        // Initialize the window
        //
        super("TES4Gecko Plugin Utility");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        //
        // Position the window using the saved position from the last time
        // the program was run.  We will let Windows position the window
        // if this is the first time.
        //
        String propValue = Main.properties.getProperty("window.main.position");
        if (propValue != null) {
            int frameX = 0;
            int frameY = 0;
            int sep = propValue.indexOf(',');
            frameX = Integer.parseInt(propValue.substring(0, sep));
            frameY = Integer.parseInt(propValue.substring(sep+1));
            setLocation(frameX, frameY);
        }

        //
        // Create the content pane
        //
        JPanel contentPane = new JPanel(new GridLayout(0, 2, 20, 20));
        contentPane.setOpaque(true);
        contentPane.setBackground(Main.backgroundColor);
        contentPane.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton button = new JButton("Merge Plugins");
        button.setActionCommand("merge plugins");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Merge To Master");
        button.setActionCommand("merge master");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Split Plugin");
        button.setActionCommand("split plugin");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Compare Plugins");
        button.setActionCommand("compare plugins");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Display/Copy");
        button.setActionCommand("display records");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Edit Description");
        button.setActionCommand("edit description");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Create Patch");
        button.setActionCommand("create patch");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Apply Patch");
        button.setActionCommand("apply patch");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Convert to Master");
        button.setActionCommand("convert master");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Convert to Plugin");
        button.setActionCommand("convert plugin");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Edit Master List");
        button.setActionCommand("edit master list");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Create Silent Voice Files");
        button.setActionCommand("generate responses");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Clean Plugin");
        button.setActionCommand("clean plugin");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Move Worldspaces");
        button.setActionCommand("move worldspaces");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        button = new JButton("Set Directory");
        button.setActionCommand("set directory");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.addActionListener(this);
        contentPane.add(button);

        setContentPane(contentPane);

        //
        // Create the application menu bar
        //
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);
        menuBar.setBackground(new Color(230,230,230));

        //
        // Add the "File" menu to the menu bar
        //
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_M);

        menuItem = new JMenuItem("Exit");
        menuItem.setActionCommand("exit");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuBar.add(menu);

        //
        // Add the "Help" menu to the menu bar
        //
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);

        menuItem = new JMenuItem("About");
        menuItem.setActionCommand("about");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuBar.add(menu);

        //
        // Add the menu bar to the window frame
        //
        setJMenuBar(menuBar);

        //
        // Receive WindowListener events
        //
        addWindowListener(new MainWindowListener());
    }

    /**
     * Action performed (ActionListener interface)
     *
     * @param       ae              Action event
     */
    public void actionPerformed(ActionEvent ae) {
        try {

            //
            // Process the action
            //
            String action = ae.getActionCommand();
            if (Main.debugMode)
            {
                System.out.printf("There are " + Runtime.getRuntime().freeMemory() + " bytes available\n");
//                JOptionPane.showMessageDialog(this, "There are " + Runtime.getRuntime().freeMemory() + " bytes available\n",
//                        "Memory Available", JOptionPane.ERROR_MESSAGE);
            }

            if (action.equals("merge plugins")) {
                mergePlugins();
            } else if (action.equals("merge master")) {
                mergeToMaster();
            } else if (action.equals("split plugin")) {
                splitRecords();
            } else if (action.equals("compare plugins")) {
                comparePlugins();
            } else if (action.equals("display records")) {
                displayRecords();
            } else if (action.equals("convert master")) {
                convertToMaster();
            } else if (action.equals("convert plugin")) {
                convertToPlugin();
            } else if (action.equals("create patch")) {
                createPatch();
            } else if (action.equals("apply patch")) {
                applyPatch();
            } else if (action.equals("edit master list")) {
                editMasterList();
            } else if (action.equals("edit description")) {
                editDescription();
            } else if (action.equals("generate responses")) {
                generateResponses();
            } else if (action.equals("clean plugin")) {
                cleanPlugin();
            } else if (action.equals("move worldspaces")) {
                moveWorldspaces();
            } else if (action.equals("set directory")) {
                setDirectory();
            } else if (action.equals("exit")) {
                exitProgram();
            } else if (action.equals("about")) {
                aboutTES4Plugin();
            }

            //
            // Clean up the spill file
            //
            Main.pluginSpill.reset();

        } catch (Throwable exc) {
            Main.logException("Exception while processing action event", exc);
        }
    }

    /**
     * Apply patch to a plugin
     */
    private void applyPatch() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Get the patch file
        //
        chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Patch File");
        chooser.setFileFilter(new PluginFileFilter(false, false, true));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File patchFile = chooser.getSelectedFile();

        //
        // Apply the patch file
        //
        ApplyPatchTask.applyPatch(this, pluginFile, patchFile);
    }

    /**
     * Clean a plugin file
     */
    private void cleanPlugin() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Clean the plugin
        //
        CleanTask.cleanPlugin(this, pluginFile);
    }

    /**
     * Compare plugin/master files
     */
    private void comparePlugins() {

        //
        // Get the first plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select First File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFileA = chooser.getSelectedFile();

        //
        // Get the second plugin file
        //
        chooser.setDialogTitle("Select Second File");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFileB = chooser.getSelectedFile();

        //
        // Create the plugin trees and display the records
        //
        PluginNode pluginNodeA = CreateTreeTask.createTree(this, pluginFileA);
        if (pluginNodeA != null) {
            PluginNode pluginNodeB = CreateTreeTask.createTree(this, pluginFileB);
            if (pluginNodeB != null) {
                if (CompareTask.comparePlugins(this, pluginNodeA, pluginNodeB))
                    CompareDialog.showDialog(this, pluginFileA, pluginFileB, pluginNodeA, pluginNodeB);
            }
        }
    }

    /**
     * Convert a plugin file to a master file
     */
    private void convertToMaster() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Form the name of the master file
        //
        String inputName = pluginFile.getName();
        int sep = inputName.lastIndexOf('.');
        if (sep <= 0) {
            JOptionPane.showMessageDialog(this, "'"+inputName+"' is not a valid plugin file name",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String outputName = String.format("%s%s%s.esm", pluginFile.getParent(), Main.fileSeparator,
                                          inputName.substring(0, sep));
        File masterFile = new File(outputName);

        //
        // Warn if the master file already exists
        //
        if (masterFile.exists()) {
            int selection = JOptionPane.showConfirmDialog(this,
                            "'"+masterFile.getName()+"' already exists.  Do you want to overwrite it?",
                            "File exists", JOptionPane.YES_NO_OPTION);
            if (selection != 0)
                return;

            masterFile.delete();
        }

        //
        // Copy the plugin file to the master file
        //
        ConvertTask.convertFile(this, pluginFile, masterFile);
    }

    /**
     * Convert a master file to a plugin file
     */
    private void convertToPlugin() {

        //
        // Get the master file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Master File");
        chooser.setFileFilter(new PluginFileFilter(true));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File masterFile = chooser.getSelectedFile();

        //
        // Form the name of the plugin file
        //
        String inputName = masterFile.getName();
        int sep = inputName.lastIndexOf('.');
        if (sep <= 0) {
            JOptionPane.showMessageDialog(this, "'"+inputName+"' is not a valid master file name",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String outputName = String.format("%s%s%s.esp", masterFile.getParent(), Main.fileSeparator,
                                          inputName.substring(0, sep));
        File pluginFile = new File(outputName);

        //
        // Warn if the plugin file already exists
        //
        if (pluginFile.exists()) {
            int selection = JOptionPane.showConfirmDialog(this,
                                "'"+pluginFile.getName()+"' already exists.  Do you want to overwrite it?",
                                "File exists", JOptionPane.YES_NO_OPTION);
            if (selection != 0)
                return;

            pluginFile.delete();
        }

        //
        // Copy the master file to the plugin file
        //
        ConvertTask.convertFile(this, masterFile, pluginFile);
    }

    /**
     * Create a plugin patch
     */
    private void createPatch() {

        //
        // Get the base file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Original File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File baseFile = chooser.getSelectedFile();

        //
        // Get the modified file
        //
        chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Modified File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File modifiedFile = chooser.getSelectedFile();

        //
        // Get the patch file name
        //
        String patchName = baseFile.getName();
        int sep = patchName.lastIndexOf('.');
        if (sep <= 0) {
            JOptionPane.showMessageDialog(this, "'"+patchName+"' is not a valid plugin file name",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        patchName = String.format("%s%s%s.esu", baseFile.getParent(), Main.fileSeparator, patchName.substring(0,sep));
        File patchFile = new File(patchName);

        //
        // Warn if the patch file already exists
        //
        if (patchFile.exists()) {
            int selection = JOptionPane.showConfirmDialog(this,
                            "'"+patchFile.getName()+"' already exists.  Do you want to overwrite it?",
                            "File exists", JOptionPane.YES_NO_OPTION);
            if (selection != 0)
                return;

            patchFile.delete();
        }

        //
        // Create the patch file
        //
        CreatePatchTask.createPatch(this, baseFile, modifiedFile, patchFile);
    }

    /**
     * Display plugin records
     */
    private void displayRecords() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter());
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Create the plugin tree and display the records
        //
        PluginNode pluginNode = CreateTreeTask.createTree(this, pluginFile);
        if (pluginNode != null)
            DisplayDialog.showDialog(this, pluginFile, pluginNode);
    }

    /**
     * Edit the plugin description
     */
    private void editDescription() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Get the current plugin description
        //
        RandomAccessFile in = null;
        float version = 0.0f;
        String creator = null;
        String summary = null;
        boolean descriptionSet = false;

        try {
            if (!pluginFile.exists() || !pluginFile.isFile())
                throw new IOException("'"+pluginFile.getName()+"' does not exist");

            in = new RandomAccessFile(pluginFile, "r");
            PluginHeader header = new PluginHeader(pluginFile);
            header.read(in);
            version = header.getVersion();
            creator = header.getCreator();
            summary = header.getSummary();
            descriptionSet = true;
        } catch (PluginException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Unable to read plugin header", exc);
        }

        try {
            if (in != null)
                in.close();
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!descriptionSet)
            return;

        //
        // Display the plugin description
        //
        PluginInfo pluginInfo = new PluginInfo(pluginFile.getName(), creator, summary, version);
        boolean descriptionUpdated = EditDialog.showDialog(this, pluginInfo);

        //
        // Rewrite the plugin file if the description was updated
        //
        if (descriptionUpdated)
            EditTask.editFile(this, pluginFile, pluginInfo);
    }

    /**
     * Edit the plugin master list
     */
    private void editMasterList() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Load the plugin and display the master list
        //
        Plugin plugin = LoadTask.loadPlugin(this, pluginFile);
        if (plugin != null)
            MasterDialog.showDialog(this, pluginFile, plugin);
    }

    /**
     * Generate response files
     */
    private void generateResponses() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Generate the response files for the plugin
        //
        GenerateTask.generateResponses(this, pluginFile);
    }

    /**
     * Merge plugins
     */
    private void mergePlugins() {

        //
        // Get the plugins to merge
        //
        String[] pluginNames = PluginDialog.showDialog(this);
        if (pluginNames == null)
            return;

        //
        // Get the description for the first plugin
        //
        RandomAccessFile in = null;
        boolean descriptionSet = false;
        String creator = null;
        String summary = null;

        try {
            File pluginFile = new File(Main.pluginDirectory+Main.fileSeparator+pluginNames[0]);
            if (!pluginFile.exists() || !pluginFile.isFile())
                throw new IOException("'"+pluginFile.getName()+"' does not exist");

            in = new RandomAccessFile(pluginFile, "r");
            PluginHeader header = new PluginHeader(pluginFile);
            header.read(in);
            creator = header.getCreator();
            summary = header.getSummary();
            descriptionSet = true;
        } catch (PluginException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
        } catch (Throwable exc) {
            Main.logException("Unable to read plugin header", exc);
        }

        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
        }

        if (!descriptionSet)
            return;

        //
        // Get the merged plugin information
        //
        PluginInfo pluginInfo = MergeDialog.showDialog(this, creator, summary);
        if (pluginInfo == null)
            return;

        //
        // Warn if the merged file already exists
        //
        File mergedFile = new File(Main.pluginDirectory+Main.fileSeparator+pluginInfo.getName());
        if (mergedFile.exists()) {
            int selection = JOptionPane.showConfirmDialog(this,
                            "'"+mergedFile.getName()+"' already exists.  Do you want to overwrite it?",
                            "File exists", JOptionPane.YES_NO_OPTION);
            if (selection != 0)
                return;

            mergedFile.delete();
        }

        //
        // Merge the plugins
        //
        MergeTask.mergePlugins(this, pluginNames, pluginInfo);
    }

    /**
     * Merge a plugin file into a master file
     */
    private void mergeToMaster() {

    	// TESTING ONLY
    	Vector<String[]> testVec = new Vector<String[]>();
    	String[] reg1 = {"0008DFD0", "Oblivion.ESM", "Tamriel", "NibenayBasinValleySubRegion06"};
    	String[] reg2 = {"0007B864", "Oblivion.ESM", "Tamriel", "CyrodiilWeatherRegion"};
    	String[] reg3 = {"0007B8CE", "Oblivion.ESM", "Tamriel", "TowerofFathisArenRegion"};
    	testVec.add(reg1);
    	testVec.add(reg2);
    	testVec.add(reg3);

    	//
        // Get the master file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Master File");
        chooser.setFileFilter(new PluginFileFilter(true));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File masterFile = chooser.getSelectedFile();

        //
        // Get the plugin file
        //
        chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Merge the plugin file into the master file
        //
        MergeTask.mergeToMaster(this, masterFile, pluginFile);
    }

    /**
     * Move worldspaces
     */
    private void moveWorldspaces() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File");
        chooser.setFileFilter(new PluginFileFilter(true, true, false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Move the worldspaces
        //
        int option = WorldspaceDialog.showDialog(this);
        if (option >= 0)
            WorldspaceTask.moveWorldspaces(this, pluginFile, option);
    }

    /**
     * Set the plugin directory
     */
    private void setDirectory() {
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin Directory");
        chooser.setFileFilter(new PluginDirectoryFilter());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showDialog(this, "Select") == JFileChooser.APPROVE_OPTION) {
            Main.pluginDirectory = chooser.getSelectedFile().getPath();
            Main.properties.setProperty("plugin.directory", Main.pluginDirectory);
        }
    }

    /**
     * Split plugin records
     */
    private void splitRecords() {

        //
        // Get the plugin file
        //
        JFileChooser chooser = new JFileChooser(Main.pluginDirectory);
        chooser.setDialogTitle("Select Plugin File to Split");
        chooser.setFileFilter(new PluginFileFilter(false));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
            return;

        File pluginFile = chooser.getSelectedFile();

        //
        // Create the plugin tree and display the split dialog
        //
        PluginNode pluginNode = CreateTreeTask.createTree(this, pluginFile);
        if (pluginNode != null)
            SplitDialog.showDialog(this, pluginFile, pluginNode);
    }

    /**
     * Exit the application
     */
    private void exitProgram() {

        //
        // Remember the current window position and size unless the window
        // is minimized
        //
        if (!windowMinimized) {
            Point p = Main.mainWindow.getLocation();
            Main.properties.setProperty("window.main.position", p.x+","+p.y);
        }

        //
        // Save the application properties
        //
        Main.saveProperties();

        //
        // Close and delete the application spill file
        //
        if (Main.pluginSpill != null) {
            try {
                Main.pluginSpill.close();
            } catch (IOException exc) {
                Main.logException("Unable to close spill file", exc);
            }
        }

        //
        // All done
        //
        System.exit(0);
    }

    /**
     * Display information about the Gecko application
     */
    private void aboutTES4Plugin() {
        StringBuilder info = new StringBuilder(256);
        info.append("<html>TES4Gecko Version 15.2<br>");
        info.append("<br>Created by TeamGecko" +
                    "<ul><li>ScripterRon (Ron Hoffman)" +
                    "<li>KomodoDave (N David Brown)" +
                    "<li>SACarrow (Steven A Carrow)" +
                    "<li>dev_akm (Aubrey K McAuley)</ul><br>");
        info.append("See the included ReadMe file for usage details.<br>");

        info.append("<br>User name: ");
        info.append((String)System.getProperty("user.name"));

        info.append("<br>Home directory: ");
        info.append((String)System.getProperty("user.home"));

        info.append("<br><br>OS: ");
        info.append((String)System.getProperty("os.name"));

        info.append("<br>OS version: ");
        info.append((String)System.getProperty("os.version"));

        info.append("<br>OS patch level: ");
        info.append((String)System.getProperty("sun.os.patch.level"));

        info.append("<br><br>Java vendor: ");
        info.append((String)System.getProperty("java.vendor"));

        info.append("<br>Java version: ");
        info.append((String)System.getProperty("java.version"));

        info.append("<br>Java home directory: ");
        info.append((String)System.getProperty("java.home"));

        info.append("<br>Java class path: ");
        info.append((String)System.getProperty("java.class.path"));

        info.append("</html>");
        JOptionPane.showMessageDialog(this, info.toString(), "About Gecko",
                                      JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Process window events
     */
    private class MainWindowListener extends WindowAdapter {

        /**
         * Create a new window listener
         */
        public MainWindowListener() {
            super();
        }

        /**
         * Window has been minimized (WindowListener interface)
         *
         * @param       we              Window event
         */
        public void windowIconified(WindowEvent we) {
            windowMinimized = true;
        }

        /**
         * Window has been restored (WindowListener interface)
         *
         * @param       we              Window event
         */
        public void windowDeiconified(WindowEvent we) {
            windowMinimized = false;
        }

        /**
         * Window is closing (WindowListener interface)
         *
         * @param       we              Window event
         */
        public void windowClosing(WindowEvent we) {
            exitProgram();
        }
    }
}


package TES4Gecko;

import java.io.*;
import java.util.Properties;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * The Gecko application manages plugins for The Elder Scrolls: Oblivion (TES 4).
 *
 * The program properties are maintained in the Oblivion application data directory which
 * is created when TES: Oblivion is installed.
 *
 * -Ddebug.plugin=1 can be specified on the Java command line to activate debug mode.
 *
 * -DOblivion.install.path="path" can be specified on the Java command line to bypass
 * the Windows registry scan.  The path must include a trailing file separator.
 */
public class Main {
    
    /** Dialog background color */
    public static Color backgroundColor = new Color(240, 240, 240);

    /** Main application window */
    public static JFrame mainWindow;
    
    /** Game install directory */
    public static String installPath;

    /** Application data directory */
    public static String dataPath;
    
    /** Plugin directory */
    public static String pluginDirectory;

    /** Filename component separator */
    public static String fileSeparator;

    /** Line separator */
    public static String lineSeparator;
    
    /** Application properties filename */
    public static File propFile;
    
    /** Plugin spill file */
    public static PluginSpill pluginSpill;

    /** Application properties */
    public static Properties properties;
    
    /** Plugin debug mode */
    public static boolean debugMode = false;
    
    /** Maximum amount of storage available */
    public static long maxMemory;
    
    /** Exception text */
    private static String deferredText;
        
    /** Exception */
    private static Throwable deferredException;

    /**
     * Main program for the Gecko application
     *
     * @param       args            Command line arguments
     */
    public static void main(String[] args) {
        
        try {
            
            //
            // Set the application debug mode from the -Ddebug.plugin command option
            //
            String debugString = System.getProperty("debug.plugin");
            if (debugString != null && debugString.equals("1"))
                debugMode = true;
            
            //
            // Display the amount of available memory in megabytes
            //
            maxMemory = Runtime.getRuntime().maxMemory();
            if (debugMode)
                System.out.println("Java has "+maxMemory/1048576L+"MB of storage available");
            
            //
            // Get the line and file separator characters
            //
            lineSeparator = System.getProperty("line.separator");
            fileSeparator = System.getProperty("file.separator");
            
            //
            // Get the application data path
            //
            dataPath = System.getProperty("user.home")+fileSeparator+"Local Settings"+
                                                       fileSeparator+"Application Data"+
                                                       fileSeparator+"Oblivion";
            if (debugMode)
                System.out.printf("Application data path: %s\n", dataPath);
            
            //
            // Create the application data directory (if necessary)
            //
            File dirFile = new File(dataPath);
            if (!dirFile.exists())
                dirFile.mkdirs();
            
            //
            // Load the application properties
            //
            propFile = new File(dataPath+fileSeparator+"TES4Gecko.properties");
            properties = new Properties();
            if (propFile.exists()) {
                FileInputStream in = new FileInputStream(propFile);
                properties.load(in);
                in.close();
            }
            
            //
            // Locate the Oblivion installation directory
            //
            installPath = System.getProperty("Oblivion.install.path");
            if (installPath == null) {
                installPath = properties.getProperty("install.directory");
                if (installPath == null) {
                    String regString = "reg query \"HKLM\\Software\\Bethesda Softworks\\Oblivion\" /v \"Installed Path\"";
                    Process process = Runtime.getRuntime().exec(regString);
                    StreamReader streamReader = new StreamReader(process.getInputStream());
                    streamReader.start();
                    process.waitFor();
                    streamReader.join();
                    String line;
                    while ((line=streamReader.getLine()) != null) {
                        int sep = line.indexOf("REG_SZ");
                        if (sep >= 0) {
                            installPath = line.substring(sep+6).trim();
                            break;
                        }
                    }

                    if (installPath == null)
                        throw new IOException("Unable to locate Oblivion installation directory");
                }
            }
                
            properties.setProperty("install.directory", installPath);
            
            //
            // Set the plugin directory to the Oblivion\Data directory if it has not
            // been set yet
            //
            pluginDirectory = properties.getProperty("plugin.directory");
            if (pluginDirectory == null) {
                pluginDirectory = installPath+"Data";
                properties.setProperty("plugin.directory", pluginDirectory);
            }
            
            //
            // Open the plugin spill file.  We will use the Windows temporary directory
            // if the TEMP environment variable is defined.  Otherwise, we will use our
            // data directory.  We will use 1/10 of the available storage for the
            // spill file cache.
            //
            String tempPath = System.getenv("TEMP");
            if (tempPath == null || tempPath.length() == 0)
                tempPath = dataPath;

            if (debugMode)
                System.out.printf("Temporary data path: %s\n", tempPath);
            
            File spillFile = new File(tempPath+fileSeparator+"Gecko.spill");
            pluginSpill = new PluginSpill(spillFile, maxMemory/10L);

            //
            // Start the Swing GUI using the system look and feel
            //
            if (debugMode)
                System.out.println("Starting the Swing GUI");
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    createAndShowGUI();
                }
            });
        } catch (Exception exc) {
            logException("Exception during program initialization", exc);
        }        
    }

    /**
     * Create and show the application GUI
     *
     * This method is invoked on the AWT event thread to avoid timing
     * problems with other window events
     */
    private static void createAndShowGUI() {

        //
        // Use the normal window decorations as defined by the look-and-feel schema
        //
        try {
            JFrame.setDefaultLookAndFeelDecorated(true);
            mainWindow = new MainWindow();
            mainWindow.pack();
            mainWindow.setVisible(true);
        } catch (Exception exc) {
            logException("Exception during GUI initialization", exc);            
        }
    }

    /**
     * Save the current application properties
     */
    public static void saveProperties() {
        try {
            FileOutputStream out = new FileOutputStream(propFile);
            properties.store(out, "TES4Gecko Properties");
            out.close();
        } catch (Exception exc) {
            logException("Exception while saving application properties", exc);
        }
    }

    /**
     * Log an exception
     *
     * @param       text        Text message describing the cause of the exception
     * @param       exc         The Java exception object
     */
    public static void logException(String text, Throwable exc) {
        
        //
        // Attempt to reclaim storage before displaying the error
        //
        System.runFinalization();
        System.gc();
        
        //
        // Display the error immediately if we are called on the event dispatch
        // thread.  Otherwise, schedule a callback on the dispatch thread.
        //
        if (SwingUtilities.isEventDispatchThread()) {
            StringBuilder string = new StringBuilder(512);

            //
            // Display our error message
            //
            string.append("<html><b>");
            string.append(text);
            string.append("</b><br><br>");

            //
            // Display the exception object
            //
            string.append(exc.toString());
            string.append("<br>");

            //
            // Display the stack trace
            //
            StackTraceElement[] trace = exc.getStackTrace();
            int count = 0;
            for (StackTraceElement elem : trace) {
                string.append(elem.toString());
                string.append("<br>");
                if (++count == 25)
                    break;
            }

            string.append("</html>");
            JOptionPane.showMessageDialog(mainWindow, string, "Error", JOptionPane.ERROR_MESSAGE);
        } else if (deferredException == null) {
            deferredText = text;
            deferredException = exc;
            try {
                javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        Main.logException(deferredText, deferredException);
                        deferredException = null;
                        deferredText = null;
                    }
                });
            } catch (Throwable swingException) {
                deferredException = null;
                deferredText = null;
            }
        }
    }

    /**
     * Dump a byte array to stdout
     *
     * @param       text        Text message
     * @param       data        Byte array
     * @param       length      Data length
     */
    public static void dumpData(String text, byte[] data, int length) {
        System.out.println(text);

        for (int i=0; i<length; i++) {
            if (i%32 == 0)
                System.out.print(String.format(" %14X  ", i));
            else if (i%4 == 0)
                System.out.print(" ");

            System.out.print(String.format("%02X", data[i]));

            if (i%32 == 31)
                System.out.println();
        }

        if (length%32 != 0)
            System.out.println();
    }
}

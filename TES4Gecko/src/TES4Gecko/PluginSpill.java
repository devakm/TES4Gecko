package TES4Gecko;

import java.io.*;
import java.util.*;

/**
 * The plugin spill class manages the spill file.  The data for plugin records is written
 * to the spill file instead of being kept in storage.  This reduces the program storage 
 * requirement at the expense of additional file I/O.
 */
public class PluginSpill {
    
    /** Plugin spill file */
    private File spillFile;
    
    /** Cache size */
    private long cacheSize;
    
    /** Cache map */
    private Map<Long, byte[]> cacheMap;
    
    /** Random access file */
    private RandomAccessFile spill;
    
    /** Next file position */
    private long nextWrite = 0;
    
    /** Current file position */
    private long currentPosition = 0;
    
    /**
     * Create the plugin spill instance
     *
     * @param       spillFile       The spill file
     * @param       cacheSize       The cache size
     * @exception   IOException     An I/O error occurred
     */
    public PluginSpill(File spillFile, long cacheSize) throws IOException {
        this.spillFile = spillFile;
        this.cacheSize = cacheSize;
        
        //
        // Delete the spill file if it already exists
        //
        if (spillFile.exists())
            spillFile.delete();
        
        //
        // Create the spill file
        //
        spill = new RandomAccessFile(spillFile, "rw");
        
        //
        // Create the cache map
        //
        cacheMap = new HashMap<Long, byte[]>(1000);
        
        if (Main.debugMode)
            System.out.println("Spill cache size is "+cacheSize/1048576L+"MB");
    }
    
    /**
     * Close and delete the spill file
     *
     * @return                      The spill file
     * @exception   IOException     An I/O error occurred
     */
    public synchronized void close() throws IOException {
        
        //
        // Close the spill file
        //
        if (spill != null) {
            spill.close();
            spill = null;
            cacheMap = null;
        }
        
        //
        // Delete the spill file
        //
        if (spillFile.exists())
            spillFile.delete();
    }
    
    /**
     * Write the data to the spill file.  The byte array is cached for reuse and must not
     * be modified by the caller.
     *
     * @param       data            The byte array to be written
     * @return                      The file position assigned to the data
     * @exception   IOException     An I/O error occurred
     */
    public synchronized long write(byte[] data) throws IOException {
        
        //
        // Add the new data to our cache
        //
        long position = nextWrite;
        if (data.length > 0) {
            cacheMap.put(new Long(nextWrite), data);
            nextWrite += data.length;
        }
        
        //
        // Write the cached data if we have reached our threshold
        //
        if (nextWrite-currentPosition >= cacheSize) {
            if (Main.debugMode)
                System.out.println("Writing cached data to spill file");
            
            spill.seek(currentPosition);
            while (currentPosition < nextWrite) {
                Long cachePosition = new Long(currentPosition);
                byte[] buffer = cacheMap.get(cachePosition);
                cacheMap.remove(cachePosition);
                spill.write(buffer);
                currentPosition += buffer.length;
            }
        }
        
        return position;
    }
    
    /**
     * Read data from the spill file
     *
     * @param       position        The file position assigned to the data
     * @param       length          The data length
     * @return                      The byte array
     * @exception   IOException     An I/O error occurred
     */
    public synchronized byte[] read(long position, int length) throws IOException {
        
        //
        // Return a zero-length array if there is no spill data
        //
        if (position < 0 || length <= 0)
            return new byte[0];
        
        //
        // Return the data from the cache if it has not been written to the spill file.
        // Otherwise, read the data from the spill file.
        //
        byte[] data = new byte[length];
        Long cachePosition = new Long(position);
        byte[] buffer = cacheMap.get(cachePosition);
        if (buffer != null) {
            if (buffer.length != length)
                throw new IOException("Cached data length "+buffer.length+" is incorrect");
            
            System.arraycopy(buffer, 0, data, 0, length);
        } else {
            spill.seek(position);
            int count = spill.read(data);
            if (count != length)
                throw new IOException("Premature end-of-data on spill file");
        }
        
        return data;
    }
    
    /**
     * Reset the spill file so that the next data array is written to the
     * beginning of the file
     */
    public synchronized void reset() {
        nextWrite = 0;
        currentPosition = 0;
        cacheMap.clear();
    }
}

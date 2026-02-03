package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * The PluginHeader class represents the TES4 header for a plugin or master file.
 */
public class PluginHeader extends SerializedElement {
    
    /** Plugin file */
    private File pluginFile;
    
    /** Plugin version */
    private float pluginVersion;
    
    /** Plugin is a master file */
    private boolean master;
    
    /** Plugin creator */
    private String creator;
    
    /** Plugin summary */
    private String summary;
    
    /** Plugin record count */
    private int recordCount;
    
    /** Plugin masters */
    private List<String> masterList;
    
    /**
     * Create a new plugin header
     *
     * @param       pluginFile      The plugin file
     */
    public PluginHeader(File pluginFile) {
        this.pluginFile = pluginFile;
        pluginVersion = 0.8f;
        master = false;
        creator = "DEFAULT";
        summary = new String();
        masterList = new ArrayList<String>();
    }
    
    /**
     * Return the plugin version.  The original TES4 version is 0.8 while the patched
     * TES4 version is 1.0.
     *
     * @return                      The plugin version
     */
    public float getVersion() {
        return pluginVersion;
    }
    
    /**
     * Set the plugin version.  The original TES4 version is 0.8 while the patched
     * TES4 version is 1.0.
     *
     * @param       version         The plugin version
     */
    public void setVersion(float version) {
        pluginVersion = version;
    }
    
    /**
     * Check if the plugin is a master file
     *
     * @return                      TRUE if the plugin is a master file
     */
    public boolean isMaster() {
        return master;
    }
    
    /**
     * Set the master status
     *
     * @param       master          TRUE if the plugin is a master file
     */
    public void setMaster(boolean master) {
        this.master = master;
    }
    
    /**
     * Return the plugin creator
     *
     * @return                      The plugin creator
     */
    public String getCreator() {
        return creator;
    }
    
    /**
     * Set the plugin creator
     *
     * @param       creator         The plugin creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    /**
     * Return the plugin summary
     *
     * @return                      The plugin summary
     */
    public String getSummary() {
        return summary;
    }
    
    /**
     * Set the plugin summary
     *
     * @param       summary         The plugin summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    /**
     * Return the plugin record count
     *
     * @return                      The record count
     */
    public int getRecordCount() {
        return recordCount;
    }
    
    /**
     * Set the plugin record count
     *
     * @param       recordCount     The plugin record count
     */
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    
    /**
     * Return the plugin master list
     *
     * @return                      The master list
     */
    public List<String> getMasterList() {
        return masterList;
    }
    
    /**
     * Set the plugin master list
     *
     * @param       masterList      The master list
     */
    public void setMasterList(List<String> masterList) {
        this.masterList = masterList;
    }
    
    /**
     * Read the plugin header
     *
     * @param       in                  Input file
     * @exception   IOException         Error while reading the plugin file
     * @exception   PluginException     The header is not valid
     */
    public void read(RandomAccessFile in) throws PluginException, IOException {
        byte[] prefix = new byte[20];
        byte[] buffer = new byte[1024];
        int count, length, headerLength;
        String type;
        
        //
        // Read the header prefix
        //
        // Bytes 00-03: 'TES4'
        // Bytes 04-07: Data length
        // Bytes 08:    00 = Plugin, 01 = Master
        // Bytes 09-19: Unused
        //
        count = in.read(prefix, 0, 20);
        if (count != 20)
            throw new PluginException(pluginFile.getName()+": File is not a TES4 file");

        type = new String(prefix, 0, 4);
        if (!type.equals("TES4"))
            throw new PluginException(pluginFile.getName()+": File is not a TES4 file");

        if ((prefix[8]&0x01) != 0)
            master = true;
        else
            master = false;
        
        headerLength = getInteger(prefix, 4);
        
        //
        // Process the header subrecords
        //
        // HEDR subrecord
        //   Bytes 00-03: 'HEDR'
        //   Bytes 04-05: Data length
        //   Bytes 06-09: Plugin version (single-precision floating-point number)
        //   Bytes 10-13: Plugin record count (excluding TES4 record).  Group records are
        //                included but subrecords are not counted.
        //   Bytes 14-17: <unknown>
        //
        // CNAM subrecord
        //   Bytes 00-03: 'CNAM'
        //   Bytes 04-05: Data length
        //   Bytes 06-nn: Null-terminated plugin creator
        //
        // SNAM subrecord
        //   Bytes 00-03: 'SNAM'
        //   Bytes 04-05: Data length
        //   Bytes 06-nn: Null-terminated plugin summary
        //
        // MAST subrecord
        //   Bytes 00-03: 'MAST'
        //   Bytes 04-05: Data length
        //   Bytes 06-nn: Null-terminated master file
        //
        // DATA subrecord
        //   Bytes 00-03: 'DATA'
        //   Bytes 04-05: Data length (8)
        //   Bytes 06-13: Zero
        //
        // The MAST/DATA subrecord pair repeats for each master file used by the plugin
        //
        while (headerLength >= 6) {
            count = in.read(prefix, 0, 6);
            if (count != 6)
                throw new PluginException(pluginFile.getName()+": Header subrecord prefix truncated");
            
            headerLength -= 6;
            length = getShort(prefix, 4);
            if (length > headerLength)
                throw new PluginException(pluginFile.getName()+": Subrecord length exceeds header length");
            
            if (length > buffer.length)
                buffer = new byte[length];
            
            count = in.read(buffer, 0, length);
            if (count != length) 
                throw new PluginException(pluginFile.getName()+": Header subrecord data truncated");
            
            headerLength -= count;
            type = new String(prefix, 0, 4);
            if (type.equals("HEDR")) {
                if (length < 8)
                    throw new PluginException(pluginFile.getName()+": HEDR subrecord is too small");
        
                int pluginIntVersion = getInteger(buffer, 0);
                pluginVersion = Float.intBitsToFloat(pluginIntVersion);
                if (Main.debugMode)
                    System.out.printf("%s: Version %f\n", pluginFile.getName(), pluginVersion);
                
                recordCount = getInteger(buffer, 4);
                if (Main.debugMode)
                    System.out.printf("%s: %d records\n", pluginFile.getName(), recordCount);
            } else if (type.equals("CNAM")) {
                if (length > 1)
                    creator = new String(buffer, 0, length-1);
            } else if (type.equals("SNAM")) {
                if (length > 1)
                    summary = new String(buffer, 0, length-1);
            } else if (type.equals("MAST")) {
                if (length > 1)
                    masterList.add(new String(buffer, 0, length-1));
            }
        }

        if (headerLength != 0)
            throw new PluginException(pluginFile.getName()+": Header is incomplete");
    }
    
    /**
     * Write the plugin header using an output stream
     *
     * @param       out             The output file stream
     * @exception   IOException     An I/O error occurred
     */
    public void write(FileOutputStream out) throws IOException {
        byte[] headerRecord = buildHeader();
        out.write(headerRecord);
    }
    
    /**
     * Write the plugin header using a random access output file
     *
     * @param       out             The random access output file
     * @exception   IOException     An I/O error occurred
     */
    public void write(RandomAccessFile out) throws IOException {
        byte[] headerRecord = buildHeader();
        out.write(headerRecord);
    }
    
    /**
     * Build the header
     *
     * @return                      The header buffer
     */
    private byte[] buildHeader() {
        int length, count, offset;
        
        //
        // Build the HEDR subrecord
        //
        int pluginIntVersion = Float.floatToIntBits(pluginVersion);
        byte[] hedrSubrecord = new byte[18];
        System.arraycopy("HEDR".getBytes(), 0,  hedrSubrecord, 0, 4);
        setShort(12, hedrSubrecord, 4);
        setInteger(pluginIntVersion, hedrSubrecord, 6);
        setInteger(recordCount, hedrSubrecord, 10);
        
        //
        // Build the CNAM subrecord
        //
        byte[] creatorBytes = creator.getBytes();
        length = creatorBytes.length+1;
        byte[] cnamSubrecord = new byte[6+length];
        System.arraycopy("CNAM".getBytes(), 0, cnamSubrecord, 0, 4);
        setShort(length, cnamSubrecord, 4);
        if (length > 1)
            System.arraycopy(creatorBytes, 0, cnamSubrecord, 6,  creatorBytes.length);
        cnamSubrecord[6+creatorBytes.length] = (byte)0;
        
        //
        // Build the SNAM subrecord (this is an optional subrecord)
        //
        byte[] snamSubrecord;
        byte[] summaryBytes = summary.getBytes();
        length = summaryBytes.length+1;
        if (length > 1) {
            snamSubrecord = new byte[6+length];
            System.arraycopy("SNAM".getBytes(), 0, snamSubrecord, 0, 4);
            setShort(length, snamSubrecord, 4);
            System.arraycopy(summaryBytes, 0, snamSubrecord, 6,  summaryBytes.length);
            snamSubrecord[6+summaryBytes.length] = (byte)0;
        } else {
            snamSubrecord = new byte[0];
        }
        
        //
        // Build the MAST/DATA subrecords (these are optional subrecords)
        //
        byte[][] masterSubrecords = new byte[masterList.size()][];
        count = 0;
        for (String master : masterList) {
            byte[] masterBytes = master.getBytes();
            length = masterBytes.length+1;
            byte[] masterSubrecord = new byte[6+length];
            System.arraycopy("MAST".getBytes(), 0, masterSubrecord, 0, 4);
            setShort(length, masterSubrecord, 4);
            if (length > 1)
                System.arraycopy(masterBytes, 0, masterSubrecord, 6, masterBytes.length);
            masterSubrecord[6+masterBytes.length] = (byte)0;
            masterSubrecords[count++] = masterSubrecord;
        }
        
        //
        // Build the header record
        //
        length = hedrSubrecord.length+cnamSubrecord.length+snamSubrecord.length;
        for (int i=0; i<masterSubrecords.length; i++)
            length += masterSubrecords[i].length+14;
        
        byte[] headerRecord = new byte[20+length];
        System.arraycopy("TES4".getBytes(), 0, headerRecord, 0, 4);
        setInteger(length, headerRecord, 4);
        headerRecord[8] = (master ? (byte)1 : (byte)0);
        offset = 20;
        System.arraycopy(hedrSubrecord, 0, headerRecord, offset, hedrSubrecord.length);
        offset += hedrSubrecord.length;
        System.arraycopy(cnamSubrecord, 0, headerRecord, offset, cnamSubrecord.length);
        offset += cnamSubrecord.length;
        
        if (snamSubrecord.length != 0) {
            System.arraycopy(snamSubrecord, 0, headerRecord, offset, snamSubrecord.length);
            offset += snamSubrecord.length;
        }
        
        for (int i=0; i<masterSubrecords.length; i++) {
            System.arraycopy(masterSubrecords[i], 0, headerRecord, offset, masterSubrecords[i].length);
            offset += masterSubrecords[i].length;
            System.arraycopy("DATA".getBytes(), 0,  headerRecord, offset, 4);
            headerRecord[offset+4] = 8;
            offset += 14;
        }
        
        return headerRecord;
    }
}

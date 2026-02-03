package TES4Gecko;

import java.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.zip.*;

/**
 * The Master class represents the contents of a master file.  Since master files can be
 * very large (Oblivion.esm is 242MB), the master file is not read into memory when it
 * is loaded.  Instead, an index file is created/read to get the record offsets within
 * the master file.  Individual records are then read from the master file as needed.
 *
 * Important note: CELL and DIAL records are not included in the index file.  In addition,
 * only the top-level groups are mapped.  This was done to reduce the size of the index
 * file and the amount of storage required to load a master file.  If all of the master
 * records are needed, the master file must be loaded as a plugin and not as a master.
 */
public class Master extends SerializedElement {
    
    /** Index file version */
    private static final int INDEX_VERSION = 5;
    
    /** Master file */
    private File masterFile;
    
    /** Master header */
    private PluginHeader masterHeader;
    
    /** Form information */
    private List<FormInfo> formList;
    
    /** Form ID map */
    private Map<Integer, FormInfo> formMap;

    /**
     * Create a new master
     *
     * @param       masterFile      The master file
     */
    public Master(File masterFile) {
        this.masterFile = masterFile;
        masterHeader = new PluginHeader(masterFile);
    }
    
    /**
     * Return the master name
     *
     * @return                      The master name
     */
    public String getName() {
        return masterFile.getName();
    }
    
    /**
     * Return the master version
     *
     * @return                      The master version
     */
    public float getVersion() {
        return masterHeader.getVersion();
    }
    
    /**
     * Set the master version
     *
     * @param       version         The master version
     */
    public void setVersion(float version) {
        masterHeader.setVersion(version);
    }
    
    /**
     * Return the master creator
     *
     * @return                      The master creator
     */
    public String getCreator() {
        return masterHeader.getCreator();
    }
    
    /**
     * Return the master summary
     *
     * @return                      The master summary
     */
    public String getSummary() {
        return masterHeader.getSummary();
    }
    
    /**
     * Return the master record count
     *
     * @return                      The record count
     */
    public int getRecordCount() {
        return masterHeader.getRecordCount();
    }
    
    /**
     * Return the master list
     *
     * @return                      The master list
     */
    public List<String> getMasterList() {
        return masterHeader.getMasterList();
    }
    
    /**
     * Return the form list
     *
     * @return                      The form list
     */
    public List<FormInfo> getFormList() {
        return formList;
    }
    
    /**
     * Return the form map
     *
     * @return                      The form map
     */
    public Map<Integer, FormInfo> getFormMap() {
        return formMap;
    }
    
    /**
     * Return a string describing the master
     *
     * @return                      The master file name
     */
    public String toString() {
        return masterFile.getName();
    }
    
    /**
     * Return the record associated with the specified Form ID.  Only the low-order 3 bytes
     * of the Form ID are significant.
     * UPDATE: This method seems to assume that all records in it are "new" - that is, not changes
     * to master entries. That makes no sense to me, so I'm changing that. This note is to remind
     * in case this change itself breaks something. Others uses are in merging and worldspace shifting. SAC
     *
     * @param       formID                  The Form ID of the record
     * @return                              The master record
     * @exception   DataFormatException     Decompression error occurred
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The master is not valid
     */
    public PluginRecord getRecord(int formID) throws DataFormatException, IOException, PluginException {
        PluginRecord record = null;
        RandomAccessFile in = null;
        // SAC: Form ID now used as is without changing the master index.
        // int masterFormID = (formID&0x00ffffff) | (masterHeader.getMasterList().size()<<24);
        int masterFormID = formID;
        FormInfo formInfo = formMap.get(new Integer(masterFormID));
        if (formInfo == null)
            throw new PluginException(String.format("%s: Record %08X not found",  masterFile.getName(), masterFormID));
        
        byte[] prefix = new byte[20];
        long fileOffset = ((Long)formInfo.getSource()).longValue();
        try {
            in = new RandomAccessFile(masterFile, "r");
            in.seek(fileOffset);
            int count = in.read(prefix);
            if (count != 20)
                throw new PluginException(String.format("%s: Record %08X truncated", masterFile.getName(), masterFormID));

            int recordLength = getInteger(prefix, 4);
            record = new PluginRecord(prefix);
            record.load(masterFile, in, recordLength);
        } finally {
            if (in != null)
                in.close();
        }
        
        return record;
    }
    
    /**
     * Load a master file and build the Form ID and Editor ID database.  The record data is not loaded
     * and will be read from the master file as needed.  The FormInfo source will be set to the
     * master file offset for the record.
     *
     * To reduce the master load time, we will maintain an index file for each master file.  This
     * index file will be recreated if the master file changes.
     *
     * @param       task                    Worker task or null
     * @exception   DataFormatException     Decompression error occurred
     * @exception   InterruptedException    The worker thread was interrupted
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The master is not valid
     */
    public void load(WorkerTask task) throws PluginException, DataFormatException, IOException, InterruptedException {
        if (task != null) {
            StatusDialog statusDialog = task.getStatusDialog();
            if (statusDialog != null)
                statusDialog.updateMessage("Loading "+masterFile.getName());
        }
        
        //
        // The master file must exist
        //
        if (!masterFile.exists() || !masterFile.isFile())
            throw new IOException("Master file '"+masterFile.getName()+"' does not exist");
            
        //
        // Read the master file header
        //
        RandomAccessFile in = null;
        try {
            in = new RandomAccessFile(masterFile, "r");
            masterHeader.read(in);
        } finally {
            if (in != null)
                in.close();
        }
        
        //
        // Build the index file name
        //
        String masterName = masterFile.getName();
        String indexName;
        int sep = masterName.lastIndexOf('.');
        if (sep > 0)
            indexName = "Gecko-"+masterName.substring(0, sep)+".index";
        else
            indexName = "Gecko-"+masterName+".index";

        File indexFile = new File(masterFile.getParent()+Main.fileSeparator+indexName);
            
        //
        // Build a new index file if the index file does not exist.  Otherwise,
        // read the existing index file.
        //
        if (!indexFile.exists())
            buildIndexFile(task, indexFile);
        else
            readIndexFile(task, indexFile);
    }
    
    /**
     * Reset the form list to empty.
     *
     */
    public void resetFormList() {
    	// Null out values first.
    	for (int i = 0; i < formList.size(); i++)
    	{
    		formList.set(i, null);
    	}
        formList = new ArrayList<FormInfo>(1000);
    }
    
    /**
     * Reset the form map to empty
     *
     */
    public void resetFormMap() {
    	formMap.clear();
        formMap = new HashMap<Integer, FormInfo>(1000);
    }
    
    /**
     * Build the record information from the master index file.
     *
     * @param       task                    Worker task or null
     * @param       indexFile               The index file
     * @exception   DataFormatException     Decompression error occurred
     * @exception   InterruptedException    The worker thread was interrupted
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The index file is not valid
     */
    private void readIndexFile(WorkerTask task, File indexFile)
                        throws DataFormatException, InterruptedException, IOException, PluginException {
        FileInputStream in = null;
        GZIPInputStream inflater = null;
        int masterID = masterHeader.getMasterList().size();
        StatusDialog statusDialog = null;
        if (task != null)
            statusDialog = task.getStatusDialog();
        
        try {
            in = new FileInputStream(indexFile);
            byte[] buffer = new byte[4096];
            boolean rebuildIndex = true;
            
            //
            // Read the index file header (uncompressed)
            //
            // The header record has the following format:
            //
            //   Bytes 00-03: 'INDX'
            //   Bytes 04-07: Version number
            //   Bytes 08-11: Entry count
            //   Bytes 12-15: Master length
            //   Bytes 16-23: Master timestamp
            //
            int count = in.read(buffer, 0, 24);
            if (count == 24) {
                String recordType = new String(buffer, 0, 4);
                if (recordType.equals("INDX")) {
                    int version = getInteger(buffer, 4);
                    if (version == INDEX_VERSION) {
                        int length = getInteger(buffer, 12);
                        if (length == (int)masterFile.length()) {
                            long timestamp = getLong(buffer, 16);
                            if (timestamp == masterFile.lastModified())
                                rebuildIndex = false;
                        }
                    }
                }
            }
            
            if (rebuildIndex) {
                
                //
                // Create a new index file because the master file has changed
                //                
                in.close();
                in = null;
                buildIndexFile(task, indexFile);
                
            } else {
                
                //
                // The remainder of the index file is compressed
                //
                inflater = new GZIPInputStream(in);
                
                //
                // Create the form list and map
                //
                int recordCount = getInteger(buffer, 8);
                formList = new ArrayList<FormInfo>(recordCount);
                formMap = new HashMap<Integer, FormInfo>(recordCount);
                int offset = 0;
                int residual = 0;
                int length;
                int processedCount = 0;
                int currentProgress = 0;

                while (true) {

                    //
                    // Read the next index entry
                    //
                    // Each record in the index file has the following format:
                    //
                    //   Bytes 00-03: File offset of the record prefix
                    //   Bytes 04-07: Form ID
                    //   Bytes 08-11: Record type
                    //   Bytes 12-15: Parent form ID
                    //   Bytes 16-n:  Null-terminated editor ID                            
                    //
                    while (residual < 17) {
                        if (residual > 0 && offset > 0)
                            System.arraycopy(buffer, offset, buffer, 0, residual);

                        offset = 0;
                        count = inflater.read(buffer, residual, buffer.length-residual);
                        if (count < 0) {
                            if (residual != 0)
                                throw new PluginException(indexFile.getName()+": Index file truncated");
                            
                            break;
                        }

                        residual += count;
                    }
                    
                    if (residual == 0)
                        break;

                    long position = (long)getInteger(buffer, offset);
                    int formID = getInteger(buffer, offset+4);
                    String recordType = new String(buffer, offset+8, 4);
                    int parentFormID = getInteger(buffer, offset+12);
                    offset += 16;
                    residual -= 16;
                    length = 0;

                    while (true) {
                        if (buffer[offset+length] == 0)
                            break;

                        length++;
                        if (length >= residual) {
                            System.arraycopy(buffer, offset, buffer, 0, residual);
                            offset = 0;
                            count = inflater.read(buffer, residual, buffer.length-residual);
                            if (count < 0)
                                throw new PluginException(indexFile.getName()+": Index file truncated");

                            residual += count;
                        }
                    }

                    String editorID = new String(buffer, offset, length);
                    offset += length+1;
                    residual -= length+1;

                    //
                    // Create the form information for the index entry
                    //
                    FormInfo info = new FormInfo(new Long(position), recordType, formID, editorID);
                    info.setParentFormID(parentFormID);
                    info.setPlugin(this);
                    formList.add(info);
                    
                    if ((formID>>>24) > masterID)
                        formID = (formID&0x00ffffff) | (masterID<<24);
                    
                    formMap.put(new Integer(formID), info);

                    //
                    // Stop now if the user has canceled the request
                    //
                    if (task != null && task.interrupted())
                        throw new InterruptedException("Request canceled");
                    
                    //
                    // Update the progress bar
                    //
                    processedCount++;
                    if (statusDialog != null) {
                        int newProgress = (processedCount*100)/recordCount;
                        if (newProgress >= currentProgress+5) {
                            currentProgress = newProgress;
                            statusDialog.updateProgress(currentProgress);
                        }
                    }
                }
            }
        } finally {
            if (inflater != null)
                inflater.close();
            else if (in != null)
                in.close();
        }
    }
    
    /**
     * Build the master index file
     *
     * @param       task                    Worker task or null
     * @param       indexFile               The index file
     * @exception   DataFormatException     Decompression error occurred
     * @exception   InterruptedException    The worker thread was interrupted
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The master is not valid
     */
    private void buildIndexFile(WorkerTask task, File indexFile) 
                                    throws DataFormatException, InterruptedException, IOException, PluginException {
        boolean completed = false;
        RandomAccessFile in = null;
        FileOutputStream out = null;
        GZIPOutputStream deflater = null;
        byte[] prefix = new byte[20];
        int masterID = masterHeader.getMasterList().size();
        StatusDialog statusDialog = null;
        if (task != null)
            statusDialog = task.getStatusDialog();
            
        //
        // Create the form list based on the header record count
        //
        int recordCount = masterHeader.getRecordCount();
        formList = new ArrayList<FormInfo>(recordCount);

        try {
            
            //
            // Open the input and output files
            //
            in = new RandomAccessFile(masterFile, "r");
            long fileSize = masterFile.length();
            int currentProgress = 0;
            
            if (indexFile.exists())
                indexFile.delete();
            
            out = new FileOutputStream(indexFile);
            
            //
            // Process each top-level group in the master file
            //
            while (true) {
                
                //
                // Read the group prefix
                //
                int count = in.read(prefix);
                if (count == -1)
                    break;
                
                if (count != 20)
                    throw new PluginException(masterFile.getName()+": Group record prefix is too short");
              
                //
                // Map the group records
                //
                buildGroup(in, prefix, formList);
                    
                //
                // Update the progress bar (0-50)
                //
                if (statusDialog != null) {
                    int newProgress = (int)((in.getFilePointer()*50L)/fileSize);
                    if (newProgress >= currentProgress+5) {
                        currentProgress = newProgress;
                        statusDialog.updateProgress(currentProgress);
                    }
                }

                //
                // Stop now if the user has canceled the request
                //
                if (task != null && task.interrupted())
                    throw new InterruptedException("Request canceled");
            }
            
            //
            // Build the Form ID mappings and create the index file
            //
            // The first record has the following format:
            //
            //   Bytes 00-03: 'INDX'
            //   Bytes 04-07: Version number
            //   Bytes 08-11: Entry count
            //   Bytes 12-15: Master length
            //   Bytes 16-23: Master timestamp
            //
            // Each record in the index file has the following format:
            //
            //   Bytes 00-03: File offset of the record prefix
            //   Bytes 04-07: Form ID
            //   Bytes 08-11: Record type
            //   Bytes 12-15: Parent form ID (currently always 0)
            //   Bytes 16-n:  Null-terminated editor ID
            //
            recordCount = formList.size();
            int processedCount = 0;
            formMap = new HashMap<Integer, FormInfo>(recordCount);
            byte[] buffer = new byte[256];
            
            //
            // Write out the index file header (uncompressed)
            //
            System.arraycopy("INDX".getBytes(), 0, buffer, 0, 4);
            setInteger(INDEX_VERSION, buffer, 4);
            setInteger(recordCount, buffer, 8);
            setInteger((int)masterFile.length(), buffer, 12);
            setLong(masterFile.lastModified(), buffer, 16);
            out.write(buffer, 0, 24);
            
            //
            // The remainder of the index file is compressed
            //
            deflater = new GZIPOutputStream(out);
            
            //
            // Write the compressed index entries
            //
            for (FormInfo info : formList) {
                
                //
                // Build the next index file entry
                //
                int formID = info.getFormID();
                byte[] recordType = info.getRecordType().getBytes();
                byte[] editorID = info.getEditorID().getBytes();
                int parentFormID = info.getParentFormID();
                int position = ((Long)info.getSource()).intValue();
                
                int length = 16+editorID.length+1;
                if (length > buffer.length)
                    buffer = new byte[length];
                
                setInteger(position, buffer, 0);
                setInteger(formID, buffer, 4);
                System.arraycopy(recordType, 0, buffer, 8, 4);
                setInteger(parentFormID, buffer, 12);
                if (editorID.length != 0)
                    System.arraycopy(editorID, 0, buffer, 16, editorID.length);
                buffer[16+editorID.length] = 0;
                deflater.write(buffer, 0, 16+editorID.length+1);
                
                if ((formID>>>24) > masterID)
                    formID = (formID&0x00ffffff) | (masterID<<24);
                
                formMap.put(new Integer(formID), info);
                
                //
                // Update the progress bar
                //
                processedCount++;
                if (statusDialog != null) {
                    int newProgress = (processedCount*50)/recordCount+50;
                    if (newProgress >= currentProgress+5) {
                        currentProgress = newProgress;
                        statusDialog.updateProgress(currentProgress);
                    }
                }
            }
            
            //
            // Index file has been built
            //
            completed = true;
        } finally {
            if (in != null)
                in.close();
            
            if (deflater != null)
                deflater.close();
            else if (out != null)
                out.close();
            
            if (!completed)
                indexFile.delete();
        }
    }
    
    /**
     * Build the index entries for a group
     *
     * @param       in                      Master file
     * @param       prefix                  The group prefix
     * @param       formList                The form list
     * @exception   DataFormatException     Decompression error occurred
     * @exception   InterruptedException    The worker thread was interrupted
     * @exception   IOException             An I/O error occurred
     * @exception   PluginException         The master is not valid
     */
    private void buildGroup(RandomAccessFile in, byte[] prefix, List<FormInfo> formList) 
                                throws DataFormatException, InterruptedException, IOException, PluginException {
        long position, stopPosition;
        int recordLength, groupLength;
        String recordType;
        int masterID = masterHeader.getMasterList().size();
        
        //
        // Bytes 00-03: 'GRUP'
        // Bytes 04-07: Record length including the 20-byte prefix
        // Bytes 08-11: Group label
        // Bytes 12-15: Group type
        // Bytes 16-19: Unknown
        //
        recordType = new String(prefix, 0, 4);
        groupLength = getInteger(prefix, 4);
        
        //
        // Skip the TES4 header
        //
        if (recordType.equals("TES4")) {
            in.skipBytes(groupLength);
            return;
        }
                
        if (!recordType.equals("GRUP"))
            throw new PluginException(masterFile.getName()+": Top-level record is not a group");
        
        groupLength -= 20;
        stopPosition = in.getFilePointer()+(long)groupLength;
        
        //
        // Process the records in the group
        //
        while ((position=in.getFilePointer()) < stopPosition) {

            //
            // Read the record prefix
            //
            int count = in.read(prefix);
            if (count != 20)
                throw new PluginException(masterFile.getName()+": Incomplete record prefix");

            recordType = new String(prefix, 0, 4);
            recordLength = getInteger(prefix, 4);

            //
            // Process the record and update the Form Info entry with the master file
            // offset.  Note that CELL and DIAL records are not included in the index
            // file and subgroups are not processed.
            //
            if (recordType.equals("GRUP")) {
                in.skipBytes(recordLength-20);
            } else if (recordType.equals("CELL") || recordType.equals("DIAL")) {
                in.skipBytes(recordLength);
            } else {
                PluginRecord record = new PluginRecord(prefix);
                int formID = record.getFormID();
                if (record.isDeleted() || record.isIgnored() || formID == 0) {
                    in.skipBytes(recordLength);
                } else {
                    record.load(masterFile, in, recordLength);
                    FormInfo formInfo = new FormInfo(new Long(position), recordType, formID, record.getEditorID());
                    formInfo.setPlugin(this);
                    formList.add(formInfo);
                }
            }
        }
    }
}

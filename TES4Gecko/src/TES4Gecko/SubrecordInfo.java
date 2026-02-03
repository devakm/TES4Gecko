package TES4Gecko;

/**
 * The SubrecordInfo contains information about a reference subrecord
 */
public class SubrecordInfo {
    
    /** Subrecord type */
    private String subrecordType;
    
    /** Allowed record types */
    private String[] recordTypes;
    
    /** Reference offsets */
    private int[] referenceOffsets;
    
    /** Empty record type array */
    private static final String[] allRecordTypes = new String[0];
    
    /**
     * Create a new subrecord information instance
     *
     * @param       subrecordType       Subrecord type
     * @param       referenceOffsets    Array of reference offsets
     * @param       recordTypes         Variable number of allowed record types
     */
    public SubrecordInfo(String subrecordType, int[] referenceOffsets, String ... recordTypes) {
        this.subrecordType = subrecordType;
        this.referenceOffsets = referenceOffsets;
        this.recordTypes = new String[recordTypes.length];
        for (int i=0; i<recordTypes.length; i++)
            this.recordTypes[i] = recordTypes[i];
    }
    
    /**
     * Create a new subrecord information instance.  There are no restrictions on the
     * record types that contain the subrecord.
     *
     * @param       subrecordType       Subrecord type
     * @param       referenceOffsets    Array of reference offsets
     */
    public SubrecordInfo(String subrecordType, int[] referenceOffsets) {
        this.subrecordType = subrecordType;
        this.referenceOffsets = referenceOffsets;
        this.recordTypes = allRecordTypes;
    }
    
    /**
     * Return the subrecord type
     *
     * @return                      Subrecord type
     */
    public String getSubrecordType() {
        return subrecordType;
    }
    
    /**
     * Return the record types.  The returned array will be empty if there are
     * no restrictions on the record types that contain the subrecord.
     *
     * @return                      Array of record types
     */
    public String[] getRecordTypes() {
        return recordTypes;
    }
    
    /**
     * Return the reference offsets
     *
     * @return                      Reference offsets
     */
    public int[] getReferenceOffsets() {
        return referenceOffsets;
    }
}

package TES4Gecko;

/**
 * The FunctionInfo contains information about a script function
 */
public class FunctionInfo {
    
    /** Function name */
    private String functionName;
    
    /** Function code */
    private int functionCode;
    
    /** First parameter is a reference */
    private boolean firstReference;
    
    /** Second parameter is a reference */
    private boolean secondReference;
    
    /**
     * Create a function information instance
     *
     * @param       name            The function name
     * @param       code            The function code
     * @param       firstParam      TRUE if the first parameter is a reference
     * @param       secondParam     TRUE if the second parameter is a reference
     */
    public FunctionInfo(String name, int code, boolean firstParam, boolean secondParam) {
        functionName = name;
        functionCode = code;
        firstReference = firstParam;
        secondReference = secondParam;
    }
    
    /**
     * Return the function name
     *
     * @return                      The function name
     */
    public String getName() {
        return functionName;
    }
    
    /**
     * Return the function code
     *
     * @return                      The function code
     */
    public int getCode() {
        return functionCode;
    }
    
    /**
     * Determine if the first parameter is a reference
     *
     * @param                       TRUE if the first parameter is a reference
     */
    public boolean isFirstReference() {
        return firstReference;
    }
    
    /**
     * Determine if the second parameter is a reference
     *
     * @param                       TRUE if the second parameter is a reference
     */
    public boolean isSecondReference() {
        return secondReference;
    }    
}

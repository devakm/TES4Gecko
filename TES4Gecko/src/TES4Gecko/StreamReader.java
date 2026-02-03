package TES4Gecko;

import java.util.*;
import java.io.*;

/**
 * The StreamReader class is used to asynchronously read from a character input stream.
 */
public class StreamReader extends Thread {
    
    /** Input stream reader */
    private InputStreamReader reader;
    
    /** The result string */
    private StringWriter writer;
    
    /** The string buffer */
    private StringBuffer buffer;
    
    /** String buffer position */
    private int index = 0;
    
    /** 
     * Create a new instance of StreamReader 
     *
     * @param       inputStream     The input stream
     */
    public StreamReader(InputStream inputStream) {
        reader = new InputStreamReader(inputStream);
        writer = new StringWriter(1024);
    }
    
    /**
     * Read from the input stream until end-of-data is reached.  After all
     * of the data has been read, the input stream will be closed and the
     * input data will be available for processing.
     */
    public void run() {
        int c;
        
        try {
            while ((c=reader.read()) != -1)
                writer.write(c);

            reader.close();
            buffer = writer.getBuffer();
        } catch (IOException exc) {
            Main.logException("Unable to read from input stream", exc);
        }
    }
    
    /**
     * Return the input stream buffer.  End-of-data must have been reached
     * on the input stream before calling this method.
     *
     * @return                                      The string buffer
     * @exception   IllegalThreadStateException     Input stream is still open
     */
    public StringBuffer getBuffer() throws IllegalThreadStateException {
        if (buffer == null)
            throw new IllegalThreadStateException("Input stream is still open");
        
        return buffer;
    }
    
    /**
     * Return the next line from the input stream.  End-of-data must have been
     * reached on the input stream before calling this method.  The return value
     * will be null if all of the input data has been processed.
     *
     * @return                                      Tne next line or null
     * @exception   IllegalThreadStateException     Input stream is still open
     */
    public String getLine() throws IllegalThreadStateException {
        if (buffer == null)
            throw new IllegalThreadStateException("Input stream is still open");
        
        String line = null;
        int length = buffer.length();
        if (index < length) {
            int sep = buffer.indexOf(Main.lineSeparator, index);
            if (sep < 0) {
                line = buffer.substring(index);
                index = length;
            } else {
                line = buffer.substring(index, sep);
                index = sep+Main.lineSeparator.length();
            }
        }
        
        return line;
    }
}

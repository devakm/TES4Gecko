package TES4Gecko;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.*;
import java.awt.*;

/**
 * EditNumber is a formatter for a JFormattedTextField component.  Addition, 
 * subtraction, multiplication and division can be specified as part of the 
 * text string using using the '+', '-', '*' and '/' operators.  Operations 
 * are processed from left to right as the operators are encountered in the 
 * text string.  The formatted value will be an Integer if only integer values
 * are allowed.  Otherwise, the formatted value will be a Double even if there
 * are no decimal positions in the entered value.
 */
public final class EditNumber extends AbstractFormatter {

    /** Number formatter */
    private NumberFormat formatter;
    
    /** Allow only integer values */
    private boolean integerOnly;

    /**
     * Create a new number editor
     *
     * @param       integerOnly         TRUE if only integer values are allowed
     * @param       useGrouping         TRUE if grouping should be used
     */
    public EditNumber(boolean integerOnly, boolean useGrouping) {
        super();
        this.integerOnly = integerOnly;
        formatter = NumberFormat.getNumberInstance();
        formatter.setParseIntegerOnly(integerOnly);
        formatter.setGroupingUsed(useGrouping);
    }

    /**
     * Convert a string to a Number.  The return value will be an Integer
     * if only integer values are allowed.  Otherwise, the return value
     * will be a Double.
     *
     * @param       string          String to convert
     * @return                      Number object
     * @exception   ParseException  The string does not represent a valid number
     */
    public Object stringToValue(String string) throws ParseException {
        int length = string.length();
        Number value;

        //
        // Return zero for an empty string
        //
        if (length == 0) {
            setEditValid(false);
            if (integerOnly)
                value = new Integer(0);
            else
                value = new Double(0.0);
            
            return value;
        }

        //
        // Parse the string
        //
        ParsePosition pos = new ParsePosition(0);
        value = formatter.parse(string, pos);
        int index = pos.getIndex();

        //
        // Error if the string does not start with a valid number
        //
        if (value == null) {
            setEditValid(false);
            throw new ParseException("Unable to parse number", index);
        }

        //
        // We are done if the entire string has been processed
        //
        if (index == length) {
            setEditValid(true);
            if (integerOnly)
                value = new Integer(value.intValue());
            else if (!(value instanceof Double))
                value = new Double(value.doubleValue());

            return value;
        }

        //
        // Process arithmetic operations (+ - * /)
        //
        double number = value.doubleValue();
        int op = 0;
        while (index < length) {

            //
            // Get the next operation
            //
            char c = string.charAt(index);
            if (c == '+') {
                op = 0;
            } else if (c == '-') {
                op = 1;
            } else if (c == '*') {
                op = 2;
            } else if (c == '/') {
                op = 3;
            } else {
                setEditValid(false);
                throw new ParseException("Unrecognized operator", index);
            }

            //
            // Error if we have a trailing operator
            //
            if (++index == length) {
                setEditValid(false);
                throw new ParseException("Trailing operator", index);
            }

            //
            // Parse the next number
            //
            pos.setIndex(index);
            value = formatter.parse(string, pos);
            index = pos.getIndex();
            if (value == null) {
                setEditValid(false);
                throw new ParseException("Unable to parse number", index);
            }

            //
            // Perform the arithmetic operation
            //
            switch (op) {
                case 0:                         // Add
                    number += value.doubleValue();
                    break;

                case 1:                         // Subtract
                    number -= value.doubleValue();
                    break;

                case 2:                         // Multiply
                    number *= value.doubleValue();
                    break;

                case 3:                         // Divide
                    number /= value.doubleValue();
                    break;
            }
        }

        //
        // Return the final value
        //
        if (integerOnly)
            value = new Integer((int)number);
        else
            value = new Double(number);

        setEditValid(true);
        return value;
    }

    /**
     * Convert a Number value to a string
     *
     * @param       value           Number value to convert
     * @return                      Converted string
     * @exception   ParseException  Value is not a Number
     */
    public String valueToString(Object value) throws ParseException {
        if (value == null) {
            setEditValid(false);
            return new String();
        }

        if (!(value instanceof Number)) {
            setEditValid(false);
            throw new ParseException("Value is not a Number", 0);
        }

        setEditValid(true);
        return formatter.format(value);
    }
}

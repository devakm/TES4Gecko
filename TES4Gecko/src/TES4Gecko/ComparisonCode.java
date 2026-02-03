/**
 * 
 */
package TES4Gecko;

import java.util.HashMap;

/**
 * @author SACarrow
 * The ComparisonCode is just a wrapper for Oblivion CS comparison opcodes.
 */

public final class ComparisonCode
{
	public static final int EqualTo = 0;
	public static final int NotEqualTo = 2;
	public static final int GreaterThan = 4;
	public static final int GreaterThanOrEqualTo = 6;
	public static final int LessThan = 8;
	public static final int LessThanOrEqualTo = 10;
	
	public static boolean isValid(int param)
	{
		return (param == EqualTo || 
				param == NotEqualTo ||
				param == GreaterThan ||
				param == GreaterThanOrEqualTo ||
				param == LessThan ||
				param == LessThanOrEqualTo);
	}
	public static final HashMap<Integer, String> compCodeSymbolMap;
	static
	{
		compCodeSymbolMap = new HashMap<Integer, String>();
		compCodeSymbolMap.put(EqualTo, "==");
		compCodeSymbolMap.put(NotEqualTo, "!=");
		compCodeSymbolMap.put(GreaterThan, ">");
		compCodeSymbolMap.put(GreaterThanOrEqualTo, ">=");
		compCodeSymbolMap.put(LessThan, "<");
		compCodeSymbolMap.put(LessThanOrEqualTo, "<=");
	}

	public static String getCompCodeSymbol(int compCodeType)
	{
		if (!compCodeSymbolMap.containsKey(compCodeType)) return "Invalid comparison operator";
		else return compCodeSymbolMap.get(compCodeType); 
	}

}


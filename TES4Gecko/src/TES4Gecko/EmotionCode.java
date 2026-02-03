package TES4Gecko;

/**
 * @author SACarrow
 * The EmotionCode is just a wrapper for Oblivion CS voice emotion codes.
 */

public final class EmotionCode
{
	private EmotionCode() {}; // Not meant to be instantiated
	
	public static final int Invalid = -1;
	public static final int Neutral = 0;
	public static final int Anger = 1;
	public static final int Disgust = 2;
	public static final int Fear = 3;
	public static final int Sad = 4;
	public static final int Happy = 5;
	public static final int Surprise = 6;
	
	public static boolean isValid(int param)
	{
		return (param == Neutral || 
				param == Anger ||
				param == Disgust ||
				param == Fear ||
				param == Sad ||
				param == Happy ||
				param == Surprise);
	}
	
	public static String getString(int param)
	{
		switch (param)
		{
			case Neutral: return "Neutral"; 
			case Anger: return "Anger";
			case Disgust: return "Disgust";
			case Fear: return "Fear";
			case Sad: return "Sad";
			case Happy: return "Happy";
			case Surprise: return "Surprise";
		}
		return "Invalid";
	}
	public static int getCode(String param)
	{
		if (param.equalsIgnoreCase("Neutral")) return Neutral; 
		if (param.equalsIgnoreCase("Anger")) return Anger;
		if (param.equalsIgnoreCase("Disgust")) return Disgust;
		if (param.equalsIgnoreCase("Fear")) return Fear;
		if (param.equalsIgnoreCase("Sad")) return Sad;
		if (param.equalsIgnoreCase("Happy")) return Happy;
		if (param.equalsIgnoreCase("Surprise")) return Surprise;
		return Invalid;
	}
}

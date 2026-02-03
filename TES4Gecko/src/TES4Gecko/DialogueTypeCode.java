package TES4Gecko;

/**
 * @author SACarrow
 * The DialogueTypeCode is just a wrapper for Oblivion CS dialogue type codes,
 * or the tabs on the Dialogue window or the tabs without the word "Quest" on the 
 * Quest Window
 */

public final class DialogueTypeCode
{
	private DialogueTypeCode() {}; // Not meant to be instantiated
	
	public static final int Invalid = -1;
	public static final int Topic = 0;
	public static final int Conversation = 1;
	public static final int Combat = 2;
	public static final int Persuasion = 3;
	public static final int Detection = 4;
	public static final int Service = 5;
	public static final int Miscellaneous = 6;
	
	public static boolean isValid(int param)
	{
		return (param == Topic || 
				param == Conversation ||
				param == Combat ||
				param == Persuasion ||
				param == Detection ||
				param == Service ||
				param == Miscellaneous);
	}
	
	public static String getString(int param)
	{
		switch (param)
		{
			case Topic: return "Topic"; 
			case Conversation: return "Conversation";
			case Combat: return "Combat";
			case Persuasion: return "Persuasion";
			case Detection: return "Detection";
			case Service: return "Service";
			case Miscellaneous: return "Miscellaneous";
		}
		return "Invalid";
	}

	public static int getCode(String param)
	{
		if (param.equalsIgnoreCase("Topic")) return Topic; 
		if (param.equalsIgnoreCase("Conversation")) return Conversation;
		if (param.equalsIgnoreCase("Combat")) return Combat;
		if (param.equalsIgnoreCase("Persuasion")) return Persuasion;
		if (param.equalsIgnoreCase("Detection")) return Detection;
		if (param.equalsIgnoreCase("Service")) return Service;
		if (param.equalsIgnoreCase("Miscellaneous")) return Miscellaneous;
		return Invalid;
	}
}

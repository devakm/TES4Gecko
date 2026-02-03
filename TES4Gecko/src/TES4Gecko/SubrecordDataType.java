package TES4Gecko;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SACarrow
 * This code represents the various entities that are represented in
 * subrecord fields. If "other" is specified, a special display method is assumed
 */

public final class SubrecordDataType
{
	private SubrecordDataType() {};
	
	public static final int Invalid = -1;
	public static final int ByteArray = 0;
	public static final int String = 1;
	public static final int FormID = 2;
	public static final int Integer = 3;
	public static final int Float = 4;
	public static final int Short = 5;
	public static final int Byte = 6;
	public static final int StringNoNull = 7;
	public static final int XYCoordinates = 8;
	public static final int ContainerItem = 9;
	public static final int Condition = 10;
	public static final int Emotion = 11;
	public static final int PositionRotation = 12;
	public static final int SpellEffectName = 13;
	public static final int SpellEffectData = 14;
	public static final int FormIDArray = 15;
	public static final int LeveledItem = 16;
	public static final int CellLighting = 17;
	public static final int Flags = 18;
	public static final int StringArray = 19;
	public static final int ActorConfig = 20;
	public static final int FactionInfo = 21;
	public static final int AIInfo = 22;
	public static final int PGNodeArray = 23;
	public static final int PGConnsInt = 24;
	public static final int PGConnsExt = 25;
	public static final int LSTexture = 26;
	
	// Since the DATA subrecord is almost always present and almost never the same,
	// these constants will have their own range. DATA types that map to a previously
	// defined type do NOT go here.
	public static final int DATAforINFO = 100;
	public static final int DATAforCREA = 101;
	
	public static final int Other = 999;
	public static final int FormatVaries = 1000;
	
	public static boolean isValid(int param)
	{
		return (param == Invalid || 
				param == ByteArray ||
				param == String ||
				param == StringNoNull ||
				param == FormID ||
				param == Integer ||
				param == Float ||
				param == Short ||
				param == Byte ||
				param == XYCoordinates ||
				param == ContainerItem ||
				param == Condition ||
				param == Emotion ||
				param == LeveledItem ||
				param == PositionRotation ||
				param == SpellEffectName ||
				param == SpellEffectData ||
				param == FormIDArray ||
				param == CellLighting ||
				param == Flags ||
				param == StringArray ||
				param == ActorConfig ||
				param == FactionInfo ||
				param == AIInfo ||
				param == PGNodeArray ||
				param == PGConnsInt ||
				param == PGConnsExt ||
				param == LSTexture ||
				(param >= DATAforINFO && param <= DATAforCREA) ||
				param == FormatVaries ||
				param == Other);
	}
	private static Map<String, Integer> dataTypeMap;
	private static Map<Integer, String> dataTypeLabelMap;
	static
	{
		dataTypeMap = new HashMap<String, Integer>();
		// Form ID data
		dataTypeMap.put("CSCR", FormID);
		dataTypeMap.put("CSDI", FormID);
		dataTypeMap.put("ENAM", FormID);
		dataTypeMap.put("NAME", FormID);
		dataTypeMap.put("PFIG", FormID);
		dataTypeMap.put("PKID", FormID);
		dataTypeMap.put("PNAM", FormID);
		dataTypeMap.put("QNAM", FormID);
		dataTypeMap.put("QSTI", FormID);
		dataTypeMap.put("QSTR", FormID);
		dataTypeMap.put("SCRI", FormID);
		dataTypeMap.put("SCRO", FormID);
		dataTypeMap.put("SPLO", FormID);
		dataTypeMap.put("TCLT", FormID);
		dataTypeMap.put("XCCM", FormID);
		dataTypeMap.put("XCWT", FormID);
		dataTypeMap.put("XESP", FormID);
		dataTypeMap.put("XGLB", FormID);
		dataTypeMap.put("XHRS", FormID);
		dataTypeMap.put("XMRC", FormID);
		dataTypeMap.put("XOWN", FormID);
		dataTypeMap.put("ZNAM", FormID);
		
		// Float data
		dataTypeMap.put("FLTV", Float);
		dataTypeMap.put("XCLW", Float);
		dataTypeMap.put("XSCL", Float);
		
		// Integer data
		dataTypeMap.put("CSDT", Integer);
		dataTypeMap.put("XRNK", Integer);
		
		// Single-byte data
		dataTypeMap.put("LVLD", Byte);
		dataTypeMap.put("LVLF", Byte);
		dataTypeMap.put("XCMT", Byte);
		
		// String data
		dataTypeMap.put("DESC", String);
		dataTypeMap.put("EDID", String);
		dataTypeMap.put("FULL", String);
		dataTypeMap.put("GNAM", String);
		dataTypeMap.put("ICO2", String);
		dataTypeMap.put("ICON", String);
		dataTypeMap.put("MOD2", String);
		dataTypeMap.put("MOD3", String);
		dataTypeMap.put("MOD4", String);
		dataTypeMap.put("MODL", String);
		dataTypeMap.put("SCVR", String);
		
		// String data w/o trailing null
		dataTypeMap.put("EFID", StringNoNull);
		dataTypeMap.put("SCTX", StringNoNull);
		
		// String array data
		dataTypeMap.put("NIFZ", StringArray);
		dataTypeMap.put("KFFZ", StringArray);

		// X-Y Coordinate data
		dataTypeMap.put("XCLC", XYCoordinates);
		
		// Container item data
		dataTypeMap.put("CNTO", ContainerItem);
		
		// Leveled item data
		dataTypeMap.put("LVLO", LeveledItem);
		
		// Condition data
		dataTypeMap.put("CTDA", Condition);
		
		// Emotion data
		dataTypeMap.put("TRDT", Emotion);
		
		// Spell effect name
		dataTypeMap.put("EFID", SpellEffectName);
		
		// Spell effect data
		dataTypeMap.put("EFIT", SpellEffectData);
		
		// Array of form IDs
		dataTypeMap.put("XCLR", FormIDArray);
		dataTypeMap.put("VNAM", FormIDArray);
		
		// Cell lighting data
		dataTypeMap.put("XCLL", CellLighting);
		
		// Actor config data, either NPC or creature
		dataTypeMap.put("ACBS", ActorConfig);
		
		// AI Info
		dataTypeMap.put("AIDT", AIInfo);
		
		// Path grid node array
		dataTypeMap.put("PGRP", PGNodeArray);
		
		// Path grid connections (internal to cell)
		dataTypeMap.put("PGRR", PGConnsInt);
		
		// Path grid connections (external to cell)
		dataTypeMap.put("PGRI", PGConnsExt);
		
		// Landscape texture info.
		dataTypeMap.put("ATXT", LSTexture);
		dataTypeMap.put("BTXT", LSTexture);
		
		// Data format varies by record type. In this section are also those variants.
		// If a subrecord is usually of one type but is different for one or two record types,
		// append "-OTHER" for the 'usual' data type.
		dataTypeMap.put("BNAM", FormatVaries);
		dataTypeMap.put("BNAM-CREA", Float);
		dataTypeMap.put("BNAM-DOOR", FormID);
		dataTypeMap.put("CNAM", FormatVaries);
		dataTypeMap.put("CNAM-NPC_", FormID);
		dataTypeMap.put("CNAM-QUST", String);
		dataTypeMap.put("CNAM-WRLD", FormID);
		dataTypeMap.put("CNAM-WTHR", String);
		dataTypeMap.put("DATA", FormatVaries);
		dataTypeMap.put("DATA-ACHR", PositionRotation);
		dataTypeMap.put("DATA-ACRE", PositionRotation);
		dataTypeMap.put("DATA-ALCH", Float);
		dataTypeMap.put("DATA-ANIO", FormID);
		dataTypeMap.put("DATA-CELL", Flags);
		dataTypeMap.put("DATA-CREA", DATAforCREA);
		dataTypeMap.put("DATA-GMST", Float);
		dataTypeMap.put("DATA-INFO", DATAforINFO);
		dataTypeMap.put("DATA-PGRD", Short);
		dataTypeMap.put("DATA-REFR", PositionRotation);
		dataTypeMap.put("ENAM", FormatVaries);
		dataTypeMap.put("ENAM-RACE", FormIDArray);
		dataTypeMap.put("ENIT", FormatVaries);
		dataTypeMap.put("FNAM", FormatVaries);
		dataTypeMap.put("FNAM-GLOB", Byte);
		dataTypeMap.put("FNAM-LIGH", Float);
		dataTypeMap.put("HNAM", FormatVaries);
		dataTypeMap.put("HNAM-RACE", FormIDArray);
		dataTypeMap.put("INAM", FormatVaries);
		dataTypeMap.put("INAM-FACT", String);
		dataTypeMap.put("INAM-CREA", FormID);
		dataTypeMap.put("INAM-NPC_", FormID);
		dataTypeMap.put("MODB", FormatVaries);
		dataTypeMap.put("MODB-LIGH", Float);
		dataTypeMap.put("NAM0", FormatVaries);
		dataTypeMap.put("NAM0-CREA", String);
		dataTypeMap.put("NAM1", FormatVaries);
		dataTypeMap.put("NAM1-RACE", ByteArray); // Actually a marker of length 0.
		dataTypeMap.put("NAM1-OTHER", String);
		dataTypeMap.put("NAM2", FormatVaries);
		dataTypeMap.put("NAM2-INFO", String);
		dataTypeMap.put("NAM2-WRLD", FormID);
		dataTypeMap.put("RNAM", FormatVaries);
		dataTypeMap.put("RNAM-CREA", Byte);
		dataTypeMap.put("RNAM-NPC_", FormID);
		dataTypeMap.put("SNAM", FormatVaries);
		dataTypeMap.put("SNAM-ACTI", FormID);
		dataTypeMap.put("SNAM-CONT", FormID);
		dataTypeMap.put("SNAM-CREA", FactionInfo);
		dataTypeMap.put("SNAM-DOOR", FormID);
		dataTypeMap.put("SNAM-LIGH", FormID);
		dataTypeMap.put("SNAM-NPC_", FactionInfo);
		dataTypeMap.put("SNAM-WRLD", FormID);
		dataTypeMap.put("TNAM", FormatVaries);
		dataTypeMap.put("TNAM-CREA", Float);
		dataTypeMap.put("TNAM-DOOR", FormID);
		dataTypeMap.put("TNAM-LVLC", FormID);
		dataTypeMap.put("WNAM", FormatVaries);
		dataTypeMap.put("WNAM-CREA", Float);
		dataTypeMap.put("WNAM-OTHER", FormID);

	}
	static
	{
		dataTypeLabelMap = new HashMap<Integer, String>();
		dataTypeLabelMap.put(Invalid, "Unknown Type (Shown As Byte Array)");
		dataTypeLabelMap.put(ByteArray, "Byte Array");
		dataTypeLabelMap.put(String, "String");
		dataTypeLabelMap.put(StringNoNull, "String");
		dataTypeLabelMap.put(StringArray, "String Array");
		dataTypeLabelMap.put(FormID, "Form ID");
		dataTypeLabelMap.put(Integer, "Integer");
		dataTypeLabelMap.put(Float, "Float");
		dataTypeLabelMap.put(Short, "Short");
		dataTypeLabelMap.put(Byte, "Byte");
		dataTypeLabelMap.put(XYCoordinates, "X,Y Coordinates");
		dataTypeLabelMap.put(ContainerItem, "Container Item Info");
		dataTypeLabelMap.put(Condition, "Condition Info");
		dataTypeLabelMap.put(Emotion, "Emotion Info");
		dataTypeLabelMap.put(LeveledItem, "Leveled Item Info");
		dataTypeLabelMap.put(PositionRotation, "Position & Rotation Info");
		dataTypeLabelMap.put(SpellEffectName, "Spell Effect Name");
		dataTypeLabelMap.put(SpellEffectData, "Spell Effect Info");
		dataTypeLabelMap.put(FormIDArray, "Form ID Array");
		dataTypeLabelMap.put(CellLighting, "Cell Lighting Info");
		dataTypeLabelMap.put(Flags, "Binary Flags");
		dataTypeLabelMap.put(ActorConfig, "Actor Configuration");
		dataTypeLabelMap.put(FactionInfo, "Faction Info");
		dataTypeLabelMap.put(AIInfo, "AI Info");
		dataTypeLabelMap.put(PGNodeArray, "Path Grid Node Array");
		dataTypeLabelMap.put(PGConnsInt, "Path Grid Internal Connections");
		dataTypeLabelMap.put(PGConnsExt, "Path Grid External Connections");
		dataTypeLabelMap.put(LSTexture, "Landscape Texture Data");
		dataTypeLabelMap.put(DATAforINFO, "DATA for INFO Type Format");
		dataTypeLabelMap.put(DATAforCREA, "DATA for CREA Type Format");
		dataTypeLabelMap.put(FormatVaries, "Record-Dependent Format");
		dataTypeLabelMap.put(Other, "Specific");
	}
	public static int getDataType(String subrecType)
	{
		if (!dataTypeMap.containsKey(subrecType)) return Invalid;
		else return dataTypeMap.get(subrecType); 
	}

	public static String getDataTypeLabel(int subrecDataType)
	{
		if (!dataTypeLabelMap.containsKey(subrecDataType)) return "Invalid";
		else return dataTypeLabelMap.get(subrecDataType); 
	}

}

package TES4Gecko;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * The data for a plugin record consists of one or more plugin subrecords.
 */
public class PluginSubrecord extends SerializedElement {
    
    /** Record type */
    private String recordType;
    
    /** Subrecord type */
    private String subrecordType;
    
    /** Subrecord spill mode */
    private boolean spillMode = false;
    
    /** Subrecord position */
    private long subrecordPosition = -1;
    
    /** Subrecord data length */
    private int subrecordLength;
    
    /** Subrecord data */
    private byte[] subrecordData;
    
    /** Subrecord lookup table */
    private static Map<String, SubrecordInfo> typeMap;

    /** Function lookup table */
    private static Map<Integer, FunctionInfo> functionMap;

    /** 4-byte array of repeating references */
    private static final int[] offsetRepeating4 = {-4};

    /** 8-byte array of repeating references */
    private static final int[] offsetRepeating8 = {-8};

    /** 12-byte array of repeating references */
    private static final int[] offsetRepeating12 = {-12};

    /** 52-byte array of repeating references */
    private static final int[] offsetRepeating52 = {-52};

    /** Single reference at offset 0 */
    private static final int[] offsetZero = {0};

    /** Single reference at offset 4 */
    private static final int[] offsetFour = {4};

    /** Two references at offsets 0 and 4 */
    private static final int[] offsetZeroFour = {0, 4};

    /** Two references at offsets 12 and 16 */
    private static final int[] offsetTwelveSixteen = {12, 16};

    /** 
     * Subrecord types containing simple references
     */
    private static final SubrecordInfo[] subrecordInfo =
        {new SubrecordInfo("ANAM", offsetZero, "DOOR"),
         new SubrecordInfo("ATXT", offsetZero, "LAND"),
         new SubrecordInfo("BNAM", offsetZero, "DOOR"),
         new SubrecordInfo("BTXT", offsetZero, "LAND"), 
         new SubrecordInfo("CNAM", offsetZero, "NPC_", "WRLD"), 
         new SubrecordInfo("CNTO", offsetZero),
         new SubrecordInfo("CSCR", offsetZero, "CREA"), 
         new SubrecordInfo("CSDI", offsetZero, "CREA"),
         new SubrecordInfo("DATA", offsetRepeating4, "ANIO", "IDLE"),
         new SubrecordInfo("DNAM", offsetZeroFour, "RACE"), 
         new SubrecordInfo("VNAM", offsetZeroFour, "RACE"), 
         new SubrecordInfo("ENAM", offsetRepeating4, "AMMO", "ARMO", "BOOK", "CLOT", "NPC_", "RACE", "WEAP"),
         new SubrecordInfo("GNAM", offsetZero, "LTEX"), 
         new SubrecordInfo("HNAM", offsetRepeating4, "NPC_", "RACE"),
         new SubrecordInfo("INAM", offsetZero, "CREA", "NPC_"),
         new SubrecordInfo("LNAM", offsetZero, "LSCR"),
         new SubrecordInfo("LVLO", offsetFour, "LVLC", "LVLI", "LVSP"),
         new SubrecordInfo("NAME", offsetZero),
         new SubrecordInfo("NAM2", offsetZero, "WRLD"), 
         new SubrecordInfo("PFIG", offsetZero, "FLOR"), 
         new SubrecordInfo("PKID", offsetZero),
         new SubrecordInfo("PNAM", offsetZero, "INFO"),
         new SubrecordInfo("PGRL", offsetZero, "PGRD"),
         new SubrecordInfo("QNAM", offsetZero, "CONT"), 
         new SubrecordInfo("QSTA", offsetZero, "QUST"), 
         new SubrecordInfo("QSTI", offsetZero), 
         new SubrecordInfo("RDSD", offsetRepeating12, "REGN"),
         new SubrecordInfo("RDOT", offsetRepeating52, "REGN"),
         new SubrecordInfo("RDWT", offsetRepeating8, "REGN"),
         new SubrecordInfo("RNAM", offsetZero, "NPC_"),
         new SubrecordInfo("SCIT", offsetZero, "ENCH", "INGR", "SPEL"),
         new SubrecordInfo("SCRI", offsetZero),
         new SubrecordInfo("SCRO", offsetZero),
         new SubrecordInfo("SNAM", offsetZero, "ACTI", "CONT", "CREA", "DOOR", "LIGH", "NPC_", "WATR", "WRLD", "WTHR"), 
         new SubrecordInfo("SPLO", offsetZero),
         new SubrecordInfo("TCLF", offsetZero, "INFO"), 
         new SubrecordInfo("TCLT", offsetZero, "INFO"), 
         new SubrecordInfo("TNAM", offsetZero, "DOOR", "LVLC"), 
         new SubrecordInfo("VNAM", offsetZeroFour, "RACE"), 
         new SubrecordInfo("WLST", offsetRepeating8, "CLMT"),
         new SubrecordInfo("WNAM", offsetZero, "REGN", "WRLD"), 
         new SubrecordInfo("XCCM", offsetZero, "CELL"),
         new SubrecordInfo("XCLR", offsetRepeating4, "CELL"),
         new SubrecordInfo("XCWT", offsetZero, "CELL"), 
         new SubrecordInfo("XESP", offsetZero), 
         new SubrecordInfo("XGLB", offsetZero), 
         new SubrecordInfo("XHRS", offsetZero, "ACHR"),
         new SubrecordInfo("XLOC", offsetFour, "REFR"),
         new SubrecordInfo("XMRC", offsetZero, "ACHR"), 
         new SubrecordInfo("XNAM", offsetZero, "FACT", "RACE"), 
         new SubrecordInfo("XOWN", offsetZero), 
         new SubrecordInfo("XPCI", offsetZero), 
         new SubrecordInfo("XRTM", offsetZero, "REFR"), 
         new SubrecordInfo("XTEL", offsetZero, "REFR"),
         new SubrecordInfo("ZNAM", offsetZero)
    };

    /** 
     * Subrecord data types (mapped string).
     */
    private static final String[][] subrecordDataTypes =
        {
    	
    };

    /**
     * Script functions containing references (or not)
     */
    private static final FunctionInfo[] functionInfo = {
    	new FunctionInfo("CanHaveFlames", 0x099, false, false),
    	new FunctionInfo("CanPayCrimeGold", 0x07f, false, false),
    	new FunctionInfo("GetActorValue", 0x00e, true, false),
    	new FunctionInfo("GetAlarmed", 0x03d, false, false),
    	new FunctionInfo("GetAmountSoldStolen", 0x0be, false, false),
    	new FunctionInfo("GetAngle", 0x008, true, false),
    	new FunctionInfo("GetArmorRating", 0x051, false, false),
    	new FunctionInfo("GetArmorRatingUpperBody", 0x112, false, false),
    	new FunctionInfo("GetAttacked", 0x03f, false, false),
    	new FunctionInfo("GetBarterGold", 0x108, false, false),
    	new FunctionInfo("GetBaseActorValue", 0x115, true, false),
    	new FunctionInfo("GetClassDefaultMatch", 0x0e5, false, false),
    	new FunctionInfo("GetClothingValue", 0x029, false, false),
    	new FunctionInfo("GetCrime", 0x07a, true, true),
    	new FunctionInfo("GetCrimeGold", 0x074, false, false),
    	new FunctionInfo("GetCurrentAIPackage", 0x06e, false, false),
    	new FunctionInfo("GetCurrentAIProcedure", 0x08f, false, false),
    	new FunctionInfo("GetCurrentTime", 0x012, false, false),
    	new FunctionInfo("GetCurrentWeatherPercent", 0x094, false, false),
    	new FunctionInfo("GetDayOfWeek", 0x0aa, false, false),
    	new FunctionInfo("GetDead", 0x02e, false, false),
    	new FunctionInfo("GetDeadCount", 0x054, true, false),
    	new FunctionInfo("GetDestroyed", 0x0cb, false, false),
    	new FunctionInfo("GetDetected", 0x02d, true, false),
    	new FunctionInfo("GetDetectionLevel", 0x0b4, true, false),
    	new FunctionInfo("GetDisabled", 0x023, false, false),
    	new FunctionInfo("GetDisease", 0x027, false, false),
    	new FunctionInfo("GetDisposition", 0x04c, true, false),
    	new FunctionInfo("GetDistance", 0x001, true, false),
    	new FunctionInfo("GetDoorDefaultOpen", 0x0d7, false, false),
    	new FunctionInfo("GetEquipped", 0x0b6, true, false),
    	new FunctionInfo("GetFactionRank", 0x049, true, false),
    	new FunctionInfo("GetFactionRankDifference", 0x03c, true, true),
    	new FunctionInfo("GetFatiguePercentage", 0x080, false, false),
    	new FunctionInfo("GetFriendHit", 0x120, true, false),
    	new FunctionInfo("GetFurnitureMarkerID", 0x0a0, false, false),
    	new FunctionInfo("GetGlobalValue", 0x04a, true, false),
    	new FunctionInfo("GetGold", 0x030, false, false),
    	new FunctionInfo("GetHeadingAngle", 0x063, true, false),
    	new FunctionInfo("GetIdleDoneOnce", 0x13e, false, false),
    	new FunctionInfo("GetIgnoreFriendlyHits", 0x152, false, false),
    	new FunctionInfo("GetInCell", 0x043, true, false),
    	new FunctionInfo("GetInCellParam", 0x0e6, true, true),
    	new FunctionInfo("GetInFaction", 0x047, true, false),
    	new FunctionInfo("GetInSameCell", 0x020, true, false),
    	new FunctionInfo("GetInWorldspace", 0x136, true, false),
    	new FunctionInfo("GetInvestmentGold", 0x131, false, false),
    	new FunctionInfo("GetIsAlerted", 0x05b, false, false),
    	new FunctionInfo("GetIsClass", 0x044, true, false),
    	new FunctionInfo("GetIsClassDefault", 0x0e4, true, false),
    	new FunctionInfo("GetIsCreature", 0x040, false, false),
    	new FunctionInfo("GetIsCurrentPackage", 0x0a1, true, false),
    	new FunctionInfo("GetIsCurrentWeather", 0x095, true, false),
    	new FunctionInfo("GetIsGhost", 0x0ed, false, false),
    	new FunctionInfo("GetIsID", 0x048, true, false),
    	new FunctionInfo("GetIsPlayableRace", 0x0fe, false, false),
    	new FunctionInfo("GetIsPlayerBirthsign", 0x0e0, true, false),
    	new FunctionInfo("GetIsRace", 0x045, true, false),
    	new FunctionInfo("GetIsReference", 0x088, true, false),
    	new FunctionInfo("GetIsSex", 0x046, true, false),
    	new FunctionInfo("GetIsUsedItem", 0x0f6, true, false),
    	new FunctionInfo("GetIsUsedItemType", 0x0f7, true, false),
    	new FunctionInfo("GetItemCount", 0x02f, true, false),
    	new FunctionInfo("GetKnockedState", 0x06b, false, false),
    	new FunctionInfo("GetLevel", 0x050, false, false),
    	new FunctionInfo("GetLineOfSight", 0x01b, true, false),
    	new FunctionInfo("GetLockLevel", 0x041, false, false),
    	new FunctionInfo("GetLocked", 0x005, false, false),
    	new FunctionInfo("GetNoRumors", 0x140, false, false),
    	new FunctionInfo("GetOffersServicesNow", 0x0ff, false, false),
    	new FunctionInfo("GetOpenState", 0x09d, false, false),
    	new FunctionInfo("GetPCExpelled", 0x0c1, true, false),
    	new FunctionInfo("GetPCFactionAttack", 0x0c7, true, false),
    	new FunctionInfo("GetPCFactionMurder", 0x0c3, true, false),
    	new FunctionInfo("GetPCFactionSteal", 0x0c5, true, false),
    	new FunctionInfo("GetPCFactionSubmitAuthority", 0x0c9, true, false),
    	new FunctionInfo("GetPCFame", 0x0f9, false, false),
    	new FunctionInfo("GetPCInFaction", 0x084, true, false),
    	new FunctionInfo("GetPCInfamy", 0x0fb, false, false),
    	new FunctionInfo("GetPCIsClass", 0x081, true, false),
    	new FunctionInfo("GetPCIsRace", 0x082, true, false),
    	new FunctionInfo("GetPCIsSex", 0x083, true, false),
    	new FunctionInfo("GetPCMiscStat", 0x138, true, false),
    	new FunctionInfo("GetPersuasionNumber", 0x0e1, false, false),
    	new FunctionInfo("GetPlayerControlsDisabled", 0x062, false, false),
    	new FunctionInfo("GetPlayerHasLastRiddenHorse", 0x16a, false, false),
    	new FunctionInfo("GetPlayerInSEWorld", 0x16d, false, false),
    	new FunctionInfo("GetPos", 0x006, true, false),
    	new FunctionInfo("GetQuestRunning", 0x038, true, false),
    	new FunctionInfo("GetQuestVariable", 0x04f, true, true),
    	new FunctionInfo("GetRandomPercent", 0x04d, false, false),
    	new FunctionInfo("GetRestrained", 0x0f4, false, false),
    	new FunctionInfo("GetScale", 0x018, false, false),
    	new FunctionInfo("GetScriptVariable", 0x035, true, true),
    	new FunctionInfo("GetSecondsPassed", 0x00c, false, false),
    	new FunctionInfo("GetShouldAttack", 0x042, true, false),
    	new FunctionInfo("GetSitting", 0x09f, false, false),
    	new FunctionInfo("GetSleeping", 0x031, false, false),
    	new FunctionInfo("GetStage", 0x03a, true, false),
    	new FunctionInfo("GetStageDone", 0x03b, true, true),
    	new FunctionInfo("GetStartingAngle", 0x00b, true, false),
    	new FunctionInfo("GetStartingPos", 0x00a, true, false),
    	new FunctionInfo("GetTalkedToPC", 0x032, false, false),
    	new FunctionInfo("GetTalkedToPCParam", 0x0ac, true, false),
    	new FunctionInfo("GetTimeDead", 0x169, false, false),
    	new FunctionInfo("GetTotalPersuasionNumber", 0x13b, false, false),
    	new FunctionInfo("GetTrespassWarningLevel", 0x090, false, false),
    	new FunctionInfo("GetUnconscious", 0x0f2, false, false),
    	new FunctionInfo("GetUsedItemActivate", 0x103, false, false),
    	new FunctionInfo("GetUsedItemLevel", 0x102, false, false),
    	new FunctionInfo("GetVampire", 0x028, false, false),
    	new FunctionInfo("GetWalkSpeed", 0x08e, false, false),
    	new FunctionInfo("GetWeaponAnimType", 0x06c, false, false),
    	new FunctionInfo("GetWeaponSkillType", 0x06d, false, false),
    	new FunctionInfo("GetWindSpeed", 0x093, false, false),
    	new FunctionInfo("HasFlames", 0x09a, false, false),
    	new FunctionInfo("HasMagicEffect", 0x0d6, true, false),
    	new FunctionInfo("HasVampireFed", 0x0e3, false, false),
    	new FunctionInfo("IsActor", 0x161, false, false),
    	new FunctionInfo("IsActorAVictim", 0x13a, false, false),
    	new FunctionInfo("IsActorEvil", 0x139, false, false),
    	new FunctionInfo("IsActorUsingATorch", 0x132, false, false),
    	new FunctionInfo("IsCellOwner", 0x118, true, true),
    	new FunctionInfo("IsCloudy", 0x10b, false, false),
    	new FunctionInfo("IsContinuingPackagePCNear", 0x096, false, false),
    	new FunctionInfo("IsCurrentFurnitureObj", 0x0a3, true, false),
    	new FunctionInfo("IsCurrentFurnitureRef", 0x0a2, true, false),
    	new FunctionInfo("IsEssential", 0x162, false, false),
    	new FunctionInfo("IsFacingUp", 0x06a, false, false),
    	new FunctionInfo("IsGuard", 0x07d, false, false),
    	new FunctionInfo("IsHorseStolen", 0x11a, false, false),
    	new FunctionInfo("IsIdlePlaying", 0x070, false, false),
    	new FunctionInfo("IsInCombat", 0x121, false, false),
    	new FunctionInfo("IsInDangerousWater", 0x14c, false, false),
    	new FunctionInfo("IsInInterior", 0x12c, false, false),
    	new FunctionInfo("IsInMyOwnedCell", 0x092, false, false),
    	new FunctionInfo("IsLeftUp", 0x11d, false, false),
    	new FunctionInfo("IsOwner", 0x116, true, false),
    	new FunctionInfo("IsPCAMurderer", 0x0b0, false, false),
    	new FunctionInfo("IsPCSleeping", 0x0af, false, false),
    	new FunctionInfo("IsPlayerInJail", 0x0ab, false, false),
    	new FunctionInfo("IsPlayerMovingIntoNewSpace", 0x166, false, false),
    	new FunctionInfo("IsPlayersLastRiddenHorse", 0x153, false, false),
    	new FunctionInfo("IsPleasant", 0x10a, false, false),
    	new FunctionInfo("IsRaining", 0x03e, false, false),
    	new FunctionInfo("IsRidingHorse", 0x147, false, false),
    	new FunctionInfo("IsRunning", 0x11f, false, false),
    	new FunctionInfo("IsShieldOut", 0x067, false, false),
    	new FunctionInfo("IsSneaking", 0x11e, false, false),
    	new FunctionInfo("IsSnowing", 0x04b, false, false),
    	new FunctionInfo("IsSpellTarget", 0x0df, true, false),
    	new FunctionInfo("IsSwimming", 0x0b9, false, false),
    	new FunctionInfo("IsTalking", 0x08d, false, false),
    	new FunctionInfo("IsTimePassing", 0x109, false, false),
    	new FunctionInfo("IsTorchOut", 0x066, false, false),
    	new FunctionInfo("IsTrespassing", 0x091, false, false),
    	new FunctionInfo("IsTurnArrest", 0x149, false, false),
    	new FunctionInfo("IsWaiting", 0x06f, false, false),
    	new FunctionInfo("IsWeaponOut", 0x065, false, false),
    	new FunctionInfo("IsXBox", 0x135, false, false),
    	new FunctionInfo("IsYielding", 0x068, false, false),
    	new FunctionInfo("MenuMode", 0x024, true, false),
    	new FunctionInfo("SameFaction", 0x02a, true, false),
    	new FunctionInfo("SameFactionAsPC", 0x085, false, false),
    	new FunctionInfo("SameRace", 0x02b, true, false),
    	new FunctionInfo("SameRaceAsPC", 0x086, false, false),
    	new FunctionInfo("SameSex", 0x02c, true, false),
    	new FunctionInfo("SameSexAsPC", 0x087, false, false),
    	new FunctionInfo("WhichServiceMenu", 0x143, false, false),
    };
    
    /**
     * Create a new subrecord
     *
     * @param       recordType          The record type
     * @param       subrecordType       The subrecord type
     * @param       subrecordData       The subrecord data
     */
    public PluginSubrecord(String recordType, String subrecordType, byte[] subrecordData) {
        
        //
        // Create the subrecord
        //
        this.recordType = recordType;
        this.subrecordType = subrecordType;
        this.subrecordData = subrecordData;
        
        //
        // Create the subrecord information
        //
        if (typeMap == null) {
            typeMap = new HashMap<String, SubrecordInfo>(subrecordInfo.length);
            for (SubrecordInfo info : subrecordInfo)
                typeMap.put(info.getSubrecordType(), info);            
        }
        
        //
        // Create the function information
        //
        if (functionMap == null) {
            functionMap = new HashMap<Integer, FunctionInfo>(functionInfo.length);
            for (FunctionInfo info : functionInfo)
                functionMap.put(new Integer(info.getCode()), info);            
        }
    }
    
    /**
     * Set the subrecord spill mode
     *
     * @param       mode            TRUE to write subrecord data to spill file
     * @exception   IOException     An I/O error occurred
     */
    public void setSpillMode(boolean mode) throws IOException {
        if (mode != spillMode) {
            if (spillMode) {
                subrecordData = Main.pluginSpill.read(subrecordPosition, subrecordLength);
                subrecordPosition = -1;
                subrecordLength = 0;
            } else if (subrecordData != null) {
                subrecordPosition = Main.pluginSpill.write(subrecordData);
                subrecordLength = subrecordData.length;
                subrecordData = null;
            } else {
                subrecordPosition = -1;
                subrecordLength = 0;
            }
            
            spillMode = mode;
        }
    }
    
    /**
     * Return the subrecord type
     *
     * @return                      The subrecord type
     */
    public String getSubrecordType() {
        return subrecordType;
    }
    
    /**
     * Return the subrecord data
     *
     * @return                      The subrecord data
     * @exception   IOException     An I/O error occurred
     */
    public byte[] getSubrecordData() throws IOException {
        if (spillMode)
            return Main.pluginSpill.read(subrecordPosition, subrecordLength);
        
        return subrecordData;
    }
    
    /**
     * Set the subrecord data.  The subrecord becomes the owner of the byte array.
     *
     * @param       subrecordData   The new subrecord data
     * @exception   IOException     An I/O error occurred
     */
    public void setSubrecordData(byte[] subrecordData) throws IOException {
        if (spillMode) {
            subrecordPosition = Main.pluginSpill.write(subrecordData);
            subrecordLength = subrecordData.length;
        } else {
            this.subrecordData = subrecordData;
        }
    }
    
    /**
     * Return the references for this subrecord.  The return array consists of the subrecord
     * data offset and the reference form ID.  The form ID will be zero if there is no
     * reference for a particular array position.
     *
     * @return                      Reference array or null if there are no references
     * @exception   IOException     An I/O error occurred
     */
    public int[][] getReferences() throws IOException {
        int[][] references = null;
        
        if (subrecordType.equals("CTDA")) {
            //
            // CTDA subrecord
            //   Byte 0:      Type
            //   Bytes 1-3:   Unknown
            //   Bytes 4-7:   Value (float)
            //   Bytes 8-11:  Function code
            //   Bytes 12-15: Parameter 1
            //   Bytes 16-19: Parameter 2
            //
            byte subrecordData[] = getSubrecordData();
            int functionCode = getInteger(subrecordData, 8);
            FunctionInfo functionInfo = functionMap.get(new Integer(functionCode));
            if (functionInfo != null) {
                references = new int[2][2];
                int index = 0;
            
                if (functionInfo.isFirstReference() && subrecordData.length >= 16) {
                    references[index][0] = 12;
                    references[index][1] = getInteger(subrecordData, 12);
                    index++;
                }
                        
                if (functionInfo.isSecondReference() && subrecordData.length >= 20) {
                    references[index][0] = 16;
                    references[index][1] = getInteger(subrecordData, 16);
                }
            }
        } else if (subrecordType.equals("DATA") && recordType.equals("MGEF")) {
            //
            // DATA subrecord for MGEF record
            //   Bytes 0-3:   Unknown
            //   Bytes 4-7:   Base cost (float)
            //   Bytes 8-11:  Unknown
            //   Bytes 12-15: Magic school
            //   Bytes 16-23: Unknown
            //   Bytes 24-27: Light
            //   Bytes 28-31: Enchantment factor (float)
            //   Bytes 32-35: Effect shader
            //   Bytes 36-39: Enchant effect
            //   Bytes 40-43: Casting sound
            //   Bytes 44-47: Bolt sound
            //   Bytes 48-51: Hit sound
            //   Bytes 52-55: Area sound
            //   Bytes 56-59: Unknown
            //   Bytes 60-63: Barter factor (float)
            //
            byte subrecordData[] = getSubrecordData();
            int[] mgefOffsets = {24, 32, 36, 40, 44, 48, 52};
            references = new int[mgefOffsets.length][2];
            for (int index=0; index<mgefOffsets.length; index++) {
                int refOffset = mgefOffsets[index];
                if (refOffset+4 > subrecordData.length)
                    break;
                
                references[index][0] = refOffset;
                references[index][1] = getInteger(subrecordData, refOffset);
            }
        } else if (subrecordType.equals("PLDT") && recordType.equals("PACK")) {
            //
            // PLDT subrecord for PACK record
            //   Bytes 0-3:   Type
            //                  0 = Near reference
            //                  1 = In cell
            //                  2 = Near current location
            //                  3 = Near editor location
            //                  4 = Object ID
            //                  5 = Object type
            //   Bytes 4-7:   Location
            //   Bytes 8-11:  Radius
            //
            byte subrecordData[] = getSubrecordData();
            int type = getInteger(subrecordData, 0);
            if (type == 0 || type == 1 || type == 4) {
                references = new int[1][2];
                references[0][0] = 4;
                references[0][1] = getInteger(subrecordData, 4);
            }
        } else if (subrecordType.equals("PTDT") && recordType.equals("PACK")) {
            //
            // PTDT subrecord for PACK record
            //   Bytes 0-3:   Type
            //                  0 = Specific reference
            //                  1 = Object ID
            //                  2 = Object type
            //   Bytes 4-7:   Target
            //   Bytes 8-11:  Count
            //
            byte subrecordData[] = getSubrecordData();
            int type = getInteger(subrecordData, 0);
            if (type == 0 || type == 1) {
                references = new int[1][2];
                references[0][0] = 4;
                references[0][1] = getInteger(subrecordData, 4);
            }
        } else {
            //
            // Locate the subrecord information based on the record and subrecord type
            //
            boolean returnReferences = false;
            SubrecordInfo subrecordInfo = typeMap.get(subrecordType);
            if (subrecordInfo != null) {
                String[] recordTypes = subrecordInfo.getRecordTypes();
                if (recordTypes.length == 0) {
                    returnReferences = true;
                } else {
                    for (int i=0; i<recordTypes.length; i++) {
                        if (recordType.equals(recordTypes[i])) {
                            returnReferences = true;
                            break;
                        }
                    }
                }
            }
            
            //
            // Return the subrecord references
            //
            if (returnReferences) {
                byte subrecordData[] = getSubrecordData();
                int[] refOffsets = subrecordInfo.getReferenceOffsets();
                int refOffset = 0;
                int refSize = 4;
                int i = -1;
                int index = 0;
                boolean repeating;
                
                if (refOffsets[0] < 0) {
                    repeating = true;
                    refSize = -refOffsets[0];
                    refOffset = -refSize;
                    references = new int[subrecordData.length/refSize][2];
                } else {
                    repeating = false;
                    references = new int[refOffsets.length][2];
                }

                while (true) {
                    if (repeating) {
                        refOffset += refSize;
                    } else {
                        i++;
                        if (i == refOffsets.length)
                            break;
                            
                        refOffset = refOffsets[i];
                    }
                        
                    if (refOffset+refSize > subrecordData.length)
                        break;
                    
                    references[index][0] = refOffset;
                    references[index][1] = getInteger(subrecordData, refOffset);
                    index++;
                }
            }
        }
        
        return references;
    }

    /**
     * Returns a FunctionInfo based on the code
     *
     * @param       funcCode            The function code
     * @return      FunctionInfo       null if invalid code
     */
    public static FunctionInfo getFunctionInfo(int funcCode)
    {
        //
        // Create the function information if not already present.
        //
        if (functionMap == null)
        {
            functionMap = new HashMap<Integer, FunctionInfo>(functionInfo.length);
            for (FunctionInfo info : functionInfo)
                functionMap.put(new Integer(info.getCode()), info);            
        }
        return functionMap.get(new Integer(funcCode));

    }

    public String getDisplayDataTypeLabel()
    {
    	int dataType = SubrecordDataType.getDataType(subrecordType);
    	if (dataType == SubrecordDataType.FormatVaries)
    	{
    		// Data type for this subrecord varies by record type; append
    		// "-" and record type and try again.
    		dataType = SubrecordDataType.getDataType(subrecordType + "-" + recordType);
        	if (dataType == SubrecordDataType.Invalid) // Not a specific type for that record/subrecord combo
        	{
        		dataType = SubrecordDataType.getDataType(subrecordType + "-" + "OTHER");
        	}    		
    	}
    	String retStr = SubrecordDataType.getDataTypeLabel(dataType);
    	if (dataType == SubrecordDataType.Other)
    	{
    		retStr = subrecordType + " " + retStr;
    	}
    	return retStr;
    }

    /**
     * Returns a string representing the displayed data based on subrecord
     * type. Default type is byte array
     *
     * @return      String       String for display
     */
    public String getDisplayData()
    {
    	byte[] subrecordData = null;
    	int dataType = SubrecordDataType.getDataType(subrecordType);
    	if (dataType == SubrecordDataType.FormatVaries)
    	{
    		// Data type for this subrecord varies by record type; append
    		// "-" and record type and try again.
    		dataType = SubrecordDataType.getDataType(subrecordType + "-" + recordType);
        	if (dataType == SubrecordDataType.Invalid) // Not a specific type for that record/subrecord combo
        	{
        		dataType = SubrecordDataType.getDataType(subrecordType + "-" + "OTHER");
        	}    		
    	}
    	String retStr = "";
        //
        // Get the subrecord data
        //
        try {
            subrecordData = getSubrecordData();
        } catch (IOException exc) {
            Main.logException("Exception while getting subrecord data", exc);
            subrecordData = new byte[0];
            dataType = SubrecordDataType.ByteArray;
        }
        switch (dataType)
        {
        case SubrecordDataType.FormID:
        	retStr = getDisplayDataFormID(subrecordData);
        	break;
        case SubrecordDataType.Float:
        	retStr = getDisplayDataFloat(subrecordData);
        	break;
        case SubrecordDataType.Integer:
        	retStr = getDisplayDataInteger(subrecordData);
        	break;
        case SubrecordDataType.Short:
        	retStr = getDisplayDataShort(subrecordData);
        	break;
        case SubrecordDataType.Byte:
        	retStr = getDisplayDataByte(subrecordData);
        	break;
        case SubrecordDataType.String:
        	retStr = getDisplayDataString(subrecordData);
        	break;
        case SubrecordDataType.StringNoNull:
        	retStr = getDisplayDataStringNoNull(subrecordData);
        	break;
        case SubrecordDataType.StringArray:
        	retStr = getDisplayDataStringArray(subrecordData);
        	break;
        case SubrecordDataType.XYCoordinates:
        	retStr = getDisplayDataXYCoordinates(subrecordData);
        	break;
        case SubrecordDataType.ContainerItem:
        	retStr = getDisplayDataContainerItem(subrecordData);
        	break;
        case SubrecordDataType.Condition:
        	retStr = getDisplayDataCondition(subrecordData);
        	break;
        case SubrecordDataType.Emotion:
        	retStr = getDisplayDataEmotion(subrecordData);
        	break;
        case SubrecordDataType.LeveledItem:
        	retStr = getDisplayDataLeveledItem(subrecordData);
        	break;
        case SubrecordDataType.PositionRotation:
        	retStr = getDisplayDataPositionRotation(subrecordData);
        	break;
        case SubrecordDataType.SpellEffectName:
        	retStr = getDisplayDataSpellEffectName(subrecordData);
        	break;
        case SubrecordDataType.SpellEffectData:
        	retStr = getDisplayDataSpellEffectData(subrecordData);
        	break;
        case SubrecordDataType.FormIDArray:
        	retStr = getDisplayDataFormIDArray(subrecordData);
        	break;
        case SubrecordDataType.CellLighting:
        	retStr = getDisplayDataCellLightingInfo(subrecordData);
        	break;
        case SubrecordDataType.Flags:
        	retStr = getDisplayDataFlags(subrecordData);
        	break;
        case SubrecordDataType.ActorConfig:
        	retStr = getDisplayDataActorConfig(subrecordData);
        	break;
        case SubrecordDataType.FactionInfo:
        	retStr = getDisplayDataFactionInfo(subrecordData);
        	break;
        case SubrecordDataType.AIInfo:
        	retStr = getDisplayDataAIInfo(subrecordData);
        	break;
        case SubrecordDataType.PGNodeArray:
        	retStr = getDisplayDataPGNodeArray(subrecordData);
        	break;
        case SubrecordDataType.PGConnsInt:
        	retStr = getDisplayDataPGConnsInt(subrecordData);
        	break;
        case SubrecordDataType.PGConnsExt:
        	retStr = getDisplayDataPGConnsExt(subrecordData);
        	break;
        case SubrecordDataType.LSTexture:
        	retStr = getDisplayDataLSTexture(subrecordData);
        	break;
        case SubrecordDataType.DATAforINFO:
        	retStr = getDisplayDataDATAforINFO(subrecordData);
        	break;
        case SubrecordDataType.DATAforCREA:
        	retStr = getDisplayDataDATAforCREA(subrecordData);
        	break;
        case SubrecordDataType.ByteArray:
        default:
        	retStr = getDisplayDataByteArray(subrecordData);
        }
        return retStr;
    }
    
    /**
     * Returns a string representing the displayed data, but always as a byte array.
     *
     * @return      String       String for display
     */
    public String getDisplayDataAsBytes()
    {
    	byte[] subrecordData = null;
    	String retStr = "";
        //
        // Get the subrecord data
        //
        try {
            subrecordData = getSubrecordData();
        } catch (IOException exc) {
            Main.logException("Exception while getting subrecord data", exc);
            subrecordData = new byte[0];
        }
    	retStr = getDisplayDataByteArray(subrecordData);
        return retStr;
    }
    
    private String getDisplayDataFormID(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to formID in hex form.
        //
    	int formID = SerializedElement.getInteger(subrecordData, 0);
    	return String.format("%08X", formID);
    }
    
    private String getDisplayDataFloat(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to float.
        //
    	int floatBits = SerializedElement.getInteger(subrecordData, 0);
    	return String.format("%.3f", Float.intBitsToFloat(floatBits));
    }
    
    private String getDisplayDataInteger(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to integer.
        //
    	int intBits = SerializedElement.getInteger(subrecordData, 0);
    	return String.format("%d", intBits);
    }
    
    private String getDisplayDataShort(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to short.
        //
    	int intBits = SerializedElement.getShort(subrecordData, 0);
    	return String.format("%d", intBits);
    }
    
    private String getDisplayDataByte(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to byte in hex form.
        //
    	byte firstByte = subrecordData[0];
    	return String.format("%d", firstByte);
    }
      
    private String getDisplayDataString(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to string.
        //
    	return new String(subrecordData, 0, subrecordData.length-1);
    }
    
    private String getDisplayDataStringNoNull(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to string without null.
    	// Currently only found for script text.
        //
    	return new String(subrecordData, 0, subrecordData.length);
    }
    
    private String getDisplayDataStringArray(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to a string array. Each individual string
    	// ends with a null and the entire subrecord ends with an additional null
        //
    	String firstStr =  new String(subrecordData, 0, subrecordData.length-2);
    	String retstr = firstStr.replace('\0', '\n');
    	return retstr;
    }
    
    private String getDisplayDataByteArray(byte[] subrecordData)
    {
        
        //
        // Convert the subrecord data to hexadecimal
        //
        StringBuilder dumpData = new StringBuilder(128+3*subrecordData.length+6*(subrecordData.length/16));
        dumpData.append(String.format("%s subrecord: Data length x'%X'\n", 
                                      getSubrecordType(), subrecordData.length));
        dumpData.append("\n       0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F\n");
        StringBuilder dumpHex = new StringBuilder(48);
        StringBuilder dumpLine = new StringBuilder(16);
        
        for (int i=0; i<subrecordData.length; i+=16) {
            for (int j=0; j<16; j++) {
                int offset = i+j;
                if (offset == subrecordData.length)
                    break;
                
                dumpHex.append(String.format(" %02X",  subrecordData[offset]));
                if (subrecordData[offset] >= 0x20 && subrecordData[offset] < 0x7f)
                    dumpLine.append(new String(subrecordData, offset, 1));
                else
                    dumpLine.append(".");
            }
            
            while (dumpHex.length() < 48)
                dumpHex.append("   ");
            
            while (dumpLine.length() < 16)
                dumpLine.append(" ");
            
            dumpData.append(String.format("%04X:", i));
            dumpData.append(dumpHex);
            dumpData.append("  *");
            dumpData.append(dumpLine);
            dumpData.append("*");
            if (i+16 < subrecordData.length)
                dumpData.append("\n");
            
            dumpHex.delete(0, 48);
            dumpLine.delete(0,16);
        }
        return dumpData.toString();
    }

    private String getDisplayDataXYCoordinates(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to X-Y coordinates.
        //
		int x = SerializedElement.getInteger(subrecordData, 0);
		int y = SerializedElement.getInteger(subrecordData, 4);
		String retStr = x + ", " + y;
    	return retStr;
    }

    private String getDisplayDataContainerItem(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to container item info: formID & count.
        //
    	int itemFormID = SerializedElement.getInteger(subrecordData, 0);
    	int itemCount = SerializedElement.getInteger(subrecordData, 4);
    	return String.format("Item form ID: %08X\nItem count: %d", itemFormID, itemCount);
    }
    
    private String getDisplayDataLeveledItem(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to leveled item info: level, formID & count.
    	// NOTE: I read on UESP that LVLO may very rarely have 8 bytes, so we check for
    	// length and if 8, assume the two unknown fields are not there.
        //
    	int formIDPos = (subrecordData.length == 12) ? 4 : 2;
    	int countPos = (subrecordData.length == 12) ? 8 : 6;
    	int itemLevel = SerializedElement.getShort(subrecordData, 0);
    	int itemFormID = SerializedElement.getInteger(subrecordData, formIDPos);
    	int itemCount = SerializedElement.getShort(subrecordData, countPos);
    	return String.format("Item level: %d\nItem form ID: %08X\nItem count: %d", itemLevel, itemFormID, itemCount);
    }
    
    private String getDisplayDataCondition(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to condition info.
        //
        //
        // CTDA subrecord
        //   Byte 0:      Type
        //   Bytes 1-3:   Unknown
        //   Bytes 4-7:   Value (float)
        //   Bytes 8-11:  Function code
        //   Bytes 12-15: Parameter 1
        //   Bytes 16-19: Parameter 2
        //   BTW this is NOT the format listed in the UESP Wiki:
		//   http://www.uesp.net/wiki/Tes4Mod:Mod_File_Format/INFO

        int subFuncCode = SerializedElement.getInteger(subrecordData, 8);
    	FunctionInfo funcInfo = PluginSubrecord.getFunctionInfo(subFuncCode);
    	boolean usesFirst = false;
    	boolean usesSecond = false;
    	if (funcInfo != null)
    	{
        	usesFirst = funcInfo.isFirstReference();
        	usesSecond = funcInfo.isSecondReference();    		
    	}

		int subCompFlags = (subrecordData[0] & 0x0F); // Lower half-byte contains the flags 
    	int subCompCode = (subrecordData[0] & 0xF0) >>> 4; // Top half-byte contains the comparison operator 
        int subCompValueInt = SerializedElement.getInteger(subrecordData, 4);
        float subCompValue = Float.intBitsToFloat(subCompValueInt);
        int param1 = 0, param2 = 0;
        if (usesFirst) param1 = SerializedElement.getInteger(subrecordData, 12);
        if (usesSecond) param1 = SerializedElement.getInteger(subrecordData, 16);
        String paramList = "()";
        if (usesFirst) paramList = String.format("(%08X)", param1);
        if (usesSecond) paramList = String.format("(%08X, %08X)", param1, param2);
        String retStr = FunctionCode.getFuncCodeName(subFuncCode) + paramList 
        + " " + ComparisonCode.getCompCodeSymbol(subCompCode) + " " + subCompValue;
        if ((subCompFlags & 0x01) != 0)
        	retStr += "\n - Is ORed to next condition";
        if ((subCompFlags & 0x02) != 0)
        	retStr += "\n - Executes on target";
        if ((subCompFlags & 0x04) != 0)
        	retStr += "\n - Uses global variables";
        return retStr;
    }
    
    private String getDisplayDataEmotion(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to emotion info.
        //
        //
        // TRDT subrecord
        //   Bytes 0-3:   Emotion type
        //   Bytes 4-7:   Emotion value
        //   Byte 12:     Response number

		int emotionCode = SerializedElement.getInteger(subrecordData, 0);
		int emotionValue = SerializedElement.getInteger(subrecordData, 4);
		int responseNum = subrecordData[12];
        String retStr = "Type: " + EmotionCode.getString(emotionCode) 
                       + "\nValue: " + emotionValue + "\nResponse number: " + responseNum; 
        return retStr;
    }
    
    private String getDisplayDataPositionRotation(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to position & rotation info.
        //
        //
        // Format:
        //   Bytes 0-3:   X position
        //   Bytes 4-7:   Y position
        //   Bytes 8-11:  Z position
        //   Bytes 12-15: X rotation (radians; displayed in degrees)
        //   Bytes 16-19: Y rotation (radians; displayed in degrees)
        //   Bytes 20-23: Z rotation (radians; displayed in degrees)

		int XPosBits = SerializedElement.getInteger(subrecordData, 0);
		int YPosBits = SerializedElement.getInteger(subrecordData, 4);
		int ZPosBits = SerializedElement.getInteger(subrecordData, 8);
		int XRotBits = SerializedElement.getInteger(subrecordData, 12);
		int YRotBits = SerializedElement.getInteger(subrecordData, 16);
		int ZRotBits = SerializedElement.getInteger(subrecordData, 20);
		float XPos = Float.intBitsToFloat(XPosBits);
		float YPos = Float.intBitsToFloat(YPosBits);
		float ZPos = Float.intBitsToFloat(ZPosBits);
		float XRot = Float.intBitsToFloat(XRotBits) * 180 / (float)Math.PI;
		float YRot = Float.intBitsToFloat(YRotBits) * 180 / (float)Math.PI;
		float ZRot = Float.intBitsToFloat(ZRotBits) * 180 / (float)Math.PI;
        String retStr = "Position: (" + XPos + ", " + YPos + ", " + ZPos + ")\n"  
                      + "Rotation: (" + XRot + "\u00B0, " + YRot + "\u00B0, " + ZRot + "\u00B0)"; 
        return retStr;
    }
    
    private String getDisplayDataCellLightingInfo(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to cell lighting info.
        //
        // XCLL subrecord format:
        //   Byte 0:      Ambient red value
        //   Byte 1:      Ambient green value
        //   Byte 2:      Ambient blue value
        //   Byte 4:      Directional red value
        //   Byte 5:      Directional green value
        //   Byte 6:      Directional blue value
        //   Byte 8:      Fog red value
        //   Byte 9:      Fog green value
        //   Byte 10:     Fog blue value
        //   Bytes 12-15: Fog near
        //   Bytes 16-19: Fog far
        //   Bytes 20-23: XY rotation (degrees)
        //   Bytes 24-27: Z rotation (degrees)
        //   Bytes 28-31: Directional fade
        //   Bytes 32-35: Fog clip distance

		int ambRed   = subrecordData[0];
		int ambGreen = subrecordData[1];
		int ambBlue  = subrecordData[2];
		int dirRed   = subrecordData[4];
		int dirGreen = subrecordData[5];
		int dirBlue  = subrecordData[6];
		int fogRed   = subrecordData[8];
		int fogGreen = subrecordData[9];
		int fogBlue  = subrecordData[10];
		int fogNearBits = SerializedElement.getInteger(subrecordData, 12);
		int fogFarBits = SerializedElement.getInteger(subrecordData, 16);
		float fogNear = Float.intBitsToFloat(fogNearBits);
		float fogFar = Float.intBitsToFloat(fogFarBits);
		int XYRot = SerializedElement.getInteger(subrecordData, 20);
		int ZRot = SerializedElement.getInteger(subrecordData, 24);
		int dirFadeBits = SerializedElement.getInteger(subrecordData, 28);
		int fogClipBits = SerializedElement.getInteger(subrecordData, 32);
		float dirFade = Float.intBitsToFloat(dirFadeBits);
		float fogClip = Float.intBitsToFloat(fogClipBits);
        String retStr = "Ambient RGB: (" + ambRed + ", " + ambGreen + ", " + ambBlue + ")\n"
                      + "Directional RGB: (" + dirRed + ", " + dirGreen + ", " + dirBlue + ")\n"  
                      + "Fog RGB: (" + fogRed + ", " + fogGreen + ", " + fogBlue + ")\n"  
                      + "Fog Near: " + fogNear + ", Fog Far: " + fogFar + "\n"
                      + "XY Rotation: " + XYRot + "\u00B0, Z Rotation: " + ZRot + "\u00B0\n" 
                      + "Directional Fade: " + dirFade + ", Fog Clip: " + fogClip + "\n";

        return retStr;
    }
    
    private String getDisplayDataSpellEffectName(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to spell effect name.
        //

        return SpellEffectType.getSpellEffectName(new String(subrecordData, 0, subrecordData.length)); 
    }

    private String getDisplayDataSpellEffectData(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to spell effect data.
        //

        String effectName = SpellEffectType.getSpellEffectName(new String(subrecordData, 0, 4)); 
        int effectMagnitude = SerializedElement.getInteger(subrecordData, 4);
        int effectArea = SerializedElement.getInteger(subrecordData, 8);
        int effectDuration = SerializedElement.getInteger(subrecordData, 12);
        int effectType = SerializedElement.getInteger(subrecordData, 16);
        int effectActorValue = SerializedElement.getInteger(subrecordData, 4);
        return "Name: " + effectName + "\nMagnitude: " + effectMagnitude
        + "\nArea: " + effectArea + "\nDuration: " + effectDuration + "\nType: " 
        + effectType + "\nActor Value: " + effectActorValue;
        
    }

    private String getDisplayDataDATAforINFO(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to the special format that the.
        // DATA subrecord has in the INFO record type.
        //
        // Format:
        //   Byte 0:      Dialogue type
        //   Byte 2:      Byte containing flags
 
        int dialogueType = subrecordData[0];
        int dialogueFlags = subrecordData[2];
        String retStr = "Type: " + DialogueTypeCode.getString(dialogueType);
        if ((dialogueFlags & 0x01) != 0)
        	retStr += "\n - Goodbye";
        if ((dialogueFlags & 0x02) != 0)
        	retStr += "\n - Random";
        if ((dialogueFlags & 0x04) != 0)
        	retStr += "\n - Say Once";
        if ((dialogueFlags & 0x10) != 0)
        	retStr += "\n - Info Refusal";
        if ((dialogueFlags & 0x20) != 0)
        	retStr += "\n - Random End";
        if ((dialogueFlags & 0x40) != 0)
        	retStr += "\n - Run for Rumors";
        return retStr;
    }
    
    private String getDisplayDataDATAforCREA(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to the special format that the.
        // DATA subrecord has in the CREA record type.
        //
        // DATA for CREA subrecord format:
        //   Byte 1:      Combat Skill
        //   Byte 2:      Magic Skill
        //   Byte 3:      Stealth Skill
        //   Byte 4:      Soulgem Type
        //   Bytes 6-7:   Health Points
        //   Bytes 10-11: Attack Damage
        //   Byte 12:     Strength
        //   Byte 13:     Intelligence
        //   Byte 14:     Willpower
        //   Byte 15:     Agility
        //   Byte 16:     Speed
        //   Byte 17:     Endurance
        //   Byte 18:     Personality
        //   Byte 19:     Luck

    	String[] soulgemTypes = {"None", "Petty", "Lesser", "Common", "Greater", "Grand"};
		int combatSkill    = subrecordData[1];
		int magicSkill     = subrecordData[2];
		int stealthSkill   = subrecordData[3];
		int soulgemIdx     = subrecordData[4];
		int strength       = subrecordData[12];
		int intelligence   = subrecordData[13];
		int willpower      = subrecordData[14];
		int agility        = subrecordData[15];
		int speed          = subrecordData[16];
		int endurance      = subrecordData[17];
		int personality    = subrecordData[18];
		int luck           = subrecordData[19];
		int healthPoints   = SerializedElement.getShort(subrecordData, 6);
		int attackDamage   = SerializedElement.getShort(subrecordData, 10);
        return             "Combat Skill: " + combatSkill + "\nMagic Skill: " + magicSkill
                         + "\nStealth Skill: " + stealthSkill + "\nSoulgem Type: " +  getIndexedString(soulgemIdx, soulgemTypes)
                         + "\nHealth Points: " + healthPoints + "\nAttack Damage: " + attackDamage  
                         + "\nStrength: " + strength + "\nIntelligence: " + intelligence
                         + "\nWillpower: " + willpower + "\nAgility: " + agility + "\nSpeed: " + speed 
                         + "\nEndurance: " + endurance + "\nPersonality: " + personality + "\nLuck: " + luck;
    }
    
    private String getDisplayDataFormIDArray(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to a CSV of formIDs in hex form,
        // five per line.
    	String retStr = "";
    	int numFormIDs = subrecordData.length / 4;
    	for (int i = 0; i < numFormIDs; i++)
    	{
        	int formID = SerializedElement.getInteger(subrecordData, i * 4);
        	if (i == 0) retStr += String.format("%08X", formID);
        	else if (i % 5 == 0) retStr += ",\n" + String.format("%08X", formID);
        	else        retStr += ", " + String.format("%08X", formID);
    	}
    	return retStr;
    }
    
    private String getDisplayDataFlags(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to binary representation of flags. Not
    	// assuming a byte length, but assuming little-endisn positioning.
        //
    	String retStr = "";
    	for (int i = subrecordData.length - 1 ; i >= 0; i--)
    	{
    		byte flagByte = subrecordData[i];
    		// No defined binary format, so roll your own.
    		retStr += ((flagByte & 0X80) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X40) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X20) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X10) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X08) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X04) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X02) == 0 ? "0" : "1"); 
    		retStr += ((flagByte & 0X01) == 0 ? "0" : "1");     		
    	}
    	return retStr;
    }
    
    private String getDisplayDataActorConfig(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to actor configuration for NPCs & creatures.
        //
        //
        // ACBS subrecord
        //   Bytes 0-3:   Flags (differs for NPCs vs creatures; byte 3 not used)
        //   Bytes 4-5:   Base Spell Points
        //   Bytes 6-7:   Fatigue
        //   Bytes 8-9:   Barter Gold
        //   Bytes 10-11: Level/Offset Level
        //   Bytes 12-13: Calc Min
        //   Bytes 14-15: Calc Max

    	byte[] flagPart = new byte[3];
        System.arraycopy(subrecordData, 0, flagPart, 0, 3);
        String flagBits = getDisplayDataFlags(flagPart);
		int baseSpell = SerializedElement.getShort(subrecordData, 4);
		int fatigue = SerializedElement.getShort(subrecordData, 6);
		int barterGold = SerializedElement.getShort(subrecordData, 8);
		int level = SerializedElement.getShort(subrecordData, 10);
		int calcMin = SerializedElement.getShort(subrecordData, 12);
		int calcMax = SerializedElement.getShort(subrecordData, 14);
        String retStr = "Flags: " + flagBits + "\nBase Spell Points: " + baseSpell 
        + "\nFatigue: " + fatigue + "\nBarter Gold: " + barterGold 
        + "\nLevel/Offset Level: " + level + "\nCalc Min: " + calcMin + "\nCalc Max: " + calcMax;
        return retStr;
    }
    
    private String getDisplayDataAIInfo(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to AI info for NPCs & creatures.
        //
        //
        // AIDT subrecord
        //   Byte 0:      Aggression
        //   Byte 1:      Confidence
        //   Byte 2:      Energy Level
        //   Byte 3:      Responsibility
        //   Bytes 4-7:   Flags (byte 7 not used)
        //   Byte 8:      Training Skill; Only relevant if training flag is set
        //   Byte 9:      Training Level; Only relevant if training flag is set

    	byte[] flagPart = new byte[3];
    	String[] skillNames = {"Armorer", "Athletics", "Blade", "Block", "Blunt",
    			               "Hand to Hand", "Heavy Armor", "Alchemy", "Alteration",
    			               "Conjuration", "Destruction", "Illusion", "Mysticism", 
    			               "Restoration", "Acrobatics", "Light Armor", "Marskman",
    			               "Mercantile", "Security", "Sneak", "Speechcraft"};
        System.arraycopy(subrecordData, 4, flagPart, 0, 3);
        String flagBits = getDisplayDataFlags(flagPart);
		int allFlags = SerializedElement.getInteger(subrecordData, 4);
		int aggression = subrecordData[0];
		int confidence = subrecordData[1];
		int energyLevel = subrecordData[2];
		int responsibility = subrecordData[3];
		int trainingSkill = subrecordData[8];
		int trainingLevel = subrecordData[9];
        String retStr = "Aggression: " + aggression + "\nConfidence: " + confidence 
        + "\nEnergy Level: " + energyLevel + "\nResponsibility: " + responsibility + "\nFlags: " + flagBits;
        if ((allFlags & 0x004000) != 0) // Training flag set.
        {
        	retStr += "\nTraining Skill: " + getIndexedString(trainingSkill, skillNames) + "\nTraining Level: " + trainingLevel;
        }
        
        return retStr;
    }
    
    private String getDisplayDataFactionInfo(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to faction info: formID in 1st 4 bytes & rank in next byte.
        //
    	int factionID = SerializedElement.getInteger(subrecordData, 0);
    	int factionRank = subrecordData[4];
    	return String.format("Faction form ID: %08X\nFaction rank: %d", factionID, factionRank);
    }
    
    private String getDisplayDataPGNodeArray(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to an array of PG node data.
        // Each array member is 16 bytes in length.
        //
        // Format:
        //   Bytes 0-3:   X position
        //   Bytes 4-7:   Y position
        //   Bytes 8-11:  Z position
        //   Byte  12:    Flag
        //   Bytes 13-15: Filler

    	String retStr = "";
    	int numPGNodes = subrecordData.length / 16;
    	for (int i = 0; i < numPGNodes; i++)
    	{
    		int XPosBits = SerializedElement.getInteger(subrecordData, i * 16);
    		int YPosBits = SerializedElement.getInteger(subrecordData, (i * 16) + 4);
    		int ZPosBits = SerializedElement.getInteger(subrecordData, (i * 16) + 8);
    		float XPos = Float.intBitsToFloat(XPosBits);
    		float YPos = Float.intBitsToFloat(YPosBits);
    		float ZPos = Float.intBitsToFloat(ZPosBits);
    		String nodeIdx = String.format("%02d", i); 
    		String flags = String.format("%d", subrecordData[(i * 16) + 12]);
            retStr += "Node " + nodeIdx + " XYZ: (" + XPos + ", " + YPos + ", " + ZPos + "), Connections:  " + flags + "\n"; 
    	}
    	return retStr;
    }
    
    private String getDisplayDataPGConnsInt(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to an array of PG internal connections.
        // Each array member is 4 bytes in length.
        //
        // Format:
        //   Bytes 0-1:   PG node 1
        //   Bytes 2-3:   PG node 2

    	String retStr = "";
    	int numPGConns = subrecordData.length / 4;
    	for (int i = 0; i < numPGConns; i++)
    	{
    		int node1 = SerializedElement.getShort(subrecordData, i * 4);
    		int node2 = SerializedElement.getShort(subrecordData, (i * 4) + 2);
    		String node1Idx = String.format("%02d", node1); 
    		String node2Idx = String.format("%02d", node2); 
    		String connIdx = String.format("%02d", i); 
            retStr += "PG Connection " + connIdx + ": [" + node1Idx + " to " + node2Idx + "]\n"; 
    	}
    	return retStr;
    }
    
    private String getDisplayDataPGConnsExt(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to an array of PG external connections.
        // Each array member is 16 bytes in length.
        //
        // Format:
        //   Byte 0:      internal node index
        //   Bytes 1-3:   filler
        //   Bytes 4-7:   X position of external node
        //   Bytes 8-11:  Y position of external node
        //   Bytes 12-15: Z position of external node

    	String retStr = "";
    	int numPGConnsExt = subrecordData.length / 16;
    	for (int i = 0; i < numPGConnsExt; i++)
    	{
    		int intNode = subrecordData[i * 16];
    		int XPosBits = SerializedElement.getInteger(subrecordData, (i * 16) + 4);
    		int YPosBits = SerializedElement.getInteger(subrecordData, (i * 16) + 8);
    		int ZPosBits = SerializedElement.getInteger(subrecordData, (i * 16) + 12);
    		float XPos = Float.intBitsToFloat(XPosBits);
    		float YPos = Float.intBitsToFloat(YPosBits);
    		float ZPos = Float.intBitsToFloat(ZPosBits);
    		String connIdx = String.format("%02d", i); 
    		String nodeIdx = String.format("%02d", intNode); 
    		String flags = String.format("%d", subrecordData[(i * 16) + 12]);
            retStr += "PG External Connection " + connIdx + ": Node " + nodeIdx + " to (" + XPos + ", " + YPos + ", " + ZPos + ")\n"; 
    	}
    	return retStr;
    }
    
    private String getDisplayDataLSTexture(byte[] subrecordData)
    {        
        //
        // Convert the subrecord data to landscape texture info.
        //
        // Format:
        //   Bytes 0-3:   LS texture form ID
        //   Byte  4:     Cell quadrant: 0 = bottom left, 1 = bottom right, 2 = top left, 3 = top right
        //   Bytes 6-7:   Layer; -1 is the base layer

    	String retStr = "";
		int LSTexID = SerializedElement.getInteger(subrecordData, 0);
		String quad = subrecordData[4] > 1 ? "Top " : "Bottom ";
		quad += subrecordData[4] % 2 == 0 ? "Left" : "Right";
		int layer = SerializedElement.getShort(subrecordData, 6);
		String layerString = layer == 0xFFFF ? "Base" : String.format("%d", layer); 
        retStr += "Landscape texture: " + String.format("%08X", LSTexID) + "\nQuadrant: " + quad + "\nLayer: " + layerString; 
    	return retStr;
    }
    
    private String getIndexedString(int idx, String[] strArray)
    {        
        //
        // Returns the string indicated by the index. If out of range, that is caught before
    	// accessing the array. Assumes zero-based indices; if there are "gaps" in the values,
    	// those strings must be filled in the array prior to calling this method.
        //
    	if (idx < 0) return "Below lower bound";
    	if (idx >= strArray.length) return "Above upper bound";
    	return strArray[idx];
    }
    
    /**
     * Determine if this subrecord is equal to another subrecord.  Two subrecords are considered to be
     * equal if they have the same subrecord type and subrecord data.
     */
    public boolean equals(Object object) {
        boolean areEqual = false;
        if (object instanceof PluginSubrecord) {
            PluginSubrecord objSubrecord = (PluginSubrecord)object;
            if (objSubrecord.getSubrecordType().equals(subrecordType)) {
                try {
                    byte[] subrecordData = getSubrecordData();
                    byte[] objSubrecordData = objSubrecord.getSubrecordData();
                    if (objSubrecordData.length == subrecordData.length) {
                        if ((subrecordType.equals("ATXT") || subrecordType.equals("BTXT")) && subrecordData.length == 8 ) {
                            
                            //
                            // The ATXT and BTXT subrecords contain garbage at offset 5
                            //
                            if (subrecordData[0] == objSubrecordData[0] &&
                                            subrecordData[1] == objSubrecordData[1] &&
                                            subrecordData[2] == objSubrecordData[2] &&
                                            subrecordData[3] == objSubrecordData[3] &&
                                            subrecordData[4] == objSubrecordData[4] &&
                                            subrecordData[6] == objSubrecordData[6] &&
                                            subrecordData[7] == objSubrecordData[7])
                                areEqual = true;
                            
                        } else if (subrecordType.equals("EFIT") && subrecordData.length == 24) {
                            
                            //
                            // The EFIT subrecord contains garbage for the effect subtype if it
                            // is not an attribute or skill effect
                            //
                            String effectName = new String(subrecordData, 0, 4);
                            int count = 20;
                            if (effectName.equals("DGAT") || effectName.equals("DRAT") || effectName.equals("DRSK") ||
                                                             effectName.equals("FOAT") || effectName.equals("FOSK") ||
                                                             effectName.equals("REAT") || effectName.equals("ABAT") ||
                                                             effectName.equals("ABSK"))
                                count = 24;
                            
                            areEqual = true;
                            for (int i=0; i<count; i++) {
                                if (subrecordData[i] != objSubrecordData[i]) {
                                    areEqual = false;
                                    break;
                                }
                            }                            
                            
                        } else if (subrecordType.equals("LVLO") && subrecordData.length == 12) {
                            
                            //
                            // The LVLO subrecord contains garbage at offsets 2-3 and 10-11
                            //
                            if (subrecordData[0] == objSubrecordData[0] &&
                                            subrecordData[1] == objSubrecordData[1] &&
                                            subrecordData[4] == objSubrecordData[4] &&
                                            subrecordData[5] == objSubrecordData[5] &&
                                            subrecordData[6] == objSubrecordData[6] &&
                                            subrecordData[7] == objSubrecordData[7] &&
                                            subrecordData[8] == objSubrecordData[8] &&
                                            subrecordData[9] == objSubrecordData[9])
                                areEqual = true;
                            
                        } else if (subrecordType.equals("PGRP") && subrecordData.length%16 == 0) {
                            
                            //
                            // The PGRP subrecord contains garbage at offsets 14-15 of each 16-byte entry
                            //
                            areEqual = true;
                            for (int i=0; i<subrecordData.length; i+=16) {
                                for (int j=0; j<13; j++) {
                                    if (subrecordData[i+j] != objSubrecordData[i+j]) {
                                        areEqual = false;
                                        break;
                                    }
                                }
                                
                                if (!areEqual)
                                    break;
                            }
                            
                        } else if (subrecordType.equals("PKDT") && subrecordData.length == 8) {
                            
                            //
                            // The PKDT subrecord contains garbage at offset 5-7
                            //
                            if (subrecordData[0] == objSubrecordData[0] &&
                                            subrecordData[1] == objSubrecordData[1] &&
                                            subrecordData[2] == objSubrecordData[2] &&
                                            subrecordData[3] == objSubrecordData[3] &&
                                            subrecordData[4] == objSubrecordData[4])
                                areEqual = true;
                            
                        } else if (subrecordType.equals("QSTA") && subrecordData.length == 8) {
                            
                            //
                            // The QSTA subrecord contains garbage at offsets 6-7
                            //
                            if (subrecordData[0] == objSubrecordData[0] &&
                                            subrecordData[1] == objSubrecordData[1] &&
                                            subrecordData[2] == objSubrecordData[2] &&
                                            subrecordData[3] == objSubrecordData[3] &&
                                            subrecordData[4] == objSubrecordData[4] &&
                                            subrecordData[5] == objSubrecordData[5])
                                areEqual = true;
                            
                        } else if (subrecordType.equals("XCLR") && subrecordData.length%4 == 0) {
                            
                            //
                            // The XCLR subrecord consists of an array of region identifiers
                            //
                            for (int i=0; i<subrecordData.length; i+=4) {
                                areEqual = false;
                                int formID = getInteger(subrecordData, i);
                                for (int j=0; j<subrecordData.length; j+=4) {
                                    int objFormID = getInteger(objSubrecordData, j);
                                    if (objFormID == formID) {
                                        areEqual = true;
                                        break;
                                    }
                                }
                                
                                if (!areEqual)
                                    break;
                            }
                            
                        } else if (subrecordType.equals("XLOC") && subrecordData.length == 12) {
                            
                            //
                            // The XLOC subrecord contains garbage at offsets 9-11
                            //
                            if (subrecordData[0] == objSubrecordData[0] &&
                                            subrecordData[1] == objSubrecordData[1] &&
                                            subrecordData[2] == objSubrecordData[2] &&
                                            subrecordData[3] == objSubrecordData[3] &&
                                            subrecordData[4] == objSubrecordData[4] &&
                                            subrecordData[5] == objSubrecordData[5] &&
                                            subrecordData[6] == objSubrecordData[6] &&
                                            subrecordData[7] == objSubrecordData[7] &&
                                            subrecordData[8] == objSubrecordData[8])
                                areEqual = true;

                        } else {
                            
                            //
                            // The subrecord data must be identical
                            //
                            areEqual = true;
                            for (int i=0; i<subrecordData.length; i++) {
                                if (subrecordData[i] != objSubrecordData[i]) {
                                    areEqual = false;
                                    break;
                                }
                            }
                        }
                    }
                } catch (IOException exc) {
                    areEqual = false;
                }
            }
        }
        
        return areEqual;
    }
        
    /**
     * Return a string describing the subrecord
     *
     * @return                          Descriptive string
     */
    public String toString() {
        return subrecordType+" subrecord";
    }
}

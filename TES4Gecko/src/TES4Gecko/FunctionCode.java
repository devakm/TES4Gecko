package TES4Gecko;

import java.util.HashMap;

/**
 * The FunctionCode is just a wrapper for Oblivion CS function opcodes.  
 */
public final class FunctionCode
{
	public static final int CanHaveFlames = 0x099;
	public static final int CanPayCrimeGold = 0x07f;
	public static final int GetActorValue = 0x00e;
	public static final int GetAlarmed = 0x03d;
	public static final int GetAmountSoldStolen = 0x0be;
	public static final int GetAngle = 0x008;
	public static final int GetArmorRating = 0x051;
	public static final int GetArmorRatingUpperBody = 0x112;
	public static final int GetAttacked = 0x03f;
	public static final int GetBarterGold = 0x108;
	public static final int GetBaseActorValue = 0x115;
	public static final int GetClassDefaultMatch = 0x0e5;
	public static final int GetClothingValue = 0x029;
	public static final int GetCrime = 0x07a;
	public static final int GetCrimeGold = 0x074;
	public static final int GetCurrentAIPackage = 0x06e;
	public static final int GetCurrentAIProcedure = 0x08f;
	public static final int GetCurrentTime = 0x012;
	public static final int GetCurrentWeatherPercent = 0x094;
	public static final int GetDayOfWeek = 0x0aa;
	public static final int GetDead = 0x02e;
	public static final int GetDeadCount = 0x054;
	public static final int GetDestroyed = 0x0cb;
	public static final int GetDetected = 0x02d;
	public static final int GetDetectionLevel = 0x0b4;
	public static final int GetDisabled = 0x023;
	public static final int GetDisease = 0x027;
	public static final int GetDisposition = 0x04c;
	public static final int GetDistance = 0x001;
	public static final int GetDoorDefaultOpen = 0x0d7;
	public static final int GetEquipped = 0x0b6;
	public static final int GetFactionRank = 0x049;
	public static final int GetFactionRankDifference = 0x03c;
	public static final int GetFatiguePercentage = 0x080;
	public static final int GetFriendHit = 0x120;
	public static final int GetFurnitureMarkerID = 0x0a0;
	public static final int GetGlobalValue = 0x04a;
	public static final int GetGold = 0x030;
	public static final int GetHeadingAngle = 0x063;
	public static final int GetIdleDoneOnce = 0x13e;
	public static final int GetIgnoreFriendlyHits = 0x152;
	public static final int GetInCell = 0x043;
	public static final int GetInCellParam = 0x0e6;
	public static final int GetInFaction = 0x047;
	public static final int GetInSameCell = 0x020;
	public static final int GetInWorldspace = 0x136;
	public static final int GetInvestmentGold = 0x131;
	public static final int GetIsAlerted = 0x05b;
	public static final int GetIsClass = 0x044;
	public static final int GetIsClassDefault = 0x0e4;
	public static final int GetIsCreature = 0x040;
	public static final int GetIsCurrentPackage = 0x0a1;
	public static final int GetIsCurrentWeather = 0x095;
	public static final int GetIsGhost = 0x0ed;
	public static final int GetIsID = 0x048;
	public static final int GetIsPlayableRace = 0x0fe;
	public static final int GetIsPlayerBirthsign = 0x0e0;
	public static final int GetIsRace = 0x045;
	public static final int GetIsReference = 0x088;
	public static final int GetIsSex = 0x046;
	public static final int GetIsUsedItem = 0x0f6;
	public static final int GetIsUsedItemType = 0x0f7;
	public static final int GetItemCount = 0x02f;
	public static final int GetKnockedState = 0x06b;
	public static final int GetLevel = 0x050;
	public static final int GetLineOfSight = 0x01b;
	public static final int GetLockLevel = 0x041;
	public static final int GetLocked = 0x005;
	public static final int GetNoRumors = 0x140;
	public static final int GetOffersServicesNow = 0x0ff;
	public static final int GetOpenState = 0x09d;
	public static final int GetPCExpelled = 0x0c1;
	public static final int GetPCFactionAttack = 0x0c7;
	public static final int GetPCFactionMurder = 0x0c3;
	public static final int GetPCFactionSteal = 0x0c5;
	public static final int GetPCFactionSubmitAuthority = 0x0c9;
	public static final int GetPCFame = 0x0f9;
	public static final int GetPCInFaction = 0x084;
	public static final int GetPCInfamy = 0x0fb;
	public static final int GetPCIsClass = 0x081;
	public static final int GetPCIsRace = 0x082;
	public static final int GetPCIsSex = 0x083;
	public static final int GetPCMiscStat = 0x138;
	public static final int GetPersuasionNumber = 0x0e1;
	public static final int GetPlayerControlsDisabled = 0x062;
	public static final int GetPlayerHasLastRiddenHorse = 0x16a;
	public static final int GetPlayerInSEWorld = 0x16d;
	public static final int GetPos = 0x006;
	public static final int GetQuestRunning = 0x038;
	public static final int GetQuestVariable = 0x04f;
	public static final int GetRandomPercent = 0x04d;
	public static final int GetRestrained = 0x0f4;
	public static final int GetScale = 0x018;
	public static final int GetScriptVariable = 0x035;
	public static final int GetSecondsPassed = 0x00c;
	public static final int GetShouldAttack = 0x042;
	public static final int GetSitting = 0x09f;
	public static final int GetSleeping = 0x031;
	public static final int GetStage = 0x03a;
	public static final int GetStageDone = 0x03b;
	public static final int GetStartingAngle = 0x00b;
	public static final int GetStartingPos = 0x00a;
	public static final int GetTalkedToPC = 0x032;
	public static final int GetTalkedToPCParam = 0x0ac;
	public static final int GetTimeDead = 0x169;
	public static final int GetTotalPersuasionNumber = 0x13b;
	public static final int GetTrespassWarningLevel = 0x090;
	public static final int GetUnconscious = 0x0f2;
	public static final int GetUsedItemActivate = 0x103;
	public static final int GetUsedItemLevel = 0x102;
	public static final int GetVampire = 0x028;
	public static final int GetWalkSpeed = 0x08e;
	public static final int GetWeaponAnimType = 0x06c;
	public static final int GetWeaponSkillType = 0x06d;
	public static final int GetWindSpeed = 0x093;
	public static final int HasFlames = 0x09a;
	public static final int HasMagicEffect = 0x0d6;
	public static final int HasVampireFed = 0x0e3;
	public static final int IsActor = 0x161;
	public static final int IsActorAVictim = 0x13a;
	public static final int IsActorEvil = 0x139;
	public static final int IsActorUsingATorch = 0x132;
	public static final int IsCellOwner = 0x118;
	public static final int IsCloudy = 0x10b;
	public static final int IsContinuingPackagePCNear = 0x096;
	public static final int IsCurrentFurnitureObj = 0x0a3;
	public static final int IsCurrentFurnitureRef = 0x0a2;
	public static final int IsEssential = 0x162;
	public static final int IsFacingUp = 0x06a;
	public static final int IsGuard = 0x07d;
	public static final int IsHorseStolen = 0x11a;
	public static final int IsIdlePlaying = 0x070;
	public static final int IsInCombat = 0x121;
	public static final int IsInDangerousWater = 0x14c;
	public static final int IsInInterior = 0x12c;
	public static final int IsInMyOwnedCell = 0x092;
	public static final int IsLeftUp = 0x11d;
	public static final int IsOwner = 0x116;
	public static final int IsPCAMurderer = 0x0b0;
	public static final int IsPCSleeping = 0x0af;
	public static final int IsPlayerInJail = 0x0ab;
	public static final int IsPlayerMovingIntoNewSpace = 0x166;
	public static final int IsPlayersLastRiddenHorse = 0x153;
	public static final int IsPleasant = 0x10a;
	public static final int IsRaining = 0x03e;
	public static final int IsRidingHorse = 0x147;
	public static final int IsRunning = 0x11f;
	public static final int IsShieldOut = 0x067;
	public static final int IsSneaking = 0x11e;
	public static final int IsSnowing = 0x04b;
	public static final int IsSpellTarget = 0x0df;
	public static final int IsSwimming = 0x0b9;
	public static final int IsTalking = 0x08d;
	public static final int IsTimePassing = 0x109;
	public static final int IsTorchOut = 0x066;
	public static final int IsTrespassing = 0x091;
	public static final int IsTurnArrest = 0x149;
	public static final int IsWaiting = 0x06f;
	public static final int IsWeaponOut = 0x065;
	public static final int IsXBox = 0x135;
	public static final int IsYielding = 0x068;
	public static final int MenuMode = 0x024;
	public static final int SameFaction = 0x02a;
	public static final int SameFactionAsPC = 0x085;
	public static final int SameRace = 0x02b;
	public static final int SameRaceAsPC = 0x086;
	public static final int SameSex = 0x02c;
	public static final int SameSexAsPC = 0x087;
	public static final int WhichServiceMenu = 0x143;

    
    public static final HashMap<String, Integer> funcCodeMap = new HashMap<String, Integer>()
    {
    	{
    		put("CanHaveFlames", CanHaveFlames);
    		put("CanPayCrimeGold", CanPayCrimeGold);
    		put("GetActorValue", GetActorValue);
    		put("GetAlarmed", GetAlarmed);
    		put("GetAmountSoldStolen", GetAmountSoldStolen);
    		put("GetAngle", GetAngle);
    		put("GetArmorRating", GetArmorRating);
    		put("GetArmorRatingUpperBody", GetArmorRatingUpperBody);
    		put("GetAttacked", GetAttacked);
    		put("GetBarterGold", GetBarterGold);
    		put("GetBaseActorValue", GetBaseActorValue);
    		put("GetClassDefaultMatch", GetClassDefaultMatch);
    		put("GetClothingValue", GetClothingValue);
    		put("GetCrime", GetCrime);
    		put("GetCrimeGold", GetCrimeGold);
    		put("GetCurrentAIPackage", GetCurrentAIPackage);
    		put("GetCurrentAIProcedure", GetCurrentAIProcedure);
    		put("GetCurrentTime", GetCurrentTime);
    		put("GetCurrentWeatherPercent", GetCurrentWeatherPercent);
    		put("GetDayOfWeek", GetDayOfWeek);
    		put("GetDead", GetDead);
    		put("GetDeadCount", GetDeadCount);
    		put("GetDestroyed", GetDestroyed);
    		put("GetDetected", GetDetected);
    		put("GetDetectionLevel", GetDetectionLevel);
    		put("GetDisabled", GetDisabled);
    		put("GetDisease", GetDisease);
    		put("GetDisposition", GetDisposition);
    		put("GetDistance", GetDistance);
    		put("GetDoorDefaultOpen", GetDoorDefaultOpen);
    		put("GetEquipped", GetEquipped);
    		put("GetFactionRank", GetFactionRank);
    		put("GetFactionRankDifference", GetFactionRankDifference);
    		put("GetFatiguePercentage", GetFatiguePercentage);
    		put("GetFriendHit", GetFriendHit);
    		put("GetFurnitureMarkerID", GetFurnitureMarkerID);
    		put("GetGlobalValue", GetGlobalValue);
    		put("GetGold", GetGold);
    		put("GetHeadingAngle", GetHeadingAngle);
    		put("GetIdleDoneOnce", GetIdleDoneOnce);
    		put("GetIgnoreFriendlyHits", GetIgnoreFriendlyHits);
    		put("GetInCell", GetInCell);
    		put("GetInCellParam", GetInCellParam);
    		put("GetInFaction", GetInFaction);
    		put("GetInSameCell", GetInSameCell);
    		put("GetInWorldspace", GetInWorldspace);
    		put("GetInvestmentGold", GetInvestmentGold);
    		put("GetIsAlerted", GetIsAlerted);
    		put("GetIsClass", GetIsClass);
    		put("GetIsClassDefault", GetIsClassDefault);
    		put("GetIsCreature", GetIsCreature);
    		put("GetIsCurrentPackage", GetIsCurrentPackage);
    		put("GetIsCurrentWeather", GetIsCurrentWeather);
    		put("GetIsGhost", GetIsGhost);
    		put("GetIsID", GetIsID);
    		put("GetIsPlayableRace", GetIsPlayableRace);
    		put("GetIsPlayerBirthsign", GetIsPlayerBirthsign);
    		put("GetIsRace", GetIsRace);
    		put("GetIsReference", GetIsReference);
    		put("GetIsSex", GetIsSex);
    		put("GetIsUsedItem", GetIsUsedItem);
    		put("GetIsUsedItemType", GetIsUsedItemType);
    		put("GetItemCount", GetItemCount);
    		put("GetKnockedState", GetKnockedState);
    		put("GetLevel", GetLevel);
    		put("GetLineOfSight", GetLineOfSight);
    		put("GetLockLevel", GetLockLevel);
    		put("GetLocked", GetLocked);
    		put("GetNoRumors", GetNoRumors);
    		put("GetOffersServicesNow", GetOffersServicesNow);
    		put("GetOpenState", GetOpenState);
    		put("GetPCExpelled", GetPCExpelled);
    		put("GetPCFactionAttack", GetPCFactionAttack);
    		put("GetPCFactionMurder", GetPCFactionMurder);
    		put("GetPCFactionSteal", GetPCFactionSteal);
    		put("GetPCFactionSubmitAuthority", GetPCFactionSubmitAuthority);
    		put("GetPCFame", GetPCFame);
    		put("GetPCInFaction", GetPCInFaction);
    		put("GetPCInfamy", GetPCInfamy);
    		put("GetPCIsClass", GetPCIsClass);
    		put("GetPCIsRace", GetPCIsRace);
    		put("GetPCIsSex", GetPCIsSex);
    		put("GetPCMiscStat", GetPCMiscStat);
    		put("GetPersuasionNumber", GetPersuasionNumber);
    		put("GetPlayerControlsDisabled", GetPlayerControlsDisabled);
    		put("GetPlayerHasLastRiddenHorse", GetPlayerHasLastRiddenHorse);
    		put("GetPlayerInSEWorld", GetPlayerInSEWorld);
    		put("GetPos", GetPos);
    		put("GetQuestRunning", GetQuestRunning);
    		put("GetQuestVariable", GetQuestVariable);
    		put("GetRandomPercent", GetRandomPercent);
    		put("GetRestrained", GetRestrained);
    		put("GetScale", GetScale);
    		put("GetScriptVariable", GetScriptVariable);
    		put("GetSecondsPassed", GetSecondsPassed);
    		put("GetShouldAttack", GetShouldAttack);
    		put("GetSitting", GetSitting);
    		put("GetSleeping", GetSleeping);
    		put("GetStage", GetStage);
    		put("GetStageDone", GetStageDone);
    		put("GetStartingAngle", GetStartingAngle);
    		put("GetStartingPos", GetStartingPos);
    		put("GetTalkedToPC", GetTalkedToPC);
    		put("GetTalkedToPCParam", GetTalkedToPCParam);
    		put("GetTimeDead", GetTimeDead);
    		put("GetTotalPersuasionNumber", GetTotalPersuasionNumber);
    		put("GetTrespassWarningLevel", GetTrespassWarningLevel);
    		put("GetUnconscious", GetUnconscious);
    		put("GetUsedItemActivate", GetUsedItemActivate);
    		put("GetUsedItemLevel", GetUsedItemLevel);
    		put("GetVampire", GetVampire);
    		put("GetWalkSpeed", GetWalkSpeed);
    		put("GetWeaponAnimType", GetWeaponAnimType);
    		put("GetWeaponSkillType", GetWeaponSkillType);
    		put("GetWindSpeed", GetWindSpeed);
    		put("HasFlames", HasFlames);
    		put("HasMagicEffect", HasMagicEffect);
    		put("HasVampireFed", HasVampireFed);
    		put("IsActor", IsActor);
    		put("IsActorAVictim", IsActorAVictim);
    		put("IsActorEvil", IsActorEvil);
    		put("IsActorUsingATorch", IsActorUsingATorch);
    		put("IsCellOwner", IsCellOwner);
    		put("IsCloudy", IsCloudy);
    		put("IsContinuingPackagePCNear", IsContinuingPackagePCNear);
    		put("IsCurrentFurnitureObj", IsCurrentFurnitureObj);
    		put("IsCurrentFurnitureRef", IsCurrentFurnitureRef);
    		put("IsEssential", IsEssential);
    		put("IsFacingUp", IsFacingUp);
    		put("IsGuard", IsGuard);
    		put("IsHorseStolen", IsHorseStolen);
    		put("IsIdlePlaying", IsIdlePlaying);
    		put("IsInCombat", IsInCombat);
    		put("IsInDangerousWater", IsInDangerousWater);
    		put("IsInInterior", IsInInterior);
    		put("IsInMyOwnedCell", IsInMyOwnedCell);
    		put("IsLeftUp", IsLeftUp);
    		put("IsOwner", IsOwner);
    		put("IsPCAMurderer", IsPCAMurderer);
    		put("IsPCSleeping", IsPCSleeping);
    		put("IsPlayerInJail", IsPlayerInJail);
    		put("IsPlayerMovingIntoNewSpace", IsPlayerMovingIntoNewSpace);
    		put("IsPlayersLastRiddenHorse", IsPlayersLastRiddenHorse);
    		put("IsPleasant", IsPleasant);
    		put("IsRaining", IsRaining);
    		put("IsRidingHorse", IsRidingHorse);
    		put("IsRunning", IsRunning);
    		put("IsShieldOut", IsShieldOut);
    		put("IsSneaking", IsSneaking);
    		put("IsSnowing", IsSnowing);
    		put("IsSpellTarget", IsSpellTarget);
    		put("IsSwimming", IsSwimming);
    		put("IsTalking", IsTalking);
    		put("IsTimePassing", IsTimePassing);
    		put("IsTorchOut", IsTorchOut);
    		put("IsTrespassing", IsTrespassing);
    		put("IsTurnArrest", IsTurnArrest);
    		put("IsWaiting", IsWaiting);
    		put("IsWeaponOut", IsWeaponOut);
    		put("IsXBox", IsXBox);
    		put("IsYielding", IsYielding);
    		put("MenuMode", MenuMode);
    		put("SameFaction", SameFaction);
    		put("SameFactionAsPC", SameFactionAsPC);
    		put("SameRace", SameRace);
    		put("SameRaceAsPC", SameRaceAsPC);
    		put("SameSex", SameSex);
    		put("SameSexAsPC", SameSexAsPC);
    		put("WhichServiceMenu", WhichServiceMenu);
    	}
    };
    
    public static final HashMap<Integer, String> funcCodeNameMap = new HashMap<Integer, String>()
    {
    	{
    		put(CanHaveFlames, "CanHaveFlames");
    		put(CanPayCrimeGold, "CanPayCrimeGold");
    		put(GetActorValue, "GetActorValue");
    		put(GetAlarmed, "GetAlarmed");
    		put(GetAmountSoldStolen, "GetAmountSoldStolen");
    		put(GetAngle, "GetAngle");
    		put(GetArmorRating, "GetArmorRating");
    		put(GetArmorRatingUpperBody, "GetArmorRatingUpperBody");
    		put(GetAttacked, "GetAttacked");
    		put(GetBarterGold, "GetBarterGold");
    		put(GetBaseActorValue, "GetBaseActorValue");
    		put(GetClassDefaultMatch, "GetClassDefaultMatch");
    		put(GetClothingValue, "GetClothingValue");
    		put(GetCrime, "GetCrime");
    		put(GetCrimeGold, "GetCrimeGold");
    		put(GetCurrentAIPackage, "GetCurrentAIPackage");
    		put(GetCurrentAIProcedure, "GetCurrentAIProcedure");
    		put(GetCurrentTime, "GetCurrentTime");
    		put(GetCurrentWeatherPercent, "GetCurrentWeatherPercent");
    		put(GetDayOfWeek, "GetDayOfWeek");
    		put(GetDead, "GetDead");
    		put(GetDeadCount, "GetDeadCount");
    		put(GetDestroyed, "GetDestroyed");
    		put(GetDetected, "GetDetected");
    		put(GetDetectionLevel, "GetDetectionLevel");
    		put(GetDisabled, "GetDisabled");
    		put(GetDisease, "GetDisease");
    		put(GetDisposition, "GetDisposition");
    		put(GetDistance, "GetDistance");
    		put(GetDoorDefaultOpen, "GetDoorDefaultOpen");
    		put(GetEquipped, "GetEquipped");
    		put(GetFactionRank, "GetFactionRank");
    		put(GetFactionRankDifference, "GetFactionRankDifference");
    		put(GetFatiguePercentage, "GetFatiguePercentage");
    		put(GetFriendHit, "GetFriendHit");
    		put(GetFurnitureMarkerID, "GetFurnitureMarkerID");
    		put(GetGlobalValue, "GetGlobalValue");
    		put(GetGold, "GetGold");
    		put(GetHeadingAngle, "GetHeadingAngle");
    		put(GetIdleDoneOnce, "GetIdleDoneOnce");
    		put(GetIgnoreFriendlyHits, "GetIgnoreFriendlyHits");
    		put(GetInCell, "GetInCell");
    		put(GetInCellParam, "GetInCellParam");
    		put(GetInFaction, "GetInFaction");
    		put(GetInSameCell, "GetInSameCell");
    		put(GetInWorldspace, "GetInWorldspace");
    		put(GetInvestmentGold, "GetInvestmentGold");
    		put(GetIsAlerted, "GetIsAlerted");
    		put(GetIsClass, "GetIsClass");
    		put(GetIsClassDefault, "GetIsClassDefault");
    		put(GetIsCreature, "GetIsCreature");
    		put(GetIsCurrentPackage, "GetIsCurrentPackage");
    		put(GetIsCurrentWeather, "GetIsCurrentWeather");
    		put(GetIsGhost, "GetIsGhost");
    		put(GetIsID, "GetIsID");
    		put(GetIsPlayableRace, "GetIsPlayableRace");
    		put(GetIsPlayerBirthsign, "GetIsPlayerBirthsign");
    		put(GetIsRace, "GetIsRace");
    		put(GetIsReference, "GetIsReference");
    		put(GetIsSex, "GetIsSex");
    		put(GetIsUsedItem, "GetIsUsedItem");
    		put(GetIsUsedItemType, "GetIsUsedItemType");
    		put(GetItemCount, "GetItemCount");
    		put(GetKnockedState, "GetKnockedState");
    		put(GetLevel, "GetLevel");
    		put(GetLineOfSight, "GetLineOfSight");
    		put(GetLockLevel, "GetLockLevel");
    		put(GetLocked, "GetLocked");
    		put(GetNoRumors, "GetNoRumors");
    		put(GetOffersServicesNow, "GetOffersServicesNow");
    		put(GetOpenState, "GetOpenState");
    		put(GetPCExpelled, "GetPCExpelled");
    		put(GetPCFactionAttack, "GetPCFactionAttack");
    		put(GetPCFactionMurder, "GetPCFactionMurder");
    		put(GetPCFactionSteal, "GetPCFactionSteal");
    		put(GetPCFactionSubmitAuthority, "GetPCFactionSubmitAuthority");
    		put(GetPCFame, "GetPCFame");
    		put(GetPCInFaction, "GetPCInFaction");
    		put(GetPCInfamy, "GetPCInfamy");
    		put(GetPCIsClass, "GetPCIsClass");
    		put(GetPCIsRace, "GetPCIsRace");
    		put(GetPCIsSex, "GetPCIsSex");
    		put(GetPCMiscStat, "GetPCMiscStat");
    		put(GetPersuasionNumber, "GetPersuasionNumber");
    		put(GetPlayerControlsDisabled, "GetPlayerControlsDisabled");
    		put(GetPlayerHasLastRiddenHorse, "GetPlayerHasLastRiddenHorse");
    		put(GetPlayerInSEWorld, "GetPlayerInSEWorld");
    		put(GetPos, "GetPos");
    		put(GetQuestRunning, "GetQuestRunning");
    		put(GetQuestVariable, "GetQuestVariable");
    		put(GetRandomPercent, "GetRandomPercent");
    		put(GetRestrained, "GetRestrained");
    		put(GetScale, "GetScale");
    		put(GetScriptVariable, "GetScriptVariable");
    		put(GetSecondsPassed, "GetSecondsPassed");
    		put(GetShouldAttack, "GetShouldAttack");
    		put(GetSitting, "GetSitting");
    		put(GetSleeping, "GetSleeping");
    		put(GetStage, "GetStage");
    		put(GetStageDone, "GetStageDone");
    		put(GetStartingAngle, "GetStartingAngle");
    		put(GetStartingPos, "GetStartingPos");
    		put(GetTalkedToPC, "GetTalkedToPC");
    		put(GetTalkedToPCParam, "GetTalkedToPCParam");
    		put(GetTimeDead, "GetTimeDead");
    		put(GetTotalPersuasionNumber, "GetTotalPersuasionNumber");
    		put(GetTrespassWarningLevel, "GetTrespassWarningLevel");
    		put(GetUnconscious, "GetUnconscious");
    		put(GetUsedItemActivate, "GetUsedItemActivate");
    		put(GetUsedItemLevel, "GetUsedItemLevel");
    		put(GetVampire, "GetVampire");
    		put(GetWalkSpeed, "GetWalkSpeed");
    		put(GetWeaponAnimType, "GetWeaponAnimType");
    		put(GetWeaponSkillType, "GetWeaponSkillType");
    		put(GetWindSpeed, "GetWindSpeed");
    		put(HasFlames, "HasFlames");
    		put(HasMagicEffect, "HasMagicEffect");
    		put(HasVampireFed, "HasVampireFed");
    		put(IsActor, "IsActor");
    		put(IsActorAVictim, "IsActorAVictim");
    		put(IsActorEvil, "IsActorEvil");
    		put(IsActorUsingATorch, "IsActorUsingATorch");
    		put(IsCellOwner, "IsCellOwner");
    		put(IsCloudy, "IsCloudy");
    		put(IsContinuingPackagePCNear, "IsContinuingPackagePCNear");
    		put(IsCurrentFurnitureObj, "IsCurrentFurnitureObj");
    		put(IsCurrentFurnitureRef, "IsCurrentFurnitureRef");
    		put(IsEssential, "IsEssential");
    		put(IsFacingUp, "IsFacingUp");
    		put(IsGuard, "IsGuard");
    		put(IsHorseStolen, "IsHorseStolen");
    		put(IsIdlePlaying, "IsIdlePlaying");
    		put(IsInCombat, "IsInCombat");
    		put(IsInDangerousWater, "IsInDangerousWater");
    		put(IsInInterior, "IsInInterior");
    		put(IsInMyOwnedCell, "IsInMyOwnedCell");
    		put(IsLeftUp, "IsLeftUp");
    		put(IsOwner, "IsOwner");
    		put(IsPCAMurderer, "IsPCAMurderer");
    		put(IsPCSleeping, "IsPCSleeping");
    		put(IsPlayerInJail, "IsPlayerInJail");
    		put(IsPlayerMovingIntoNewSpace, "IsPlayerMovingIntoNewSpace");
    		put(IsPlayersLastRiddenHorse, "IsPlayersLastRiddenHorse");
    		put(IsPleasant, "IsPleasant");
    		put(IsRaining, "IsRaining");
    		put(IsRidingHorse, "IsRidingHorse");
    		put(IsRunning, "IsRunning");
    		put(IsShieldOut, "IsShieldOut");
    		put(IsSneaking, "IsSneaking");
    		put(IsSnowing, "IsSnowing");
    		put(IsSpellTarget, "IsSpellTarget");
    		put(IsSwimming, "IsSwimming");
    		put(IsTalking, "IsTalking");
    		put(IsTimePassing, "IsTimePassing");
    		put(IsTorchOut, "IsTorchOut");
    		put(IsTrespassing, "IsTrespassing");
    		put(IsTurnArrest, "IsTurnArrest");
    		put(IsWaiting, "IsWaiting");
    		put(IsWeaponOut, "IsWeaponOut");
    		put(IsXBox, "IsXBox");
    		put(IsYielding, "IsYielding");
    		put(MenuMode, "MenuMode");
    		put(SameFaction, "SameFaction");
    		put(SameFactionAsPC, "SameFactionAsPC");
    		put(SameRace, "SameRace");
    		put(SameRaceAsPC, "SameRaceAsPC");
    		put(SameSex, "SameSex");
    		put(SameSexAsPC, "SameSexAsPC");
    		put(WhichServiceMenu, "WhichServiceMenu");
    	}
    };
    
	public static String getFuncCodeName(int funcCodeType)
	{
		if (!funcCodeNameMap.containsKey(funcCodeType))
			return String.format("Unknown function [0x%03x hex, %d dec]", funcCodeType, funcCodeType);
		else return funcCodeNameMap.get(funcCodeType); 
	}
	
    // Array for pop-up. Change anything here, make sure to change the maps above.
    public static final String[] funcCodeList =
    {
    	"CanHaveFlames",
    	"CanPayCrimeGold",
    	"GetActorValue",
    	"GetAlarmed",
    	"GetAmountSoldStolen",
    	"GetAngle",
    	"GetArmorRating",
    	"GetArmorRatingUpperBody",
    	"GetAttacked",
    	"GetBarterGold",
    	"GetBaseActorValue",
    	"GetClassDefaultMatch",
    	"GetClothingValue",
    	"GetCrime",
    	"GetCrimeGold",
    	"GetCurrentAIPackage",
    	"GetCurrentAIProcedure",
    	"GetCurrentTime",
    	"GetCurrentWeatherPercent",
    	"GetDayOfWeek",
    	"GetDead",
    	"GetDeadCount",
    	"GetDestroyed",
    	"GetDetected",
    	"GetDetectionLevel",
    	"GetDisabled",
    	"GetDisease",
    	"GetDisposition",
    	"GetDistance",
    	"GetDoorDefaultOpen",
    	"GetEquipped",
    	"GetFactionRank",
    	"GetFactionRankDifference",
    	"GetFatiguePercentage",
    	"GetFriendHit",
    	"GetFurnitureMarkerID",
    	"GetGlobalValue",
    	"GetGold",
    	"GetHeadingAngle",
    	"GetIdleDoneOnce",
    	"GetIgnoreFriendlyHits",
    	"GetInCell",
    	"GetInCellParam",
    	"GetInFaction",
    	"GetInSameCell",
    	"GetInWorldspace",
    	"GetInvestmentGold",
    	"GetIsAlerted",
    	"GetIsClass",
    	"GetIsClassDefault",
    	"GetIsCreature",
    	"GetIsCurrentPackage",
    	"GetIsCurrentWeather",
    	"GetIsGhost",
    	"GetIsID",
    	"GetIsPlayableRace",
    	"GetIsPlayerBirthsign",
    	"GetIsRace",
    	"GetIsReference",
    	"GetIsSex",
    	"GetIsUsedItem",
    	"GetIsUsedItemType",
    	"GetItemCount",
    	"GetKnockedState",
    	"GetLevel",
    	"GetLineOfSight",
    	"GetLockLevel",
    	"GetLocked",
    	"GetNoRumors",
    	"GetOffersServicesNow",
    	"GetOpenState",
    	"GetPCExpelled",
    	"GetPCFactionAttack",
    	"GetPCFactionMurder",
    	"GetPCFactionSteal",
    	"GetPCFactionSubmitAuthority",
    	"GetPCFame",
    	"GetPCInFaction",
    	"GetPCInfamy",
    	"GetPCIsClass",
    	"GetPCIsRace",
    	"GetPCIsSex",
    	"GetPCMiscStat",
    	"GetPersuasionNumber",
    	"GetPlayerControlsDisabled",
    	"GetPlayerHasLastRiddenHorse",
    	"GetPlayerInSEWorld",
    	"GetPos",
    	"GetQuestRunning",
    	"GetQuestVariable",
    	"GetRandomPercent",
    	"GetRestrained",
    	"GetScale",
    	"GetScriptVariable",
    	"GetSecondsPassed",
    	"GetShouldAttack",
    	"GetSitting",
    	"GetSleeping",
    	"GetStage",
    	"GetStageDone",
    	"GetStartingAngle",
    	"GetStartingPos",
    	"GetTalkedToPC",
    	"GetTalkedToPCParam",
    	"GetTimeDead",
    	"GetTotalPersuasionNumber",
    	"GetTrespassWarningLevel",
    	"GetUnconscious",
    	"GetUsedItemActivate",
    	"GetUsedItemLevel",
    	"GetVampire",
    	"GetWalkSpeed",
    	"GetWeaponAnimType",
    	"GetWeaponSkillType",
    	"GetWindSpeed",
    	"HasFlames",
    	"HasMagicEffect",
    	"HasVampireFed",
    	"IsActor",
    	"IsActorAVictim",
    	"IsActorEvil",
    	"IsActorUsingATorch",
    	"IsCellOwner",
    	"IsCloudy",
    	"IsContinuingPackagePCNear",
    	"IsCurrentFurnitureObj",
    	"IsCurrentFurnitureRef",
    	"IsEssential",
    	"IsFacingUp",
    	"IsGuard",
    	"IsHorseStolen",
    	"IsIdlePlaying",
    	"IsInCombat",
    	"IsInDangerousWater",
    	"IsInInterior",
    	"IsInMyOwnedCell",
    	"IsLeftUp",
    	"IsOwner",
    	"IsPCAMurderer",
    	"IsPCSleeping",
    	"IsPlayerInJail",
    	"IsPlayerMovingIntoNewSpace",
    	"IsPlayersLastRiddenHorse",
    	"IsPleasant",
    	"IsRaining",
    	"IsRidingHorse",
    	"IsRunning",
    	"IsShieldOut",
    	"IsSneaking",
    	"IsSnowing",
    	"IsSpellTarget",
    	"IsSwimming",
    	"IsTalking",
    	"IsTimePassing",
    	"IsTorchOut",
    	"IsTrespassing",
    	"IsTurnArrest",
    	"IsWaiting",
    	"IsWeaponOut",
    	"IsXBox",
    	"IsYielding",
    	"MenuMode",
    	"SameFaction",
    	"SameFactionAsPC",
    	"SameRace",
    	"SameRaceAsPC",
    	"SameSex",
    	"SameSexAsPC",
    	"WhichServiceMenu",
    };

    // Two arrays solely for pop-up selection

	public static boolean isValid(int param)
	{
		return (param == CanHaveFlames ||
				param == CanPayCrimeGold ||
				param == GetActorValue ||
				param == GetAlarmed ||
				param == GetAmountSoldStolen ||
				param == GetAngle ||
				param == GetArmorRating ||
				param == GetArmorRatingUpperBody ||
				param == GetAttacked ||
				param == GetBarterGold ||
				param == GetBaseActorValue ||
				param == GetClassDefaultMatch ||
				param == GetClothingValue ||
				param == GetCrime ||
				param == GetCrimeGold ||
				param == GetCurrentAIPackage ||
				param == GetCurrentAIProcedure ||
				param == GetCurrentTime ||
				param == GetCurrentWeatherPercent ||
				param == GetDayOfWeek ||
				param == GetDead ||
				param == GetDeadCount ||
				param == GetDestroyed ||
				param == GetDetected ||
				param == GetDetectionLevel ||
				param == GetDisabled ||
				param == GetDisease ||
				param == GetDisposition ||
				param == GetDistance ||
				param == GetDoorDefaultOpen ||
				param == GetEquipped ||
				param == GetFactionRank ||
				param == GetFactionRankDifference ||
				param == GetFatiguePercentage ||
				param == GetFriendHit ||
				param == GetFurnitureMarkerID ||
				param == GetGlobalValue ||
				param == GetGold ||
				param == GetHeadingAngle ||
				param == GetIdleDoneOnce ||
				param == GetIgnoreFriendlyHits ||
				param == GetInCell ||
				param == GetInCellParam ||
				param == GetInFaction ||
				param == GetInSameCell ||
				param == GetInWorldspace ||
				param == GetInvestmentGold ||
				param == GetIsAlerted ||
				param == GetIsClass ||
				param == GetIsClassDefault ||
				param == GetIsCreature ||
				param == GetIsCurrentPackage ||
				param == GetIsCurrentWeather ||
				param == GetIsGhost ||
				param == GetIsID ||
				param == GetIsPlayableRace ||
				param == GetIsPlayerBirthsign ||
				param == GetIsRace ||
				param == GetIsReference ||
				param == GetIsSex ||
				param == GetIsUsedItem ||
				param == GetIsUsedItemType ||
				param == GetItemCount ||
				param == GetKnockedState ||
				param == GetLevel ||
				param == GetLineOfSight ||
				param == GetLockLevel ||
				param == GetLocked ||
				param == GetNoRumors ||
				param == GetOffersServicesNow ||
				param == GetOpenState ||
				param == GetPCExpelled ||
				param == GetPCFactionAttack ||
				param == GetPCFactionMurder ||
				param == GetPCFactionSteal ||
				param == GetPCFactionSubmitAuthority ||
				param == GetPCFame ||
				param == GetPCInFaction ||
				param == GetPCInfamy ||
				param == GetPCIsClass ||
				param == GetPCIsRace ||
				param == GetPCIsSex ||
				param == GetPCMiscStat ||
				param == GetPersuasionNumber ||
				param == GetPlayerControlsDisabled ||
				param == GetPlayerHasLastRiddenHorse ||
				param == GetPlayerInSEWorld ||
				param == GetPos ||
				param == GetQuestRunning ||
				param == GetQuestVariable ||
				param == GetRandomPercent ||
				param == GetRestrained ||
				param == GetScale ||
				param == GetScriptVariable ||
				param == GetSecondsPassed ||
				param == GetShouldAttack ||
				param == GetSitting ||
				param == GetSleeping ||
				param == GetStage ||
				param == GetStageDone ||
				param == GetStartingAngle ||
				param == GetStartingPos ||
				param == GetTalkedToPC ||
				param == GetTalkedToPCParam ||
				param == GetTimeDead ||
				param == GetTotalPersuasionNumber ||
				param == GetTrespassWarningLevel ||
				param == GetUnconscious ||
				param == GetUsedItemActivate ||
				param == GetUsedItemLevel ||
				param == GetVampire ||
				param == GetWalkSpeed ||
				param == GetWeaponAnimType ||
				param == GetWeaponSkillType ||
				param == GetWindSpeed ||
				param == HasFlames ||
				param == HasMagicEffect ||
				param == HasVampireFed ||
				param == IsActor ||
				param == IsActorAVictim ||
				param == IsActorEvil ||
				param == IsActorUsingATorch ||
				param == IsCellOwner ||
				param == IsCloudy ||
				param == IsContinuingPackagePCNear ||
				param == IsCurrentFurnitureObj ||
				param == IsCurrentFurnitureRef ||
				param == IsEssential ||
				param == IsFacingUp ||
				param == IsGuard ||
				param == IsHorseStolen ||
				param == IsIdlePlaying ||
				param == IsInCombat ||
				param == IsInDangerousWater ||
				param == IsInInterior ||
				param == IsInMyOwnedCell ||
				param == IsLeftUp ||
				param == IsOwner ||
				param == IsPCAMurderer ||
				param == IsPCSleeping ||
				param == IsPlayerInJail ||
				param == IsPlayerMovingIntoNewSpace ||
				param == IsPlayersLastRiddenHorse ||
				param == IsPleasant ||
				param == IsRaining ||
				param == IsRidingHorse ||
				param == IsRunning ||
				param == IsShieldOut ||
				param == IsSneaking ||
				param == IsSnowing ||
				param == IsSpellTarget ||
				param == IsSwimming ||
				param == IsTalking ||
				param == IsTimePassing ||
				param == IsTorchOut ||
				param == IsTrespassing ||
				param == IsTurnArrest ||
				param == IsWaiting ||
				param == IsWeaponOut ||
				param == IsXBox ||
				param == IsYielding ||
				param == MenuMode ||
				param == SameFaction ||
				param == SameFactionAsPC ||
				param == SameRace ||
				param == SameRaceAsPC ||
				param == SameSex ||
				param == SameSexAsPC ||
				param == WhichServiceMenu);
	}
}


Download release 15.2 from https://www.nexusmods.com/oblivion/mods/8665

-------------------------
TES4Gecko - Version 15.2
-------------------------

Created by TeamGecko
 - New development and Original TES4 Plugin Utility code: ScripterRon (Ron 
Hoffman)
 - Original Gecko plugin splitter and compare code: KomodoDave (N David Brown)
 - Random Guy with Code Access: Steve Carrow
 - Gecko project leader: dev_akm (Aubrey K McAuley)

Credits: For Version 15, Steve would like to thank:
 - ScripterRon and dev_akm for the use of their code and the answering of my 
   stupid questions,
 - The guys at Silgrad Tower (http://www.silgrad.com/wbb2/portal.php),
   especially sandor. ST is a pretty demanding mod to work with and the guys there
   were invaluable with their suggestions, encouragement and patience while testing.
 - bg2408 at BSF for suggestions and observations incorporated into version 15.1.

TES4Gecko Version 13 represents ScripterRon's return to working on 
TES4Gecko project, and the results are dramatic. TES4Gecko Version 12 was 
created using the source code from ScripterRon's wonderful TES4 Plugin Utility 
(Version 11.1), which he graciously donated to the project. The vast majority of 
the functionality here was created by ScripterRon -- TeamGecko merely attempted 
to expand and enhance his amazing work.


Overview
----------------------------------

TES4Gecko works with The Elder Scrolls IV: Oblivion plugin and master files 
(*.esp/*.esm). It is designed to let mod-creators manipulate plugin and master 
files in ways that Bethesda either did not imagine or decided to leave out of 
the official Construction Set.

TES4Gecko has a wide variety of useful functions, such as merging two or more 
plugins into a single plugin or removing data that doesn't need to be in a 
plugin (accidental changes).  It can convert a plugin file to a master file or a 
master file to a plugin file, move worldspaces, generate silent dialog MP3s, and 
a ton of other useful stuff. Selected plugin records can be removed from a 
plugin or copied to another plugin. It will also split a single plugin file into 
master/plugin components and compare two plugins, highlighting the differences 
between them.

Please see the Available Functions section below for critical details and 
warnings about how individual functions work.

Discussion of TES4Gecko is on the Bethesda Elder Scrolls Construction Set forum 
(http://www.bethsoft.com/bgsforums/). Search for TES4Gecko if you don't see the 
thread right away.


New Features and Changes
----------------------------------

Version 15.2 adds or fixes the following:
[*]Added quest reference (QSTI) and landscape texture (LTEX) search.
- Added abilty to dump all dialogue for a plugin.
- Slightly better memory management, which should allow for using the same instance of Gecko more times before having to restart.
- Added factions to lip-synch plugin generation; I'll post a HOWTO after I release this one.
- Now silence file gen algorithm is 3 words per second, not 4. Also added additional voice files for 16- through 20-second lengths.
- Added the ability to set the starting form ID for the merge.
- Added the option to "manage" the merge to master. If merge is not managed, default behavior as in v14 and v15.0 takes place.
- Fixed a bug that Vagrant0 found that would throw an exception in managed merging if an exterior cell is in more than 5 regions.
- Fixed an error in cleaning reported by RMWChaos.
- Added more subrecord types for viewing.
- Finally (I think) fixed the merging of deleted INFOs.
- Added a "remove extraneous quest references" feature for porting dialogue quests to different masters.

Version 15.1 adds or revamps the following:
- Now the music type of all exterior cells can be changed to one common type (Default,
  Public or Dungeon). Apparently StreamMusic operates more consistently if the music 
  type for the cell is Public.
- Thanks to the observations of bg2408, the generation of silence files and the 
  production of the LIP ESPs have been revamped. Now all plugins in the master list are
  loaded to determine if there are races or NPCs that have dialogue added or modified in 
  the current plugin. For the LIP ESP generation, NPCs used in the dialogue have been added
  to the ESP and also conditions relating to NPC or race are now kept, which results in far
  less editing of conditions in the LIP ESP. For a brief discussion on LIP ESPs, please see
  the dev thread.
- Also at bg2408's suggestion, generic LIP files have been added to the silence file
  generation so that lips are moving as the dialogue captions are seen.

***
Version 15 came about mostly because Steve wanted TES4Gecko to do stuff that it
didn't do and that nobody else wanted. Since Steve on occasion gets paid to write
Java code, he asked if he could try his hand at it. ScripterRon and dev_akm
graciously gave both their consent and the then-current code base. :) Since then
Steve has been beavering away in the decrepit and ill-lit back rooms of Silgrad
Tower, working with the advice, requests and encouragement of the rest of the
Silgrad Core, in particular sandor, our Master of the Master File. :D Steve started
out with his list of wants, which were mostly related to quest cloning and working
with dialogue. As time went on, more requests were received relating to the
maintenance of a very large landmass mod as the "stone-souping" of this version
continued. Now we feel it's ready to unleash upon the world. :D Here is an
incomplete list of features added in this version:
 - The ability to generate reports on objects modified within a certain plugin that
   are defined in master files to detect contamination.
 - The ability to dump quest or NPC dialogue into a text file, or to modify that
   text file and put the result back into the plugin.
 - The cloning of dialogue quests by selecting a quest and associated topics,
   copying them to the clipboard, renumbering the form IDs and renaming the quest.
 - The ability to copy quests and dialogues as shown above, and then "boiling down"
   the clipboard plugin to the minimal size needed for loading in CS1.0 for LIP
   generation.
 - Now the clipboard can be saved to a new file and then cleared to allow for
   further copying and saving. Also saving is now disabled unless something has
   actually changed.
 - Generate reports for a plugin of objects introduced by that plugin.
 - Searching has been greatly extended. Searches can now be done of form ID, editor
   ID, XY location for cells, base reference ID, script text and response text among
   others. String searches can use the asterisk as it is used in Windows, e.g. *bat*
   will match "bat", "abattoir" and "Black Sabbath's Greatest Singer: Ozzy or Dio?"
 - Generation of silence files has been revamped. Rather than one 10-sec silence
   files, there are now 15 more, from 1- to 15-sec with a basic word count done for
   each response to roughly guess the correct length; no more waiting for ten
   seconds after "Hello".
 - References with a common base ID can now be removed or changed en masse at the
   cell, region, world space or plugin level.
 - Now when merging, there is the option to include or exclude sets of regions; very
   useful with landscaping claims.
 - Displaying subrecords as their actual type.
 - Fixed the VWD merging bug (missing interiors).
 - Fixed the cell deletion bug.
 - Fixed the deleted topic merging bug.

Incorporated into this release is an extensive right-click system on the tree nodes;
this is where most of the new functionality resides. Although thoroughly tested, all
users should still back up their plugins while working with this version. Public
release has a way of uncovering what even the best beta testers miss.

***
Hot on the heels of the last release, Version 14 mostly adds several important
worldspace fixes. It now adds the exterior cell X,Y coordinates to the tree node
description, assigns interior cell block/subblock based on the merged form ID for
the cell, and changes the Split Plugins function to move visible-when-distant groups
to the plugin.

***
TES4Gecko Version 13 adds a ton of new features, drastically revamps the code 
base, and fixes a bunch of bugs. Here's a few of the biggest new features.

The Compare Plugins function has been redesigned to improve performance. It now 
allows comparisons of very large files, such as Oblivion.esm, which in turn made 
it possible to add a new "Clean Plugin" function to remove any entries from a 
plugin that do not actually differ from Oblivion.esm (accidental changes). The 
'Toggle Ignore' and 'Copy to Clipboard' functions are now available on the 
Compare Plugins screen. The "Split Plugin" function  now has an option to create 
an independent ESM/ESP pair (where the ESM has no dependency on the ESP). The 
Display/Copy function now displays the plugin master list with the associated 
modindex for records tied to that master, supports both FormID and EditorID 
search functions, and provides automatic highlighting of master records. The 
master list order can now be changed, Worldspaces can be moved into the 
Oblivion.esm modindex (00) to avoid vanishing landscape problems, and BOOK 
subrecords are merged.

See the version history near the end of this document for a complete list of 
changes and fixes.


Installation
----------------------------------

To install this utility, place the TES4Gecko.jar and the TES4Gecko-Silence MP3 and
LIP files into a directory of your choice.  To run the utility, create a program 
shortcut and specify "javaw -Xmx1024m -jar <install-directory>\TES4Gecko.jar" as 
the program to run, where <install-directory> should be replaced with the 
directory where you extracted the jar file. If the path for java isn't set on 
your system, you will need to specify the full path to javaw.

A sample program shortcut is included that specifies \Tmp as the install 
directory.  The -Xmx1024m argument specifies the maximum heap size in megabytes 
(the example specifies a heap of 1024Mb, or 1Gb).  You can increase the size if 
you run out of space merging very large plugins.  Note that Windows will start 
swapping if the Java heap size exceeds the amount of available storage and this 
will significantly impact performance.

The Sun Java 1.5 runtime is required.  You can download JRE 1.5 from 
http://java.com/download/index.jsp.  If you are unsure what version of Java is 
installed on your system, open a command prompt window and enter "java -
version".

=========================================================================
=========================================================================


Available functions:


Apply Patch
-----------

This function will recreate a modified plugin file by applying a patch file to a 
plugin file.  The patch file must have been created by the Create Patch function 
and the plugin file must be the base plugin file that was used to create the 
patch.

The 'Merge Plugins' function should be used if you want to combine two different 
plugin files and the 'Merge to Master' function should be used if you want to 
merge a plugin into a master file.


Clean Plugin
------------

This function will remove records from the plugin that are the same as records 
in the master.  Plugin records that modify or delete master records will not be 
removed.


Compare Plugins
---------------

This function will let you browse the record structures of two plugins side-by-
side.  Differences between the two plugins will be highlighted. This will let 
you easily see what changes have been made in a new version of a plugin.  You 
can copy records from either plugin to the clipboard plugin.  You can also 
delete records in either plugin.  Refer to the "Display Plugin" function for 
more information on copying and deleting plugin records.

Note that records copied from the plugins are not merged into the clipboard - 
they are copied as-is.  This means that a record will not be copied if the 
clipboard already contains a record with the same form ID.


Convert to Master
-----------------

This function will convert a plugin file (*.esp) to a master file (*.esm).  The 
plugin file will not be deleted.  The output file will be overwritten if it 
exists.


Convert to Plugin
-----------------

This function will convert a master file (*.esm) to a plugin file (*.esp).  The 
master file will not be deleted.  The output file will be overwritten if it 
exists.


Create Patch
------------

This function will create a patch file by comparing a modified plugin file to 
the original plugin file.  The master list must be the same for both the 
original plugin and the modified plugin.  The patch file will contain just those 
items that were added, changed or deleted.  The patch file name will be the same 
as the original plugin file name but with an extension of ".esu" instead of 
".esp".  The modified plugin file can then be recreated by applying the patch 
file to the original plugin file using the Apply Patch function.  The 
Display/Copy function can be used to display the contents of the patch file.

A record is considered to be changed if the subrecord data is not identical.  
Since the Construction Set does not always write out the subrecord data in the 
same order when saving a plugin and sometimes writes random data in unused 
subrecord fields, this can cause records to be treated as changed even though no 
changes were made.


Display/Copy
------------

This function will display the records in a plugin.  Records that modify a 
master record will be highlighted to make it easier to distinguish between local 
records and shared records.

The "Ignore" flag can be set or reset to cause a record to be ignored when 
saving the plugin.  The record is physically removed from the plugin when the 
plugin is saved.  The "Ignore" flag for all records in a group will be toggled 
when a group is selected.  An attempt to set the "Ignore" flag for a subrecord 
will be ignored since subrecords do not have an "Ignore" flag.  However, 
subrecords can be deleted and will be removed from the record.

Records can be copied to a clipboard plugin called "Gecko Clipboard.esp".  An 
existing clipboard plugin will be overwritten each time the clipboard is saved.  
The clipboard plugin can be merged with another plugin or loaded by the 
Construction Set.  Optionally, any records referenced by the copied records will 
also be copied to the clipboard plugin.

The "Copy" and "Toggle Ignore" buttons work on the selected rows in the plugin 
tree display.  Multiple records can be selected using the normal Windows 
conventions.  Hold down the SHIFT key to select all rows between the previous 
selection and the new row.  Hold down the CTRL key to add just the new row to 
the selection.

You can search for a record based on:
 - editor ID [formID],
 - form ID [formID],
 - item name,
 - XY location for cells [integer, integer],
 - ownership [formID],
 - base reference ID [formID],
 - script text,
 - response text 
All searches not otherwise specified use regular expression while form ID searches
use hexadecimal form IDs.  If a match is found, the tree node for the matching 
record will be selected.

An editor ID search expression can be the complete editor ID such as 
"OOOBraidedCuirass".  Or it can contain regular expression constructs to specify 
the match pattern.  The matching is case-insensitive because the editor ID is 
not case senstive, thus "OOO" will match "ooo".  The following are some useful 
regular expression constructs.  Refer to 
http://java.sun.com/javase/6/docs/api/java/util/regex/Pattern.html for more 
information on regular expression patterns.  A special character can be 
specified as part of the search string by escaping it.  For example, if you want 
to find an editor ID containing a period, specify "\." instead of "." in the 
search expression since an unescaped period will match any character.

. = Any character
X* = Zero or more occurrences of X
X? = Zero or one occurrences of X
X+ = One or more occurrences of X
X{n} = n occurrences of X

For example, to search for any record starting with "OOO", the search term would 
be "OOO.*".  Note that the records are searched in the order they appear in the 
plugin and not in the order they are displayed.  Use the "Find Next" button to 
continue the search from the last match. Also note that "*" may be used as in 
indows; see that *bat* example earlier in this file. It's possible that this
alteRnate usage may collide with the POSIX usage in weird cases. If this happens,
please post on the release thread at BSF.

There are many changes to this function not listed here. Please check either the
BSF release thread or the ST dev thread referenced in the release thread OP for
more details.


Edit Description
----------------

This function will edit the plugin version, creator and summary fields.  The 
version can be set to 0.8 (base Oblivion) or 1.0 (Shivering Isles expansion).


Edit Master List
----------------

This function will edit the master list for a plugin.  A master entry can be 
renamed or removed.  An error will be posted if the master file is still 
referenced by an item in the plugin.  The user can continue and forcibly remove 
the master entry, in which case all records referencing the master will also be 
removed.  The user can also change the order of the master list entries.  This 
doesn't change the order in which master files are loaded, but it can be used to 
synchronize form ID values when comparing two plugins (two items are considered 
to be different if they both reference the same master but the masters are in 
different positions in their respective master lists).


Generate Slient Voice Files
------------------

This function will create response silent voice files for each dialog entry in the 
plugin.  Any existing response files for the plugin will be deleted.  One of the 
TES4Gecko silence. MP files is copied to the correct file names; which files is copied
depends on the word count of the entry.  Races and genders are filtered based on both
quest and response conditions; any logic errors biased toward generation rather than 
exclusion. If no conditions are found, two files (male and female) will be created for
each playable race.

There are 16 silence files: the TES4Gecko-Silence.mp3 file provides 8 seconds of silence,
while 15 others provide from 1 to 15 seconds of silence..  You can replace 
this file with another one of your choice to change the length.  The files must 
be recorded as a mono file with a 44KHz sample rate and a 64Kbps bit rate.


Merge Plugins
-------------

This function will merge two or more plugin files and create a new merged plugin 
file.  The output file will be overwritten if it exists.  The files will be 
merged in the order specified by the assigned priority values.  Priority 1 files 
will be merged before priority 2 files, priority 2 files will be merged before 
priority 3 files, etc.  Files with the same priority will be merged based on the 
file modification timestamps.  A file will not be merged if it has not been 
assigned a priority or if the priority value is not valid (priority values must 
be greater than zero).  Specify priority values by clicking on a row in the 
plugin table and then typing in the desired number.

If you check the 'Delete last master record conflict' box, the second 
conflicting record will be removed from the merged file.  Otherwise, the first 
conflicting record will be removed.  Two records conflict if they both modify 
the same object in the master file.

If you check the 'Edit master leveled list conflicts' box, leveled list 
conflicts will be displayed and you will be able to manually merge the leveled 
list items.  Items can be copied from the plugin leveled list to the merged 
leveled list, items can be deleted from the merged leveled list, and the level 
and count values for items in the merged leveled list can be changed.  If this 
box is not checked, leveled list conflicts will be resolved by adding the unique 
entries from the second leveled list to the first leveled list (an entry is 
considered to be a duplicate of an existing entry if it has the same level 
number and item form ID).

Form ID and Editor ID conflicts are resolved by assigning new values.  For this 
reason, an existing save game may be invalidated if a new merged plugin file is 
created and a conflict exists that did not exist in an earlier merged plugin 
file.  The Form ID and Editor ID will not be changed if there are no conflicts 
with the existing values.

IMPORTANT NOTE: TES4Gecko does not contain support for processing scripts.  If 
an Editor ID conflict occurs and you choose to rename the Editor ID, then you 
must check the scripts in the merged plugin (using the Bethesda Construction 
Set) and manually change the Editor ID from the old value to the new value.  The 
compiled scripts will work properly after the merge without any changes but the 
scripts will fail if they are recompiled without first updating the Editor ID 
values in the script source.

If two plugins modify the same record in a master file, the conflict is resolved 
by selecting those subrecords that are not the same as the corresponding master 
subrecords.  If both plugins modify the same subrecord, the subrecord from the 
first plugin or the second plugin will be used depending on which option was 
selected in the merge dialog.  Lists are handled by adding unique list items 
from the second plugin to the list from the first plugin.

Voice response files for each plugin are merged.  Landscape files are not merged 
since there is no way to tell if a particular landscape record is used by a 
given plugin.  Instead, the Move Worldspaces function should be used to move all 
worldspaces in a plugin to the master index before attempting to merge the 
plugin.


Merge to Master
---------------

This function will merge a plugin file into an existing master file.  Items in 
the master file which are modified by the plugin will be replaced by the plugin 
definitions.  This allows master changes to be tested first in a plugin before 
committing the changes to the master.

Voice response files are merged.  Landscape files are not merged since there is 
no way to tell if a particular landscape record is used by a given plugin.  
Instead, the Move Worldspaces function should be used to move all worldspaces in 
a plugin to the master index before attempting to merge the plugin.

If there are exterior cells in the file to be merged, the user is given the option
to either include or exclude cells based on assigned region.


Move Worldspaces
----------------

This function will move WRLD and visible-when-distant STAT records from the 
plugin index to the 00 index.  Distant land files will be renamed if they are 
found in Meshes\Landscape\LOD or Textures\LandscapeLOD\Generated.  Distant 
statics records will be updated if they are found in DistantLOD.  The DistantLOD 
files must be generated before moving worldspaces if a plugin contains visible-
when-distant references to statics defined in a master other than the first 
master.  The DistantLOD files are optional if all of the visible-when-distant 
references are to statics defined in the plugin or in the first master.

Placeholders can be inserted to mark moved worldspaces and statics.  These are 
scrolls with the original form ID and an editor ID consisting of "TES4Gecko" 
followed by the 8-digit relocated form ID.  Placeholders are required if you are 
moving worldspaces in a master and you have one or more dependent plugins that 
reference the master.  Otherwise, inserting placeholders is up to you.

If you want to relocate worldspace in a master with one or more plugins, you 
must first relocate the worldspaces in the master (be sure to insert 
placeholders).  Then relocate worldspaces for each of the dependent plugins 
(inserting placeholders at this point is optional).

If a plugin contains a visible-when-distant reference to a static defined in a 
master but the master version of the static has not been relocated, a cloned 
static definition will be added to the plugin and this definition will be 
relocated.  If the plugin already contains the static definition, the plugin 
definition will be relocated, which means the plugin definition will no longer 
override the master definition for the static.


Set Directory
-------------

This function set the directory containing the plugin files.  This is usually 
<Oblivion-install-directory>\Oblivion\Data but can be set to any directory.  The 
directory must already exist.  Note that some functions require access to the 
files in the master list for a plugin.  If you set the directory to a different 
location, you must copy any required master files to this directory.


Split Plugin
---------------

This function will split a plugin file into an ESM/ESP pair with the ESP 
dependent on the ESM. The resulting split master will be "clean", containing 
only new content that did not already exist in another master. The resulting 
plugin will contain only changes to other masters. Doors linking new cells to 
existing cells will be split correctly to avoid contaminating the master with 
changes (which can cause landscape problems). New content added to existing 
Cells (interiors) and Worldspaces will be correctly kept in the ESP because they 
are in effect changing another master.

In addition, visible-when-distant references will be placed in the split plugin.  
This is necessary because Oblivion has a problem with missing land when a non-00 
master contains visible-when-distant references.

A dependent ESM contains all new items from the original plugin while the ESP 
contains all modifications to another master, including references added to a 
cell defined in another master.  This will cause the ESM to require the ESP if 
any of the items in the ESM (such as packages or scripts) refer to any of these 
references.  Loading the ESM without the ESP will result in errors because these 
references will not be defined.  Items defined in the ESP will use the ESM 
module index instead of the ESP module index so that the master can reference 
them.

An independent ESM contains all new items from the original plugin with the 
exception of items that reference anything still defined in the ESP.  For 
example, if a new script references a character added to a cell defined by 
another master, the script will remain in the ESP and will not be moved to the 
ESM.  The result is the ESM can be used without the ESP.  Items defined in the 
ESP will use the ESP module index since they are not referenced by any items in 
the ESM.

The resulting split files will be saved automatically with an OUTPUT_ prefix, so 
splitting mymod.esp will create an OUTPUT_mymod.esm and an OUTPUT_mymod.esp. 
You'll need to remove this prefix before using the new split pair.

Used in conjunction with Merge to Master and Compare Plugins (see below), the 
Split Plugin feature should help to facilitate large team projects. Once an 
initial split is made and the files are distributed to team members, they can 
keep adding new material to the ESP or can create new plugins based on the 
master. At the end of each development phase (an arbitrary point you agree on 
with other team members), all the new changes and work should be merged back 
into the master and a new set of resulting baseline files distributed back out 
to the team. When conflicting changes occur during this process, you can use the 
Compare Plugins feature to examine the conflicts and use Merge Plugins to 
resolve them.


=========================================================================
=========================================================================


Record merge rules for master object conflicts
----------------------------------------------

ACTI - Activator

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


ALCH - Potion

The initial effects merged list contains all of the effects from the first 
occurrence of the potion in a plugin.  Unique effects from subsequents 
occurrences of the potion are then added to the merged list.  An effect is 
unique if it has a different effect name/subtype.  For a script effect, the 
effect is unique if it uses a different script.

The potion value will be set to the highest value encountered.  Auto-calculate 
will be turned off if it is off for any occurrence of the potion in a plugin.


AMMO - Ammunition

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


ANIO - Animated object

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


APPA - Apparatus

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


ARMO - Armor

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


BOOK - Book

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


BSGN - Birthsign

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

The initial merged spell list contains all of the spells from the first 
occurrence of the birthsign in a plugin.  Unique spells from subsequent plugins 
are then added to the merged list.


CELL - Cell

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


CLAS - Class

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


CLOT - Clothing

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


CLMT - Climate

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


CONT - Container

Attribute subrecords are merge on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

The initial merged container contains all of the items from the first occurrence 
of the container in a plugin.  Unique items from subsequent containers are then 
added to the merged container.


CREA - Creature

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

The initial faction list contains all of the factions from the first occurrence 
of the creature in a plugin.  Unique factions from subsequent plugins are then 
added to the merged list.

The initial inventory list contains all of the items from the first occurrence 
of the creature in a plugin.  Unique items from subsequent plugins are then 
added to the merged list.

The initial merged spell list contains all of the spells from the first 
occurrence of the creature in a plugin.  Unique spells from subsequent plugins 
are then added to the merged list.

The initial merged package list contains all of the AI packages from the first 
occurrence of the creature in a plugin.  Unique packages from subsequent plugins 
are then added to the merged list.


CSTY - Combat style

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


DIAL - Dialog

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


DOOR - Door

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


EFSH - Effect shaders

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


ENCH - Enchantment

The initial effects merged list contains all of the effects from the first 
occurrence of the enchantment in a plugin.  Unique effects from subsequents 
occurrences of the enchantment are then added to the merged list.  An effect is 
unique if it has a different effect name/subtype.  For a script effect, the 
effect is unique if it uses a different script.

The total charge and cost for the enchantment will be set to the highest values 
encountered.  Auto-calculate will be turned off if it is off for any occurrence 
of the enchantment in a plugin.


EYES - Eyes

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


FACT - Faction

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

Faction ranks are merged as a single entity based on which plugin has a 
different number of ranks than the master.  If both plugins have a different 
number of ranks, then the faction ranks are chosen based on whether the first or 
the last conflict should be deleted.

The initial interfaction relations merged list contains all of the factions from 
the first occurrence of the faction in a plugin.  Unique interfaction relations 
from subsequent occurrences of the faction are then added to the merged list.


FLOR - Flora

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


FURN - Furniture

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


GRAS - Grass

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


GLOB - Global Variable

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


GMST - Game Setting

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be delete.


HAIR - Hair

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


IDLE - Idle animation

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


INGR - Ingredient

The initial effects merged list contains all of the effects from the first 
occurrence of the ingredient in a plugin.  Unique effects from subsequents 
occurrences of the ingredient are then added to the merged list.  An effect is 
unique if it has a different effect name/subtype.  For a script effect, the 
effect is unique if it uses a different script.

The ingredient value will be set to the highest value encountered.  Auto-
calculate will be turned off if it is off for any occurrence of the ingredient 
in a plugin.


KEYM - Key

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


LIGH - Light

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


LSCR - Load screen

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


LVLC - Leveled Creature, LVLI - Leveled Item, LVSP - Leveled Spell

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

The initial merged list contains all of the list items from the first occurrence 
of the leveled list in a plugin.  Unique list items from subsequent leveled 
lists are then added to the merged list.


LTEX - Land texture

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


MGEF - Magic effects

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


MISC - Miscellaneous item

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


NPC - Non-player character

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

The initial faction list contains all of the factions from the first occurrence 
of the NPC in a plugin.  Unique factions from subsequent plugins are then added 
to the merged list.

The initial inventory list contains all of the items from the first occurrence 
of the NPC in a plugin.  Unique items from subsequent plugins are then added to 
the merged list.

The initial merged spell list contains all of the spells from the first 
occurrence of the NPC in a plugin.  Unique spells from subsequent plugins are 
then added to the merged list.

The initial merged package list contains all of the AI packages from the first 
occurrence of the NPC in a plugin.  Unique packages from subsequent plugins are 
then added to the merged list.


PACK - Package

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


QUST - Quest

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


RACE - Race

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.

Racial abilities and reactions are merged by adding unique entries from each 
plugin to the current merged list.

Face and body meshes/textures are chosen based on which ones are different from 
the corresponding master value.  If both plugins are different from the master, 
then the mesh/texture is chosen based on whether the first or the last conflict 
should be deleted.


REGN - Region

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SBSP - Subspace

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SCPT - Script

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SGST - Sigil stone

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SKIL - Skill

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


SLGM - Soul gem

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SOUN - Sound

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


SPEL - Spell

The initial effects merged list contains all of the effects from the first 
occurrence of the spell in a plugin.  Unique effects from subsequents 
occurrences of the spell are then added to the merged list.  An effect is unique 
if it has a different effect name/subtype.  For a script effect, the effect is 
unique if it uses a different script.

The total cost and skill level for the spell will be set to the highest values 
encountered.  Auto-calculate will be turned off if it is off for any occurrence 
of the spell in a plugin.


STAT - Static

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


TREE - Tree

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


WATR - Water

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


WEAP - Weapon

Attribute subrecords are merged on a one-by-one basis.  A plugin subrecord is 
chosen if it is not the same as the corresponding subrecord in the master.  If 
both plugin subrecords are different from the master subrecord, then the 
subrecord is chosen based on whether the first or the last conflict should be 
deleted.


WRLD - World space

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


WTHR - Weather

The entire record is merged.  A plugin record is chosen if it is not the same as 
the corresponding record in the master.  If both plugin records are different 
from the master record, then the record is chosen based on whether the first or 
the last conflict should be deleted.


=========================================================================
=========================================================================


Version 1: TES4Plugin
---------------------

Initial release.


Version 2: TES4Plugin
---------------------

Top-level records marked as 'Ignore' will not be copied to the merged plugin.  
Records in sub-groups will still be copied even if marked as 'Ignore' until I 
know more about what happens when you omit records found in a sub-group.

Compressed records are expanded as needed instead of expanding all records when 
loading the plugin.  This should significantly reduce the storage requirements 
when merging large plugins.

WRLD and CELL records in a master can be modified by multiple plugins.  An error 
will still be posted if any other record type is modified by more than one 
plugin.  Research is continuing to determine what can safely be modified without 
breaking the merged plugin.


Version 3: TES4Plugin
---------------------

Add the capability to convert a master file to a plugin file.

Add the capability to edit the plugin description.

Create the application preferences data directory if it doesn't exist.

The XCHG subrecord is now recognized.

References in additional subrecords are now adjusted when merging plugins.

The CS ignores underscores in an Editor ID, so conflicts will now be resolved by 
appending 'X' to the Editor ID.


Version 4: TES4Plugin
---------------------

Add the Display Records function.

All ignored records are deleted during a merge.

Leveled lists (creature, item, spell) are now merged.


Version 5: TES4Plugin
---------------------

Add the capability to display the complete subrecord data in a popup window.

Conditions are now processed properly in quest (QUST), package (PACK) and dialog 
(INFO) records.

Top-level groups now have descriptive names instead of the 4-character record 
type.


Version 6: TES4Plugin
---------------------

Deleted records are indicated in the record display tree view.

Eyes (ENAM) and Hair (HNAM) subrecords are now processed properly for Races.

Conflicts will be removed from the merged file.

Duplicate items will be removed from the merged file.

The plugin merge list is now sorted by the last modified date.

The Set Directory function now uses a file chooser dialog.

Leveled list conflicts can be edited during the merge.


Version 7: TES4Plugin
---------------------

Both references in the race voice override subrecord (VNAM) will be updated 
during a merge.

Add the capability to generate silent dialog response files.

Add the capability to merge a plugin file into a master file.

Merge the associated voice files when merging plugins.

The merge order is now specified explicitly by the user instead of being 
determined by the file modification dates.

Change the Display Records function to display both master files and plugin 
files.

WRLD, CELL and DIAL subgroups will be merged instead of being duplicated.


Version 8: TES4Plugin
---------------------

Fix problem with form ID conflicts after a master record conflict.

Duplicate references not removed when merging subgroup.

Group label not updated when the label is a form ID (WRLD/CELL/DIAL).

Update references in XTEL and XLOC subrecords.

Replace NL with CRLF in plugin summary.


Version 9: TES4Plugin
---------------------

Use a case-insensitive compare when checking for Editor ID conflicts.

When displaying a plugin, sort the records for a top-level group based on the 
Editor ID.

Correctly handle ENAM subrecord for SKIL record.

Correctly handle MGEF record.

Correctly handle SCIT subrecord for ENCH and SPEL records.

Correctly handle adding a tail to an existing race.

Add spill file support.

Toggle the "Ignore" flag for all records in a group when "Toggle Ignore" is 
selected for a group.

Accept Version 1.0 plugin files.  New plugin files are still created as Version 
0.8.

Don't save ignored records and empty groups.


Version 10: TES4Plugin
----------------------

Add subrecord merge support.

Copy records referenced by the selected records for the Display/Copy function.

Don't build the subrecord nodes for the Display/Copy function until they are 
needed.

Correctly handle the same CELL record in different groups.

Correctly handle XCLR subrecord for CELL records.

Allow a plugin to rename an object in a master file.


Version 11: TES4Plugin
----------------------

Allow any record or group to be copied.

Update GetDistance function references.

Handle escape sequences (%xx) in file URL paths.

Initialize the merged plugin description to the description from the first 
plugin in the merge.

Add confirmation dialog if the output file already exists.

Add function to edit the plugin master list.

Add functions to create and apply plugin patches.


Version 12: TES4Gecko
---------------------

Renamed the project TES4Gecko.

Added Plugin Splitter to create an ESM/ESP pair from a source plugin.

Added Compare Plugins to view two plugins and highlight differences between 
them.

Added "Yes to All" button on Merge to Master.

Fixed other minor interface issues.

Revised documentation.


Version 13: TES4Gecko
---------------------

Check master file size as well as master timestamp to decide if the index file 
should be rebuilt.

Set the initial directory to the Oblivion\Data directory.

Preserve the plugin version whenever the plugin is modified.

Add progress bar when loading/saving/updating plugins.

Add support for patching master files as well as plugin files.

Fix problem with "\r\r\n" sequences when the edited description already contains 
"\r\n".

Fix reversed 'Yes' and 'No' options in merge confirmation dialog.

Use file chooser when renaming a master list entry, allow selection of master or 
plugin file.

Allow a master list entry to be deleted even if the plugin contains references 
to the master.  The records containing the references will be deleted.

Fix bug where removing a master did not update the plugin record form ID values.

Allow the master list order to be changed.  While this doesn't affect the order 
in which master files are loaded, it can be used to synchronize form ID values 
for the Compare function.

Redesign the Compare Plugins function to improve the performance.

Add 'Toggle Ignore' and 'Copy to Clipboard' functions to the Compare Plugins 
dialog.

Add Clean Plugin function.

Add Move Worldspaces function.

Form ID references in LNAM subrecords were not being updated.

Subgroup CELL record references were not getting updated, causing CELL 
references to be lost or moved.

Allow the plugin version to be changed by the Edit Description function.

Correctly merge GMST and MGEF records with the same editor ID but different form 
ID values.

Add the ability to split a plugin into an independent ESM/ESP pair.

Update the Display/Copy function to display the plugin master list and the 
associated index values.

Add search option to the Display/Copy function.

Highlight master records for the Display/Copy function.

Merge BOOK subrecords.

Fix dialog chains when merging/copying info records.

Form ID references in PGRL subrecords were not being updated.

Add the ability to copy an item without also copying its references.

Add the ability to delete subrecords.

Insert the 'visible distant children' subgroup before the 'temporary children' 
subgroup.

Form ID reference in SNAM subrecord of WTHR record were not being updated.

Form ID references in WLST subrecord of CLMT record were not being updated.

Form ID references in ATXT subrecord of LAND record were not being updated.

The "Move to plugin index" option for the Move Worldspaces function has been 
removed because it does not work for plugins referencing relocated masters.

Visible-when-distant statics defined in a master but referenced in a plugin will 
be cloned in the plugin if they need to be relocated.


Version 14: TES4Gecko
---------------------

Add the exterior cell X,Y coordinates to the tree node description.

Assign interior cell block/subblock based on the merged form ID for the cell.

Change the Split Plugins function to move visible-when-distant groups to the 
plugin.

Form ID references in DATA subrecord of IDLE record were not being updated.

Creature sounds merged incorrectly.

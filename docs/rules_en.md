# Rule List

## PistonBlockChunkLoader
If enabled, when this piston/sticky piston head generates a piston head push/pull event, a load ticket of type "piston_block" is added to the chunk where the piston head block is located at the game tick that created the push/pull event, with a duration of 60gt (3s).
### In any dimension, a diamond ore can be weakly loaded into a 1x1 lazy chunk above a piston.
### In any dimension, a redstone ore can be weakly loaded into a 1x1 Active chunk above a piston.
### If there is bedrock below the netherworld and a redstone torch above the next block, a 1x1 block can be lazy loaded.
### When there are 3X3 weak loading chunks, the central chunk will become a Active loading chunk
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pistonBlockChunkLoader ture`
* Categories: `REMS` , `Survival`

>This rule can be used as an alternative if you do not want to use the Nether portal Load.

## MergeTNTPro
Merging a large amount of TNT to reduce the lag caused by entities and explosions can significantly reduce mspt

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet mergeTNTPro true`
* Categories: `REMS`, `Feature`, `Survival`,`TNT`

## TeleportToPoiWithoutPortals
Re-added rules for teleporting to portal POIs without portal blocks
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet teleportToPoiWithoutPortals true`
* Categories: `REMS`, `Experimental`

## PearlTickets
#### When MC<1.21.2 can use it
This mod allows ender pearl entities to selectively load chunks that they are about to pass through, so that pearls fired by the pearl cannon will not be lost due to entering unloaded chunks. It can be used instead of the nether portal loading chain in 1.14+.
This mod has a significant performance improvement over the enderPearlChunkLoading function of @gnembon/carpet-extra mod.  
(When enabled in Minecraft>=1.21.2, it can significantly improve the performance of the Pearl Cannon)

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pearlTickets true`
* Categories: `REMS` , `Survival`

**Ported from：**
SunnySlopes's [PearlTickets](https://github.com/SunnySlopes/PearlTickets)

## SoundSuppressionRadius
#### When MC>1.19.4 can use it
Controls the monitoring radius of the sound suppressor. You can enter a positive integer. The default value in the original version is 16 grids.The maximum value cannot exceed 64.
* Default Value:  `false`
* Optional Parameters: `8`,`16`,`32`
* Open Method: `/carpet soundSuppressionRadius true`
* Categories: `REMS` , `Feature`

## Commandsetnoisesuppressor
#### When MC>1.19.4 can use it
Enables /setnoisesuppressor command to place a sound suppressor
* Default Value:  `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet commandsetnoisesuppressor true`
* Categories: `REMS` , `CREATIVE`

## ComparatorIgnoresStateUpdatesFromBelow
#### When MC>=1.20.2 can use it
When this option is turned on, the comparator ignores state updates from below.  
Means that opening the trap gate will not destroy the comparator
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet ComparatorIgnoresStateUpdatesFromBelow true`
* Categories: `REMS` , `Feature`

## PearlPosVelocity
When the ender pearl loading (PearlTickets) is turned on, the pearl will only show the position of the first gt, and the real position and speed of the pearl cannot be checked. After turning this on, it will be displayed on the public screen.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pearlPosVelocity true`
* Categories: `REMS` , `Survival`

## Endstonefram
You can build Endstonefram like 1.16,it can make it work
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet endstonefram true`
* Categories: `REMS` , `Experimental`

## ProjectileRaycastLength
Changes the distance of the Raycast. If set to 0, all chunks will be checked to reach the destination.
This reduces lag for fast travel. In 1.12 this value is 200.

* Default Value: `0`
* Optional Parameters: `0`, `200`
* Open Method: `/carpet ProjectileRaycastLength 200`
* Categories: `REMS` , `Survival`

**Ported from：**[EpsilonSMP](https://github.com/EpsilonSMP/Epsilon-Carpet)

## PortalPearlWarp
You can trigger a supertransmission at certain locations.
Below are the locations of the Hell Gates, all positive or negative.

| Nether portal location (center) | Overworld portal location (center) |
|:-------------------------------:|:----------------------------------:|
|             915,915             |         29999600,29999600          |
|            7324,7324            |          3749942,3749942           |
|           58592,58592           |           468735,468735            |
|          468743,468743          |            58585,58585             |

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet PortalPearlWarp true`
* Categories: `REMS` , `Feature`

## ChestMinecartChunkLoader
A chest minecart can force load a 1x1 chunk for 2 seconds. This is enabled when the chest minecart's name is Load.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet chestMinecartChunkLoader true`
* Categories: `REMS` , `Feature`

## EndGatewayChunkLoader
#### When MC<1.21 can use it
When an entity passes through the End gateway, the target chunk will be loaded for 3 seconds like a nether portal.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet endGatewayChunkLoader true`
* Categories: `REMS` , `Survival`

## ScheduledRandomTickPlants

The planned tick event can trigger the random tick growth behavior of all the following plants, which is used to restore the forced ripening feature of version 1.15.

Cactus, bamboo, chorus flower, sugar cane, kelp, twisting vines, weeping vines

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet scheduledRandomTickPlants true`
* Categories: `REMS` , `Feature`,`Survival`

**Ported from：**[OhMyVanillaMinecraft](https://github.com/hit-mc/OhMyVanillaMinecraft)

## KeepWorldTickUpdate

Minecraft will stop updating entities after 300 ticks of no players in the server's current dimension. This rule will bypass this limitation.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet keepWorldTickUpdate true`
* Categories: `REMS` , `Feature`

## DisableBatCanSpawn
Stop bats from spawning naturally

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet disableBatCanSpawn true`
* Categories: `REMS` , `Feature`

## CactusWrenchSound
Play 'BLOCK_DISPENSER_LAUNCH' sound effect when using the Cactus Wrench.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet cactusWrenchSound true`
* Categories: `REMS` , `Survival` ,`Creative`

## DisablePortalUpdate
Nether portal blocks do not react to block updates.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet disablePortalUpdate true`
* Categories: `REMS` , `Survival` ,`Experimental`

## StringDupeReintroduced
#### When MC>=1.21.2 can use it
Reintroduced the line-stirring feature, and you can continue to use the line-stirring machine through this rule.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet stringDupeReintroduced true`
* Categories: `REMS` , `Survival` ,`Experimental`

## SharedVillagerDiscounts
The discount obtained by players who cure zombie villagers into villagers will be shared by all players.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet sharedVillagerDiscounts true`
* Categories: `REMS` , `Survival`,`Feature`

## SignCommand
The player right-clicks the sign to execute the command on the sign.The sign starts with /  
(only say tick and player are allowed)

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet SignCommand true`
* Categories: `REMS` , `Survival`

## SignAllowedCommands
Set a list of allowed commands on the sign, separated by commas, for example: say, tick, player
* Default Value: `false`
* Optional Parameters:`false`, `say`, `player,tick`, `say,player,tick`
* Open Method: `/carpet SignCommand XXX`
* Categories: `REMS` , `Survival`

## Enderpearlloadchunk
This ender pearl loading is ported from 1.21.2. Very useful.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet enderpearlloadchunk true`
* Categories: `REMS` , `FEATURE`

## Pearltime
This rule controls how many gts the pearl will be destroyed after it exceeds 20m/gt.

* Default Value: `40`
* Optional Parameters: `40`, `0`
* Open Method: `/carpet pearltime true`
* Categories: `REMS` , `FEATURE`


## ItemShadowing
Reintroduced the logic of swapping between inventory slots in 1.16.5.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet itemShadowing true`
* Categories: `REMS` , `Experimental`

**Ported from：**[CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)

## Blockentityreplacement
#### When MC>=1.20.2 can use it
Allows saving and replacing of block entities, used for creating CCE and IAE.

* Default Value: `false`
* Optional Parameters:  `true`, `false`
* Open Method: `/carpet blockentityreplacement true`
* Categories: `REMS` , `ExperimentalL`

**Ported from：**[CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)

## Reloadrefreshirongolem
You can build a heavy iron spawner in the end like in 1.14, this rule will make it work
* Default Value:  `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet reloadrefreshirongolem true`
* Categories: `REMS` , `survival`

## Pre21ThrowableEntityMovement
#### When MC>=1.21.2 can use it
Restored the order of projectile movement from 1.21.2, you can use EnderPearl Cannon like in 1.21.2-
* Default Value: `false`
* Optional Parameters:`true`, `false`
* Open Method: `/carpet pre21ThrowableEntityMovement true`
* Categories: `REMS` , `Feature`

## Fixedpearlloading
#### When MC>=1.21.2 can use it
Fixed an issue where ender pearls would unload at high speeds due to being unable to load the current chunk.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet fixedpearlloading true`
* Categories: `REMS` , `bugfix`

## WanderingTraderNoDisappear
Wandering Trader will no disappear customName is Load
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet wanderingTraderNoDisappear true`
* Categories: `REMS` , `feature`

## Pearlnotloadingchunk
Enderpearl no load any chunk
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pearlnotloadingchunk true`
* Categories: `REMS` , `feature`
* 
## DurableItemShadow
The item Shadow do not disappear after restarting.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet durableItemShadow true`
* Categories: `REMS` , `feature`

## ReintroduceLlamaItemDuplicating
Reintroduce item duping with llama.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet reintroduceLlamaItemDuplicating true`
* Categories: `REMS` , `feature`

## OpInSurvivalCommandBlocks
Allows OPs to edit Command Blocks in Survival mode.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet opInSurvivalCommandBlocks true`
* Categories: `REMS` , `feature`

## IntroduceHighVersionThrowableEntityMovement
Introducing projectile motion logic from version 1.21.2+.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet introduceHighVersionThrowableEntityMovement true`
* Categories: `REMS` , `feature`

## NoSensationPearlLoad
When the speed is greater than 300 and it is the first time, a small number of raycast calls will be made. The calculations are saved and the stuttering is reduced on the next call.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet noSensationPearlLoad true`
* Categories: `REMS` , `feature`

## DisableAIEntitities
Set a list of entities to remove from any AI, separated by commas, for example: zombie,piglin
* Default Value: `false`
* Optional Parameters: `false`, `zombie`, `creeper`, `zombie,skeleton`
* Open Method: `/carpet disableAIEntitities XXX`
* Categories: `REMS` , `feature`

## DisableAiGoals
Select the AI function you want to delete, such as: move, attack, look.
* Default Value:`false`
* Optional Parameters: `false`, `move`, `attack`, `move,attack,shoot`, `all`
* Open Method: `/carpet disableAiGoals XXX`
* Categories: `REMS` , `feature`

## Stridergodie
Strider cannot be generated in Hell difficulty above level 170.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet stridergodie true`
* Categories: `REMS` , `Survival`

## FlushFakePlayerNetworkQueue
Every minute, the backlog of outbound packets in the underlying EmbeddedChannel of the fake player is cleared to free up heap memory.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet flushFakePlayerNetworkQueue true`
* Categories: `REMS` , `Bugfix`

## DispenserSpearCharge
Dispensers can use spears to attack entities in front of them.
#### When MC>=1.21.11 can use it
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet dispenserSpearCharge true`
* Categories: `REMS` , `Experimental`

## AllowTripwirePlatformDeletion
Players can use the tripwire's abnormal state to interrupt the spawn of the End Obsidian spawn platform, thereby deleting the platform.
* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet allowTripwirePlatformDeletion true`
* Categories: `REMS` , `feature`

## CommandBlockWhitelist
The command block whitelist can be enabled or disabled through the backend, preventing certain people with operation privileges from misusing command blocks.
* Open Method: `/cbwhitelist open`
* Close Method: `/cbwhitelist close`
* How to add to the whitelist: `/cbwhitelist add XXX`
* How to remove from the whitelist: `/cbwhitelist remove XXX`

## PetCapture
Use an anvil to rename an Eye of Ender to `Capture Eye` (case-insensitive), then right-click to launch the projectile. Each hit on a valid mob has a 25% chance of capturing it; valid targets include standard hostile mobs, the Wither, and the Ender Dragon, but exclude players, other players' pets, and mobs currently mounting or being ridden. An Eye is consumed upon a miss or failed capture (except in Creative mode). The mod must be installed on both the client and the server.
Upon success, the original mob vanishes, and a special spawn egg is generated in its place. For the first capture, the egg's name appears as `Pet: Official Name` based on Carpet's `language` setting; if the mob has a name tag name, that name is used instead. Subsequent recalls preserve the pet's current name tag name; otherwise, the official name is used. The egg stores the original mob's UUID, owner, health, name, equipment, and other NBT data.
Only the owner can right-click a block to release the pet; sufficient space is required at the location (bosses require extra space). A successful summon consumes the egg (even in Creative mode), while a failure preserves it. Pet eggs cannot be used to modify spawners, spawn baby mobs, or trigger direct mob spawning via dispensers. Owner and "stay" status are saved; disabling the rule prevents capture and release, though already released pets remain loyal.
Pets do not attack proactively and generally follow the owner within the same dimension; the owner can toggle between "follow" and "stay" modes by right-clicking with an empty hand. If the owner sneaks and right-clicks their pet with an empty hand, the pet is recalled with 100% certainty, generating a spawn egg containing the original data. Pets do not cross dimensions or teleport automatically, and some mobs' vanilla movement mechanics may limit their ability to follow.
With `/carpet commandpetOwnerTransfer true` enabled, the owner can transfer pets by executing `/petTransfer <petUUID> <targetPlayer>`. The pet must be released first; transfer cannot be performed directly on an egg held in hand.

* Default Value:`false`
* Optional Parameters: `true`, `false`
* Open Method:`/carpet petCapture true`
* Categories:`REMS`, `Feature`, `Survival`

## PetCaptureLimit
The number of captured pets is calculated independently for each player based on the creature type; for instance, if the limit is set to `3`, a player can simultaneously own 3 zombies, 3 skeletons, etc., rather than being limited to a total of 3 pets across all types. Pet slots are consumed by both pet eggs (including those stored in chests) and currently released pets; releasing or recalling the same pet does not increment the count multiple times. Slots are freed upon a pet's natural death, whereas unloading chunks, logging off, or disabling the rule does not reset the count. Only pets obtained via the capture function are tracked; the number of pets tamed using vanilla mechanics remains unrestricted.
When transferring a captured pet, the recipient's limit for that specific type is checked; upon a successful transfer, the original owner's slot is freed, and the new owner's slot usage increases. Lowering the limit does not delete existing pets; eggs already included in the count can still be deployed, though no new pets exceeding the limit can be added. Setting the limit to `0` disables the restriction, though the records are retained. Destroying or losing an egg does not automatically free up a slot; the pet must be released and then die naturally. Administrator-forced entity deletion does not count as a natural death. In the event of file corruption or read/write failures, the system prevents new registrations or transfers rather than silently resetting the count to bypass the limit.
* Default Value:`0`（不限）
* Optional Parameters:任意非负整数，例如 `0`, `3`, `4`
* Open Method:`/carpet petCaptureLimit 3`
* Categories:`REMS`, `Feature`, `Survival`

## NegativeItemStacks
This re-implements the "zero-count item" and dispenser duplication effects from the [original tutorial](https://www.bilibili.com/video/BV1Cs411S7ss/); it does not fully revert the old item system.
This mod must be installed on both the client and the server. To obtain a zero-count item, open a chest containing items and—within 6 game ticks (0.3 seconds at normal tick speed) of the chest being destroyed by TNT or another player—remove an item using a standard click or a number key.
* Default Value:`false`
* Optional Parameters: `true`, `false`
* Open Method:`/carpet negativeItemStacks true`
* Categories:`REMS`, `Experimental`
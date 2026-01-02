# Rule List

## PistonBlockChunkLoader
If enabled, when this piston/sticky piston head generates a piston head push/pull event, a load ticket of type "piston_block" is added to the chunk where the piston head block is located at the game tick that created the push/pull event, with a duration of 60gt (3s).
### In any dimension, a diamond ore can be weakly loaded into a 1x1 Active chunk above a piston.
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

## ~~TeleportToPoiWithoutPortals~~
~~Re-added rules for teleporting to portal POIs without portal blocks~~

* ~~Default Value: `false`~~
* ~~Optional Parameters: `true`, `false`~~
* ~~Open Method: `/carpet teleportToPoiWithoutPortals true`~~
* ~~Categories: `REMS`, `Experimental`~~

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
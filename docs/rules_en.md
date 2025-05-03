# Rule List

## PistonBlockChunkLoader
If enabled, when this piston/sticky piston head generates a piston head push/pull event, a load ticket of type "piston_block" is added to the chunk where the piston head block is located at the game tick that created the push/pull event, with a duration of 60gt (3s).
### Piston top block type
|  Block type  |   Load chunk size   |
|:------------:|:-------------------:|
| Diamond_ore  | Lazy Load 1x1 chunk |
| Redstone_ore |   Load 3x3 chunk    |
|   Hold_ore   |   Load 1x1 chunk    |**
### 
If there is bedrock below the netherworld and a redstone torch above the next block, a 1x1 block can be lazy loaded.
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
This mod allows ender pearl entities to selectively load chunks that they are about to pass through, so that pearls fired by the pearl cannon will not be lost due to entering unloaded chunks. It can be used instead of the nether portal loading chain in 1.14+.
This mod has a significant performance improvement over the enderPearlChunkLoading function of @gnembon/carpet-extra mod.  
(When enabled in Minecraft>=1.21.2, it can significantly improve the performance of the Pearl Cannon)

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pearlTickets true`
* Categories: `REMS` , `Survival`

**Ported from：**
SunnySlopes's [PearlTickets](https://github.com/SunnySlopes/PearlTickets)

## PearlPosVelocity
When the ender pearl loading (PearlTickets) is turned on, the pearl will only show the position of the first gt, and the real position and speed of the pearl cannot be checked. After turning this on, it will be displayed on the public screen.

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet pearlPosVelocity true`
* Categories: `REMS` , `Survival`

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
When an entity passes through the End gateway, the target chunk will be loaded for 3 seconds like a nether portal.  
(Minecraft < 1.21 can be enabled)

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
Reintroduced the line-stirring feature, and you can continue to use the line-stirring machine through this rule.  
(Minecraft>=1.21.2 can be enabled)

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

## MagicBox
Reintroduced update suppression for type conversions  
(Minecraft>=1.20.6 can enable it)

* Default Value: `false`
* Optional Parameters: `true`, `false`
* Open Method: `/carpet magicBox true`
* Categories: `REMS` , `ExperimentalL`

**Ported from：**[CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)
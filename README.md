# REMS-Carpet-Addition

## 支持版本
|  游戏版本  | 开发状态 |
|:------:|:----:|
| 1.21.4 |  ✔   |
| 1.21.3 |  ✔   |
| 1.21.2 |  ✔   |
| 1.21.1 |  ✔   |
|  1.21  |  ✔   |
| 1.20.6 |  ✔   |
| 1.20.1 |  ✔   |
|  1.20  |  ✔   |
| 1.19.4 |  ✔   |


# 新增功能

## 活塞头加载(PistonBlockChunkLoader)
开启后，当该活塞/黏性活塞头产生活塞头的推出/拉回事件时，在创建推出/拉回事件的那一游戏刻为**活塞头方块所在区块**添加类型为"piston_block"的加载票，持续时间为60gt（3s）。
## 活塞上面方块类型
| 方块类型 |   加载大小   |
|:----:|:--------:|
| 钻石矿  | 弱加载1x1区块 |
| 红石矿  | 强加载3X3区块 |
|  金矿  | 强加载1X1区块 |
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet pistonBlockChunkLoader ture`
* 分类: `REMS` , `Survival` 
> 如果不想使用地狱门加载链的话，此规则可作为替代方案。

## 更好的TNT合并(MergeTNTPro)
合并大量TNT以减小实体及爆炸带来的卡顿，能显著降低mspt  
TNT当量为1753+184的情况下，开启TIS-Carpet的optimizedFastEntityMovement,optimizedTNT和mergeTNTPro的mspt比未开启的降低5-6mspt

* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet mergeTNTPro true`
* 分类: `REMS`, `Feature`, `Survival`,`TNT`

## 末影珍珠加载(PearlTickets)
这个mod允许末影珍珠实体选择性地加载即将通过的区块，这样珍珠炮打出的珍珠就不会因为进入未加载区块而丢失。在1.14+中可以替代地狱门加载链使用。   
该mod相比于@gnembon/carpet-extra mod的enderPearlChunkLoading功能有显著的性能提升。  
(Minecraft>=1.21.2时开启后，可以显著提升珍珠炮的性能)
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet pearlTickets true`
* 分类: `REMS` , `Survival`

 
**移植自：**
SunnySlopes 的[PearlTickets](https://github.com/SunnySlopes/PearlTickets)

## 末影真实位置(PearlPosVelocity)
在开启末影珍珠加载(PearlTickets)的时候，珍珠只会显示第一gt的位置，查看不到珍珠的真实位置和速度，开启这个后，会在公屏显示出来。
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet pearlPosVelocity true`
* 分类: `REMS` , `Survival`s

## 投掷物Raycast长度(ProjectileRaycastLength)
更改Raycast的距离。如果设置为0，将检查所有到达目的地的块。  
这减少了快速移动的延迟。在1.12中该值为200。
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet ProjectileRaycastLength true`
* 分类: `REMS` , `Survival`

**移植自：**[EpsilonSMP](https://github.com/EpsilonSMP/Epsilon-Carpet)

## 末地折跃门加载(EndGatewayChunkLoader)
当实体穿越末路之地折跃门时，目标区块会像下界传送门一样使目标区块获得3s的加载。  
(Minecraft<1.21时允许开启)
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet endGatewayChunkLoader true`
* 分类: `REMS` , `Survival`


## 计划刻催熟植物
| 植物类型 | 是否支持 |    命令     |
|:----:|:---------:|:---------:|
| 仙人掌  |    ✔    |    /carpet scheduledRandomTickCactus true    |
|  竹子   |    ✔    |    /carpet scheduledRandomTickBamboo true    |
| 紫颂花  |    ✔    |    /carpet scheduledRandomTickChorusFlower true    |
|  甘蔗   |    ✔    |    /carpet scheduledRandomTickSugarCane true    |
当表上的植物收到计划刻时仍会给予生长随机刻

**移植自：**[OhMyVanillaMinecraft](https://github.com/hit-mc/OhMyVanillaMinecraft)

## 计划刻催熟所有作物(ScheduledRandomTickAllPlants)
当所有作物方块收到计划刻时仍会给予生长随机刻
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet scheduledRandomTickAllPlants true`
* 分类: `REMS`, `Feature`, `Survival`

**移植自：**[OhMyVanillaMinecraft](https://github.com/hit-mc/OhMyVanillaMinecraft)

## 保持实体更新(KeepWorldTickUpdate)
在服务器当前维度没有玩家的300tick后，Minecraft会停止实体相关的更新，这条规则会绕过这个限制。
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet keepWorldTickUpdate true`
* 分类: `REMS` , `Feature`

## 禁止蝙蝠生成(DisableBatCanSpawn)
阻止蝙蝠自然生成
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet disableBatCanSpawn true`
* 分类: `REMS` , `Feature`

## 仙人掌扳手音效(CactusWrenchSound)
使用仙人掌扳手时播放 'BLOCK_DISPENSER_LAUNCH' 音效
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet cactusWrenchSound true`
* 分类: `REMS` , `Survival` ,`Creative`

## 禁止传送门更新(DisablePortalUpdate)
下界传送门方块收到方块更新后不会做出反应
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet disablePortalUpdate true`
* 分类: `REMS` , `Survival` ,`Experimental`

## 重新引入拌线骗特性(StringDupeReintroduced)
重新引入拌线骗特性，可以通过此规则来继续使用刷线机  
(Minecraft>=1.21.2时允许开启)
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet stringDupeReintroduced true`
* 分类: `REMS` , `Survival` ,`Experimental`

## 共享打折(SharedVillagerDiscounts)
玩家将僵尸村民治疗为村民后的获得的折扣将共享给所有玩家
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet sharedVillagerDiscounts true`
* 分类: `REMS` , `Survival`,`Feature`

## 物品分身(ItemShadowing)
重新引入1.16.5物品栏之间交换的逻辑
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet itemShadowing true`
* 分类: `REMS` , `Experimental`

**移植自：**[CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)

## CCE抑制器(MagicBox)
重新引入类型转换的更新抑制  
(Minecraft>=1.20.6时允许开启)
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet magicBox true`
* 分类: `REMS` , `ExperimentalL`

**移植自：**[CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)

# Works Cited
- gnembon [fabric-carpet](https://github.com/gnembon/fabric-carpet)
- Copetan [lunaar-carpet-addons](https://github.com/Lunaar-SMP/lunaar-carpet-addons)
- 1024-byteeeee [Carpet-AMS-Addition](https://github.com/Minecraft-AMS/Carpet-AMS-Addition)
- totorewa [totos-carpet-tweaks](https://github.com/totorewa/totos-carpet-tweaks)
- Crystal_0404 [CrystalCarpetAddition](https://github.com/Crystal0404/CrystalCarpetAddition)

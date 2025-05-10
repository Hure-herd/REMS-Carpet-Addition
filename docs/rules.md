# 功能列表

## 活塞头加载(PistonBlockChunkLoader)
开启后，当该活塞/黏性活塞头产生活塞头的推出/拉回事件时，在创建推出/拉回事件的那一游戏刻为**活塞头方块所在区块**添加类型为"piston_block"的加载票，持续时间为60gt（3s）。
### 活塞上面方块类型
| 方块类型 |   加载大小   |
|:----:|:--------:|
| 钻石矿  | 弱加载1x1区块 |
| 红石矿  | 强加载3X3区块 |
|  金矿  | 强加载1X1区块 |**
### 在地狱下方有基岩，后一格上方是红石火把时可以弱加载1x1的区块
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet pistonBlockChunkLoader ture`
* 分类: `REMS` , `Survival`
> 如果不想使用地狱门加载链的话，此规则可作为替代方案。

## 更好的TNT合并(MergeTNTPro)
合并大量TNT以减小实体及爆炸带来的卡顿，能显著降低mspt

* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet mergeTNTPro true`
* 分类: `REMS`, `Feature`, `Survival`,`TNT`

## ~~重新引入POI传送(TeleportToPoiWithoutPortals)~~
~~重新添加传送至没有传送门方块的传送门POI的规则~~

* ~~默认值: `false`~~
* ~~可选参数: `true`, `false`~~
* ~~开启方法: `/carpet teleportToPoiWithoutPortals true`~~
* ~~分类: `REMS`, `Experimental`~~

## 末影珍珠加载(PearlTickets)
这个规则允许末影珍珠实体选择性地加载即将通过的区块，这样珍珠炮打出的珍珠就不会因为进入未加载区块而丢失。在1.14+中可以替代地狱门加载链使用。   
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
* 分类: `REMS` , `Survival`

## 投掷物Raycast长度(ProjectileRaycastLength)
更改Raycast的距离。如果设置为0，将检查所有到达目的地的块。  
这减少了快速移动的延迟。在1.12中该值为200。
* 默认值: `0`
* 可选参数: `0`, `200`
* 开启方法: `/carpet ProjectileRaycastLength 200`
* 分类: `REMS` , `Survival`

**移植自：**[EpsilonSMP](https://github.com/EpsilonSMP/Epsilon-Carpet)

## 声音抑制移植(Soundsuppression) 
#### MC>1.21.1时存在
将校准幽匿感测体的方块实体数据保留到陷阱箱！！！注意是陷阱箱
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet soundsuppression true`
* 分类: `REMS` , `Feature`

## 比较器忽略来自下方的状态更新(ComparatorIgnoresStateUpdatesFromBelow)
#### MC>=1.20.6时存在
比较器会忽略来自下方的状态更新,意味着开启活板门不会破坏比较器
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet ComparatorIgnoresStateUpdatesFromBelow true`
* 分类: `REMS` , `Feature`

## 珍珠超传(PortalPearlWarp)
可以在某些特定的位置触发超传。  
下面是地狱门的位置，都是正正或者负负

| 地狱传送门位置（正中央)  |   主世界传送门位置（正中央)    |
|:-------------:|:------------------:|
|    915,915    | 29999600,29999600  |
|   7324,7324   |  3749942,3749942   |
|  58592,58592  |   468735,468735    |
| 468743,468743 |   58585,58585      |
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet PortalPearlWarp true`
* 分类: `REMS` , `Feature`

## 箱子矿车加载区块(ChestMinecartChunkLoader)
箱子矿车可以强加载1x1的区块持续2s,当箱子矿车的名字是Load时生效。
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet chestMinecartChunkLoader true`
* 分类: `REMS` , `Feature`

## 末地折跃门加载(EndGatewayChunkLoader)
当实体穿越末路之地折跃门时，目标区块会像下界传送门一样使目标区块获得3s的加载。  
(Minecraft<1.21时允许开启)
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet endGatewayChunkLoader true`
* 分类: `REMS` , `Survival`

## 计划刻催熟植物(ScheduledRandomTickPlants)
可以让计划刻事件触发以下所有植物的随机刻生长行为，用于恢复1.15版本的强制催熟特性。  

仙人掌、竹子、紫颂花、甘蔗、海带、缠怨藤、垂泪藤
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet scheduledRandomTickPlants true`
* 分类: `REMS` , `Feature`,`Survival`


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

## 命令告示牌(SignCommand)
玩家右键单击标牌，执行标牌上的命令  
在告示牌以/开头（只允许say tick和player）
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet SignCommand true`
* 分类: `REMS` , `Survival`

## 末影珍珠加载(Enderpearlloadchunk)
这个末影珍珠加载是从1.21.2移植下来的。十分好用
* 默认值: `false`
* 可选参数: `true`, `false`
* 开启方法: `/carpet enderpearlloadchunk true`
* 分类: `REMS` , `FEATURE`

## 珍珠加载时间(Pearltime)
这个规则可以控制珍珠在大于20m/gt后多少gt被销毁
* 默认值: `40`
* 可选参数: `40`, `0`
* 开启方法: `/carpet pearltime true`
* 分类: `REMS` , `FEATURE`


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

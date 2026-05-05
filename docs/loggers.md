# 记录器

## 地狱门POI
`/log displaypoi`  
通过在poi上渲染一个玻璃来显示POI  
<div align="center"> 
<img src="./img/loggers/log_poi.png" width="400px">
</div>  

属性:
- 默认选项: `portal`
- 参考选项: `village`, `portal`, `bee_home`, `full`

## 查看区块加载情况
`/log loadedChunks`  
一个 HUD 记录器
显示整个游戏世界里指定/全部维度的区块加载情况
<div align="center"> 
<img src="./img/loggers/log_loadChunks-zh.png" width="800x">
</div>  

从左到右分别是:`全部世界`,`主世界`,`地狱`,`末地`  
左侧数字代表强加载区块数量，右侧数字代表弱加载加上强加载的区块总数

属性:
- 默认选项: `brief`
- 参考选项: `brief`, `overworld`, `nether`, `end`, `full`
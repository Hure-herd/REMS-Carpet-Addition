# Logger

## Netherportal_POI_Display
`/log displaypoi`  
Displaying POI by rendering a glass on POI
<div align="center"> 
<img src="./img/loggers/log_poi.png" width="400px">
</div>  

Attributes:
- Default option: `portal`
- Suggested options: `village`, `portal`, `bee_home`, `full`

## Check_chunkloading_Status
`/log loadedChunks`  
A HUD Logger
Displays the chunk loading status of a specified/all dimensions within the entire game world.
<div align="center"> 
<img src="./img/loggers/log_loadChunks-us.png" width="800px">
</div>  

From left to right are::`All Worlds`,`Overworld`,`Nether`,`End`  

The number on the left represents the number of strongly loaded blocks, and the number on the right represents the total number of weakly loaded and strongly loaded blocks combined.

Attributes:
- Default option: `brief`
- Suggested options: `brief`, `overworld`, `nether`, `end`, `full`
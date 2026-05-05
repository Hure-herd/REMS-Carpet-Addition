# Logger

## Netherportal_POI_Display
`/log displaypoi`  
Displaying POI by rendering a purple glass on POI
<div align="center"> 
<img src="./img/loggers/log_displaypoi-1.png" width="400px">
<img src="./img/loggers/log_displaypoi-2.png" width="400px">
</div>  

The left image shows the device not in operation, and the right image shows the device in operation.

Attributes:
- Default option: N/A
- Suggested options: N/A

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
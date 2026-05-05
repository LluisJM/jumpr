function jumpr:level/clear
execute at @e[type=marker, tag=level.start] run place template jumpr:level/mountain/long/back ~-8 ~-2 ~46
execute at @e[type=marker, tag=level.start] run place template jumpr:level/mountain/long/front ~-8 ~-2 ~-2

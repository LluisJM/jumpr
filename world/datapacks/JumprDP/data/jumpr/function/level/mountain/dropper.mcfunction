function jumpr:level/clear
execute at @e[type=marker, tag=level.start] run place template jumpr:level/mountain/dropper/bottom ~-14 ~-45 ~-2
execute at @e[type=marker, tag=level.start] run place template jumpr:level/mountain/dropper/top ~-14 ~3 ~-2

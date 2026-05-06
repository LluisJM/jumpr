function jumpr:level/clear
scoreboard players set $level game_settings 7
execute at @e[type=marker, tag=level.start] run place template jumpr:level/plains/vertical ~-7 ~-4 ~-2

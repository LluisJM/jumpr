function jumpr:level/clear
scoreboard players set $level game_settings 1
execute at @e[type=marker, tag=level.start] run place template jumpr:level/badlands/vertical ~-9 ~-2 ~-2

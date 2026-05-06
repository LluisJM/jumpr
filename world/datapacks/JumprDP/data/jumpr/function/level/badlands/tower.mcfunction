function jumpr:level/clear
scoreboard players set $level game_settings 0
execute at @e[type=marker, tag=level.start] run place template jumpr:level/badlands/tower ~-9 ~-3 ~-2

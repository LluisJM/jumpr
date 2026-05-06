function jumpr:level/clear
scoreboard players set $level game_settings 4
execute at @e[type=marker, tag=level.start] run place template jumpr:level/mountain/normal ~-7 ~-5 ~-2

function jumpr:level/clear
scoreboard players set $level game_settings 6
execute at @e[type=marker, tag=level.start] run place template jumpr:level/plains/normal ~-5 ~-3 ~-2

function jumpr:level/clear
scoreboard players set $level game_settings 5
execute at @e[type=marker, tag=level.start] run place template jumpr:level/plains/long ~-6 ~-5 ~-2

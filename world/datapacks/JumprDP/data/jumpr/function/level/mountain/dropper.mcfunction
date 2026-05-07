function jumpr:level/clear
scoreboard players set $level game_settings 2
execute at @e[type=marker, tag=level.start] run function jumpr:level/mountain/dropper/nested_execute_0
execute at @e[type=marker, tag=level.start] run function jumpr:level/mountain/dropper/nested_execute_1
function jumpr:level/sort_bottom_markers

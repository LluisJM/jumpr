function jumpr:level/clear
scoreboard players set $level game_settings 0
execute at @e[type=marker, tag=level.start] run function jumpr:level/badlands/tower/nested_execute_0
function jumpr:level/sort_bottom_markers

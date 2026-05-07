execute as @e[type=marker, tag=level.bottom] run function jumpr:game/tick/nested_execute_1
execute if score $phase game_data matches 1 run function jumpr:game/tick/nested_execute_11
execute if score $phase game_data matches 2 run function jumpr:game/tick/nested_execute_12
execute if score $phase game_data matches 3 as @e[type=minecraft:item] run function jumpr:game/tick/nested_execute_14
execute if score $phase game_data matches 4 run function jumpr:game/tick/nested_execute_24

scoreboard players add $phase game_data 1
execute if score $phase game_data matches 5.. run scoreboard players set $phase game_data 1
title @a times 0.1s 2s 0.5s
execute if score $phase game_data matches 1 run function jumpr:game/start_next_phase/nested_execute_0
execute if score $phase game_data matches 2 run function jumpr:game/start_next_phase/nested_execute_3
execute if score $phase game_data matches 3 run function jumpr:game/start_next_phase/nested_execute_6
execute if score $phase game_data matches 4 run function jumpr:game/start_next_phase/nested_execute_7

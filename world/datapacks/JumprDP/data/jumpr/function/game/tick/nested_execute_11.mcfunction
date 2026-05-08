function jumpr:timer/display
title @a times 0s 2s 0s
execute if score $ticks timer matches 20 run function jumpr:game/tick/nested_execute_2
execute if score $ticks timer matches 40 run function jumpr:game/tick/nested_execute_3
execute if score $ticks timer matches 60 run function jumpr:game/tick/nested_execute_4
execute if score $ticks timer matches 80 run function jumpr:game/tick/nested_execute_5
execute if score $ticks timer matches 100 run function jumpr:game/tick/nested_execute_6
execute as @e[type=marker, tag=level.start] run function jumpr:game/tick/nested_execute_8
scoreboard players remove $particle_cooldown temp 1
execute if score $particle_cooldown temp matches ..0 run function jumpr:game/tick/nested_execute_10

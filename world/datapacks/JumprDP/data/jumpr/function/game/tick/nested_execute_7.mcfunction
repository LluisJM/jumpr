title @a times 0s 2s 0s
execute if score $ticks timer matches 20 run function jumpr:game/tick/nested_execute_0
execute if score $ticks timer matches 40 run function jumpr:game/tick/nested_execute_1
execute if score $ticks timer matches 60 run function jumpr:game/tick/nested_execute_2
execute if score $ticks timer matches 80 run function jumpr:game/tick/nested_execute_3
execute if score $ticks timer matches 100 run function jumpr:game/tick/nested_execute_4
execute store success score $3gooidkub8jev_34 bolt.expr.temp if score $particle_cooldown temp matches ..0
execute unless score $3gooidkub8jev_34 bolt.expr.temp matches 0 run function jumpr:game/tick/nested_execute_6
execute if score $3gooidkub8jev_34 bolt.expr.temp matches 0 run scoreboard players remove $particle_cooldown temp 1

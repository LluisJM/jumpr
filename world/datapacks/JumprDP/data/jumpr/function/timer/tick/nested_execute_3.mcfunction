scoreboard players add $ticks timer 1
scoreboard players operation $i0 bolt.expr.temp = $ticks timer
scoreboard players operation $i0 bolt.expr.temp %= $2 bolt.expr.const
execute if score $i0 bolt.expr.temp matches 0 run function jumpr:timer/tick/nested_execute_2

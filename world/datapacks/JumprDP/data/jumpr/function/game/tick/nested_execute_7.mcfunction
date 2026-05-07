scoreboard players operation $i0 bolt.expr.temp = $level_start_z temp
scoreboard players add $i0 bolt.expr.temp 1
execute store result score $i1 bolt.expr.temp run data get entity @s Pos[2] 1
execute if score $i1 bolt.expr.temp > $i0 bolt.expr.temp run tp @s ~ ~ ~-0.2806

execute store result score @s temp run data get entity @s Pos[2] 1
scoreboard players operation $i0 bolt.expr.temp = $level_start_z temp
scoreboard players add $i0 bolt.expr.temp 1
execute if score @s temp > $i0 bolt.expr.temp run tp @s ~ ~ ~-0.2806

execute store result score $i0 bolt.expr.temp run data get entity @s Pos[1] 1
execute store success score $4ucm2yqq81f77_2 bolt.expr.temp if score $i0 bolt.expr.temp > $level_lowest_y temp
execute unless score $4ucm2yqq81f77_2 bolt.expr.temp matches 0 run kill @s
execute if score $4ucm2yqq81f77_2 bolt.expr.temp matches 0 store result score $level_lowest_y temp run data get entity @s Pos[1] 1

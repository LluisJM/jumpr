scoreboard players operation $i0 bolt.expr.temp = $level_bottom_y temp
scoreboard players remove $i0 bolt.expr.temp 1
execute store result score $i1 bolt.expr.temp run data get entity @s Pos[1] 1
execute if score $i1 bolt.expr.temp < $i0 bolt.expr.temp run kill @s

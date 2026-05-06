kill @e[type=creeper]
kill @e[type=shulker]
execute if score $level game_settings matches 0 run function jumpr:level/badlands/tower
execute if score $level game_settings matches 1 run function jumpr:level/badlands/vertical
execute if score $level game_settings matches 2 run function jumpr:level/mountain/dropper
execute if score $level game_settings matches 3 run function jumpr:level/mountain/long
execute if score $level game_settings matches 4 run function jumpr:level/mountain/normal
execute if score $level game_settings matches 5 run function jumpr:level/plains/long
execute if score $level game_settings matches 6 run function jumpr:level/plains/normal
execute if score $level game_settings matches 7 run function jumpr:level/plains/vertical

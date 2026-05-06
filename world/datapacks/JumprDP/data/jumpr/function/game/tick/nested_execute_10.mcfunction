function jumpr:timer/display
execute as @a[tag=!finished] at @s if block ~ ~-1 ~ minecraft:lodestone run function jumpr:game/player_finish
execute unless entity @a[tag=!finished] run function jumpr:game/start_next_phase

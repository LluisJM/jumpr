execute as @e[type=creeper] run function jumpr:game/start_next_phase/nested_execute_2
execute as @e[type=shulker] run function jumpr:game/start_next_phase/nested_execute_3
title @a title {translate: "jumpr.game.phase.run.start", fallback: "Run!", with: [""], color: "green"}
function jumpr:timer/set/from_settings
function jumpr:timer/start
tag @a remove finished
scoreboard players reset @a round_deaths
execute as @e[type=item] run data modify entity @s PickupDelay set value 0
execute as @e[tag=level.start] at @s run fill ~10 ~5 ~2 ~-10 ~-5 ~2 air replace barrier

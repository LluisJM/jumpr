team empty build_phase.done
team empty build_phase.not_done
gamemode adventure @a
scoreboard players set $ticks timer 100
function jumpr:timer/start
tp @a @n[tag=level.start]
execute as @e[tag=level.start] at @s run fill ~10 ~5 ~2 ~-10 ~-5 ~2 barrier replace air

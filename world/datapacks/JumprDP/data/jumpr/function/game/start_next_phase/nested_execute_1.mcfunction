title @a title {translate: "jumpr.game.phase.round", fallback: "Round %s", with: [{score: {name: "$round", objective: "game_data"}}], color: "yellow"}
team empty build_phase.done
team empty build_phase.not_done
gamemode adventure @a
scoreboard players set $ticks timer 200
function jumpr:timer/start
tp @a @n[tag=level.start]
execute as @a at @n[tag=level.start] run spawnpoint @s ~ ~ ~
execute as @e[tag=level.start] at @s run fill ~10 ~5 ~2 ~-10 ~-5 ~2 barrier replace air
scoreboard players set $particle_cooldown temp 0

kill @e[type=shulker_bullet]
execute as @e[type=creeper] run function jumpr:game/start_next_phase/nested_execute_4
execute as @e[type=shulker] run function jumpr:game/start_next_phase/nested_execute_5
title @a actionbar ""
title @a title {translate: "jumpr.game.phase.run.end", fallback: "Round over!", with: [""], color: "green"}
tellraw @a[tag=!finished] {translate: "jumpr.game.phase.run.end.not_finished", fallback: "You didn't finish this round!", with: [""], color: "red"}
scoreboard players set $ticks timer 100
function jumpr:timer/start

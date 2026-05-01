title @a actionbar ""
title @a title {translate: "jumpr.game.phase.run.end", fallback: "Round over!", with: [""], color: "green"}
tellraw @a[tag=!finished] {translate: "jumpr.game.phase.run.end.not_finished", fallback: "You didn't finish this round!", with: [""], color: "red"}
scoreboard players set $ticks timer 100
function jumpr:timer/start

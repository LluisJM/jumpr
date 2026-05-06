tag @s add done
team join build_phase.done @s
tellraw @s {translate: "jumpr.game.build_phase.done", fallback: "%s is done!", with: [{selector: "@s"}], color: "green"}

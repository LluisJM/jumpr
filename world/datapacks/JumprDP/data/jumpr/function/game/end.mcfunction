function jumpr:timer/stop
scoreboard players set $phase game_data 0
gamemode adventure @a
team empty build_phase.done
team empty build_phase.not_done
tag @a remove winner
scoreboard players set $highest_points temp 0
execute as @a if score @s points > $highest_points temp run scoreboard players operation $highest_points temp = @s points
execute as @a if score @s points = $highest_points temp run tag @s add winner
title @a times 0s 5s 0.5s
title @a subtitle {translate: "jumpr.game.over", fallback: "%s wins!", with: [{selector: "@a[tag=winner]", color: "white"}], color: "gold"}
title @a title {translate: "jumpr.game.over", fallback: "Game Over!", with: [""], color: "gold"}

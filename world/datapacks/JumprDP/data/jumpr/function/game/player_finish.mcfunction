execute store success score $3gooidkub8jev_54 bolt.expr.temp if score $sec timer matches 10..
execute unless score $3gooidkub8jev_54 bolt.expr.temp matches 0 run tellraw @a {translate: "jumpr.game.player_finish", fallback: "%s finished with %s left!", with: [{selector: "@s", color: "white"}, [{text: "", color: "white"}, {score: {name: "$min", objective: "timer"}}, ":", {score: {name: "$sec", objective: "timer"}}, ".", {score: {name: "$ds", objective: "timer"}}]], color: "green"}
execute if score $3gooidkub8jev_54 bolt.expr.temp matches 0 run tellraw @a {translate: "jumpr.game.player_finish", fallback: "%s finished with %s left!", with: [{selector: "@s", color: "white"}, [{text: "", color: "white"}, {score: {name: "$min", objective: "timer"}}, "0", ":", {score: {name: "$sec", objective: "timer"}}, ".", {score: {name: "$ds", objective: "timer"}}]], color: "green"}
scoreboard players add @s points 1
tellraw @a [{text: "     +1p \u2192 ", color: "gray"}, {translate: "jumpr.game.player_finish.points.finished", fallback: "Finished", with: [""]}]
execute unless entity @a[tag=finished] run function jumpr:game/player_finish/nested_execute_0
execute unless score @s round_deaths matches 1.. run function jumpr:game/player_finish/nested_execute_1
execute if entity @s[nbt={Inventory: [{components: {"minecraft:custom_data": {coin: 1b}}}]}] run function jumpr:game/player_finish/nested_execute_2
tag @s add finished

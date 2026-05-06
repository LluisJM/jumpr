scoreboard players operation $3gooidkub8jev_76 bolt.expr.temp = @s temp
scoreboard players operation $3gooidkub8jev_76 bolt.expr.temp *= $5 bolt.expr.const
scoreboard players operation $i0 bolt.expr.temp = @s temp
scoreboard players operation $i0 bolt.expr.temp *= $5 bolt.expr.const
scoreboard players operation @s points += $i0 bolt.expr.temp
scoreboard players set $3gooidkub8jev_84 bolt.expr.temp 0
execute store success score $3gooidkub8jev_84 bolt.expr.temp unless score @s temp matches ..-1 unless score @s temp matches 1..
execute unless score $3gooidkub8jev_84 bolt.expr.temp matches 0 run tellraw @a [{text: "     +", color: "gray"}, {score: {name: "$3gooidkub8jev_76", objective: "bolt.expr.temp"}}, "p \u2192 ", {translate: "jumpr.game.player_finish.points.coin", fallback: "Coin", with: [""]}]
execute if score $3gooidkub8jev_84 bolt.expr.temp matches 0 run tellraw @a [{text: "     +", color: "gray"}, {score: {name: "$3gooidkub8jev_76", objective: "bolt.expr.temp"}}, "p \u2192 ", {translate: "jumpr.game.player_finish.points.coin", fallback: "Coin", with: [""]}, " x", {score: {name: "@s", objective: "temp"}}]
scoreboard players reset @s temp

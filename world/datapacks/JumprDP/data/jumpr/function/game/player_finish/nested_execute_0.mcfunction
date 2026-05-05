scoreboard players add @s points 2
tellraw @a [{text: "     +", color: "gray"}, "2", "p \u2192 ", {translate: "jumpr.game.player_finish.points.first", fallback: "Finished 1st", with: [""]}]

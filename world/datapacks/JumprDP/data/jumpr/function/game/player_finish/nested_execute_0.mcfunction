scoreboard players add @s points 2
tellraw @a [{text: "     +2p \u2192 ", color: "gray"}, {translate: "jumpr.game.player_finish.points.first", fallback: "Finished 1st", with: [""]}]

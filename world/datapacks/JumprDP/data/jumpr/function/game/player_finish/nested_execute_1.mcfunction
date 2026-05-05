scoreboard players add @s points 5
tellraw @a [{text: "     +", color: "gray"}, "5", "p \u2192 ", {translate: "jumpr.game.player_finish.points.no_deaths", fallback: "No deaths", with: [""]}]

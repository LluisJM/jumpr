scoreboard players add @s points 5
tellraw @a [{text: "     +5p \u2192 ", color: "gray"}, {translate: "jumpr.game.player_finish.points.no_deaths", fallback: "No deaths", with: [""]}]

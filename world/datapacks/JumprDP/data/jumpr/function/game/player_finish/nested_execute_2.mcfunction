clear @s *[minecraft:custom_data~{coin: 1b}] 1
scoreboard players add @s points 5
tellraw @a [{text: "     +5p \u2192 ", color: "gray"}, {translate: "jumpr.game.player_finish.points.coin", fallback: "Coin", with: [""]}]

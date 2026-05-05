clear @s *[minecraft:custom_data~{coin: 1b}] 1
scoreboard players add @s temp 1
execute unless entity @s[nbt={Inventory: [{components: {"minecraft:custom_data": {coin: 1b}}}]}] run function jumpr:game/player_finish/count_coins/nested_execute_0
function jumpr:game/player_finish/count_coins

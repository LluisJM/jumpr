execute if score $phase game_data matches 1 run function jumpr:game/tick/nested_execute_5
execute if score $phase game_data matches 2 run function jumpr:game/tick/nested_execute_6
execute if score $phase game_data matches 4 unless entity @a[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run function jumpr:game/start_next_phase

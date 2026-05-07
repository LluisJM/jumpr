execute if entity @s[nbt=!{Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run function jumpr:game/tick/nested_execute_20
execute if entity @s[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run tag @s remove ensured_items

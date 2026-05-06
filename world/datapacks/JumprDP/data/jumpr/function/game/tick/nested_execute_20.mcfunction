execute as @e[type=minecraft:item] run function jumpr:game/tick/nested_execute_14
clear @a minecraft:bucket
execute as @a[tag=!done] run function jumpr:game/tick/nested_execute_17
execute unless entity @a[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run function jumpr:game/tick/nested_execute_19
execute if entity @a[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run scoreboard players reset $checked_for_items temp

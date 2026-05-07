execute as @e[type=creeper] run function jumpr:game/tick/nested_execute_15
execute as @e[type=shulker] run function jumpr:game/tick/nested_execute_16
execute as @e[type=minecraft:item] run function jumpr:game/tick/nested_execute_18
clear @a minecraft:bucket
execute as @a[tag=!done] run function jumpr:game/tick/nested_execute_21
execute unless entity @a[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run function jumpr:game/tick/nested_execute_23
execute if entity @a[nbt={Inventory: [{components: {"minecraft:custom_data": {keep_through_phase: 0b}}}]}] run scoreboard players reset $checked_for_items temp
title @a actionbar {selector: "@a", separator: " - "}

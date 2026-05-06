execute if entity @s[nbt={Item: {components: {"minecraft:custom_data": {coin: 1b}}}}] run data modify entity @s PickupDelay set value 200
execute unless entity @s[nbt={Item: {components: {"minecraft:custom_data": {coin: 1b}}}}] run function jumpr:game/tick/nested_execute_13

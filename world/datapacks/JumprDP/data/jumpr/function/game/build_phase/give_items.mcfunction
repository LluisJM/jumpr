execute store success score $3gooidkub8jev_23 bolt.expr.temp if score $share_items game_settings matches 1
execute unless score $3gooidkub8jev_23 bolt.expr.temp matches 0 run loot give @s loot jumpr:item/any
execute if score $3gooidkub8jev_23 bolt.expr.temp matches 0 as @a run loot give @s loot jumpr:item/any
scoreboard players remove $items_to_give temp 1
execute if score $items_to_give temp matches 1.. run function jumpr:game/build_phase/give_items

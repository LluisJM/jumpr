execute store success score $3gooidkub8jev_40 bolt.expr.temp if score $checked_for_items temp matches 1
execute unless score $3gooidkub8jev_40 bolt.expr.temp matches 0 run function jumpr:game/tick/nested_execute_11
execute if score $3gooidkub8jev_40 bolt.expr.temp matches 0 run scoreboard players set $checked_for_items temp 1

execute store success score $3gooidkub8jev_52 bolt.expr.temp if score $checked_for_items temp matches 20
execute unless score $3gooidkub8jev_52 bolt.expr.temp matches 0 run function jumpr:game/tick/nested_execute_20
execute if score $3gooidkub8jev_52 bolt.expr.temp matches 0 run scoreboard players add $checked_for_items temp 1

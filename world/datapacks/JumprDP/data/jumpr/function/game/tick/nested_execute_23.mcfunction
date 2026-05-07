execute store success score $3gooidkub8jev_71 bolt.expr.temp if score $checked_for_items temp matches 20
execute unless score $3gooidkub8jev_71 bolt.expr.temp matches 0 run function jumpr:game/tick/nested_execute_22
execute if score $3gooidkub8jev_71 bolt.expr.temp matches 0 run scoreboard players add $checked_for_items temp 1

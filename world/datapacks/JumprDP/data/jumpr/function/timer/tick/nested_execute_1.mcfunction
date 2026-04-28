scoreboard players remove $sec timer 1
scoreboard players set $ds timer 9
execute if score $sec timer matches ..-1 run function jumpr:timer/tick/nested_execute_0

scoreboard players remove $ds timer 1
execute if score $ds timer matches ..-1 run function jumpr:timer/tick/nested_execute_1

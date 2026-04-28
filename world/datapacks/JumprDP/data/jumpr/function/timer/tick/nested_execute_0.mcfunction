scoreboard players remove $min timer 1
scoreboard players set $sec timer 59
execute if score $min timer matches ..-1 run function jumpr:timer/stop

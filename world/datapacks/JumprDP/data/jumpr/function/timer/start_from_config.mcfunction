scoreboard players set $running timer 1
scoreboard players set $ticks timer 0
scoreboard players set $ds timer 0
scoreboard players operation $sec timer = $round_length.sec game_settings
scoreboard players operation $min timer = $round_length.min game_settings

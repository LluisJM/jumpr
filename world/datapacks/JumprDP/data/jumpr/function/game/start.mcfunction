function jumpr:level/reload
scoreboard players reset * points
scoreboard players set @a points 0
scoreboard players set $round game_data 0
scoreboard players set $phase game_data 0
function jumpr:game/start_next_phase

execute store success score $5z74kaidjvz25_70 bolt.expr.temp if score $ticks timer matches ..0
execute unless score $5z74kaidjvz25_70 bolt.expr.temp matches 0 run function jumpr:timer/over
execute if score $5z74kaidjvz25_70 bolt.expr.temp matches 0 run scoreboard players remove $ticks timer 1

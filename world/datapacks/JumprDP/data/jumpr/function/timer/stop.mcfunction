execute as @a at @s anchored eyes run playsound minecraft:ui.button.click master @s ~ ~ ~ 0.5 1.8
scoreboard players set $running timer 0
function jumpr:game/start_next_phase

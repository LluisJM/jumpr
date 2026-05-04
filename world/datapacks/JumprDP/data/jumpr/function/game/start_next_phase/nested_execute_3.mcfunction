team join build_phase.not_done @a
gamemode survival @a
attribute @s minecraft:block_break_speed base set 0
tag @a remove done
title @a title {translate: "jumpr.game.phase.build.start", fallback: "Build Phase", with: [""], color: "yellow"}
tellraw @a {translate: "jumpr.game.phase.build.description", fallback: "Build obstacles using the items given to you to stop the other players from reaching the end.", with: [""], color: "gray"}
scoreboard players operation $items_to_give temp = $build_items game_settings
function jumpr:game/build_phase/give_items

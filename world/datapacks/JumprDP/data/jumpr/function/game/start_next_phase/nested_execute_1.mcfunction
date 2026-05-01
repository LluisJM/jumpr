title @a title {translate: "jumpr.game.phase.run.start", fallback: "Run!", with: [""], color: "green"}
function jumpr:timer/set/from_settings
function jumpr:timer/start
tag @a remove finished
scoreboard players reset @a round_deaths

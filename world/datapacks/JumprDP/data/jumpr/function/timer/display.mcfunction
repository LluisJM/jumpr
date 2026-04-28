execute store success score $5z74kaidjvz25_22 bolt.expr.temp if score $sec timer matches ..9
execute unless score $5z74kaidjvz25_22 bolt.expr.temp matches 0 run title @a actionbar [{score: {name: "$min", objective: "timer"}}, ":0", {score: {name: "$sec", objective: "timer"}}, ".", {score: {name: "$ds", objective: "timer"}}]
execute if score $5z74kaidjvz25_22 bolt.expr.temp matches 0 run title @a actionbar [{score: {name: "$min", objective: "timer"}}, ":", {score: {name: "$sec", objective: "timer"}}, ".", {score: {name: "$ds", objective: "timer"}}]

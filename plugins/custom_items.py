from beet import Context, Function, LootTable
from libs.text_components import to_snake_case, translated
from libs.debugger import debug

def item_name(name: str) -> dict:
    return translated(f"item.{to_snake_case(name)}.name", name)

def item_description(id: str, description: list[str] | str) -> list[dict]:
    def get_translation(description: str, suffix: str | int = "") -> dict:
        return translated(f"item.{id}.description{suffix}", description, [""], {"color": "gray", "italic": False})

    if isinstance(description, str) or description.__len__() <= 1:
        if isinstance(description, list):
            description = description[0]
        return [get_translation(description)]
    else:
        to_return = []
        for i in range(description.__len__()):
            to_return.append(get_translation(description[i], i))
        return to_return

class CustomItem:
    name: str | dict
    description: list[str | dict]
    id: str
    dummy_item: str
    default_count: int
    keep_through_phase: bool
    extra_identifiers: list[str]

    def __init__(self, 
            name: str | dict, 
            description: list[str] | str, 
            dummy_item: str, 
            default_count: int = 1,
            keep_through_phase: bool = False,
            extra_identifiers: list[str] | str = None):
        self.name = item_name(name) if isinstance(name, str) else name
        self.id = (to_snake_case(name) if isinstance(name, str) else name["text"] if "text" in name else name["fallback"]) if name else dummy_item.split(":")[-1]
        self.description = item_description(name if name else dummy_item.split(":")[-1], description)
        
        self.dummy_item = dummy_item
        self.default_count = default_count
        self.keep_through_phase = keep_through_phase
        if keep_through_phase:
            self.description.append(translated("jumpr.item.keep_through_phase.description", "This item is kept after build phase is over.", 
                                           formatting={"color": "gray", "italic": False}))

        if extra_identifiers:
            self.extra_identifiers = extra_identifiers if isinstance(extra_identifiers, list) else [extra_identifiers]
        else:
            self.extra_identifiers = None
        
        
        debug(__name__, f'created custom item "{self.name["fallback"] if self.name else self.id}" which is actually "{self.dummy_item}" with {self.description.__len__()} line(s) of description')

    def as_loot_table_entry(self, count: int = None):
        if not count:
            count = self.default_count
        
        tag = {
            "keep_through_phase": self.keep_through_phase
        }

        if self.extra_identifiers:
            for identifier in self.extra_identifiers:
                tag.setdefault(identifier, True)

        functions = [
            {
                "function": "minecraft:set_lore",
                "lore": self.description,
                "mode": "append"
            },
            {
                "function": "minecraft:set_count",
                "count": count
            },
            {
              "function": "minecraft:set_custom_data",
              "tag": tag
            }
        ]
        if self.name:
            functions.append({
                "function": "minecraft:set_name",
                "name": self.name,
                "target": "item_name",
                "conditions": []
            })

        return {
            "type": "minecraft:item",
            "name": self.dummy_item,
            "functions": functions
        }
        

def beet_default(ctx: Context):
    debug(__name__, 'running plugin', True)

    items = [
        CustomItem("Building Block", "Just a building block.", "minecraft:yellow_concrete", 5),
        CustomItem("Coin", "Bring this to the finish line during the running phase to score extra points.", "minecraft:gold_ingot", extra_identifiers="coin"),
        CustomItem(None, "That's pretty hot.", "minecraft:lava_bucket"),
        CustomItem(None, "Better than dirt?", "minecraft:scaffolding", 5),
        CustomItem("Mr Puffer", "A very respectable pufferfish.", "minecraft:pufferfish_bucket"),
        CustomItem(None, "Yummy.", "minecraft:baked_potato", 3, True),
        CustomItem(None, "Place them or eat them; your choice.", "minecraft:sweet_berries", 3),
        CustomItem(None, "Climb to the top with these!", "minecraft:ladder", 10)
    ]

    any_entries = []
    for item in items:
        entry = item.as_loot_table_entry()
        any_entries.append(entry)
        ctx.data[f"jumpr:item/{item.id}"] = LootTable({
            "pools": [
                {
                    "rolls": 1,
                    "entries": [
                        entry
                    ],
                    "functions": []
                }
            ]
        })
        ctx.data[f"jumpr:item/{item.id}/give"] = Function([
            f'loot give @s loot jumpr:item/{item.id}'
        ])

    ctx.data["jumpr:item/any"] = LootTable({
        "pools": [
            {
                "rolls": 1,
                "entries": any_entries,
                "functions": []
            }
        ]
    })
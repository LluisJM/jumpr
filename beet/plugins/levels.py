from beet import Context, Function
from libs.debugger import debug

finish_line_prot_height = 3.02
finish_line_prot_width = 3.02

# noinspection PyTypeChecker
def beet_default(ctx: Context):
    debug(__name__, "running plugin", True, True)

    functions: dict[str, list[str]] = {}
    i: int = 0

    for structure in ctx.data.structures:
        if structure.startswith("jumpr:level/"):
            debug(__name__, f'generating files for level or level piece "{structure}"', False, True)

            file_data = ctx.data.structures[structure].data
            
            start_pos: list[int] | None = None

            delete_queue: list[list[int]] = []

            def get_pos(this_block, offset = None):
                if offset is None:
                    offset = [0, 0, 0]
                to_return = []
                for i in range(3):
                    to_return.append(this_block["pos"][i].__int__() + offset[i])
                return to_return

            lodestone_id = -1
            lodestone_positions = []

            for i, block_id in enumerate(file_data["palette"]):
                if block_id["Name"] == "minecraft:lodestone":
                    lodestone_id = i

            for block in file_data["blocks"]:
                if "nbt" in block:
                    nbt = block["nbt"]
                    if "front_text" in nbt:
                        for line in nbt["front_text"]["messages"]:
                            if line:
                                debug(__name__, f'sign line -> {line}')
                            split = line.split(":")
                            if split[0] == "level_start":
                                start_pos = get_pos(block)
                                debug(__name__, f'found level start at x:{start_pos[0]} y:{start_pos[1]} z:{start_pos[2]}')
                                if split.__len__() == 4:
                                    start_pos = get_pos(block, [-int(split[1]), -int(split[2]), -int(split[3])])
                                    debug(__name__, f'applying offset to level start: x:{start_pos[0]} y:{start_pos[1]} z:{start_pos[2]}')
                            elif line == "remove_support":
                                delete_queue.append(get_pos(block, [0, -1, 0]))
                            elif line == "remove":
                                delete_queue.append(get_pos(block))

            for block in file_data["blocks"]:
                if get_pos(block) in delete_queue:
                    file_data["blocks"].remove(block)

            if lodestone_id != -1:
                for block in file_data["blocks"]:
                    if block["state"].__int__() == lodestone_id:
                        lodestone_pos = get_pos(block)
                        debug(__name__, f'found lodestone at x:{lodestone_pos[0]} y:{lodestone_pos[1]} z:{lodestone_pos[2]}')
                        lodestone_positions.append(lodestone_pos)
                        debug(__name__, f'lodestone positions: {lodestone_positions}')
            else:
                debug(__name__, f'structure contains no lodestone')

            if not start_pos:
                debug(__name__, f'missing level start for "{structure}"')
            
                start_pos = [0, 0, 0]

            name = structure
            split = structure.split("/")

            if split.__len__() > 3:
                name = "/".join(split[:3])

            if name in functions:
                contents = functions[name]
            else:
                contents = [
                    'function jumpr:level/clear',
                    f'scoreboard players set .level settings {i}'
                ]
                i += 1
            contents += [
                f'execute at @e[type=marker, tag=level.start] run place template {structure} ~{-start_pos[0]} ~{-start_pos[1]} ~{-start_pos[2]}',
                f'execute at @e[type=marker, tag=level.start] run summon marker ~ ~{-start_pos[1]} ~ {{Tags:["level.bottom"], data:{{name:"level.bottom"}}}}',
                'function jumpr:level/set_borders'
            ]
            for pos in lodestone_positions:
                contents.append(f'execute at @e[type=marker, tag=level.start] run summon interaction ~{pos[0] - start_pos[0]} ~{pos[1] - 0.51 - start_pos[1]} ~{pos[2] - start_pos[2]} {{Tags:["level.finish_line"], height: {finish_line_prot_height}, width: {finish_line_prot_width}}}')

            functions.setdefault(name, contents)
    
    reload_function = [
        'kill @e[type=creeper]',
        'kill @e[type=shulker]',

        f'execute if score .level settings matches ..{functions.__len__()} run scoreboard players set .level settings 0'
        f'execute if score .level settings matches ..-1 run scoreboard players set .level settings {i}'
    ]
    for i, name in enumerate(functions):
        debug(__name__, f'created function "{name}" for loading map')
        ctx.data[name] = Function(functions[name] + ["function jumpr:level/sort_bottom_markers"])
        reload_function += [
            f'execute if score .level settings matches {i} run function {name}'
        ]
    
    ctx.data["jumpr:level/reload"] = Function(reload_function)

from beet import Context, Function
from libs.debugger import debug

def beet_default(ctx: Context):
    debug(__name__, "running plugin", True, True)

    ctx.data["jumpr:level/clear"] = Function([
        'at @e[type=marker, tag=level.load]:',
        '   for i in range(10):',
        '       z1 = i * 10',
        '       z2 = (i + 1) * 10',
        '       fill ~20 ~20 ~z1 ~-20 ~-50 ~z2 air',
        '       fill ~20 ~80 ~z1 ~-20 ~21 ~z2 air'
    ])

    functions: dict[str, list[str]] = {}

    for structure in ctx.data.structures:
        if structure.startswith("jumpr:level/"):
            debug(__name__, f'generating files for level or level piece "{structure}"', False, True)

            file_data = ctx.data.structures[structure].data
            
            start_pos: list[int] = None

            delete_queue: list[list[int]] = []

            def get_pos(block, offset: list[int] = [0, 0, 0]):
                to_return = []
                for i in range(3):
                    to_return.append(block["pos"][i].__int__() + offset[i])
                return to_return

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

            if not start_pos:
                debug(__name__, f'missing level start for "{structure}"')
            
                start_pos = [0, 0, 0]

            name = structure
            split = structure.split("/")

            if split.__len__() > 3:
                name = "/".join(split[:3])

            contents = functions[name] if name in functions else ['function jumpr:level/clear']
            contents += [
                'at @e[type=marker, tag=level.start]:',
                f'   place template {structure} ~{-start_pos[0]} ~{-start_pos[1]} ~{-start_pos[2]}'
            ]

            functions.setdefault(name, contents)
    
    for name in functions:
        debug(__name__, f'created function "{name}" for loading map')
        ctx.data[name] = Function(functions[name])

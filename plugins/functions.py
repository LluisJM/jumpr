from beet import Context, FunctionTag
from libs.debugger import debug

def beet_default(ctx: Context):
    debug(__name__, 'running plugin', True)

    tick_functions = []
    load_functions = []
    for function in ctx.data.functions:
        split = function.split(":")[-1].split("/")
        if split[-1] == "tick":
            tick_functions.append(function)
            debug(__name__, f'added "{function}" to tick functions')
        elif split[-1] == "load":
            load_functions.append(function)
            debug(__name__, f'added "{function}" to load functions')

    ctx.data["minecraft:tick"] = FunctionTag({
        "values": tick_functions
    })
    debug(__name__, f'created "minecraft:tick" function tag with {tick_functions.__len__()} entries')
    ctx.data["minecraft:load"] = FunctionTag({
        "values": load_functions
    })
    debug(__name__, f'created "minecraft:load" function tag with {tick_functions.__len__()} entries')
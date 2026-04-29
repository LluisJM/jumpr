from beet import Context, FunctionTag
from libs.debugger import debug

def beet_default(ctx: Context):
    debug(__name__, 'running plugin', True)

    tick_functions = []
    for function in ctx.data.functions:
        split = function.split(":")[-1].split("/")
        if split[-1] == "tick":
            tick_functions.append(function)
            debug(__name__, f'added "{function}" to tick functions')

    ctx.data["minecraft:tick"] = FunctionTag({
        "values": tick_functions
    })
    debug(__name__, f'created "minecraft:tick" function tag with {tick_functions.__len__()} entries')
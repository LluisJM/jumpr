from beet import Context, ItemModel

from libs.debugger import debug


def beet_default(ctx: Context):
    debug(__name__, "running plugin", True, True)

    for model in ctx.assets["jumpr"].models:
        debug(__name__, f"model found -> {model}")
        if model.startswith("item/"):
            debug(__name__, f'creating item model from "{model}"')
            ctx.assets["jumpr"][model.removeprefix("item/")] = ItemModel({
                "model": {
                    "type": "minecraft:model",
                    "model": f"jumpr:{model}"
                }
            })
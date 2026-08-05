from beet import Context, ItemModel, Model

from libs.debugger import debug


def beet_default(ctx: Context):
    debug(__name__, "running plugin", True, True)

    debug(__name__, "saning models", True)
    for texture in ctx.assets["jumpr"].textures:
        debug(__name__, f"texture found -> {texture}")
        if texture.startswith("item/") and not texture in ctx.assets["jumpr"].models:
            debug(__name__, f'creating model from "{texture}"')
            ctx.assets["jumpr"][texture] = Model({
                "parent": "minecraft:item/generated",
                "textures": {
                    "layer0": f"jumpr:{texture}"
                }
            })

    debug(__name__, "saning item models", True)
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
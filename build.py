from beet import Context
from libs.debugger import debug

def beet_default(ctx: Context):
    ctx.data.save(path="world/datapacks/jumpr", zipped=True, overwrite=True)
    debug(__name__, "built data pack into world", True)
    ctx.assets.save(path="world/resources", zipped=True, overwrite=True)
    debug(__name__, "built resource pack into world", True)
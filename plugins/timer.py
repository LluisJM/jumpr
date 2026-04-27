from beet import Context, Function
from libs.debugger import debug

def beet_default(ctx: Context):
    debug(__name__, "running plugin", True)
    namespace = ctx.project_name.casefold()
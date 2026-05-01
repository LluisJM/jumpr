from beet import Context
from libs.debugger import debug

def beet_default(ctx: Context):
    debug(__name__, "running bolt", True)
import math
from timeit import default_timer as timer
from typing import TypeVar

T = TypeVar('T')

def truncate(number: T, decimals: int = 0) -> T:
    "Returns a value truncated to a specific number of decimal places."

    if decimals < 0:
        raise ValueError("decimal places has to be 0 or more")
    elif decimals == 0:
        return math.trunc(number)

    factor = 10.0 ** decimals
    return math.trunc(number * factor) / factor

start_time = timer()

def debug(module: str, msg: str, highlight: bool = False):
    def get_time() -> str:
        elapsed = timer() - start_time
        elapsed_ms = elapsed * 1000 - truncate(elapsed) * 1000
        return f"{truncate(elapsed)}s {truncate(elapsed_ms, 3):.3f}ms"

    color1 = "\033[0m"
    color2 = "\033[0m"
    if highlight:
        color1 = "\033[104m"
        color2 = "\033[34m\033[1m"
    print(f"\033[90m{get_time()}\033[0m \t{color1}[{module}]\033[0m {color2}{msg}\033[0m")

debug(__name__, "started debugging")
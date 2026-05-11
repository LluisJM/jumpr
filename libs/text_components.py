from beet import Context, Language
from libs.debugger import debug

import re

translation_keys: dict[str, str] = {}
TRANSLATION_MISSING = "<TRANSLATION MISSING>"

def to_snake_case(string):
    string = re.sub(r'(?<=[a-z])(?=[A-Z])|[^a-zA-Z]', ' ', string).strip().replace(' ', '_')
    return ''.join(string.lower())

def from_snake_case(string):
    return string.capitalize().strip().replace('_', ' ').replace('.', ' ')

def translated(key: str, text: str = None, inserted: list[str] = [""], formatting: dict = {}) -> dict:
    if key not in translation_keys:
        translation_keys.setdefault(key, text if text else TRANSLATION_MISSING)
        debug(__name__, f'added translation key "{key}", with value "{text}" to queue')


    to_return = {
        "translate": key,
        "fallback": text if text else TRANSLATION_MISSING,
        "with": inserted
    }
    to_return.update(formatting)

    return to_return

def score(name: str, objective: str) -> dict:
    return {
        "score": {
            "name": name,
            "objective": objective
        }
    }

def beet_default(ctx: Context):
    debug(__name__, "running library", True, True)
    namespace = ctx.project_name.casefold()
    lang_file = f"{namespace}:en_us"

    lang_body = {}
    if lang_file in ctx.assets.languages:
        lang_body = ctx.assets.languages[lang_file]._content
    
    lang_body.update(translation_keys)
    ctx.assets[lang_file] = Language(lang_body)

    missing_translations = 0
    for key in lang_body:
        if lang_body[key] == TRANSLATION_MISSING:
            missing_translations += 1

    debug(__name__, f"created language file with {lang_body.__len__()} keys, {missing_translations} of those missing a translation")
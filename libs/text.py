from beet import Context, Language
from libs.debugger import debug

def translated(ctx: Context, key: str, text: str = None, inserted: list[str] = []) -> str:
    lang_body = {}
    namespace = ctx.project_name.casefold()
    key = f"{namespace}.{key}"

    if f"{namespace}:en_us" in ctx.assets.languages:
        lang_body = ctx.assets.languages[f"{namespace}:en_us"]._content
    
    if not key in lang_body:
        lang_body.setdefault(key, text if text else "<TRANSLATION MISSING>")

        ctx.assets[f"{namespace}:en_us"] = Language(lang_body)
        msg = ""
        if text:
            msg += f'added translation key "{key}" with value: "{text}"'
        else:
            msg += f'added translation key "{key}" with missing value'
        msg += f'; file has now {lang_body.__len__()} entry/ies'
        debug(__name__, msg)

    return f"""{{
        "translate": {key},
        "fallback": "{text if text else "<TRANSLATION MISSING>"}",
        "with": {inserted}
    }}"""
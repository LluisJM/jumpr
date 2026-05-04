from beet import Context, Function

def beet_default(ctx: Context):
    for structure in ctx.data.structures:
        if structure.startswith("jumpr:level/"):
            ctx.data[structure] = Function([
                'at @e[type=marker, tag=level.load]:',
                '   for i in range(10):',
                '       z1 = i * 10',
                '       z2 = (i + 1) * 10',
                '       fill ~20 ~20 ~z1 ~-20 ~-50 ~z2 air',
                f'   place template {structure} ~ ~ ~'
            ])
            
"""Edit Kaleb's turtle .bbmodel: flippers -> legs, lift body, scale 2x.

Verified against the file itself:
- Box UV layout derived from the head + flipper cubes (all 6 faces match):
    north: [u0+d, v0+d, u0+d+w, v0+d+h]
    east:  [u0, v0+d, u0+d, v0+d+h]
    south: [u0+d+w+d, v0+d, u0+2d+2w, v0+d+h]
    west:  [u0+d+w, v0+d, u0+2d+w, v0+d+h]
    up:    [u0+d+w, v0+d, u0+d, v0]          (reversed corners)
    down:  [u0+d+2w, v0, u0+d+w, v0+d]      (reversed corners)
- Cubes are in project coordinates; group origins are pivots. Translating a
  group's origin AND its cubes by the same vector translates the rendered
  result by that vector, whatever the group's rotation.
- Vanilla turtle is 0.4 blocks tall; head tops at y=6, shell is 6 thick, so
  the shell sits at about y 1..7. Lifting the body +4 puts the shell bottom
  at y=5, leaving room for 5-tall legs (feet at y=0).
- 2x scale multiplies every from/to, group origin, visible_box and UV
  coordinate by 2, and the texture image by 2 (nearest neighbour, so the
  paint stays crisp). Uniformly scaling the whole UV layout cannot create
  overlaps, because the original layout had none.
"""
import base64
import io
import json
import sys

SRC = "/home/hatch/workspace/user/files/turtle_-_Converted.bbmodel"
DST = "/home/hatch/workspace/user/files/sacred_tortoise.bbmodel"

LIFT = 4          # body lift in original units (shell bottom 1 -> 5)
SCALE = 2         # horse-size scale factor

with open(SRC) as f:
    d = json.load(f)

# ---------------------------------------------------------------- helpers
def box_uv(u0, v0, w, h, dep):
    """Per-face UV rects for a Box-UV cube, corner order as in the file."""
    return {
        "north": {"uv": [u0 + dep, v0 + dep, u0 + dep + w, v0 + dep + h], "texture": 0},
        "east":  {"uv": [u0, v0 + dep, u0 + dep, v0 + dep + h], "texture": 0},
        "south": {"uv": [u0 + dep + w + dep, v0 + dep, u0 + 2 * dep + 2 * w, v0 + dep + h], "texture": 0},
        "west":  {"uv": [u0 + dep + w, v0 + dep, u0 + 2 * dep + w, v0 + dep + h], "texture": 0},
        "up":    {"uv": [u0 + dep + w, v0 + dep, u0 + dep, v0], "texture": 0},
        "down":  {"uv": [u0 + dep + 2 * w, v0, u0 + dep + w, v0 + dep], "texture": 0},
    }

def shift_y(obj, dy):
    if isinstance(obj, list) and len(obj) == 3:
        obj[1] += dy

groups = {g["name"]: g for g in d["groups"]}
elems = {e["name"]: e for e in d["elements"]}

# ---------------------------------------------------------------- leg swap
# New legs: 4 wide x 5 tall x 4 long columns, feet at y=0, tops tucked
# into the body at y=5. Centred on the old flipper pivots; pivot moved to
# the top-centre of the leg (the hip) so it swings like a leg.
NEW_LEGS = {
    "leg1": {"from": [1.5, 0, 9],  "to": [5.5, 5, 13],  "origin": [3.5, 5, 11]},
    "leg2": {"from": [-5.5, 0, 9], "to": [-1.5, 5, 13], "origin": [-3.5, 5, 11]},
    "leg3": {"from": [3, 0, -6],  "to": [7, 5, -2],    "origin": [5, 5, -4]},
    "leg4": {"from": [-7, 0, -6], "to": [-3, 5, -2],   "origin": [-5, 5, -4]},
}
# All four legs share one fresh UV region. The old flipper regions cannot be
# reused: a 5-tall leg needs 5-tall side faces, and those run into the
# shell's UV (the flippers were only 1 tall, so their side faces were 1
# texel tall and just missed it). [64, 0] is a 12x9 blank, fully transparent
# patch on the 128x64 texture, clear of every other part's UV. All four
# legs sample the same paint, which is what we want: identical legs.
SHARED_LEG_UV = [64, 0]
for name, spec in NEW_LEGS.items():
    e = elems[name]
    u0, v0 = SHARED_LEG_UV
    w = spec["to"][0] - spec["from"][0]
    h = spec["to"][1] - spec["from"][1]
    dep = spec["to"][2] - spec["from"][2]
    e["from"] = spec["from"]
    e["to"] = spec["to"]
    e["uv_offset"] = [u0, v0]
    e["faces"] = box_uv(u0, v0, w, h, dep)
    groups[name]["origin"] = spec["origin"]

# ---------------------------------------------------------------- body lift
# Head: origin + cube up by LIFT. Body + body2: origins + cubes up by LIFT.
for gname in ("head", "body", "body_rotation", "body2", "body2_rotation"):
    shift_y(groups[gname]["origin"], LIFT)
for ename in ("head", "body_rotation", "body2_rotation"):
    # body_rotation / body2_rotation names repeat (two cubes share the name)
    for e in d["elements"]:
        if e["name"] == ename:
            shift_y(e["from"], LIFT)
            shift_y(e["to"], LIFT)

# ---------------------------------------------------------------- global 2x
def scale_vec(v):
    return [c * SCALE for c in v]

for e in d["elements"]:
    e["from"] = scale_vec(e["from"])
    e["to"] = scale_vec(e["to"])
    e["uv_offset"] = scale_vec(e["uv_offset"])
    for face in e["faces"].values():
        face["uv"] = scale_vec(face["uv"])
for g in d["groups"]:
    g["origin"] = scale_vec(g["origin"])
if d.get("visible_box"):
    d["visible_box"] = scale_vec(d["visible_box"])

# Texture: 2x nearest-neighbour, same paint, crisp pixels.
from PIL import Image
tex = d["textures"][0]
raw = base64.b64decode(tex["source"].split(",", 1)[1])
im = Image.open(io.BytesIO(raw))
assert im.size == (128, 64), im.size
im2 = im.resize((256, 128), Image.NEAREST)
buf = io.BytesIO()
im2.save(buf, format="PNG")
tex["source"] = "data:image/png;base64," + base64.b64encode(buf.getvalue()).decode()
tex["width"] = tex["uv_width"] = "256"
tex["height"] = tex["uv_height"] = "128"
d["resolution"] = {"width": 256, "height": 128}

d["name"] = "sacred_tortoise"
d["model_identifier"] = "sacred_tortoise"

with open(DST, "w") as f:
    json.dump(d, f, indent=2)

print("wrote", DST)

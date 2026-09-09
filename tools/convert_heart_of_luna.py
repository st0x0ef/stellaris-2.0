import base64
import json
import math
import pathlib
import re
import sys

root = pathlib.Path(__file__).resolve().parents[1]
source = pathlib.Path(sys.argv[1]) if len(sys.argv) > 1 else root / 'Heart of Luna BOSS.bbmodel'
data = json.loads(source.read_text(encoding='utf-8'))
client = root / 'common/src/main/java/org/exodusstudio/stellaris/client/renderers/mobs/heartofluna'
common = root / 'common/src/main/java/org/exodusstudio/stellaris/common/entities/mobs/heartofluna'
client.mkdir(parents=True, exist_ok=True)
common.mkdir(parents=True, exist_ok=True)
groups = {g['uuid']: g for g in data['groups']}
elements = {e['uuid']: e for e in data['elements']}
names = ['WALK', 'IDLE', 'BLOCK', 'ROAR', 'PULSE_RAY', 'PUNCH', 'DEATH']
f = lambda v: f'{float(v):.7f}F'
vec = lambda v: ', '.join(f(x) for x in v)
native = lambda p: [-p[0], -p[1], p[2]]

def animation_vector(p, channel, animation):
    if animation == 'Punch Attack':
        if channel == 'rotation':
            return native(p)
        if channel == 'position':
            return [-p[0], p[1], p[2]]
    return p

geometry = []
parents = {}
counter = 0

def part(node, parent, origin):
    global counter
    g = groups[node['uuid']]
    parents[g['uuid']] = parent
    var = 'bone' + str(counter)
    counter += 1
    pos = native(g['origin'])
    offset = [pos[i] - origin[i] for i in range(3)]
    rotation = [math.radians(v) for v in native(g.get('rotation', [0, 0, 0]))]
    geometry.append(f'        PartDefinition {var} = {parent}.addOrReplaceChild({json.dumps(g["name"])}, CubeListBuilder.create(), PartPose.offsetAndRotation({vec(offset)}, {vec(rotation)}));')
    for child in node['children']:
        if isinstance(child, dict):
            part(child, var, pos)
        else:
            e = elements[child]
            assert e['box_uv'] and e.get('export', True)
            rotated = any(e.get('rotation', [0, 0, 0]))
            pivot = native(e['origin']) if rotated else pos
            low = [-e['to'][0] - pivot[0], -e['to'][1] - pivot[1], e['from'][2] - pivot[2]]
            size = [e['to'][i] - e['from'][i] for i in range(3)]
            off = [pivot[i] - pos[i] for i in range(3)]
            rot = [math.radians(v) for v in native(e.get('rotation', [0, 0, 0]))]
            uv = e.get('uv_offset', [0, 0])
            cube = f'CubeListBuilder.create().texOffs({int(uv[0])}, {int(uv[1])})'
            if e.get('mirror_uv', False):
                cube += '.mirror()'
            cube += f'.addBox({vec(low)}, {vec(size)}, new CubeDeformation({f(e.get("inflate", 0))}))'
            geometry.append(f'        {var}.addOrReplaceChild("cube_{counter}", {cube}, PartPose.offsetAndRotation({vec(off)}, {vec(rot)}));')
            counter += 1

for node in data['outliner']:
    part(node, 'root', [0, -24, 0])
header = 'package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;\n\n'
model = header + '''import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public final class LunaBoss extends EntityModel<HeartOfLunaRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("heart_of_luna"), "main");
    private final KeyframeAnimation[] animations;

    public LunaBoss(ModelPart root) {
        super(root);
        animations = new KeyframeAnimation[] {
''' + ',\n'.join('            HeartOfLunaAnimations.' + name + '.bake(root)' for name in names) + '''
        };
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
''' + '\n'.join(geometry) + '''
        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(HeartOfLunaRenderState state) {
        resetPose();
        if (state.blend < 1) animations[state.previousAnimation].apply((long) (state.previousTicks * 50.0F), 1 - state.blend);
        animations[state.animation].apply((long) (state.animationTicks * 50.0F), state.blend);
    }
}
'''
(client / 'LunaBoss.java').write_text(model, encoding='utf-8')
animation = header + '''import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public final class HeartOfLunaAnimations {
'''
for name, a in zip(names, data['animations']):
    animation += f'    public static final AnimationDefinition {name} = AnimationDefinition.Builder.withLength({f(a["length"])})' + ('.looping()' if a['loop'] == 'loop' else '') + '\n'
    for b in a['animators'].values():
        for channel, target, helper in [('rotation', 'ROTATION', 'degreeVec'), ('position', 'POSITION', 'posVec'), ('scale', 'SCALE', 'scaleVec')]:
            keys = sorted([k for k in b.get('keyframes', []) if k['channel'] == channel], key=lambda k: k['time'])
            if not keys:
                continue
            animation += f'        .addAnimation({json.dumps(b["name"])}, new AnimationChannel(AnimationChannel.Targets.{target},\n'
            lines = []
            for k in keys:
                p = [float(k['data_points'][0][axis]) for axis in 'xyz']
                p = animation_vector(p, channel, a['name'])
                interpolation = 'CATMULLROM' if k['interpolation'] == 'catmullrom' else 'LINEAR'
                lines.append(f'            new Keyframe({f(k["time"])}, KeyframeAnimations.{helper}({vec(p)}), AnimationChannel.Interpolations.{interpolation})')
            animation += ',\n'.join(lines) + '\n        ))\n'
    animation += '        .build();\n\n'
animation += '    private HeartOfLunaAnimations() {}\n}\n'
(client / 'HeartOfLunaAnimations.java').write_text(animation, encoding='utf-8')
texture = base64.b64decode(data['textures'][0]['source'].split(',')[1])
target = root / 'common/src/main/resources/assets/stellaris/textures/entity/heart_of_luna.png'
target.write_bytes(texture)
print(f'Converted {len(elements)} cubes, {len(groups)} bones, {len(names)} animations, {len(texture)} texture bytes')

identity = lambda: [[float(i == j) for j in range(4)] for i in range(4)]
def mul(a, b):
    return [[sum(a[i][k] * b[k][j] for k in range(4)) for j in range(4)] for i in range(4)]

def transform(pos, rot, scale):
    m = identity()
    for i in range(3):
        m[i][3] = pos[i]
    for axis in [2, 1, 0]:
        r = identity()
        i, j = (axis + 1) % 3, (axis + 2) % 3
        r[i][i] = r[j][j] = math.cos(rot[axis])
        r[i][j], r[j][i] = -math.sin(rot[axis]), math.sin(rot[axis])
        m = mul(m, r)
    for i in range(3):
        for j in range(3):
            m[i][j] *= scale[j]
    return m

def sample(keys, t, default):
    if not keys:
        return default[:]
    keys = sorted(keys, key=lambda k: k['time'])
    values = [[float(k['data_points'][0][axis]) for axis in 'xyz'] for k in keys]
    j = next((i for i, k in enumerate(keys) if k['time'] >= t), len(keys) - 1)
    i = max(0, j - 1)
    u = max(0, min(1, (t - keys[i]['time']) / (keys[j]['time'] - keys[i]['time']))) if i != j else 0
    if keys[j]['interpolation'] == 'linear':
        return [values[i][a] * (1-u) + values[j][a] * u for a in range(3)]
    p0, p1, p2, p3 = values[max(0, i-1)], values[i], values[j], values[min(len(keys)-1, j+1)]
    return [0.5 * (2*p1[a] + (p2[a]-p0[a])*u + (2*p0[a]-5*p1[a]+4*p2[a]-p3[a])*u*u + (3*p1[a]-p0[a]-3*p2[a]+p3[a])*u*u*u) for a in range(3)]

parent_ids = {}
def ancestry(node, parent=None):
    parent_ids[node['uuid']] = parent
    for c in node['children']:
        if isinstance(c, dict):
            ancestry(c, node['uuid'])
for n in data['outliner']:
    ancestry(n)

anchors = [('HEART', 'Pulsing heart', [3.5, 35.75, 6]), ('EYE', 'Central Eye', [0, 52.75, -7]), ('LEFT_HAND', 'Rock_13', [-20, 25, 6]), ('RIGHT_HAND', 'Rock_5', [12, 25, 6]), ('MOUTH', 'Mouth Lower', [0, 45.25, -5]), ('LEFT_EYE', 'Left Eye', [-7, 50.75, -6.25]), ('RIGHT_EYE', 'Right Eye', [7, 50.75, -6.25])]
pose = 'package org.exodusstudio.stellaris.common.entities.mobs.heartofluna;\n\nimport net.minecraft.world.phys.Vec3;\n\npublic final class HeartOfLunaPose {\n    public static final float SCALE = 1.6F;\n'
for i, (name, _, _) in enumerate(anchors):
    pose += f'    public static final int {name} = {i};\n'
pose += '    private static final float[][][] SAMPLES = {\n' + ',\n'.join('        {' + ', '.join(f'pose{a}_{b}()' for b in range(len(anchors))) + '}' for a in range(7)) + '\n    };\n'
for ai, a in enumerate(data['animations']):
    for bi, (_, bone, center) in enumerate(anchors):
        gid = next(g['uuid'] for g in groups.values() if g['name'] == bone)
        chain = []
        current = gid
        while current:
            chain.insert(0, current)
            current = parent_ids[current]
        results = []
        for frame in range(math.ceil(a['length'] * 40) + 1):
            t = min(frame / 40, a['length'])
            matrix = identity()
            previous = [0, -24, 0]
            for ident in chain:
                g = groups[ident]
                base = native(g['origin'])
                keys = a['animators'].get(ident, {}).get('keyframes', [])
                position = animation_vector(sample([k for k in keys if k['channel']=='position'], t, [0, 0, 0]), 'position', a['name'])
                position[1] = -position[1]
                rotation = animation_vector(sample([k for k in keys if k['channel']=='rotation'], t, [0, 0, 0]), 'rotation', a['name'])
                rotation = [math.radians(rotation[i] + native(g.get('rotation', [0,0,0]))[i]) for i in range(3)]
                scale = sample([k for k in keys if k['channel']=='scale'], t, [1,1,1])
                matrix = mul(matrix, transform([base[i]-previous[i]+position[i] for i in range(3)], rotation, scale))
                previous = base
            local = [native(center)[i]-previous[i] for i in range(3)] + [1]
            point = [sum(matrix[i][j]*local[j] for j in range(4)) for i in range(3)]
            results.extend([point[0]/16*1.6, (24-point[1])/16*1.6+0.0016, -point[2]/16*1.6])
        pose += f'    private static float[] pose{ai}_{bi}() {{\n        return new float[] {{' + ', '.join(f(v) for v in results) + '};\n    }\n'
pose += '''    public static Vec3 local(int animation, float ticks, int anchor) {
        float[] values = SAMPLES[animation][anchor];
        float frame = Math.max(0, ticks * 2);
        if (animation < 2) frame %= animation == 0 ? 64.0F : 46.6668F;
        frame = Math.min(frame, values.length / 3.0F - 1);
        int start = (int) frame * 3;
        int end = Math.min(start + 3, values.length - 3);
        float alpha = frame - (int) frame;
        return new Vec3(values[start] + (values[end] - values[start]) * alpha,
                values[start + 1] + (values[end + 1] - values[start + 1]) * alpha,
                values[start + 2] + (values[end + 2] - values[start + 2]) * alpha);
    }

    private HeartOfLunaPose() {}
}
'''
(common / 'HeartOfLunaPose.java').write_text(pose, encoding='utf-8')

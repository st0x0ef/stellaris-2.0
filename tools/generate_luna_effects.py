import math
import pathlib
import struct
import zlib

target = pathlib.Path(__file__).resolve().parents[1] / 'common/src/main/resources/assets/stellaris/textures/effect'
target.mkdir(parents=True, exist_ok=True)

def chunk(kind, data):
    return struct.pack('>I', len(data)) + kind + data + struct.pack('>I', zlib.crc32(kind + data))

def texture(name, width, height, sample):
    rows = bytearray()
    for y in range(height):
        rows.append(0)
        for x in range(width):
            alpha = max(0, min(255, round(sample(x / (width - 1), y / (height - 1)) * 255)))
            rows.extend((255, 255, 255, alpha))
    png = b'\x89PNG\r\n\x1a\n' + chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    png += chunk(b'IDAT', zlib.compress(bytes(rows), 9)) + chunk(b'IEND', b'')
    (target / (name + '.png')).write_bytes(png)
    (target / (name + '.png.mcmeta')).write_text('{"texture":{"blur":true,"clamp":false}}\n')

def glow(u, v):
    radius = math.hypot(u * 2 - 1, v * 2 - 1)
    return max(0, math.exp(-radius * radius * 5.5) - math.exp(-5.5)) * max(0, 1 - radius ** 8)

def ribbon(u, v):
    edge = math.sin(u * math.pi) ** 2
    flow = 0.62 + 0.22 * math.sin(v * math.tau * 3 + math.sin(u * math.tau) * 1.7)
    flow += 0.12 * math.sin(v * math.tau * 7 - u * math.tau * 2)
    return edge * flow

texture('luna_glow', 128, 128, glow)
texture('luna_ribbon', 128, 256, ribbon)
print('Generated Luna glow and scrolling ribbon textures')

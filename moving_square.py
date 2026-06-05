import pygame
import sys
import math

# --- Init ---
pygame.init()

WIDTH, HEIGHT = 800, 600
FPS = 60

screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Moving Square")
clock = pygame.time.Clock()

# --- Colors ---
BG_TOP    = (15, 12, 41)
BG_BOTTOM = (48, 43, 99)
SQUARE_COLOR_A = (94, 234, 212)   # teal
SQUARE_COLOR_B = (168, 85, 247)   # purple
TRAIL_COLOR    = (255, 255, 255)
GLOW_COLOR     = (94, 234, 212, 60)

# --- Square settings ---
SQUARE_SIZE = 50
SPEED = 5

square_x = WIDTH  // 2 - SQUARE_SIZE // 2
square_y = HEIGHT // 2 - SQUARE_SIZE // 2

# Trail
trail = []
TRAIL_LENGTH = 20

# Rotation angle
angle = 0

# --- Helpers ---

def lerp_color(c1, c2, t):
    return tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))

def draw_gradient_bg(surface, top_color, bot_color):
    for y in range(HEIGHT):
        t = y / HEIGHT
        color = lerp_color(top_color, bot_color, t)
        pygame.draw.line(surface, color, (0, y), (WIDTH, y))

def draw_glow(surface, x, y, size, color, radius=30):
    glow_surf = pygame.Surface((size + radius * 2, size + radius * 2), pygame.SRCALPHA)
    for r in range(radius, 0, -5):
        alpha = int(80 * (1 - r / radius))
        pygame.draw.rect(
            glow_surf,
            (*color[:3], alpha),
            (radius - r, radius - r, size + r * 2, size + r * 2),
            border_radius=8
        )
    surface.blit(glow_surf, (x - radius, y - radius))

def draw_rotated_square(surface, color, cx, cy, size, angle_deg):
    half = size / 2
    corners = [
        (-half, -half),
        ( half, -half),
        ( half,  half),
        (-half,  half),
    ]
    rad = math.radians(angle_deg)
    cos_a, sin_a = math.cos(rad), math.sin(rad)
    rotated = [
        (cx + x * cos_a - y * sin_a,
         cy + x * sin_a + y * cos_a)
        for x, y in corners
    ]
    pygame.draw.polygon(surface, color, rotated)
    pygame.draw.polygon(surface, (255, 255, 255, 80), rotated, 2)

def draw_trail(surface, trail_points):
    for i, (tx, ty, ta) in enumerate(trail_points):
        alpha = int(180 * (i / len(trail_points)))
        t = i / len(trail_points)
        color = lerp_color(SQUARE_COLOR_B, SQUARE_COLOR_A, t)
        size = int(SQUARE_SIZE * 0.6 * (i / len(trail_points)))
        if size < 4:
            continue
        trail_surf = pygame.Surface((size, size), pygame.SRCALPHA)
        trail_surf.fill((*color, alpha))
        surface.blit(trail_surf, (tx - size // 2, ty - size // 2))

# --- HUD font ---
font = pygame.font.SysFont("Segoe UI", 18)

# --- Main loop ---
running = True
while running:
    dt = clock.tick(FPS)

    # Events
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
        if event.type == pygame.KEYDOWN:
            if event.key == pygame.K_ESCAPE:
                running = False

    # Movement
    keys = pygame.key.get_pressed()
    moving = False

    if keys[pygame.K_LEFT]  or keys[pygame.K_a]:
        square_x -= SPEED
        moving = True
    if keys[pygame.K_RIGHT] or keys[pygame.K_d]:
        square_x += SPEED
        moving = True
    if keys[pygame.K_UP]    or keys[pygame.K_w]:
        square_y -= SPEED
        moving = True
    if keys[pygame.K_DOWN]  or keys[pygame.K_s]:
        square_y += SPEED
        moving = True

    # Keep in bounds
    square_x = max(0, min(WIDTH  - SQUARE_SIZE, square_x))
    square_y = max(0, min(HEIGHT - SQUARE_SIZE, square_y))

    # Rotation
    if moving:
        angle += 4
    else:
        angle += 0.5   # slow spin when idle

    # Trail
    cx = square_x + SQUARE_SIZE // 2
    cy = square_y + SQUARE_SIZE // 2
    trail.append((cx, cy, angle))
    if len(trail) > TRAIL_LENGTH:
        trail.pop(0)

    # Pulsing color
    t_color = (math.sin(pygame.time.get_ticks() * 0.002) + 1) / 2
    square_color = lerp_color(SQUARE_COLOR_A, SQUARE_COLOR_B, t_color)

    # --- Draw ---
    draw_gradient_bg(screen, BG_TOP, BG_BOTTOM)

    # Grid lines (subtle)
    for gx in range(0, WIDTH, 80):
        pygame.draw.line(screen, (255, 255, 255, 10), (gx, 0), (gx, HEIGHT), 1)
    for gy in range(0, HEIGHT, 80):
        pygame.draw.line(screen, (255, 255, 255, 10), (0, gy), (WIDTH, gy), 1)

    draw_trail(screen, trail)
    draw_glow(screen, square_x, square_y, SQUARE_SIZE, square_color)
    draw_rotated_square(screen, square_color, cx, cy, SQUARE_SIZE, angle)

    # HUD
    hud = font.render("WASD / Arrow Keys to move  |  ESC to quit", True, (200, 200, 220))
    screen.blit(hud, (10, HEIGHT - 30))

    pos_text = font.render(f"x: {square_x}  y: {square_y}", True, (160, 160, 180))
    screen.blit(pos_text, (10, 10))

    pygame.display.flip()

pygame.quit()
sys.exit()

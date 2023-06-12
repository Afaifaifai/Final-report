import pygame as pg

FPS = 1000
FIELD_COLOR = '#444444'
BG_COLOR = '#AAAAAA'
WIDTH = 10
HEIGHT = 20
TILE = 30
RES = WIDTH * TILE, HEIGHT * TILE

SCALE_W, SCALE_H = 1.7, 1.0
WIN_RES = WIN_W, WIN_H = RES[0] * SCALE_W, RES[1] * SCALE_H
SIZE = WIDTH, HEIGHT

TETROMINO = {
    'T': { # T ## 90 270
            0: [(1,0), (0,1), (1,1), (2,1)],
            90: [(0,1), (1,2), (1,1), (1,0)],
            180: [(1,2), (2,1), (1,1), (0,1)],
            270: [(2,1), (1,0), (1,1), (1,2)]
        },
    'O': { # O
            0: [(1,0), (2,0), (1,1), (2,1)],
            90: [(1,0), (2,0), (1,1), (2,1)],
            180: [(1,0), (2,0), (1,1), (2,1)],
            270: [(1,0), (2,0), (1,1), (2,1)]
        },
    'J': { # J ## 90 270
            0: [(1,0), (1,1), (1,2), (0,2)],
            90: [(0,1), (1,1), (2,1), (2,2)],
            180: [(1,2), (1,1), (1,0), (2,0)],
            270: [(2,1), (1,1), (0,1), (0,0)]
        },
    'L': { # L
            0: [(1,0), (1,1), (1,2), (2,2)],
            90: [(0,1), (1,1), (2,1), (2,0)],
            180: [(1,2), (1,1), (1,0), (0,0)],
            270: [(2,1), (1,1), (0,1), (0,2)]
        },
    'I': { # I
            0: [(0,0), (1,0), (2,0), (3,0)],
            90: [(1,0), (1,1), (1,2), (1,3)],
            180: [(3,0), (2,0), (1,0), (0,0)],
            270: [(1,3), (1,2), (1,1), (1,0)]
        },
#         
    'S': { # S
            0: [(2,0), (1,0), (1,1), (0,1)],
            90: [(0,0), (0,1), (1,1), (1,2)],
            180: [(0,1), (1,1), (1,0), (2,0)],
            270: [(1,2), (1,1), (0,1), (0,0)]
        },
    'Z': { # Z
            0: [(0,0), (1,0), (1,1), (2,1)],
            90: [(0,2), (0,1), (1,1), (1,0)],
            180: [(2,1), (1,1), (1,0), (0,0)],
            270: [(1,0), (1,1), (0,1), (0,2)]
        }
}
COLORS = {
    'T': 'purple',
    'O': 'yellow',
    'J': 'blue',
    'L': 'orange',
    'I': '#00FFFF',
    'S': '#00FF00',
    'Z': '#FF3030'
}

MOVE_DIRECTIONS = {'L' : (-1, 0), 'R' : (1, 0), 'D' : (0, 1)} # left, right, down
vec = pg.math.Vector2
INIT_POS_OFFSET = vec(WIDTH // 2 - 1, 0) # 4
NEXT_POS_OFFSET = vec(WIDTH * 1.3, HEIGHT * 0.45)

ANIM_TIME_INTERNAL = 1 # 萬分之一秒
FAST_ANIM_TIME_INTERVAL = 15

MAP_EMPTY = 0
MAP_BLOCK = 1 
MAP_PLAYER = 2
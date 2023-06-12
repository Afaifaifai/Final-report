from setting import *
import random 

class Tetromino:
    def __init__(self, tetris, shape, current=True):
        self.tetris = tetris
        self.shape = shape
        self.angle = 0
        self.blocks = [Block(self, pos+vec(0, -5)) for pos in TETROMINO[self.shape][self.angle]]
        self.landing = False
        self.current = current
        self.pivot_angle = 0
        self.is_already_landing = False
    
    def rotate(self):
        pivot_angle = (self.angle + 90) % 360
        new_block_positions = []
        for i in range(len(self.blocks)):
            temp = self.blocks[i].pos + TETROMINO[self.shape][pivot_angle][i] - TETROMINO[self.shape][self.angle][i]
            new_block_positions.append(temp)

        # 檢查旋轉附近有沒有方塊擋住
        if not self.is_collide(new_block_positions):
            for i, block in enumerate(self.blocks):
                block.pos = new_block_positions[i]
            self.angle = pivot_angle

    def is_collide(self, block_positions):
        return any(map(Block.is_collide, self.blocks, block_positions))
    
    def move(self, direction):
        move_direction = MOVE_DIRECTIONS[direction]
        new_block_positions = [block.pos + move_direction for block in self.blocks]
        is_collide = self.is_collide(new_block_positions)

        if not(is_collide):
            for block in self.blocks:
                block.pos += move_direction
        elif direction == 'D':
            self.landing = True # 每update一次，就會觸發一次move down，landing一變成True，Tetris的check_tetromino_landing()就會觸發

    def update(self):
        if not self.is_already_landing:
            self.move(direction = 'D') # 固定向下

class Block(pg.sprite.Sprite):
    def __init__(self, tetromino, pos):
        self.tetromino = tetromino
        self.pos = vec(pos) + INIT_POS_OFFSET # 方塊位置轉換為vector，並加上指向中央的位置，使方塊出現在中央
        self.static_in_array = 0
        self.next_pos = vec(pos) + NEXT_POS_OFFSET + vec(-1, 3)
        self.alive = True # 判斷方塊消除
        
        super().__init__(tetromino.tetris.sprite_group)
        self.image = pg.Surface([TILE, TILE])
        pg.draw.rect(self.image, COLORS[self.tetromino.shape], (1, 1, TILE - 2, TILE - 2), border_radius=8)
        self.rect = self.image.get_rect()

    def is_alive(self):
        if not self.alive:
            self.kill()

    def set_rect_pos(self):
        pos = [self.next_pos, self.pos][self.tetromino.current]
        self.rect.topleft = pos * TILE

    def update(self):
        self.is_alive()
        self.set_rect_pos()

    def is_collide(self, pos):
        x, y = int(pos[0]), int(pos[1])
        if (0 <= x < WIDTH) and (y < HEIGHT) and (y < 0 or not self.tetromino.tetris.field_array[y][x]):
            return False
        return True
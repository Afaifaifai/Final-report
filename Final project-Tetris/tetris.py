import pygame as pg
from setting import *
from tetromino import Tetromino
import pygame.font as pf
import random

class Text:
    def __init__(self, app):
        self.app = app       
        self.font = pg.font.Font('freesansbold.ttf', 40)
        self.font2 = pg.font.Font('freesansbold.ttf', 20)
        self.font3 = pg.font.Font('freesansbold.ttf', 30)

    def draw(self):
        text1 = self.font.render('TETRIS', True, 'black')
        self.app.screen.blit(text1, (WIN_W * 0.65, WIN_H*0.1))
        text2 = self.font2.render(f'Score: {round(self.app.tetris.score, 2)}', True, 'black')
        self.app.screen.blit(text2, (WIN_W * 0.675, WIN_H*0.7))
        text3 = self.font2.render(f'Lines: {round(self.app.tetris.full_lines)}', True, 'black')
        self.app.screen.blit(text3, (WIN_W * 0.675, WIN_H*0.75))
        text4 = self.font2.render(f'Count: {round(self.app.tetris.counts)}', True, 'black')
        self.app.screen.blit(text4, (WIN_W * 0.675, WIN_H*0.8))
        text5 = self.font2.render(f'Episodes: {self.app.episodes}', True, 'black')
        self.app.screen.blit(text5, (WIN_W * 0.675, WIN_H*0.65))
        text6 = self.font3.render(f'NEXT', True, 'black')
        self.app.screen.blit(text6, (WIN_W * 0.7, WIN_H*0.25))

class Tetris:

    def __init__(self, app):
        self.app = app
        self.reset()
        
    def reset(self):
        self.FPS = 1000
        self.sprite_group = pg.sprite.Group()
        self.field_array = [[0 for x in range(WIDTH)] for y in range(HEIGHT)]
        self.board = [[0 for x in range(WIDTH)] for y in range(HEIGHT)]
        self.shape_list = list(TETROMINO.keys())
        random.shuffle(self.shape_list)
        self.tetromino = Tetromino(self, self.shape_list.pop())
        self.tetromino.is_already_landing = True
        self.next_tetromino = Tetromino(self, self.shape_list.pop(), current=False)
        self.speed_up = False
        # score
        self.score = 0
        self.lines = 0
        self.full_lines = 0
        self.counts = 0
        self.points_per_line = {0 : 0, 1 : 100, 2 : 300, 3 : 600, 4 : 1200}
        self.game_over = False
        self.landing_height = 0
        self.pause = False
        
    def new_round(self):
        self.tetromino.is_already_landing = False
   
    def get_score(self):
        self.score += self.points_per_line[self.lines]
        self.lines = 0

    def update_board(self):
        for y in range(HEIGHT):
            for x in range(WIDTH):
                if self.field_array[y][x]:
                    self.board[y][x] = MAP_BLOCK

    def is_game_over(self):
        if sum(self.board[0]) and (self.tetromino.blocks[0].pos[1] <= 0\
            or self.tetromino.blocks[1].pos[1] <= 0\
            or self.tetromino.blocks[2].pos[1] <= 0\
            or self.tetromino.blocks[3].pos[1] <= 0):
            return True
        return False

    def check_full_lines(self):
        row = HEIGHT - 1
        for y in range(HEIGHT-1, -1, -1):
            for x in range(WIDTH):
                self.field_array[row][x] = self.field_array[y][x]

                if self.field_array[y][x]:
                    self.field_array[row][x].pos = vec(x, y)
            
            if sum(map(bool, self.field_array[y])) < WIDTH:
                row -= 1
            else:
                for x in range(WIDTH):
                    self.field_array[row][x].alive = False
                    self.field_array[row][x] = 0
                self.lines += 1
                self.full_lines += 1
        self.update_board()

    
    def put_tetromino_blocks_in_array(self):
        for block in self.tetromino.blocks:
            x, y = int(block.pos.x), int(block.pos.y)
            self.field_array[y][x] = block
            self.board[y][x] = MAP_BLOCK

    def check_tetromino_landing(self):
        if self.tetromino.landing:
            self.times = 0      
            if self.is_game_over():
                self.game_over = True
                self.tetromino.is_already_landing = True
            else:
                self.speed_up = False
                self.score += 1 # when alive, +1
                self.counts += 1 # number of tetromino have landed
                self.put_tetromino_blocks_in_array()
                self.update_board()
                self.landing_height = self.get_landing_height()
                """ 現在的方塊換成下一個 """
                self.next_tetromino.current = True
                self.tetromino = self.next_tetromino
                self.tetromino.is_already_landing = True
                if not self.shape_list:
                    self.shape_list = list(TETROMINO.keys())
                    random.shuffle(self.shape_list)
                self.next_tetromino = Tetromino(self, self.shape_list.pop(), current=False)
                
    def AI_move(self, x, rotation):
        for i in range(4):
            self.tetromino.blocks[i].pos = TETROMINO[self.tetromino.shape][rotation][i] + vec(x, 0)
    
    def control(self, pressed_key):
        if pressed_key == pg.K_LEFT:
            self.tetromino.move(direction='L')
        elif pressed_key == pg.K_RIGHT:
            self.tetromino.move(direction='R')
        elif pressed_key == pg.K_UP:
            self.tetromino.rotate()
        elif pressed_key == pg.K_DOWN:
            self.speed_up = True
        elif pressed_key == pg.K_SPACE:
            self.pause = not self.pause

    def draw_grid(self):
        for x in range(WIDTH):
            for y in range(HEIGHT):
                pg.draw.rect(self.app.screen, 'black', (x * TILE, y * TILE, TILE, TILE), 1)

    def update(self):
        trigger = [self.app.anim_trigger, self.app.fast_anim_trigger][self.speed_up]
        if not self.pause:
            if trigger:
                self.tetromino.update()
                self.check_tetromino_landing()
                self.check_full_lines()
                self.get_score()
            self.sprite_group.update()

    def draw(self):
        self.draw_grid()
        self.sprite_group.draw(self.app.screen)
    
    def get_landing_height(self):
        h = []
        for block in self.tetromino.blocks:
            h.append(int(block.pos[1]))
        landing_height = HEIGHT - max(h)
        return landing_height
    
    """ needs for state """    
    def _clear_lines(self, board):
        '''Clears completed lines in a board'''
        # Check if lines can be cleared
        lines_to_clear = [index for index, row in enumerate(board) if sum(row) == WIDTH]
        if lines_to_clear:
            board = [row for index, row in enumerate(board) if index not in lines_to_clear]
            # Add new lines at the top
            for _ in lines_to_clear:
                board.insert(0, [0 for _ in range(WIDTH)])
        return lines_to_clear, board

    def _bumpiness(self, board):
        '''Sum of the differences of heights between pair of columns'''
        total_bumpiness = 0
        max_bumpiness = 0
        min_ys = []

        for col in zip(*board):
            i = 0
            while i < HEIGHT and col[i] != MAP_BLOCK: # 加了等於
                i += 1
            min_ys.append(i)
        
        for i in range(len(min_ys) - 1):
            bumpiness = abs(min_ys[i] - min_ys[i+1])
            max_bumpiness = max(bumpiness, max_bumpiness)
            total_bumpiness += abs(min_ys[i] - min_ys[i+1])

        return total_bumpiness, max_bumpiness

    def get_row_transitions(self, board):
        """Returns the number of horizontal cell transitions."""
        total = 0
        for y in range(HEIGHT):
            row_count = 0
            last_empty = False
            for x in range(WIDTH):
                empty = self.board[y][x] == 0
                if last_empty != empty:
                    row_count += 1
                    last_empty = empty

            if last_empty:
                row_count += 1

            if last_empty and row_count == 2:
                continue

            total += row_count
        return total

    def get_column_transitions(self, board):
        """Returns the number of vertical cell transitions."""
        total = 0
        for x in range(WIDTH):
            column_count = 0
            last_empty = False
            for y in reversed(range(HEIGHT)):
                empty = board[y][x] == 0
                if last_empty and not empty:
                    column_count += 2
                last_empty = empty

            if last_empty and column_count == 1:
                continue

            total += column_count
        return total

    def get_cumulative_wells(self, board):
        """Returns the sum of all wells."""
        wells = [0 for i in range(WIDTH)]
        for y, row in enumerate(board):
            left_empty = True
            for x, code in enumerate(row):
                if code == 0:
                    well = False
                    right_empty = WIDTH > x + 1 >= 0 and board[y][x + 1] == 0
                    if left_empty or right_empty:
                        well = True
                    wells[x] = 0 if well else wells[x] + 1
                    left_empty = True
                else:
                    left_empty = False
        return sum(wells)

    def get_aggregate_height(self, board):
        """Returns the sum of the heights of each column."""
        aggregate_height = 0
        for x in range(WIDTH):
            for y in range(HEIGHT):
                if board[y][x] != 0:
                    aggregate_height += HEIGHT - y
                    break
        return aggregate_height

    def get_hole_count(self, board):
        """returns the number of empty cells covered by a full cell."""
        hole_count = 0
        for x in range(WIDTH):
            below = False
            for y in range(HEIGHT):
                empty = board[y][x] == 0
                if not below and not empty:
                    below = True
                elif below and empty:
                    hole_count += 1

        return hole_count
    
    def put_tetromino_blocks_in_array_for_prediction(self, piece, pos):    
        '''Place a piece in the board, returning the resulting board'''        
        board = [x[:] for x in self.board]
        for x, y in piece:
            board[y + pos[1]][x + pos[0]] = MAP_BLOCK
        return board
    
    def is_collide_for_prediction(self, piece, pos):
        '''Check if there is a collision between the current piece and the board'''
        for x, y in piece:
            x += pos[0]
            y += pos[1]
            if x < 0 or x >= WIDTH \
                    or y < 0 or y >= HEIGHT \
                    or self.board[y][x]:
                return True
        return False
    
    def get_rotated_piec(self):
        return TETROMINO[self.tetromino.shape][self.tetromino.rotation]
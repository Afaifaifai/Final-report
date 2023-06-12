from setting import *
import sys
import pygame as pg
from tetris import Tetris, Text

class App:
    def __init__(self):
        pg.init()
        pg.display.set_caption('Tetris')
        self.screen = pg.display.set_mode(WIN_RES)
        self.clock = pg.time.Clock()
        self.set_timer()
        self.tetris = Tetris(self)
        self.landing = False
        self.speed_up = False
        self.text = Text(self)
        self.total_score = 0
        self.text = Text(self)
        self.episodes = 0

    def set_timer(self):
        self.user_event = pg.USEREVENT + 0
        self.fast_user_event = pg.USEREVENT + 1
        self.anim_triger = False
        self.fast_anim_trigger = False
        pg.time.set_timer(self.user_event, ANIM_TIME_INTERNAL)
        pg.time.set_timer(self.fast_user_event, FAST_ANIM_TIME_INTERVAL)

    def update(self):
        self.tetris.update()
        self.clock.tick(FPS)
        self.check_events()

    def draw(self):
        self.screen.fill(color = BG_COLOR)
        self.screen.fill(color = FIELD_COLOR, rect = (0, 0, *RES))
        self.tetris.draw()
        self.text.draw()
        self.text.draw()
        pg.display.flip()

    def check_events(self):
        self.anim_trigger = False
        self.fast_anim_trigger = False
        for event in pg.event.get():
            if event.type == pg.QUIT or (event.type == pg.KEYDOWN and event.key == pg.K_ESCAPE):
                pg.quit()
                sys.exit()
            elif event.type == pg.KEYDOWN:
                self.tetris.control(pressed_key=event.key)       
            elif event.type == self.user_event:
                self.anim_trigger = True
            elif event.type == self.fast_user_event:
                self.fast_anim_trigger = True
                
    def get_next_states(self):
        '''Get all possible next states'''
        states = {}
        piece_id = self.tetris.tetromino.shape
        
        if piece_id == 'O': 
            rotations = [0]
        elif piece_id == 'I':
            rotations = [0, 90]
        else:
            rotations = [0, 90, 180, 270]

        """ For all rotations """
        for rotation in rotations:
            piece = TETROMINO[piece_id][rotation]
            min_x = min([p[0] for p in piece])
            max_x = max([p[0] for p in piece])

            # For all positions
            for x in range(-min_x, WIDTH - max_x): # -min_x ~ WIDTH-max_x-1 為piece在特定rotation可移動範圍
                pos = [x, 0]

                # Drop piece
                while not self.tetris.is_collide_for_prediction(piece, pos):
                    pos[1] += 1
                pos[1] -= 1

                if pos[1] >= 0:
                    board = self.tetris.put_tetromino_blocks_in_array_for_prediction(piece, pos)
                    new_coords = [(x+pos[0], y+pos[1]) for x, y in piece]
                    states[(x, rotation)] = self._get_board_props(board, last_piece_coords=new_coords)
                else:
                    states[(0, 0)] = [0 for x in range(9)]

        return states
    
    def new_round(self):
        self.tetris.tetromino.is_already_landing = False

    def reset(self):
        self.tetris.reset()
        return self._get_board_props(self.tetris.board) # array 為初始狀態(全都是0)，可以直接傳
    
    def _get_board_props(self, board, last_piece_coords=[(0, 0)]):
        '''Get properties of the board'''
        lines, board = self.tetris._clear_lines(board)
        total_bumpiness, max_bumpiness = self.tetris._bumpiness(board)
        
        eroded_piece_cells = len(lines) * sum(y in lines for x, y in last_piece_coords) ####
        if board == [[0 for x in range(WIDTH)] for y in range(HEIGHT)]:
            landing_height = 0           
        else:
            landing_height = 20 -max(last_piece_coords[0][1], last_piece_coords[1][1], last_piece_coords[2][1], last_piece_coords[3][1]) - len(lines)

        return [
                len(lines),                                 # 消除的行數
                total_bumpiness,                            # 每兩直行的高度差總和
                self.tetris.get_hole_count(board),          # 上方有方塊遮擋而產生的空洞
                landing_height,                             # 方塊預計下降的高度
                self.tetris.get_row_transitions(board),     # 水平方向的方塊交錯情形
                self.tetris.get_column_transitions(board),  # 垂直方向的方塊交錯情形
                self.tetris.get_cumulative_wells(board),    # 所有的直行(上方無遮擋方塊)空洞總和
                eroded_piece_cells,                         # 方塊放置後消除的行數 * 方塊本身被消除的數量，EX:消除的1行有2個剛放置的方塊 
                self.tetris.get_aggregate_height(board),    # 每直行的高度總和
            ]

    def run(self):
        self.check_events()
        self.update()
        self.draw()

    def play(self, x, rotation, render=False, render_delay=None):
        '''Makes a play given a position and a rotation, returning the reward and if the game is over'''
        for block in self.tetris.tetromino.blocks:
            block.pos[1] += 4
        self.tetris.AI_move(x, rotation)
        
        score = self.tetris.score
        score += (10 - self.tetris.landing_height) * 10
        self.total_score += score
        
        # Start new round
        if self.tetris.game_over:
            if self.episodes >= 1500:
                score -= 200
            else:
                score -= 20
        return score, self.tetris.game_over
    
    def get_game_score(self):
        return self.total_score
    
    def quit(self):
        pg.quit()
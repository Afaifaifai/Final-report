from keras.layers import Dense
from keras.models import Sequential
from collections import deque
import numpy as np
import random
import os
from pathlib import Path
import keras
import pickle
from app import App
from tqdm import tqdm

WEIGHT_PATH = os.path.join(os.path.dirname(__file__), 'weights-20000.h5') 
MEMORY_PATH = os.path.join(os.path.dirname(__file__), 'memory')

class DQNAgent:
    def __init__(self, state_size, mem_size=20000, discount=0.95,
                 epsilon=1, epsilon_min=0, epsilon_stop_episode=500, replay_start_size=None):
        self.state_size = state_size
        self.memory = ExperienceBuffer(buffer_size=mem_size)
        self.discount = discount
        self.epsilon = epsilon
        self.epsilon_min = epsilon_min
        self.epsilon_decay = (self.epsilon - self.epsilon_min) / (epsilon_stop_episode)
        if not replay_start_size:
            replay_start_size = mem_size / 2
        self.replay_start_size = replay_start_size
        self.model = self._build_model()
        self.env = App()
        
    def _build_model(self):
        '''Builds a Keras deep neural network model'''
        model = Sequential([
            Dense(64, input_dim=self.state_size, activation='relu'),
            Dense(64, activation='relu'),
            Dense(32, activation='relu'),
            Dense(1, activation='linear')
        ])
        
        model.compile(loss='mse', optimizer='adam', metrics=['mean_squared_error'])        
        return model  
    
    def load(self):
        """Load the model and wxperience."""
        if Path(WEIGHT_PATH).is_file():
            self.model = keras.models.load_model(WEIGHT_PATH)
        if Path(MEMORY_PATH).is_file():
            with open('memory.pickle', 'rb') as f:
                self.memory = pickle.load(f)

    def save(self):
        """Save the mdoel and experience."""
        if not os.path.exists(os.path.dirname(WEIGHT_PATH)):
            os.makedirs(os.path.dirname(WEIGHT_PATH))

        self.model.save(WEIGHT_PATH)
        with open('memory.pickle', 'wb') as f:
            pickle.dump(self.memory, f)

    def add_to_memory(self, current_state, next_state, reward, done):
        '''Adds a play to the replay memory buffer'''
        self.memory.add((current_state, next_state, reward, done))

    def random_value(self):
        '''Random score for a certain action'''
        return random.random()

    def predict_value(self, state):
        '''Predicts the score for a certain state'''
        return self.model.predict(state)[0]

    def act(self, state):
        '''Returns the expected score of a certain state'''
        state = np.reshape(state, [1, self.state_size])
        if random.random() <= self.epsilon:
            return self.random_value()
        else:
            return self.predict_value(state)


    def best_state(self, states):
        '''Returns the best state for a given collection of states'''
        max_value = None
        best_state = None

        if random.random() <= self.epsilon:
            return random.choice(list(states))

        else:
            for state in states:
                value = self.predict_value(np.reshape(state, [1, 9]))
                if not max_value or value > max_value:
                    max_value = value
                    best_state = state

        return best_state
    
    def train(self, episodes=2500):
        scores = []
        result = [['Episode, ', 'Score, ', 'Lines, ', 'Counts\n']]
        for episode in tqdm(range(episodes)):
            current_state = self.env.reset()
            done = False
            steps = 0
            max_steps = 0
            
            # Game
            self.env.episodes = episode + 1
            while not done and (not max_steps or steps < max_steps):
                self.env.run()
                if self.env.tetris.tetromino.is_already_landing:
                    next_states = self.env.get_next_states()
                    best_state = self.best_state(next_states.values())

                    best_action = None
                    for action, state in next_states.items():
                        if state == best_state:
                            best_action = action
                            break
                    self.env.new_round()
                    reward, done = self.env.play(best_action[0], best_action[1])
                    self.add_to_memory(current_state, next_states[best_action], reward, done)
                    current_state = next_states[best_action]
                    steps += 1

            # Learn
            self.learn(batch_size=512, epochs=1)

            scores.append(reward)
            result.append([(str(episode+1)+", "), (str(round(self.env.tetris.score))+", "), (str(self.env.tetris.full_lines)+", "),
                           (str(self.env.tetris.counts)+"\n")])

            if (episode+1) % 500 == 0:
                self.save()
        self.env.quit()
        return scores, result        
        
    def learn(self, batch_size=32, epochs=3):
        '''Trains the agent'''
        n = len(self.memory.buffer)
    
        if n >= self.replay_start_size and n >= batch_size:

            batch = random.sample(self.memory.buffer, batch_size) # (current_state, next_state, reward, done)

            # Get the expected score for the next states, in batch (better performance)
            next_states = np.array([x[1] for x in batch])
            next_qs = [x[0] for x in self.model.predict(next_states)]

            x = []
            y = []

            # Build xy structure to fit the model in batch (better performance)
            for i, (state, _, reward, done) in enumerate(batch):
                if not done:
                    # Partial Q formula
                    new_q = reward + self.discount * next_qs[i]
                else:
                    new_q = reward

                x.append(state)
                y.append(new_q)

            # Fit the model to the given values
            self.model.fit(np.array(x), np.array(y), batch_size=batch_size, epochs=epochs, verbose=0)

            # Update the exploration variable
            if self.epsilon > self.epsilon_min:
                self.epsilon -= self.epsilon_decay
                
class ExperienceBuffer:
    def __init__(self, buffer_size=20000):
        self.buffer = deque(maxlen=buffer_size)

    def add(self, experience):
        self.buffer.append(experience)
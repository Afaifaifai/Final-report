import java.util.concurrent.atomic.AtomicBoolean;

public class Settings {
    final int difficulty = 4;
    final AtomicBoolean running = new AtomicBoolean(true);
    final int MERGE_LIMIT = 3;
    final int TIME_SLEEP = 1;
    final Block INITIAL_BLOCK = new Block(0, "0", "Initial Block", System.currentTimeMillis(), 0);
    final int RESET_FORK_INDEX = -1;

    // render usage
    final int WIDTH = 1920;
    final int HEIGHT = 1000;

    final int RECT_WIDTH = 200;
    final int RECT_HEIGHT = 250;


    // final int MID_PANE__WIDTH = 300;
    // final int MID_PANE__HEIGHT = HEIGHT - 20;

    // final int LEFT_PANE__WIDTH = (WIDTH - MID_PANE__WIDTH) / 2;
    // final int LEFT_PANE__HEIGHT = HEIGHT - 20;

    // final int RIGHT_PANE__WIDTH = (WIDTH - MID_PANE__WIDTH) / 2;
    // final int RIGHT_PANE__HEIGHT = HEIGHT - 20;
    final int MID_PANE__WIDTH = 300;
    final int MID_PANE__HEIGHT = HEIGHT - 20;

    final int LEFT_PANE__WIDTH = WIDTH - MID_PANE__WIDTH;
    final int LEFT_PANE__HEIGHT = HEIGHT / 2;

    final int RIGHT_PANE__WIDTH = WIDTH - MID_PANE__WIDTH;
    final int RIGHT_PANE__HEIGHT = HEIGHT / 2;

    final int RECT_X_COORD = 50;
    final int RECT_Y_COORD = (LEFT_PANE__HEIGHT - RECT_HEIGHT) / 2;
    final double FORK_UP_Y_COORD = (LEFT_PANE__HEIGHT - RECT_HEIGHT) / 2 - RECT_HEIGHT / 2 * 1.05;
    final double FORK_DOWN_Y_COORD = (LEFT_PANE__HEIGHT - RECT_HEIGHT) / 2 + RECT_HEIGHT / 2 * 1.05;

    final int NODE_WIDTH = 35;
    final double EXPAND_SIZE = RECT_WIDTH * 1.5;
    final int MAX_BLOCK_EDGE = LEFT_PANE__WIDTH / RECT_WIDTH;
    final String[] LABEL_NAME = {"Index : ", "Previous Hash : ", "Data : ", "Time Stamp : ", "Nonce : ", "Hash : "};

    final int FONT_SIZE = 10; 
    final int PANE_HEIGHT = LEFT_PANE__HEIGHT / 2;
}

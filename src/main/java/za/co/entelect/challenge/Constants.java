package za.co.entelect.challenge;

import za.co.entelect.challenge.domain.XY;

import java.awt.*;
import java.util.*;
import static za.co.entelect.challenge.Util.R;

public class Constants {

    public static final long DEFAULT_TICK_INTERVAL = 3000;

    public static final long THINK_TIME = 4 * 1000 - 500;
    public static final long THINK_TIME_GRACE = 200;

    public static final String APP_TITLE = "<<p.a.c.m.a.n>>";

    public static final String OUTPUT_FILE_NAME = "game.state";

    public static final Color COLOR_SWING_BLANK = Color.black;

    public static final int ZOOM_LEVEL = 25;

    public static final int MAX_MOVES_NO_PILLS = 100;

    public static final int WIDTH = 19;
    public static final int HEIGHT = 22;
    public static final int MX = 10;
    public static final int MY = 11;
    public static final int PORTAL_X = 9;
    public static final int PORTAL_Y = 10;
    public static final XY PORTAL = new XY(PORTAL_X, PORTAL_Y);
    public static final XY WARP_A = new XY(0, 10);
    public static final XY WARP_B = new XY(18, 10);

    public static final float MAX_MAZE_DIST = 44;

    public static final char WALL = '#';
    public static final char SPACE = ' ';
    public static final char PILL = '.';
    public static final char BONUS_PILL = '*';
    public static final char POISON_PILL = '!';
    public static final char PLAYER_A = 'A';
    public static final char PLAYER_B = 'B';

    public static final int ZOBRIST_WALL = 0;
    public static final int ZOBRIST_SPACE = 1;
    public static final int ZOBRIST_PILL = 2;
    public static final int ZOBRIST_BONUS_PILL = 3;
    public static final int ZOBRIST_POISON_PILL = 4;
    public static final int ZOBRIST_PLAYER_A = 5;
    public static final int ZOBRIST_PLAYER_B = 6;
    public static final int ZOBRIST_NUM_STATES = 7;

    public static final Map<Character, Integer> ZOBRIST_MAP = new HashMap<>();
    static {
        ZOBRIST_MAP.put(WALL, ZOBRIST_WALL);
        ZOBRIST_MAP.put(SPACE, ZOBRIST_SPACE);
        ZOBRIST_MAP.put(PILL, ZOBRIST_PILL);
        ZOBRIST_MAP.put(BONUS_PILL, ZOBRIST_BONUS_PILL);
        ZOBRIST_MAP.put(POISON_PILL, ZOBRIST_POISON_PILL);
        ZOBRIST_MAP.put(PLAYER_A, ZOBRIST_PLAYER_A);
        ZOBRIST_MAP.put(PLAYER_B, ZOBRIST_PLAYER_B);
    }

    public static final long[][][] ZOBRIST = new long[Constants.WIDTH][Constants.HEIGHT][Constants.ZOBRIST_NUM_STATES];
    static {
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {
                for (int k = 0; k < Constants.ZOBRIST_NUM_STATES; k++) {
                    ZOBRIST[i][j][k] = R.nextLong();
                }
            }
        }
    }

    public static final Color PLAYER_A_COLOR = new Color(168, 255, 125);
    public static final Color PLAYER_B_COLOR = new Color(255, 188, 76);
    public static final Color SEARCH_PATH_COLOR = new Color(150, 121, 255, 180);
    public static final Color WALL_CEMENT_COLOR = new Color(255, 255, 255, 10);
    public static final Color CLUSTER_PILL_COLOR = new Color(246, 243, 64, 64);

    public static final int PILL_SCORE = 1;
    public static final int BONUS_PILL_SCORE = 10;

    public static final Map<Character, Color> COLOR_MAP = new HashMap<>();
    static {
        COLOR_MAP.put(WALL, Color.gray);
        COLOR_MAP.put(SPACE, Color.black);
        COLOR_MAP.put(PILL, Color.white);
        COLOR_MAP.put(BONUS_PILL, Color.yellow);
        COLOR_MAP.put(POISON_PILL, Color.green);
        COLOR_MAP.put(PLAYER_A, PLAYER_A_COLOR);
        COLOR_MAP.put(PLAYER_B, PLAYER_B_COLOR);
    }

    public static final Set<XY> SPAWN_ZONE = new HashSet<>();
    static {
        SPAWN_ZONE.add(new XY(9, 9));
        SPAWN_ZONE.add(new XY(8, 10));
        SPAWN_ZONE.add(new XY(9, 10));
        SPAWN_ZONE.add(new XY(10, 10));
        SPAWN_ZONE.add(new XY(9, 11));
    }

    public static final int[][] WALLS = new int[][]{{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,0,1},
            {1,0,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,0,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1},
            {1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,0,0,0,0,0,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,1},
            {0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,0,0,0},
            {1,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,0,0,0,0,0,0,1,0,1,1,1,1},
            {1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1},
            {1,0,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,0,1},
            {1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1},
            {1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1},
            {1,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,1},
            {1,0,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
}


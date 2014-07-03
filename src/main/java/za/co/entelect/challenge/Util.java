package za.co.entelect.challenge;

import com.codahale.metrics.MetricRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.ai.search.*;
import za.co.entelect.challenge.domain.*;
import za.co.entelect.challenge.groovy.GameFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class Util {

    public static final Logger logger = LoggerFactory.getLogger(Util.class);

    public static Random R = new Random();
    public static final MetricRegistry metrics = new MetricRegistry();
    /*
    static {
        JmxReporter.forRegistry(metrics).registerWith(ManagementFactory.getPlatformMBeanServer()).build().start();
    }
    */

    public static int manhattanDist(XY start, XY end) {
        return manhattanDist(start.x, start.y, end.x, end.y);
    }

    public static int manhattanDist(int startX, int startY, int endX, int endY) {
        return Math.abs(startX - endX) + Math.abs(startY - endY);
    }

    public static float falloff(int dist) {
        //return (float)Math.max(0, 1 - dist * 0.2);
        return 1 / (float)Math.sqrt(dist + 1);
        //return Math.min(1, 1 - dist / (Constants.MAX_MAZE_DIST));
       // return (float) Math.pow(0.9, dist + 1);
        //return 1f;
    }

    public static boolean isWall(XY pos) {
        return isWall(pos.x, pos.y);
    }

    public static boolean isWall(int x, int y) {
        return Constants.WALLS[y][x] == 1;
    }

    public static boolean inSpawnZone(int x, int y) {
        return inSpawnZone(new XY(x, y));
    }

    public static boolean inSpawnZone(XY point) {
        return Constants.SPAWN_ZONE.contains(point);
    }

    public static boolean isInBounds(XY pos) {
        return isInBounds(pos.x, pos.y);
    }

    public static boolean isInBounds(int x, int y) {
        return x >= 0 && x < Constants.WIDTH && y >= 0 && y < Constants.HEIGHT;
    }

    public static boolean canMoveTo(GameState gameState, XY moveTo, XY pos) {
        boolean isWall = isWall(moveTo);
        if (inSpawnZone(pos)) {
            return !isWall;
        } else if (inSpawnZone(moveTo)) {
            return gameState.isPoisonPill(pos) && !gameState.getOpponentPosition().equals(Constants.PORTAL);
        } else {
            return !isWall && !inSpawnZone(moveTo);
        }
    }

    public static boolean isWarp(XY pos) {
        return pos.equals(Constants.WARP_A) || pos.equals(Constants.WARP_B);
    }

    public static XY getWarp(XY pos) {
        if (pos.equals(Constants.WARP_A)) {
            return Constants.WARP_B;
        } else if (pos.equals(Constants.WARP_B)) {
            return Constants.WARP_A;
        }
        return null;
    }

    public static Collection<XY> getWarpNeighbors(XY pos) {
        Set<XY> neighbors = new HashSet<>();
        if (pos.equals(Constants.WARP_A)) {
            neighbors.add(Constants.WARP_B);
            neighbors.add(new XY(pos.x + 1, pos.y));
        } else if (pos.equals(Constants.WARP_B)) {
            neighbors.add(Constants.WARP_A);
            neighbors.add(new XY(pos.x - 1, pos.y));
        }
        return neighbors;
    }

    private static int[][] MAZE_DISTANCES = new int[Constants.WIDTH * Constants.HEIGHT][Constants.WIDTH * Constants.HEIGHT];

    static {
        for (int[] m : MAZE_DISTANCES) {
            Arrays.fill(m, -2);
        }
    }

    public static int mazeDistance(XY ni, XY ji) {
        int x = ni.y * Constants.WIDTH + ni.x - 1;
        int y = ji.y * Constants.WIDTH + ji.x - 1;
        int val = MAZE_DISTANCES[x][y];
        if (val != -2) {
            return val;
        }

        if (ni.equals(ji)) {
            MAZE_DISTANCES[x][y] = 0;
            return 0;
        }

        if (isWall(ni.x, ni.y) || isWall(ji.x, ji.y)) {
            MAZE_DISTANCES[x][y] = -1;
            return -1;
        }

        Stack<SearchNode> path = Search.mazeDist(ni, ji);
        if (path == null) {
            logger.error("No path from {} to {}", ni, ji);
            return -3;
        }
        int w = path.size();
        MAZE_DISTANCES[x][y] = w;
        return w;
    }

    public static boolean isBesides(XY y, XY o) {
        boolean isWarpNeighbor = isWarp(y) && getWarpNeighbors(y).contains(o) ||
                isWarp(o) && getWarpNeighbors(o).contains(y);

        if (isWarpNeighbor
                || (y.x == o.x && (y.y == o.y - 1 || y.y == o.y + 1))
                || (y.y == o.y && (y.x == o.x - 1 || y.x == o.x + 1))) {
            return true;
        }

        return false;
    }

    public static float[][] mult(float[][] input, float val) {
        float[][] result = new float[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                result[i][j] = input[i][j] * val;
            }
        }
        return result;
    }

    public static char[][] clone(char[][] val) {
        char[][] clone = new char[val.length][val[0].length];
        for (int i = 0; i < val.length; i++) {
            System.arraycopy(val[i], 0, clone[i], 0, val[0].length);
        }
        return clone;
    }

    public static float[][] clone(float[][] val) {
        float[][] clone = new float[val.length][val[0].length];
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {
                clone[i][j] = val[i][j];
            }
        }
        return clone;
    }

    public static byte[] clone(byte[] source) {
        byte[] clone = new byte[source.length];
        System.arraycopy(source, 0, clone, 0, source.length);
        return clone;
    }

    public static char[] clone(char[] source) {
        char[] clone = new char[source.length];
        System.arraycopy(source, 0, clone, 0, source.length);
        return clone;
    }

    public static Set clone(Set val) {
        Set clone = new HashSet<>();
        for (Object item : val) {
            clone.add(item);
        }
        return clone;
    }

    public static boolean isValidMove(GameState gameState, XY movePoint, XY currentPosition) {
        for (SearchNode node : Search.getAvailableNeighbors(gameState, new SearchNode(currentPosition))) {
            if (node.pos.equals(movePoint)) {
                return true;
            }
        }
        return false;
    }

    public static char getOtherPlayer(char player) {
        if (player == Constants.PLAYER_A) {
            return Constants.PLAYER_B;
        }
        return Constants.PLAYER_A;
    }

    public static String toSymbol(int gamma) {
        if (gamma == Integer.MIN_VALUE) {
            return "-∞";
        } else if (gamma == Integer.MAX_VALUE) {
            return "∞";
        }
        return String.valueOf(gamma);
    }

    public static Set<XY> getAllPills(GameState gameState) {
        Set<XY> allPills = new HashSet<>();
        allPills.addAll(gameState.getPills());
        allPills.addAll(gameState.getBonusPills());
        return allPills;
    }

    public static Set<XY> getAllNonPillSpaces(GameState gameState) {
        Set<XY> spaces = new HashSet<>();
        char[][] cells = gameState.getCells();
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                char c = cells[x][y];
                if (c == Constants.SPACE || c == Constants.PLAYER_A || c == Constants.PLAYER_B) {
                    spaces.add(new XY(x, y));
                }
            }
        }
        return spaces;
    }

    public static boolean isPillAt(GameState gameState, XY pos) {
        char c = gameState.getCell(pos);
        return c == Constants.PILL || c == Constants.BONUS_PILL;
    }

    public static long hashFirstPrincipal(GameState gameState) {
        long hashFp = 0;

        char[][] cells = gameState.getCells();
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {
                char c = cells[i][j];
                hashFp ^= Constants.ZOBRIST[i][j][Constants.ZOBRIST_MAP.get(c)];
            }
        }
        return hashFp;
    }

    public static long hashFirstPrincipal(UCTGameState gameState) {
        long hashFp = 0;

        byte[] board = gameState.getBoard();
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {
                byte c = board[i * Constants.HEIGHT + j];
                hashFp ^= Constants.ZOBRIST[i][j][Constants.ZOBRIST_MAP.get((char)c)];
            }
        }
        return hashFp;
    }

    public static char getWinner(GameState gameState) {
        if (!gameState.isGameOver()) {
            return 'X';
        }

        if (gameState.getPlayerAScore() > gameState.getPlayerBScore()) {
            return Constants.PLAYER_A;
        } else if (gameState.getPlayerBScore() > gameState.getPlayerAScore()) {
            return Constants.PLAYER_B;
        } else {
            return '=';
        }
    }

    public static String getWinnerDescription(GameState gameState) {
        if (!gameState.isGameOver()) {
            return "N/A";
        }

        if (gameState.getPlayerAScore() > gameState.getPlayerBScore()) {
            return "Player A won!";
        } else if (gameState.getPlayerBScore() > gameState.getPlayerAScore()) {
            return "Player B won!";
        } else {
            return "It was a tie!";
        }
    }

    public static void writeTmpMaze(GameState gameState) {
        new File("/tmp/opta").mkdirs();
        String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String filename = "/tmp/opta/pacman_" + date + ".txt";
        GameFactory.writeMaze(filename, gameState);
    }

    public static void writeInfluenceMapToExperiments(GameState gameState) {
        String directory = "C:\\Users\\leonard\\Dropbox\\Software Developer\\java\\entelect\\pacman\\pacman\\experiments";
        InfluenceMap influenceMap = gameState.getInfluenceMap();
        Util.writeToFile(directory, "y_inf.txt", Util.toSv(influenceMap.getyInfluenceMap()));
        Util.writeToFile(directory, "o_inf.txt", Util.toSv(influenceMap.getoInfluenceMap()));
        Util.writeToFile(directory, "pill_cluster.txt", Util.toSv(influenceMap.getPillCluster()));
        //Util.writeToFile(directory, "bp" + bpNum + "_inf.txt", Util.toSv(bpInf));
        Util.writeToFile(directory, "bp_sum_inf.txt", Util.toSv(influenceMap.getBpInf()));
        Util.writeToFile(directory, "p_sum_inf.txt", Util.toSv(influenceMap.getpInf()));
    }

    public static String toMathematica(float[][] field) {
        StringBuilder buffer = new StringBuilder();

        buffer.append("{");
        for (int y = 0; y < Constants.HEIGHT; y++) {
            buffer.append("{");
            for (int x = 0; x < Constants.WIDTH; x++) {
                buffer.append(field[x][y]);
                if (x != Constants.WIDTH - 1) {
                    buffer.append(", ");
                }
            }
            buffer.append("}");
            if (y != Constants.HEIGHT - 1) {
                buffer.append(",\n");
            }
        }
        buffer.append("}");
        return buffer.toString();
    }

    public static String toSv(int[][] field) {
        return toSv(field, " ");
    }

    public static String toSv(int[][] field, String delimeter) {
        StringBuilder buffer = new StringBuilder();
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                buffer.append(field[x][y]);
                if (x != Constants.WIDTH - 1) {
                    buffer.append(delimeter);
                }
            }
            if (y != Constants.HEIGHT - 1) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    public static String toSv(float[][] field) {
        return toSv(field, " ");
    }

    public static String toSv(float[][] field, String delimeter) {
        StringBuilder buffer = new StringBuilder();
        for (int y = 0; y < Constants.HEIGHT; y++) {
            for (int x = 0; x < Constants.WIDTH; x++) {
                buffer.append(field[x][y]);
                if (x != Constants.WIDTH - 1) {
                    buffer.append(delimeter);
                }
            }
            if (y != Constants.HEIGHT - 1) {
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    public static float[][] normalize(float[][] input) {
        float[][] result = new float[Constants.WIDTH][Constants.HEIGHT];
        float max = 0;
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                if (input[x][y] > max) {
                    max = input[x][y];
                }
            }
        }
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                result[x][y] = input[x][y]/max;
            }
        }
        return result;
    }

    public static void writeToFile(String directory, String filename, String output) {
        try {
            PrintWriter writer = new PrintWriter(new File(directory, filename));
            writer.print(output);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

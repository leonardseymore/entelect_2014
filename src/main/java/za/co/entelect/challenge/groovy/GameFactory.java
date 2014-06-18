package za.co.entelect.challenge.groovy;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GameFactory {

    private static final Logger logger = LoggerFactory.getLogger(GameFactory.class);

    public static Game fromScriptFile(String filename) throws ScriptException, NoSuchMethodException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(GameFactory.class.getResourceAsStream(filename)));
        StringBuilder builder = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {
                builder.append(line + "\n");
                line = reader.readLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return fromScript(builder.toString());
    }

    public static Game fromScript(String script) throws ScriptException, NoSuchMethodException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine scriptEngine = sem.getEngineByName("groovy");
        scriptEngine.eval(script);
        Invocable inv = (Invocable) scriptEngine;
        String mapDesc = (String) inv.invokeFunction("getMap");
        GameState gameState = fromString(mapDesc);

        PacmanAgent agentA = (PacmanAgent) inv.invokeFunction("getA");
        PacmanAgent agentB = (PacmanAgent) inv.invokeFunction("getB");

        Game game = new Game(gameState, agentA, agentB);
        return game;
    }



    public static GameState fromFile(String filePath) throws FileNotFoundException {
        Scanner reader = new Scanner(new File(filePath));
        return fromScanner(reader);
    }

    public static GameState fromClasspathFile(String filePath) throws FileNotFoundException {
        Scanner reader = new Scanner(GameState.class.getResourceAsStream(filePath));
        return fromScanner(reader);
    }

    public static GameState fromString(String text) {
        Scanner reader = new Scanner(new StringReader(text));
        return fromScanner(reader);
    }

    public static GameState fromScanner(Scanner reader) {
        XY playerAPos = null;
        XY playerBPos = null;
        Set<XY> pills = new HashSet<>();
        Set<XY> bonusPills = new HashSet<>();
        Set<XY> poisonPills = new HashSet<>();
        int scoreLeft = 0;
        char[][] map = new char[Constants.HEIGHT][Constants.WIDTH];
        int rowCount = 0;
        while (reader.hasNext()) {
            String row = reader.nextLine();
            if (row.length() < Constants.WIDTH) {
                row = String.format("%1$-" + Constants.WIDTH + "s", row);
            }
            map[rowCount] = row.toCharArray();
            for (int i = 0; i < Constants.WIDTH; i++) {
                char c = map[rowCount][i];

                if (c == Constants.WALL) {
                    assert Util.isWall(i, rowCount) : "Expected wall at " + i + ", " + rowCount;
                }

                XY pos = new XY(i, rowCount);
                if (c == Constants.PILL) {
                    scoreLeft += Constants.PILL_SCORE;
                    pills.add(pos);
                } else if (c == Constants.BONUS_PILL) {
                    scoreLeft += Constants.BONUS_PILL_SCORE;
                    bonusPills.add(pos);
                } else if (c == Constants.POISON_PILL) {
                    poisonPills.add(pos);
                } else if (c == Constants.PLAYER_A) {
                    playerAPos = pos;
                } else if (c == Constants.PLAYER_B) {
                    playerBPos = pos;
                }
            }
            rowCount++;
        }

        char[][] cells = new char[Constants.WIDTH][Constants.HEIGHT];
        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {
                cells[i][j] = map[j][i];
            }
        }

        GameState gameState = new GameState(cells, scoreLeft, pills, bonusPills, poisonPills, playerAPos, playerBPos, Constants.PLAYER_A);
        return gameState;
    }

    public static void writeMaze(String filePath, GameState gameState) {
        try {
            PrintWriter writer = new PrintWriter(filePath);
            String output = gameStateToString(gameState);
            writer.print(output);
            writer.close();
            logger.debug("Maze written to {}", filePath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static String gameStateToString(GameState gameState) {
        String output = "";
        for (int x = 0; x < Constants.HEIGHT; x++) {
            for (int y = 0; y < Constants.WIDTH; y++) {
                output += gameState.getCell(new XY(y, x));
            }
            if (x != Constants.HEIGHT - 1) output += ('\n');
        }
        return output;
    }
}

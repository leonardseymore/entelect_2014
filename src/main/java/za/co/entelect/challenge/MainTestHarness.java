package za.co.entelect.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.groovy.GameFactory;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static za.co.entelect.challenge.Util.R;

public class MainTestHarness {

    private static final Logger logger = LoggerFactory.getLogger(MainTestHarness.class);

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, FileNotFoundException, NoMoveFoundException {
        String botClass = args[0];
        String stateFilePath = args[1];

        logger.debug("Loading bot {} and state file {}", botClass, stateFilePath);

        PacmanAgent agent = (PacmanAgent) Class.forName(botClass).newInstance();
        GameState gameState = GameFactory.fromFile(stateFilePath);

        XY pos = gameState.getCurrentPosition();
        XY move = agent.pullNextMove(gameState, pos);
        gameState.updateCell(pos, Constants.SPACE, false);
        gameState.updateCell(move, Constants.PLAYER_A, false);

        logger.debug("{} move to {}", agent.getClass().getSimpleName(), pos);
        GameFactory.writeMaze(Constants.OUTPUT_FILE_NAME, gameState);
    }
}

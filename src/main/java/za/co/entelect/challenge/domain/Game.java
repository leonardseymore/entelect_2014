package za.co.entelect.challenge.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.agents.PacmanAgent;

import java.util.Stack;

public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private GameState initialGameState;
    private GameState gameState;
    private PacmanAgent playerAAgent;
    private PacmanAgent playerBAgent;

    private Stack<GameState> gameStateStack;

    public Game(GameState gameState, PacmanAgent playerAAgent, PacmanAgent playerBAgent) {
        this.initialGameState = gameState;
        this.gameState = gameState.clone();
        this.playerAAgent = playerAAgent;
        this.playerBAgent = playerBAgent;

        gameStateStack = new Stack<>();
    }

    public void restart() {
        this.gameState = initialGameState.clone();
        playerAAgent.reset();
        playerBAgent.reset();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void update() {
        if (gameState.isGameOver()) {
            return;
        }

        pushGameState();
        XY move;
        char currentPlayer = gameState.getCurrentPlayer();
        try {
            move = getMove();
        } catch (NoMoveFoundException ex) {
            logger.error("No valid move found for player " + currentPlayer, ex);
            return;
        }

        gameState.makeMove(gameState.getCurrentPosition(), move, GameState.UPDATE_ALL);
    }

    public void pushGameState() {
        gameStateStack.push(gameState.clone());
    }

    public boolean canPopGameState() {
        return gameStateStack.size() > 0;
    }

    public GameState popGameState() {
        gameState = gameStateStack.pop();
        playerAAgent.reset();
        playerBAgent.reset();
        return gameState;
    }

    private XY getMove() throws NoMoveFoundException {
        XY pos = gameState.getCurrentPosition();
        PacmanAgent agent = getCurrentPlayerAgent();
        XY move = agent.pullNextMove(gameState, pos);
        agent.getBlackboard().reset();
        return move;
    }

    public PacmanAgent getCurrentPlayerAgent() {
        if (gameState.getCurrentPlayer() == Constants.PLAYER_A) {
            return playerAAgent;
        }
        return playerBAgent;
    }

    public String getWinnerAgent() {
        char winner = Util.getWinner(gameState);
        if (winner == Constants.PLAYER_A) {
            return getPlayerAAgentClass();
        } else if (winner == Constants.PLAYER_B) {
            return getPlayerBAgentClass();
        }
        return null;
    }

    public String getPlayerAAgentClass() {
        return playerAAgent.getClass().getSimpleName();
    }

    public String getPlayerBAgentClass() {
        return playerBAgent.getClass().getSimpleName();
    }

    public void swapPlayerAgents() {
        PacmanAgent oldA = playerAAgent;
        playerAAgent = playerBAgent;
        playerBAgent = oldA;
    }
}

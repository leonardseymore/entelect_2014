package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.mcts.UCT;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.ai.mcts.UCTPos;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Arrays;

public class GuessBestMoveUct extends Task {

    private int maxDepth;
    private long timeout;
    private float heuristic;

    public GuessBestMoveUct(int maxDepth, long timeout, float heuristic) {
        this.maxDepth = maxDepth;
        this.timeout = timeout;
        this.heuristic = heuristic;
    }

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        byte[] board = new byte[UCTGameState.SIZE];
        Arrays.fill(board, (byte)0);
        char[][] cells = gameState.getCells();

        /*
        StringBuilder buffer = new StringBuilder();
        for (int j = 0; j < UCTGameState.HEIGHT; j++) {
            for (int i = 0; i < UCTGameState.WIDTH; i++) {
                buffer.append(cells[i][j]);
            }
            buffer.append("\n");
        }
        System.out.println("CELLS\n" + buffer.toString());
        */

        for (int x = 0; x < UCTGameState.WIDTH; x++) {
            for (int y = 0; y < UCTGameState.HEIGHT; y++) {
                board[x * UCTGameState.HEIGHT + y] = (byte)cells[x][y];
            }
        }

        /*
        buffer = new StringBuilder();
        for (int j = 0; j < UCTGameState.HEIGHT; j++) {
            for (int i = 0; i < UCTGameState.WIDTH; i++) {
                buffer.append((char)board[i * UCTGameState.HEIGHT + j]);
                assert cells[i][j] == (char)board[i * UCTGameState.HEIGHT + j] : "Board not converted correctly from cells: cell " + i + ", " + j + "=" + cells[i][j] + ", board=" + (char)board[i * UCTGameState.WIDTH + j];
            }
            buffer.append("\n");
        }
        System.out.println("BOARD\n" + buffer.toString());
        */

        UCTPos ypos = new UCTPos(gameState.getCurrentPosition().x, gameState.getCurrentPosition().y);
        UCTPos opos = new UCTPos(gameState.getOpponentPosition().x, gameState.getOpponentPosition().y);
        short scoreLeft = (short)gameState.getScoreLeft();

        UCTGameState uctGameState = new UCTGameState(board, ypos, opos, scoreLeft);
        UCTPos uctPos = UCT.getInstance().uct(uctGameState, maxDepth, timeout, true, false, heuristic);
        XY move = new XY(uctPos.x, uctPos.y);
        if (move == null) {
            return false;
        }

        blackboard.target = move;
        return true;
    }

    @Override
    protected String getLabel() {
        return "GuessBestMove x" + maxDepth;
    }
}

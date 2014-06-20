package za.co.entelect.challenge.agents;

import com.codahale.metrics.Timer;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.awt.*;

public abstract class PacmanAgent {

    private final Timer moveTimer;

    private XY nextMove;

    protected Blackboard blackboard;

    protected PacmanAgent() {
        moveTimer = Util.metrics.timer(getClass().getSimpleName() + "_move-time");
        blackboard = new Blackboard();
    }

    public Blackboard getBlackboard() {
        return blackboard;
    }

    public XY peekNextMove(GameState gameState, XY pos) throws NoMoveFoundException {
        if (nextMove == null) {
            final Timer.Context timerContext = moveTimer.time();
            try {
                nextMove = getMove(gameState, pos);
            } finally {
                timerContext.close();
            }
        }
        return nextMove;
    }

    public XY pullNextMove(GameState gameState, XY pos) throws NoMoveFoundException {
        try {
            if (nextMove == null) {
                return doGetMove(gameState, pos);
            } else {
                return nextMove;
            }
        } finally {
            nextMove = null;
        }
    }

    public void reset() {
        nextMove = null;
    }

    private XY doGetMove(GameState gameState, XY pos) throws NoMoveFoundException {
        long startTime = System.currentTimeMillis();
        final Timer.Context timerContext = moveTimer.time();
        try {
            XY move = getMove(gameState, pos);
            long thinkTime = System.currentTimeMillis() - startTime;
            assert thinkTime <= Constants.THINK_TIME + Constants.THINK_TIME_GRACE : "Out of time [" + thinkTime + " > " + Constants.THINK_TIME + "ms], " + getName() + " did not timeout";
            return move;
        } finally {
            timerContext.close();
        }
    }

    protected abstract XY getMove(GameState gameState, XY pos) throws NoMoveFoundException;

    public abstract void drawDebugInfo(Graphics2D g, GameState gameState, XY pos);

    public String getName() {
        return getClass().getSimpleName();
    }
}

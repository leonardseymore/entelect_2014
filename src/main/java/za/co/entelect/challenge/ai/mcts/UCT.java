package za.co.entelect.challenge.ai.mcts;

import com.codahale.metrics.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class UCT {

    private static final Logger logger = LoggerFactory.getLogger(UCT.class);

    static UCTNodeComparatorVisits comparatorVisits = new UCTNodeComparatorVisits();

    private static UCT instance;

    private final Timer rolloutTimer;

    private ExecutorService executorService;

    private static final int NUM_THREADS = 8;

    private UCT() {
        rolloutTimer = Util.metrics.timer(getClass().getSimpleName() + "_rollout-time");
        executorService = new ThreadPoolExecutor(NUM_THREADS, NUM_THREADS, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(NUM_THREADS * 2, true), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "UCT Worker");
            }
        }, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static UCT getInstance() {
        if (instance == null) {
            instance = new UCT();
        }
        return instance;
    }

    public static UCTGameState convert(GameState gameState) {
        byte[] board = new byte[UCTGameState.SIZE];
        Arrays.fill(board, (byte)0);
        char[][] cells = gameState.getCells();

        for (int x = 0; x < UCTGameState.WIDTH; x++) {
            for (int y = 0; y < UCTGameState.HEIGHT; y++) {
                board[x * UCTGameState.HEIGHT + y] = (byte)cells[x][y];
            }
        }

        XY ypos = new XY(gameState.getCurrentPosition().x, gameState.getCurrentPosition().y);
        XY opos = new XY(gameState.getOpponentPosition().x, gameState.getOpponentPosition().y);
        short scoreLeft = (short)gameState.getScoreLeft();

        UCTGameState uctGameState = new UCTGameState(board, ypos, opos, (short)gameState.getPlayerAScore(), (short)gameState.getPlayerBScore(), (byte)gameState.getCurrentPlayer(), scoreLeft);
        return uctGameState;
    }

    public XY uct(UCTGameState rootstate, int iterations, long timeout, boolean parallel, boolean verbose, float heuristic) {
        long start = System.currentTimeMillis();
        UCTNode rootnode = new UCTNode(rootstate, heuristic);

        if ( iterations == -1 && timeout == -1) {
            throw new IllegalArgumentException("Iterations and timeout cannot both be infinite");
        }

        int[] numPlayouts = new int[Constants.WIDTH * Constants.HEIGHT];
        Arrays.fill(numPlayouts, 1);
        int[] wonPlayouts = new int[Constants.WIDTH * Constants.HEIGHT];
        int i;
        for (i = 0; (i < iterations || iterations == -1) && (System.currentTimeMillis() - start < timeout || timeout == -1); i++) {
            if (parallel) {
                executorService.execute(new UCTWorker(rootnode, rootstate, numPlayouts, wonPlayouts));
            } else {
                uctSync(rootnode, rootstate, numPlayouts, wonPlayouts);
            }
        }

        logger.debug("UCT did {} iterations in {}ms", i, System.currentTimeMillis() - start);

        if (verbose) {
            logger.debug(rootnode.treeToString(0));
        }

        List<UCTNode> children = rootnode.children;
        Collections.sort(children, comparatorVisits);
        return children.get(0).move;
    }

    public void uctSync(UCTNode rootnode, UCTGameState rootstate, int[] numPlayouts, int[] wonPlayouts) {
        UCTNode node = rootnode;
        UCTGameState state = rootstate.clone();

        // Select
        synchronized (node) {
            while (!node.hasUntriedMoves() && node.hasChildren()) {
                node = node.uctSelectChild(numPlayouts, wonPlayouts);
                state.doMove(node.move);
            }

            // Expand
            if (node.hasUntriedMoves()) {
                XY move = node.getRandomUntriedMove();
                state.doMove(move);
                node = node.addChild(move, state);
            }
        }

        // Rollout
//        final Timer.Context timerContext = rolloutTimer.time();
//        try {
            while (state.hasMoves()) {
                XY move = state.getRandomMove();
                state.doMove(move);
                //logger.debug("{} -> \n{}", move, state.toAscii());
            }
//        } finally {
//            timerContext.close();
//        }
        //logger.debug("Running average of rollouts are ~{}ms", String.format("%,.2f", rolloutTimer.getSnapshot().getMean() / 1e6));

        // Backpropagate
        while (node != null) {
            XY move = node.move;
            if (move == null) {
                break;
            }
            numPlayouts[move.x * Constants.HEIGHT + move.y]++;
            float result = state.getResult(node.currentPlayer);
            if (result == 1.0f) {
                wonPlayouts[move.x * Constants.HEIGHT + move.y]++;
            }
            node.update(result);
            node = node.parentNode;
        }
    }

    private class UCTWorker implements  Runnable {

        private UCTNode rootnode;
        private UCTGameState rootstate;
        private int[] numPlayouts;
        private int[] wonPlayouts;

        private UCTWorker(UCTNode rootnode, UCTGameState rootstate, int[] numPlayouts, int[] wonPlayouts) {
            this.rootnode = rootnode;
            this.rootstate = rootstate;
            this.numPlayouts = numPlayouts;
            this.wonPlayouts = wonPlayouts;
        }

        @Override
        public void run() {
            uctSync(rootnode, rootstate, numPlayouts, wonPlayouts);
        }
    }
}


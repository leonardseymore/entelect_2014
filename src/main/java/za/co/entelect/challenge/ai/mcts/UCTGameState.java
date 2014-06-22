package za.co.entelect.challenge.ai.mcts;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.XY;

import java.util.*;
import static za.co.entelect.challenge.Util.R;
import static za.co.entelect.challenge.Util.clone;

public class UCTGameState implements Cloneable {

    public static int WIDTH = 19;
    public static int HEIGHT = 22;
    public static int SIZE = WIDTH * HEIGHT;

    public static byte A = 'A';
    public static byte B = 'B';
    public static byte S = ' ';
    public static byte P = '.';
    public static byte BP = '*';
    public static byte PP = '!';
    public static byte W = '#';

    public static byte P_SCORE = 1;
    public static byte BP_SCORE = 10;

    public static final XY PORTAL = new XY(9, 10);
    public static final XY WARP_A = new XY(0, 10);
    public static final XY WARP_B = new XY(18, 10);

    public static final Set<XY> SPAWN_ZONE = new HashSet<>();
    static {
        SPAWN_ZONE.add(new XY(9, 9));
        SPAWN_ZONE.add(new XY(8, 10));
        SPAWN_ZONE.add(new XY(9, 10));
        SPAWN_ZONE.add(new XY(10, 10));
        SPAWN_ZONE.add(new XY(9, 11));
    }

    private long hash;
    byte currentPlayer;
    byte[] board;
    short ascore;
    short bscore;
    short scoreLeft;
    XY ypos;
    XY opos;
    float weightedAscore;
    float weightedBscore;

    private List<XY> moves;

    UCTGameState() {

    }

    public UCTGameState(byte[] board, XY ypos, XY opos, short ascore, short bscore, byte currentPlayer, short scoreLeft) {
        this.board = board;
        this.ypos = ypos;
        this.opos = opos;
        this.ascore = ascore;
        this.bscore = bscore;
        this.currentPlayer = currentPlayer;
        this.scoreLeft = scoreLeft;
        loadMoves();
        this.hash = Util.hashFirstPrincipal(this);
    }

    public UCTGameState clone() {
        UCTGameState clone = new UCTGameState();
        clone.currentPlayer = currentPlayer;
        clone.ascore = ascore;
        clone.bscore = bscore;
        clone.weightedAscore = weightedAscore;
        clone.weightedBscore = weightedBscore;
        clone.ypos = ypos;
        clone.opos = opos;
        clone.scoreLeft = scoreLeft;
        byte[] boardClone = new byte[SIZE];
        System.arraycopy(board, 0, boardClone, 0, SIZE);
        clone.board = boardClone;
        clone.hash = hash;
        return clone;
    }

    public static byte getOtherPlayer(byte player) {
        return (byte) (131 - player);
    }

    public void doMove(XY move) {
        doMove(move, true, 0);
    }

    public void doMove(XY move, boolean swapPlayers, int depth) {
        assert canMoveTo(move) : "Invalid move";

        byte otherPlayer = getOtherPlayer(currentPlayer);

        byte moveVal = getCell(move);
        byte scoreAdjust = 0;
        if (moveVal == P) {
            scoreAdjust = P_SCORE;
        } else if (moveVal == BP) {
            scoreAdjust = BP_SCORE;
        }

        if (scoreAdjust > 0) {
            scoreLeft -= scoreAdjust;
            if (currentPlayer == A) {
                ascore += scoreAdjust;
                weightedAscore += scoreAdjust * Util.falloff(depth);
                weightedAscore -= scoreAdjust * Util.falloff(Util.mazeDistance(opos, move)) / 2;
            } else {
                bscore += scoreAdjust;
                weightedBscore += scoreAdjust * Util.falloff(depth);
                weightedBscore -= scoreAdjust * Util.falloff(Util.mazeDistance(ypos, move)) / 2;
            }
        }

        if (isPoisonPill(move)) {
            setCell(ypos, S);
            setCell(move, S);
            setCell(PORTAL, currentPlayer);
            ypos = PORTAL;
        } else if (opos.equals(move)) {
            setCell(ypos, S);
            setCell(move, currentPlayer);
            setCell(PORTAL, otherPlayer);
            opos = PORTAL;
            ypos = move;
        } else {
            setCell(ypos, S);
            setCell(move, currentPlayer);
            ypos = move;
        }

        // swap positions for next round
        if (swapPlayers) {
            ypos = opos;
            opos = move;

            currentPlayer = (byte) (131 - currentPlayer);
            assert currentPlayer == A || currentPlayer == B : "Player is no longer valid";
        }

        loadMoves();
    }

    public boolean hasMoves() {
        return moves.size() > 0 && scoreLeft > 0;
    }

    public XY getRandomMove() {
        return moves.get(R.nextInt(moves.size()));
    }

    public List<XY> getMoves() {
        return moves;
    }

    public byte[] getBoard() {
        return board;
    }

    public short getAscore() {
        return ascore;
    }

    public short getBscore() {
        return bscore;
    }

    public short getScoreLeft() {
        return scoreLeft;
    }

    public XY getYpos() {
        return ypos;
    }

    public XY getOpos() {
        return opos;
    }

    private void loadMoves() {
        XY moveTo = ypos;
        if (isPoisonPill(moveTo)) {
            moveTo = PORTAL;
        }

        moves = new ArrayList<>();
        if (isWarp(moveTo)) {
            for (XY xy : getWarpNeighbors(moveTo)) {
                testNeighbor(xy, moves);
            }
        } else {
            testNeighbor(new XY(moveTo.x, moveTo.y - 1), moves);
            testNeighbor(new XY(moveTo.x + 1, moveTo.y), moves);
            testNeighbor(new XY(moveTo.x, moveTo.y + 1), moves);
            testNeighbor(new XY(moveTo.x - 1, moveTo.y), moves);
        }
    }

    public void testNeighbor(XY moveTo, Collection<XY> moves) {
        if (!isInBounds(moveTo) || !canMoveTo(moveTo)) {
            return;
        }
        moves.add(moveTo);
    }

    public float getResult(byte player) {
        if (ascore > bscore) {
            return player == A ? 1.0f : 0;
        } else if (bscore > ascore) {
            return player == B ? 1.0f : 0;
        } else {
            return 0.5f;
        }
    }

    public int getPlayerScore(byte player) {
        if (player == A) {
            return ascore;
        } else {
            return bscore;
        }
    }

    public float getWeightedScore(byte player) {
        if (player == A) {
            return weightedAscore;
        } else {
            return weightedBscore;
        }
    }

    public boolean isInBounds(XY pos) {
        return pos.x >= 0 && pos.x < WIDTH && pos.y >= 0 && pos.y < HEIGHT;
    }

    public boolean isPoisonPill(XY pos) {
        return getCell(pos) == PP;
    }

    public boolean isWall(XY pos) {
        return getCell(pos) == W;
    }

    public byte getCell(XY pos) {
        return board[pos.x * HEIGHT + pos.y];
    }

    public void setCell(XY pos, byte value) {
        hash = Constants.ZOBRIST[pos.x][pos.y][Constants.ZOBRIST_MAP.get((char)board[pos.x * HEIGHT + pos.y])] ^ hash;
        hash ^= Constants.ZOBRIST[pos.x][pos.y][Constants.ZOBRIST_MAP.get((char)value)];
        board[pos.x * HEIGHT + pos.y] = value;
    }

    public boolean isWarp(XY pos) {
        return pos.equals(WARP_A) || pos.equals(WARP_B);
    }

    public boolean isGameOver() {
        return scoreLeft == 0;
    }

    public XY getCurrentPosition() {
        if (currentPlayer == A) {
            return ypos;
        } else {
            return opos;
        }
    }

    public Collection<XY> getWarpNeighbors(XY pos) {
        Set<XY> neighbors = new HashSet<>();
        if (pos.equals(WARP_A)) {
            neighbors.add(WARP_B);
            neighbors.add(new XY(pos.x + 1, pos.y));
        } else if (pos.equals(WARP_B)) {
            neighbors.add(WARP_A);
            neighbors.add(new XY(pos.x - 1, pos.y));
        }
        return neighbors;
    }

    public boolean canMoveTo(XY moveTo) {
        boolean isWall = isWall(moveTo);

        if (inSpawnZone(ypos)) {
            return !isWall;
        } else if (inSpawnZone(moveTo)) {
            return isPoisonPill(ypos) && !opos.equals(PORTAL);
        } else {
            return !isWall && !inSpawnZone(moveTo);
        }
    }

    public boolean inSpawnZone(XY point) {
        return SPAWN_ZONE.contains(point);
    }

    public String toAscii() {
        StringBuilder buffer = new StringBuilder();
        for (int j = 0; j < HEIGHT; j++) {
            for (int i = 0; i < WIDTH; i++) {
                buffer.append((char)board[i * HEIGHT + j]);
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    public long getHash() {
        return hash;
    }

    public byte getCurrentPlayer() {
        return currentPlayer;
    }
}

package za.co.entelect.challenge.ai.mcts;

import java.util.*;
import static za.co.entelect.challenge.Util.R;

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

    public static final UCTPos PORTAL = new UCTPos(9, 10);
    public static final UCTPos WARP_A = new UCTPos(0, 10);
    public static final UCTPos WARP_B = new UCTPos(18, 10);

    public static final Set<UCTPos> SPAWN_ZONE = new HashSet<>();
    static {
        SPAWN_ZONE.add(new UCTPos(9, 9));
        SPAWN_ZONE.add(new UCTPos(8, 10));
        SPAWN_ZONE.add(new UCTPos(9, 10));
        SPAWN_ZONE.add(new UCTPos(10, 10));
        SPAWN_ZONE.add(new UCTPos(9, 11));
    }

    byte playerJustMoved = B;
    byte[] board;
    short ascore;
    short bscore;
    short scoreLeft;
    UCTPos ypos;
    UCTPos opos;

    private List<UCTPos> moves;

    UCTGameState() {

    }

    public UCTGameState(byte[] board, UCTPos ypos, UCTPos opos, short scoreLeft) {
        this.board = board;
        this.ypos = ypos;
        this.opos = opos;
        this.scoreLeft = scoreLeft;
        loadMoves();
    }

    public UCTGameState clone() {
        UCTGameState clone = new UCTGameState();
        clone.playerJustMoved = playerJustMoved;
        clone.ascore = ascore;
        clone.bscore = bscore;
        clone.ypos = ypos;
        clone.opos = opos;
        clone.scoreLeft = scoreLeft;
        byte[] boardClone = new byte[SIZE];
        System.arraycopy(board, 0, boardClone, 0, SIZE);
        clone.board = boardClone;
        return clone;
    }

    public void doMove(UCTPos move) {
        assert canMoveTo(move) : "Invalid move";

        byte otherPlayer = playerJustMoved;
        playerJustMoved = (byte) (131 - playerJustMoved);
        assert playerJustMoved == A || playerJustMoved == B : "Player is no longer valid";

        byte moveVal = getCell(move);
        byte scoreAdjust = 0;
        if (moveVal == P) {
            scoreAdjust = P_SCORE;
        } else if (moveVal == BP) {
            scoreAdjust = BP_SCORE;
        }

        if (scoreAdjust > 0) {
            scoreLeft -= scoreAdjust;
            if (playerJustMoved == A) {
                ascore += scoreAdjust;
            } else {
                bscore += scoreAdjust;
            }
        }

        if (isPoisonPill(move)) {
            setCell(ypos, S);
            setCell(move, S);
            setCell(PORTAL, playerJustMoved);
            ypos = PORTAL;
        } else if (opos.equals(move)) {
            setCell(ypos, S);
            setCell(move, playerJustMoved);
            setCell(PORTAL, otherPlayer);
            opos = PORTAL;
            ypos = move;
        } else {
            setCell(ypos, S);
            setCell(move, playerJustMoved);
            ypos = move;
        }

        // swap positions for next round
        ypos = opos;
        opos = move;

        loadMoves();
    }

    public boolean hasMoves() {
        return moves.size() > 0 && scoreLeft > 0;
    }

    public UCTPos getRandomMove() {
        return moves.get(R.nextInt(moves.size()));
    }

    public List<UCTPos> getMoves() {
        return moves;
    }

    private void loadMoves() {
        UCTPos moveTo = ypos;
        if (isPoisonPill(moveTo)) {
            moveTo = PORTAL;
        }

        moves = new ArrayList<>();
        if (isWarp(moveTo)) {
            for (UCTPos xy : getWarpNeighbors(moveTo)) {
                testNeighbor(xy, moves);
            }
        } else {
            testNeighbor(new UCTPos(moveTo.x, moveTo.y - 1), moves);
            testNeighbor(new UCTPos(moveTo.x + 1, moveTo.y), moves);
            testNeighbor(new UCTPos(moveTo.x, moveTo.y + 1), moves);
            testNeighbor(new UCTPos(moveTo.x - 1, moveTo.y), moves);
        }
    }

    public void testNeighbor(UCTPos moveTo, Collection<UCTPos> moves) {
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

    public boolean isInBounds(UCTPos pos) {
        return pos.x >= 0 && pos.x < WIDTH && pos.y >= 0 && pos.y < HEIGHT;
    }

    public boolean isPoisonPill(UCTPos pos) {
        return getCell(pos) == PP;
    }

    public boolean isWall(UCTPos pos) {
        return getCell(pos) == W;
    }

    public byte getCell(UCTPos pos) {
        return board[pos.x * HEIGHT + pos.y];
    }

    public void setCell(UCTPos pos, byte value) {
        board[pos.x * HEIGHT + pos.y] = value;
    }

    public boolean isWarp(UCTPos pos) {
        return pos.equals(WARP_A) || pos.equals(WARP_B);
    }

    public Collection<UCTPos> getWarpNeighbors(UCTPos pos) {
        Set<UCTPos> neighbors = new HashSet<>();
        if (pos.equals(WARP_A)) {
            neighbors.add(WARP_B);
            neighbors.add(new UCTPos(pos.x + 1, pos.y));
        } else if (pos.equals(WARP_B)) {
            neighbors.add(WARP_A);
            neighbors.add(new UCTPos(pos.x - 1, pos.y));
        }
        return neighbors;
    }

    public boolean canMoveTo(UCTPos moveTo) {
        boolean isWall = isWall(moveTo);

        if (inSpawnZone(ypos)) {
            return !isWall;
        } else if (inSpawnZone(moveTo)) {
            return isPoisonPill(ypos) && !opos.equals(PORTAL);
        } else {
            return !isWall && !inSpawnZone(moveTo);
        }
    }

    public boolean inSpawnZone(UCTPos point) {
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
}

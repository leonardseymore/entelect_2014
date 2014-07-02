package za.co.entelect.challenge.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.InfluenceMap;

import java.util.*;

public class GameState implements Cloneable {

    public static final Logger logger = LoggerFactory.getLogger(GameState.class);

    public static int UPDATE_INFLUENCE_MAP = 1;
    public static int UPDATE_ZOBRIST_HASH = 2;
    public static int UPDATE_CELLS = 2;
    public static int UPDATE_ALL = UPDATE_INFLUENCE_MAP | UPDATE_ZOBRIST_HASH | UPDATE_CELLS;

    private char[][] cells;
    private char currentPlayer;
    private boolean gameOver;
    private Set<XY> pills;
    private Set<XY> bonusPills;
    private Set<XY> poisonPills;
    private int playerAScore;
    private int playerBScore;
    private XY playerAPos;
    private XY playerBPos;
    private int scoreLeft;

    private int numMoves;
    private int movesNoPills;
    private long hash;
    private InfluenceMap influenceMap;

    public GameState(char[][] cells, char currentPlayer, boolean gameOver, Set<XY> pills, Set<XY> bonusPills, Set<XY> poisonPills, int playerAScore, int playerBScore, XY playerAPos, XY playerBPos, int scoreLeft, int numMoves, int movesNoPills, long hash, InfluenceMap influenceMap) {
        this.cells = cells;
        this.currentPlayer = currentPlayer;
        this.gameOver = gameOver;
        this.pills = pills;
        this.bonusPills = bonusPills;
        this.poisonPills = poisonPills;
        this.playerAScore = playerAScore;
        this.playerBScore = playerBScore;
        this.playerAPos = playerAPos;
        this.playerBPos = playerBPos;
        this.scoreLeft = scoreLeft;

        this.numMoves = numMoves;
        this.movesNoPills = movesNoPills;
        this.hash = hash;
        this.influenceMap = influenceMap;
    }

    public GameState(char[][] cells, int scoreLeft, Set<XY> pills, Set<XY> bonusPills, Set<XY> poisonPills, XY playerAPos, XY playerBPos, char currentPlayer) {
        this.cells = cells;
        this.scoreLeft = scoreLeft;
        this.pills = pills;
        this.bonusPills = bonusPills;
        this.poisonPills = poisonPills;
        this.playerAPos = playerAPos;
        this.playerBPos = playerBPos;
        this.currentPlayer = currentPlayer;

        this.hash = Util.hashFirstPrincipal(this);
        this.influenceMap = new InfluenceMap();
        influenceMap.generate(this);
    }

    public long getHash() {
        return hash;
    }

    public InfluenceMap getInfluenceMap() {
        if (influenceMap == null) {
            influenceMap = InfluenceMap.forGameState(this);
        }
        return influenceMap;
    }

    public int getPlayerScore(char player) {
        if (player == Constants.PLAYER_A) {
            return playerAScore;
        }
        return playerBScore;
    }

    public int getCurrentPlayerScore() {
        return getPlayerScore(currentPlayer);
    }

    public int getOpponentScore() {
        return getPlayerScore(Util.getOtherPlayer(currentPlayer));
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public char[][] getCells() {
        return cells;
    }

    public char getCell(XY pos) {
        return cells[pos.x][pos.y];
    }

    public boolean isPoisonPill(XY pos) {
        return poisonPills.contains(pos);
    }

    public Set<XY> getPills() {
        return pills;
    }

    public Set<XY> getBonusPills() {
        return bonusPills;
    }

    public Set<XY> getPoisonPills() {
        return poisonPills;
    }

    public int getPlayerAScore() {
        return playerAScore;
    }

    public int getPlayerBScore() {
        return playerBScore;
    }

    public XY getPlayerPos(char player) {
        if (player == Constants.PLAYER_A) {
            return playerAPos;
        }  else {
            return playerBPos;
        }
    }

    public XY getCurrentPosition() {
        return getPlayerPos(currentPlayer);
    }

    public XY getOpponentPosition() {
        return getPlayerPos(Util.getOtherPlayer(currentPlayer));
    }

    public boolean isPortalOpen() {
        return !playerAPos.equals(Constants.PORTAL) && !playerBPos.equals(Constants.PORTAL);
    }

    public int getMovesNoPills() {
        return movesNoPills;
    }

    public XY getPlayerAPos() {
        return playerAPos;
    }

    public XY getPlayerBPos() {
        return playerBPos;
    }

    public int getScoreLeft() {
        return scoreLeft;
    }

    public int getNumMoves() {
        return numMoves;
    }

    private void removePill(XY pos) {
        pills = Util.clone(pills);
        pills.remove(pos);
    }

    private void removeBonusPill(XY pos) {
        bonusPills = Util.clone(bonusPills);
        bonusPills.remove(pos);
    }

    private void removePoisonPill(XY pos) {
        poisonPills = Util.clone(poisonPills);
        poisonPills.remove(pos);
    }

    public void updateCell(XY pos, char newValue, boolean updateZobristHash) {
        if (updateZobristHash) {
            hash = Constants.ZOBRIST[pos.x][pos.y][Constants.ZOBRIST_MAP.get(cells[pos.x][pos.y])] ^ hash;
            hash ^= Constants.ZOBRIST[pos.x][pos.y][Constants.ZOBRIST_MAP.get(newValue)];
        }
        cells[pos.x][pos.y] = newValue;
    }

    public int makeMove(XY currentPoint, XY movePoint) {
        return makeMove(currentPoint, movePoint, 0);
    }

    public int makeMove(XY currentPoint, XY movePoint, int flags) {
        return makeMove(currentPoint, movePoint, flags, true);
    }

    public int makeMove(XY currentPoint, XY movePoint, int flags, boolean swapPositions) {
        assert Util.isValidMove(this, movePoint, currentPoint) : "Player " + getCurrentPlayer() + " made an invalid move from " + currentPoint + " to " + movePoint;

        int scoreAdjust = 0;
        if (!currentPoint.equals(movePoint)) {
            cells = Util.clone(cells);
            numMoves++;
            char moveVal = getCell(movePoint);

            if (moveVal == Constants.PILL) {
                removePill(movePoint);
                scoreAdjust = Constants.PILL_SCORE;
            } else if (moveVal == Constants.BONUS_PILL) {
                removeBonusPill(movePoint);
                scoreAdjust = Constants.BONUS_PILL_SCORE;
            } else {
                movesNoPills++;
            }

            if (scoreAdjust > 0) {
                movesNoPills = 0;
                scoreLeft -= scoreAdjust;
                if (currentPlayer == Constants.PLAYER_A) {
                    playerAScore += scoreAdjust;
                } else {
                    playerBScore += scoreAdjust;
                }

                if (scoreLeft == 0) {
                    gameOver = true;
                }
            }

            boolean updateZobristHash = (flags & UPDATE_ZOBRIST_HASH) == UPDATE_ZOBRIST_HASH;
            if (isPoisonPill(movePoint)) {
                removePoisonPill(movePoint);

                if (currentPlayer == Constants.PLAYER_A) {
                    playerAPos = Constants.PORTAL;
                } else {
                    playerBPos = Constants.PORTAL;
                }

                updateCell(currentPoint, Constants.SPACE, updateZobristHash);
                updateCell(movePoint, Constants.SPACE, updateZobristHash);
                updateCell(Constants.PORTAL, currentPlayer, updateZobristHash);
            } else if (getOpponentPosition().equals(movePoint)) {
                if (currentPlayer == Constants.PLAYER_A) {
                    playerBPos = Constants.PORTAL;
                    playerAPos = movePoint;
                } else {
                    playerAPos = Constants.PORTAL;
                    playerBPos = movePoint;
                }

                updateCell(currentPoint, Constants.SPACE, updateZobristHash);
                updateCell(movePoint, currentPlayer, updateZobristHash);
                updateCell(Constants.PORTAL, Util.getOtherPlayer(currentPlayer), updateZobristHash);
            } else {
                if (currentPlayer == Constants.PLAYER_A) {
                    playerAPos = movePoint;
                } else {
                    playerBPos = movePoint;
                }

                updateCell(currentPoint, Constants.SPACE, updateZobristHash);
                updateCell(movePoint, currentPlayer, updateZobristHash);
            }

            if (updateZobristHash) {
                assert hash == Util.hashFirstPrincipal(this) : "Zobrish hash out of sync";
            }
        }

        if (swapPositions) {
            if (currentPlayer == Constants.PLAYER_A) {
                currentPlayer = Constants.PLAYER_B;
            } else {
                currentPlayer = Constants.PLAYER_A;
            }
        }

        if ((flags & UPDATE_INFLUENCE_MAP) == UPDATE_INFLUENCE_MAP) {
            influenceMap.generate(this);
        }

        return scoreAdjust;
    }

    public GameState clone() {
        GameState clone = new GameState(cells, currentPlayer, gameOver, pills, bonusPills, poisonPills, playerAScore, playerBScore, playerAPos, playerBPos, scoreLeft, numMoves, movesNoPills, hash, influenceMap.clone());
        return clone;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}

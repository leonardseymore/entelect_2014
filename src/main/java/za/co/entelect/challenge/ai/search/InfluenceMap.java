package za.co.entelect.challenge.ai.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.swing.Draw;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public class InfluenceMap {

    private static final Logger logger = LoggerFactory.getLogger(InfluenceMap.class);

    private float decay = 0;

    private int w;
    private int h;
    private float[][] yInfluenceMap;
    private float[][] oInfluenceMap;
    private float[][] influenceYMap;
    private float[][] influenceOMap;
    private float[][] tensionMap;
    private float[][] vulnerabilityMap;
    private float[][] potentialMap;
    private float[][] yPotentialMap;
    private float[][] oPotentialMap;
    private float[][] potentialYMap;
    private float[][] potentialOMap;
    private float[][] pillCluster;
    private int[][] frontLine;
    private int[] maxTension;
    private int[] maxVulnerability;
    private float totalYInfluence;
    private float totalOInfluence;
    private float totalYPotential;
    private float totalOPotential;

    public InfluenceMap() {
        w = Constants.WIDTH;
        h = Constants.HEIGHT;
        yInfluenceMap = new float[w][h];
        oInfluenceMap = new float[w][h];
        influenceYMap = new float[w][h];
        influenceOMap = new float[w][h];
        yPotentialMap = new float[w][h];
        oPotentialMap = new float[w][h];
        potentialYMap = new float[w][h];
        potentialOMap = new float[w][h];
        potentialMap = new float[w][h];
        tensionMap = new float[w][h];
        pillCluster = new float[w][h];
        vulnerabilityMap = new float[w][h];
        frontLine = new int[w][h];
        maxTension = new int[2];
        maxVulnerability = new int[2];
    }

    public float[][] getyInfluenceMap() {
        return yInfluenceMap;
    }

    public float[][] getoInfluenceMap() {
        return oInfluenceMap;
    }

    public float[][] getInfluenceYMap() {
        return influenceYMap;
    }

    public float[][] getInfluenceOMap() {
        return influenceOMap;
    }

    public float[][] getTensionMap() {
        return tensionMap;
    }

    public float[][] getVulnerabilityMap() {
        return vulnerabilityMap;
    }

    public float[][] getPotentialYMap() {
        return potentialYMap;
    }

    public float[][] getPotentialOMap() {
        return potentialOMap;
    }

    public float[][] getPillCluster() {
        return pillCluster;
    }

    public float getTotalYInfluence() {
        return totalYInfluence;
    }

    public float getTotalOInfluence() {
        return totalOInfluence;
    }

    public float getTotalYPotential() {
        return totalYPotential;
    }

    public float getTotalOPotential() {
        return totalOPotential;
    }

    public int[] getMaxTension() {
        return maxTension;
    }

    public int[] getMaxVulnerability() {
        return maxVulnerability;
    }

    public int[][] getFrontLine() {
        return frontLine;
    }

    public float[][] getyPotentialMap() {
        return yPotentialMap;
    }

    public float[][] getoPotentialMap() {
        return oPotentialMap;
    }

    public float[][] getPotentialMap() {
        return potentialMap;
    }

    public void generate(GameState gameState) {
        long start = System.currentTimeMillis();

        // decay old values
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                yInfluenceMap[i][j] = 0;
                oInfluenceMap[i][j] = 0;
                potentialMap[i][j] = 0;
                pillCluster[i][j] = 0;
            }
        }

        // apply new influence
        // influence = 1 / sqrt(dist)
        for (char player : new char[]{Constants.PLAYER_A, Constants.PLAYER_B}) {
            XY pos = gameState.getPlayerPos(player);
            Collection<SearchNode> influence = FloodFillInfluence.getInfluence(gameState, pos);
            for (SearchNode node : influence) {
                if (player == gameState.getCurrentPlayer()) {
                    yInfluenceMap[node.pos.x][node.pos.y] += falloff(node.runningCost);
                } else {
                    oInfluenceMap[node.pos.x][node.pos.y] += falloff(node.runningCost);
                }
            }
        }

        pillCluster = PillCluster.getCellPotential(gameState);
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                potentialMap[i][j] += pillCluster[i][j];
            }
        }

        // calculate and normalize influence maps
        float influenceYMax = 0;
        float influenceOMax = 0;
        totalYInfluence = 0;
        totalOInfluence = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                influenceYMap[i][j] = yInfluenceMap[i][j] - oInfluenceMap[i][j];
                if (influenceYMap[i][j] > influenceYMax) {
                    influenceYMax = influenceYMap[i][j];
                }
                influenceOMap[i][j] = oInfluenceMap[i][j] - yInfluenceMap[i][j];
                if (influenceOMap[i][j] > influenceOMax) {
                    influenceOMax = influenceOMap[i][j];
                }
            }
        }

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                influenceYMap[i][j] /= influenceYMax;
                if (influenceYMap[i][j] > 0) {
                    totalYInfluence += influenceYMap[i][j];
                } else if (influenceYMap[i][j] < 0) {
                    totalOInfluence += Math.abs(influenceYMap[i][j]);
                }
                influenceOMap[i][j] /= influenceOMax;
            }
        }


        float maxT = 0;
        float maxV = 0;
        totalYPotential = 0;
        totalOPotential = 0;
        float potentialYMax = 0;
        float potentialOMax = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                //influenceYMap[i][j] = yInfluenceMap[i][j] - oInfluenceMap[i][j];
                //influenceOMap[i][j] = oInfluenceMap[i][j] - yInfluenceMap[i][j];

                yPotentialMap[i][j] = yInfluenceMap[i][j] * potentialMap[i][j];
                oPotentialMap[i][j] = oInfluenceMap[i][j] * potentialMap[i][j];
                
                potentialYMap[i][j] = yPotentialMap[i][j] - oPotentialMap[i][j];
                if (potentialYMap[i][j] > potentialYMax) {
                    potentialYMax = potentialYMap[i][j];
                }
                
                potentialOMap[i][j] = oPotentialMap[i][j] - yPotentialMap[i][j];
                if (potentialOMap[i][j] > potentialOMax) {
                    potentialOMax = potentialOMap[i][j];
                }

                float tension = yInfluenceMap[i][j] + oInfluenceMap[i][j];
                tensionMap[i][j] = tension;
                if (tension > maxT) {
                    maxT = tension;
                    maxTension[0] = i;
                    maxTension[1] = j;
                }
                float vulnerability = tensionMap[i][j] - Math.abs(influenceYMap[i][j]);
                vulnerabilityMap[i][j] = vulnerability;
                if (vulnerability > maxV) {
                    maxV = vulnerability;
                    maxVulnerability[0] = i;
                    maxVulnerability[1] = j;
                }
            }
        }

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                potentialYMap[i][j] /= potentialYMax;
                if (potentialYMap[i][j] > 0) {
                    totalYPotential += potentialYMap[i][j];
                } else if (potentialYMap[i][j] < 0) {
                    totalOPotential += Math.abs(potentialYMap[i][j]);
                }
                potentialOMap[i][j] /= potentialOMax;
            }
        }

        for (int j = 1; j < h - 1; j++) {
            for (int i = 1; i < w - 1; i++) {
                frontLine[i][j] = 0;
                if (influenceYMap[i - 1][j] > 0 && influenceYMap[i + 1][j] < 0
                        || influenceYMap[i - 1][j] > 0 && influenceYMap[i + 1][j] < 0
                        || influenceYMap[i][j - 1] > 0 && influenceYMap[i][j + 1] < 0
                        || influenceYMap[i][j + 1] > 0 && influenceYMap[i][j - 1] < 0) {
                    frontLine[i][j] = 1;
                }
            }
        }
        //logger.debug("Influence map generation took [" + (System.currentTimeMillis() - start) + "ms]");
    }

    private static float falloff(float dist) {
        //return 1 / (float) Math.pow(dist == 0 ? 1f : dist, 0.1);
        return Math.min(1, 1 - dist / (Constants.MAX_MAZE_DIST));
    }

    public static InfluenceMap forGameState(GameState gameState) {
        InfluenceMap influenceMap = new InfluenceMap();
        influenceMap.generate(gameState);
        return influenceMap;
    }
}

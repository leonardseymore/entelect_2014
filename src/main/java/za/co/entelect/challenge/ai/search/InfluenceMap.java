package za.co.entelect.challenge.ai.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

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
    private int[][] frontLine;
    private int[] maxTension;
    private int[] maxVulnerability;
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
            }
        }
        // apply new influence
        for (char player : new char[]{Constants.PLAYER_A, Constants.PLAYER_B}) {
            XY pos = gameState.getPlayerPos(player);
            Collection<SearchNode> influence = FloodFillInfluence.getInfluence(gameState, pos);
            for (SearchNode node : influence) {
                if (player == gameState.getCurrentPlayer()) {
                    yInfluenceMap[node.pos.x][node.pos.y] += 1 / (float) (node.runningCost == 0 ? 1 : node.runningCost);
                } else {
                    oInfluenceMap[node.pos.x][node.pos.y] += 1 / (float) (node.runningCost == 0 ? 1 : node.runningCost);
                }
            }
        }

        float maxPotential = 0;
        for (SearchNode node : FloodFillPotential.getPotential(gameState)) {
            potentialMap[node.pos.x][node.pos.y] += (float)node.getProp(FloodFillPotential.POTENTIAL, 0) + 1 / (float) (node.runningCost == 0 ? 1 : node.runningCost);
            if (potentialMap[node.pos.x][node.pos.y] > maxPotential) {
                maxPotential = potentialMap[node.pos.x][node.pos.y];
            }
        }
        if (maxPotential > 0) {
            for (int j = 0; j < h; j++) {
                for (int i = 0; i < w; i++) {
                    potentialMap[i][j] /= maxPotential;
                }
            }
        }

        // calculate influence maps
        float maxT = 0;
        float maxV = 0;
        totalYPotential = 0;
        totalOPotential = 0;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                influenceYMap[i][j] = yInfluenceMap[i][j] - oInfluenceMap[i][j];
                influenceOMap[i][j] = oInfluenceMap[i][j] - yInfluenceMap[i][j];

                yPotentialMap[i][j] = yInfluenceMap[i][j] * potentialMap[i][j];
                oPotentialMap[i][j] = oInfluenceMap[i][j] * potentialMap[i][j];

                potentialYMap[i][j] = yPotentialMap[i][j] - oPotentialMap[i][j];
                potentialOMap[i][j] = oPotentialMap[i][j] - yPotentialMap[i][j];

                totalYPotential += potentialYMap[i][j] > 0 ? potentialYMap[i][j] : 0;
                totalOPotential += potentialOMap[i][j] > 0 ? potentialOMap[i][j] : 0;

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

    public static InfluenceMap forGameState(GameState gameState) {
        InfluenceMap influenceMap = new InfluenceMap();
        influenceMap.generate(gameState);
        return influenceMap;
    }
}

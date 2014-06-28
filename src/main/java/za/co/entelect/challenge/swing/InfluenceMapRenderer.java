package za.co.entelect.challenge.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.filters.PathCluster;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.domain.GameState;

import java.awt.*;
import java.awt.event.KeyEvent;

public class InfluenceMapRenderer {

    public static final Logger logger = LoggerFactory.getLogger(InfluenceMapRenderer.class);

    private static final float MULTIPLIER = 1f;

    private enum InfluenceMapType {
        COMBINED, YINFLUENCE, YREACHABLE, OREACHABLE, OINFLUENCE, INFLUENCEY, INFLUENCEO, TENSION, VULNERABILITY, POTENTIALY, POTENTIALO, PILL_CLUSTER, PATH_CLUSTER
    }

    private InfluenceMapType mapType = InfluenceMapType.COMBINED;

    public InfluenceMapRenderer() {
    }

    public void render(Graphics2D g, GameState gameState) {
        Keyboard keyboard = Keyboard.getInstance();
        if (keyboard.keyDownOnce(KeyEvent.VK_0)) {
            mapType = InfluenceMapType.COMBINED;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            mapType = InfluenceMapType.YINFLUENCE;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            mapType = InfluenceMapType.OINFLUENCE;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            mapType = InfluenceMapType.INFLUENCEY;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_4)) {
            mapType = InfluenceMapType.INFLUENCEO;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_5)) {
            mapType = InfluenceMapType.TENSION;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_6)) {
            mapType = InfluenceMapType.PATH_CLUSTER;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_7)) {
            mapType = InfluenceMapType.POTENTIALY;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_8)) {
            mapType = InfluenceMapType.POTENTIALO;
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_9)) {
            mapType = InfluenceMapType.PILL_CLUSTER;
        }

        //g.setColor(Constants.COLOR_SWING_BOARD);
        //g.fillRect(0, 0, Constants.WIDTH, Constants.HEIGHT);

        InfluenceMap imap = gameState.getInfluenceMap();
        float[][] yInfluenceMap = imap.getyInfluenceMap();
        float[][] oInfluenceMap = imap.getoInfluenceMap();
        int[][] frontLine = imap.getFrontLine();
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                Color color = null;
                float[][] influenceMap;
                float val;
                switch (mapType) {
                    case YINFLUENCE:
                        influenceMap = imap.getyInfluenceMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.66f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case OINFLUENCE:
                        influenceMap = imap.getoInfluenceMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case INFLUENCEY:
                        influenceMap = imap.getInfluenceYMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.66f, 1, Math.min(1f, val));
                        } else if (val < 0) {
                            color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case INFLUENCEO:
                        influenceMap = imap.getInfluenceOMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.66f, 1, Math.min(1f, val));
                        } else if (val < 0) {
                            color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case TENSION:
                        influenceMap = imap.getTensionMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.8f, 0.5f, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case VULNERABILITY:
                        influenceMap = imap.getVulnerabilityMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.2f, 0.0f, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case YREACHABLE:
                        color = Color.magenta;
                        break;
                    case OREACHABLE:
                        color = Color.orange;
                        break;
                    case POTENTIALY:
                        influenceMap = imap.getPotentialYMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.66f, 1, Math.min(1f, val));
                        } else if (val < 0) {
                            color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case POTENTIALO:
                        influenceMap = imap.getPotentialOMap();
                        val = influenceMap[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.66f, 1, Math.min(1f, val));
                        } else if (val < 0) {
                            color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case PILL_CLUSTER:
                        float[][] pillCluster = imap.getPillCluster();
                        val = pillCluster[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.5f, 0.5f, Math.min(1f, val));
                        } else {
                            color = Color.getHSBColor(1f, 0.5f, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    case PATH_CLUSTER:
                        float[][] pathCluster = imap.getPathCluster();
                        val = pathCluster[x][y] * MULTIPLIER;
                        if (val > 0) {
                            color = Color.getHSBColor(0.4f, 1, Math.min(1f, val));
                        } else if (val < 0) {
                            color = Color.getHSBColor(0.1f, 1, Math.min(1f, Math.abs(val)));
                        }
                        break;
                    default:
                        if (!(oInfluenceMap[x][y] == 0 && yInfluenceMap[x][y] == 0)) {
                            color = new Color(Math.min(1f, oInfluenceMap[x][y] * MULTIPLIER), 0, Math.min(1f, yInfluenceMap[x][y] * MULTIPLIER));
                        }
                        break;
                }

                if (color != null) {
                    g.setColor(color);
                    Draw.fillRect(g, x, y, 0);
                }

                if (frontLine[x][y] == 1) {
                    g.setColor(new Color(237, 237, 237, 127));
                    Draw.fillRect(g, x, y, 2);
                }
            }
        }

        int[] maxVulnerability = imap.getMaxVulnerability();
        g.setColor(new Color(237, 237, 237, 127));
        Draw.drawRect(g, maxVulnerability[0], maxVulnerability[1], 2);
        Draw.fillRect(g, maxVulnerability[0], maxVulnerability[1], 5);
    }
}

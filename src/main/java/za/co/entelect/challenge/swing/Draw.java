package za.co.entelect.challenge.swing;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.GameStateGraph;
import za.co.entelect.challenge.domain.MST;
import za.co.entelect.challenge.domain.XY;

import java.awt.*;
import java.util.Collection;
import java.util.Stack;

public class Draw {
    public static final Color GREEN_TRANSPARENT = new Color(0, 255, 41, 196);
    public static final Color DARK_GREEN_TRANSPARENT = new Color(0, 92, 6, 196);

    public static void clearArea(Graphics2D g) {
        g.setColor(Constants.COLOR_SWING_BLANK);
        g.fillRect(0, 0, GUI.DEFAULT_WIDTH, GUI.DEFAULT_HEIGHT);
    }

    public static void drawString(Graphics2D g, int x, int y, String text) {
        drawString(g, x, y, text, 0, 0);
    }

    public static void drawString(Graphics2D g, int x, int y, String text, int offsetX, int offsetY) {
        g.drawString(text, x * Constants.ZOOM_LEVEL + offsetX, y * Constants.ZOOM_LEVEL + offsetY);
    }

    public static void drawRect(Graphics2D g, int x, int y, int padding) {
        g.drawRect(x * Constants.ZOOM_LEVEL + padding, y * Constants.ZOOM_LEVEL + padding, Constants.ZOOM_LEVEL - padding * 2, Constants.ZOOM_LEVEL - padding * 2);
    }

    public static void fillRect(Graphics2D g, int x, int y, int padding) {
        g.fillRect(x * Constants.ZOOM_LEVEL + padding, y * Constants.ZOOM_LEVEL + padding, Constants.ZOOM_LEVEL - padding * 2 + 1, Constants.ZOOM_LEVEL - padding * 2 + 1);
    }

    public static void fillOval(Graphics2D g, int x, int y, int radius) {
        g.fillOval(x * Constants.ZOOM_LEVEL + Constants.ZOOM_LEVEL / 2 - radius / 2, y * Constants.ZOOM_LEVEL + Constants.ZOOM_LEVEL / 2 - radius / 2, radius, radius);
    }

    public static void fillOval(Graphics2D g, int x, int y, int width, int height) {
        g.fillOval(x * Constants.ZOOM_LEVEL + Constants.ZOOM_LEVEL / 2 - width / 2, y * Constants.ZOOM_LEVEL + Constants.ZOOM_LEVEL / 2 - height / 2, width, height);
    }

    public static void drawCross(Graphics2D g, int x, int y) {
        g.drawLine(x * Constants.ZOOM_LEVEL, y * Constants.ZOOM_LEVEL, (x + 1) * Constants.ZOOM_LEVEL, (y + 1) * Constants.ZOOM_LEVEL);
        g.drawLine(x * Constants.ZOOM_LEVEL, (y + 1) * Constants.ZOOM_LEVEL, (x + 1) * Constants.ZOOM_LEVEL, y * Constants.ZOOM_LEVEL);
    }

    public static void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1 * Constants.ZOOM_LEVEL, y1 * Constants.ZOOM_LEVEL, x2 * Constants.ZOOM_LEVEL, y2 * Constants.ZOOM_LEVEL);
    }

    public static void drawMST(Graphics2D g, MST mst) {
        if (mst == null) {
            return;
        }

        int i = 0;
        for (MST.Edge edge : mst.getEdges()) {
            g.setColor(i % 2 == 0 ? GREEN_TRANSPARENT : DARK_GREEN_TRANSPARENT);
            g.setStroke(new BasicStroke(edge.weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            Draw.drawLine(g, edge.v.x, edge.v.y, edge.w.x, edge.w.y);
            i++;
        }
        g.setStroke(new BasicStroke(1));
    }

    public static void drawPath(Graphics2D g, Stack<? extends SearchNode> path) {
        if (path == null) {
            return;
        }

        for (SearchNode node : path) {
            g.setColor(Constants.SEARCH_PATH_COLOR);
            g.setStroke(new BasicStroke(2));
            drawCross(g, node.pos.x, node.pos.y);
        }
        g.setStroke(new BasicStroke(1));
    }

    public static void drawInfluenceMap(Graphics2D g, float[][] influenceMap) {
        drawInfluenceMap(g, influenceMap, true, true);
    }

    public static void drawInfluenceMap(Graphics2D g, float[][] influenceMap, boolean showFills, boolean showLabels) {
        float colorMultiplier = 5f;
        for (int x = 0; x < influenceMap.length; x++) {
            for (int y = 0; y < influenceMap[0].length; y++) {
                float val = influenceMap[x][y] * colorMultiplier;
                if (showFills) {
                    Color color = null;

                    if (val > 0) {
                        color = Color.getHSBColor(0.66f, 1, Math.min(1f, val));
                    } else if (val < 0) {
                        color = Color.getHSBColor(1f, 1, Math.min(1f, Math.abs(val)));
                    }
                    if (color != null) {
                        g.setColor(color);
                        Draw.fillRect(g, x, y, 0);
                    }
                }

                if (showLabels) {
                    if (val != 0) {
                        g.setColor(Color.white);
                        Draw.drawString(g, x, y, String.format("%,.2f", val));
                    }
                }
            }
        }
    }

    public static void drawMaze(Graphics2D g, GameState gameState, float pulse, int phase) {
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {

                char c = gameState.getCell(new XY(i, j));
                g.setColor(Constants.COLOR_MAP.get(c));
                switch (c) {
                    case Constants.PILL:
                        Draw.fillRect(g, i, j, 10);
                        break;
                    case Constants.BONUS_PILL:
                        g.setColor(Color.getHSBColor(Math.min(1f, pulse), 0.22f, 1));
                        //Draw.fillOval(g, i, j, Math.abs((int) (15 * Math.cos((pulse + 1) * Math.PI))), 15);
                        Draw.fillOval(g, i, j, (int) (15 * pulse), 15);
                        break;
                    case Constants.PLAYER_A:
                        Draw.fillRect(g, i, j, 2);
                        g.setColor(Color.black);
                        Draw.drawString(g, i, j, "" + Constants.PLAYER_A, 10, 10);
                        Draw.drawString(g, i, j, "" + gameState.getPlayerAScore(), 3, 18);
                        break;
                    case Constants.PLAYER_B:
                        Draw.fillRect(g, i, j, 2);
                        g.setColor(Color.black);
                        Draw.drawString(g, i, j, "" + Constants.PLAYER_B, 10, 10);
                        Draw.drawString(g, i, j, "" + gameState.getPlayerBScore(), 3, 18);
                        break;
                    case Constants.SPACE:
                        if (Util.inSpawnZone(i, j)) {
                            g.setColor(Color.lightGray);
                            Draw.drawCross(g, i, j);
                        }
                        break;
                    case Constants.POISON_PILL:
                        Draw.drawRect(g, i, j, 2 + phase % 2);

                        g.setColor(new Color(0, 255, 0, (int) (127 * Math.abs(pulse))));
                        Draw.fillRect(g, i, j, (int)(10 * pulse) + phase * 2 % 3);
                        break;
                    default:
                        Draw.fillRect(g, i, j, 0);
                        g.setColor(Constants.WALL_CEMENT_COLOR);
                        Draw.drawRect(g, i, j, 0);
                        break;
                }

                if (i == 0 || j == 0) {
                    g.setColor(Color.black);
                    Draw.drawString(g, i, j, "" + (i == 0 ? j : i), 7, 15);
                }
            }
        }

        g.setColor(Color.green);
        Draw.drawRect(g, Constants.PORTAL_X, Constants.PORTAL_Y, 0);
        Draw.drawRect(g, Constants.PORTAL_X, Constants.PORTAL_Y, 2);

        g.setColor(new Color(0, 255, 0, (int) (127 * Math.abs(pulse))));
        Draw.fillRect(g, Constants.PORTAL_X, Constants.PORTAL_Y, 2);
    }

    public static void drawGameGraph(Graphics2D g, GameStateGraph graph) {
        java.util.List<GameStateGraph.Edge> edges = graph.getEdges();
        int i = 1;
        for (GameStateGraph.Edge edge : edges) {
            if (false && i > 4) {
                break;
            }
            if (i % 2 == 0) {
                g.setColor(Color.getHSBColor(i / (float)edges.size(), 1, 1));
            } else {
                g.setColor(Color.getHSBColor(0.5f - i / (float)edges.size(), 1, 1));
            }
            for (XY xy : edge.getCells()) {
                if (true || i == 3) {
                    Draw.fillRect(g, xy.x, xy.y, 0);
                }
            }
            i++;
        }

        Collection<GameStateGraph.Vertex> vertices = graph.getIntersects();
        for (XY xy : vertices) {
            g.setColor(Color.pink);
            Draw.drawCross(g, xy.x, xy.y);
        }
    }
}

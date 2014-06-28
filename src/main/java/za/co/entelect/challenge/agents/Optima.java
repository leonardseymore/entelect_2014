package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.ai.opta.Opta;
import za.co.entelect.challenge.ai.opta.Visit;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.swing.Draw;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Optima extends PacmanAgent {

    private List<XY> moves;
    private List<Visit> visitList;
    private int count = 0;
    private int frameCount = 0;
    private int frames = 10;

    protected XY getMove(GameState gameState, XY pos) throws NoMoveFoundException {
        visitList = Opta.solve(gameState);
        moves = Opta.moves(visitList, gameState);
        XY target = moves.get(0);
        Stack<SearchNode> path = Search.tactical(gameState, pos, target);
        if (path == null) {
            return null;
        }

        SearchNode moo = path.peek();
        while ((pos.equals(moo.pos)) && !path.isEmpty()) {
            moo = path.pop();
        }
        return moo.pos;
    }

    @Override
    public void drawDebugInfo(Graphics2D g, GameState gameState, XY pos) {

        int i = 0;
        /*
        for (Visit visit : visitList) {
            if (frameCount++ > frames) {
                frameCount = 0;

                if (count++ > visitList.size()) {
                    count = 0;
                }
            }

            int x1 = visit.getCell().x;
            int y1 = visit.getCell().y;
            int x2 = visit.getPreviousCell().getCell().x;
            int y2 = visit.getPreviousCell().getCell().y;

            g.setColor(Color.getHSBColor(0.6f, 1 - i / (float)visitList.size(), 1f));
//            Draw.drawString(g, x1, y1);
//            g.setColor(Color.red);
//            g.drawString("s", x2, y2);
//            g.setColor(Color.green);
            Draw.drawLine(g, x1, y1, x2, y2);
            i++;

            if (i > count) {
                break;
            }
        }
        */

        if (frameCount++ > frames) {
            frameCount = 0;

            if (count++ > moves.size()) {
                count = 0;
            }
        }

        for (XY move : moves) {
            int x = move.x;
            int y = move.y;

            g.setColor(Color.getHSBColor(0.6f, 1 - i / (float)visitList.size(), 1f));
            Draw.drawRect(g, x, y, 0);
            Draw.drawString(g, x, y, "" + i);
            i++;

            if (i > count) {
                break;
            }
        }
    }
}

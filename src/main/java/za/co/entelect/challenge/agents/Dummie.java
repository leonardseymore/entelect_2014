package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.swing.Draw;

import java.awt.*;

public class Dummie extends PacmanAgent {

    protected XY getMove(GameState gameState, XY pos) throws NoMoveFoundException {
        return Search.getAvailableNeighbors(gameState, new SearchNode(pos)).iterator().next().pos;
    }

    @Override
    public void drawDebugInfo(Graphics2D g, GameState gameState, XY pos) {
        g.setColor(Color.gray);
        g.setStroke(new BasicStroke(3));
        Draw.drawCross(g, pos.x, pos.y);
        g.setStroke(new BasicStroke(1));
    }
}

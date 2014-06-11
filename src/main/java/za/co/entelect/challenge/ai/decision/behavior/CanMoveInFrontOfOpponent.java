package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;

public class CanMoveInFrontOfOpponent extends Task {
    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();
        XY y = gameState.getCurrentPosition();
        XY o = gameState.getOpponentPosition();

        if (Util.inSpawnZone(y)) {
            return false;
        }


        SearchNode node = new SearchNode(o);
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, node);
        for (SearchNode neighbor : neighbors) {
            if (Util.isBesides(y, neighbor.pos)) {
                blackboard.target = neighbor.pos;
                return true;
            }
        }

        return false;
    }

    @Override
    protected String getLabel() {
        return "CanMoveInFrontOfOpponent?";
    }
}

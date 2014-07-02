package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class MoveToHighestPotentialNeighbor extends Task {

    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        Blackboard blackboard = pacmanAgent.getBlackboard();

        XY pos = gameState.getCurrentPosition();

//        int quad = 0;
//        if (pos.x < 10 && pos.y < 11) {
//            quad = 1;
//        } else if (pos.x < 10 && pos.y >= 11) {
//            quad = 2;
//        } else if (pos.x >= 10 && pos.y < 11) {
//            quad = 3;
//        } else {
//            quad = 4;
//        }

        XY bestMatch = null;
        float bestPotential = 0;
        for (SearchNode node : Search.getAvailableNeighbors(gameState, new SearchNode(pos))) {
            float potential = gameState.getInfluenceMap().getPotentialYMap()[node.pos.x][node.pos.y];
//            XY p = node.pos;
//            int q = 0;
//            if (p.x < pos.x) {
//                q = 1;
//            } else if (p.y < pos.y) {
//                q = 2;
//            } else if (p.x > pos.x) {
//                q = 3;
//            } else {
//                q = 4;
//            }
//
//            if (gameState.getCell(p) == Constants.PILL && (quad == 1 && (q == 1 || q == 2)) || quad == 2 && (q == 1 || q == 4) || (quad == 3 && (q == 2 || q == 3) || (quad == 4 && (q == 3 || q == 4)))) {
//                potential *= 10;
//            }

            if (potential > bestPotential) {
                bestPotential = potential;
                bestMatch = node.pos;
            }
        }
        if (bestMatch == null) {
            return false;
        }

        blackboard.nextMove = bestMatch;
        return true;
    }
}

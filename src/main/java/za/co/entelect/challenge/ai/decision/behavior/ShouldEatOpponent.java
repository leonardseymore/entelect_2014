package za.co.entelect.challenge.ai.decision.behavior;


import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

public class ShouldEatOpponent extends Task {
    public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
        XY y = gameState.getCurrentPosition();
        XY o = gameState.getOpponentPosition();
        InfluenceMap influenceMap = gameState.getInfluenceMap();
        float currentPotential = influenceMap.getTotalOPotential();

        GameState gameStateAfterMove = gameState.clone();
        gameStateAfterMove.makeMove(y, o, GameState.UPDATE_INFLUENCE_MAP);

        InfluenceMap influenceMapAfterMove = gameStateAfterMove.getInfluenceMap();
        float futurePotential = influenceMapAfterMove.getTotalYPotential();

        if (futurePotential < currentPotential) {
            return true;
        }

        return false;
    }

    @Override
    protected String getLabel() {
        return "ShouldEatOpponent?";
    }
}

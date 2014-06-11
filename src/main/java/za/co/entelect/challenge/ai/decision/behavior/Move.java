package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.ai.blackboard.Blackboard;
import za.co.entelect.challenge.domain.GameState;

public class Move extends Task {
  public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
    Blackboard blackboard = pacmanAgent.getBlackboard();
    blackboard.moveTo = blackboard.nextMove;
    return true;
  }

  @Override
  protected String getLabel() {
    return "Move";
  }
}

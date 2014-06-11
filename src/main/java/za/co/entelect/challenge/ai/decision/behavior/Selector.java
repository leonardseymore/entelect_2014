package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.GameState;

public class Selector extends Task {
  public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
    for (Task child : children) {
      if (child.run(pacmanAgent, gameState)) {
        return true;
      }
    }
    return false;
  }

  @Override
  protected String getLabel() {
    return "?";
  }
}

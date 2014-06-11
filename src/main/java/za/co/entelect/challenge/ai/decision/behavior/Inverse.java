package za.co.entelect.challenge.ai.decision.behavior;

import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.GameState;

public class Inverse extends Decorator {

  public Inverse(Task child) {
    super(child);
  }

  public boolean run(PacmanAgent pacmanAgent, GameState gameState) {
    return !child.run(pacmanAgent, gameState);
  }

  @Override
  protected String getLabel() {
    return "~" + child.getLabel();
  }
}

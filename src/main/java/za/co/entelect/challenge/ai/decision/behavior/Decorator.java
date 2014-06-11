package za.co.entelect.challenge.ai.decision.behavior;

public abstract class Decorator extends Task {
  protected Task child;

  public Decorator(Task child) {
    this.child = child;
  }
}

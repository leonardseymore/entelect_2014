package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Epa extends BehaviorTreeAgent {

    public Epa(long thinkTime, float heuristic) {
        super(
                new Sequence()
                        .a(new GuessBestMoveUct(-1, thinkTime, heuristic))
                        .a(new MoveToTarget())
                        .a(new Move())
        );
    }
}

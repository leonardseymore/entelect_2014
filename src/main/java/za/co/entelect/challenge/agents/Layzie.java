package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.Move;
import za.co.entelect.challenge.ai.decision.behavior.MoveToClosestPill;
import za.co.entelect.challenge.ai.decision.behavior.Sequence;

public class Layzie extends BehaviorTreeAgent {

    public Layzie() {
        super(
                new Sequence()
                .a(new MoveToClosestPill())
                .a(new Move())
        );
    }

}

package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Eddie extends BehaviorTreeAgent {

    public Eddie() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToOpponent())
                                .a(new MoveToClosestPill())
                )
                .a(new Move())
        );
    }
}

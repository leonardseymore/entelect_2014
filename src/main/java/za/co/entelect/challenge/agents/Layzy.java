package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Layzy extends BehaviorTreeAgent {

    public Layzy() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToClosestInfluencePill())
                                .a(new MoveToClosestPill())
                )
                .a(new Move())
        );
    }

}

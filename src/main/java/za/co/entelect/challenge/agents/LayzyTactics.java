package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class LayzyTactics extends BehaviorTreeAgent {

    public LayzyTactics() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToClosestInfluencePill(true))
                                .a(new MoveToClosestPill(true))
                )
                .a(new Move())
        );
    }

}

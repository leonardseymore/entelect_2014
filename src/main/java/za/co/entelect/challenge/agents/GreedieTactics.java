package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class GreedieTactics extends BehaviorTreeAgent {

    public GreedieTactics() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToClosestBonusPill(true))
                                .a(new MoveToClosestPill(true))
                )
                .a(new Move())
        );
    }
}

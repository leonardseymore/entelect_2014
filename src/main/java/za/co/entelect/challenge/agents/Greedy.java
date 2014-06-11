package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Greedy extends BehaviorTreeAgent {
    public Greedy() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToClosestInfluenceBonusPill())
                                .a(new MoveToClosestBonusPill())
                                .a(new MoveToClosestInfluencePill())
                                .a(new MoveToClosestPill())
                )
                .a(new Move())
        );
    }
}

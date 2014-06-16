package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Attack extends BehaviorTreeAgent {

    public Attack() {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                .a(new CanEatOpponent())
                                                .a(new ShouldEatOpponent())
                                                .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                .a(new MoveToHighestPotential(true))
                                                .a(new MoveToClosestPill())
                                        )
                        )
                        .a(new Move())
        );
    }

}

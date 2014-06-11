package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Vladimir extends BehaviorTreeAgent {

    public Vladimir() {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                        .a(new CanEatOpponent())
                                                        .a(new ShouldEatOpponent())
                                                        .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                        .a(new MoveToHighestPotential())
                                                        .a(new MoveToClosestPill())
                                        )
                        )
                        .a(new Move())
        );
    }

}

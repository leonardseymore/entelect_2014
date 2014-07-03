package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Grr extends BehaviorTreeAgent {

    public Grr() {
        super(
                new Sequence()
                        .a(new Selector()
                                .a(new Sequence()
                                        .a(new CanEatOpponent())
                                        .a(new ShouldEatOpponent())
                                        .a(new MoveToTarget())
                                )
                                .a(new Selector()
                                        .a(new MoveToBiggestCluster())
//                                        .a(new MoveToHighestPotentialNeighbor())
//                                        .a(new MoveToClosestPill(true))
                                )
                        )
                        .a(new Move())
        );
    }

}

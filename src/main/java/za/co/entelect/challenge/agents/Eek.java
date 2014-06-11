package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Eek extends BehaviorTreeAgent {

    public Eek() {
        super(
                new Sequence()
                        .a(new Selector()
                                .a(new Sequence()
                                        .a(new CanEatOpponent())
                                        .a(new ShouldEatOpponent())
                                        .a(new MoveToTarget())
                                )
                                .a(new Sequence()
                                        .a(new IsAnyPillCloserThan(8))
                                        .a(new IsOpponentCloserThan(8))
                                        .a(new GuessBestMoveMinimax(18))
                                        .a(new MoveToTarget())
                                )
                                .a(new Selector()
                                        .a(new MoveToClosestBonusPill())
                                        .a(new MoveToClosestInfluencePill())
                                        .a(new MoveToClosestPill())
                                )
                        )
                        .a(new Move())
        );
    }

}

package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Angus extends BehaviorTreeAgent {

    public Angus() {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                        .a(new CanEatOpponent())
                                                        .a(new ShouldEatOpponent())
                                                        .a(new MoveToTarget())
                                        )
                                        .a(new Sequence()
                                                        .a(new CanMoveInFrontOfOpponent())
                                                        .a(new ShouldMoveToTarget())
                                                        .a(new MoveToTarget())
                                        )
                                        .a(new Sequence()
                                                        .a(new IsAnyPillCloserThan(5))
                                                        .a(new IsOpponentCloserThan(5))
                                                        .a(new GuessBestMoveMinimax(12))
                                                        .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                        .a(new MoveToClosestInfluencePill())
                                                        .a(new MoveToClosestPill())
                                        )
                        )
                        .a(new Move())
        );
    }

}

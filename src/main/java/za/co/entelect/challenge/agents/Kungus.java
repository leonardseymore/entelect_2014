package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Kungus extends BehaviorTreeAgent {

    public Kungus() {
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
                                .a(new Selector()
                                        .a(new MoveToClosestBonusPill())
                                        .a(new MoveToClosestPill())
                                )
                        )
                        .a(new Move())
        );
    }

}

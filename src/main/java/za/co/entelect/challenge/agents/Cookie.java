package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.AIProps;
import za.co.entelect.challenge.ai.decision.behavior.*;

public class Cookie extends BehaviorTreeAgent {

    public Cookie() {
        super(
                new Sequence()
                        .a(new Selector()
                                .a(new Sequence()
                                        .a(new CanEatOpponent())
                                        .a(new ShouldEatOpponent())
                                        .a(new MoveToTarget())
                                )
                                .a(new MoveToClosestBonusPill(true))
                                /*
                                .a(new Sequence()
                                        .a(new IsAnyPillCloserThan(6))
                                        .a(new IsOpponentCloserThan(6))
                                        .a(new GuessBestMoveMinimax(14))
                                        .a(new MoveToTarget())
                                )
                                */
                                .a(new Sequence()
                                        .a(new IsAnyPillCloserThan(14))
                                        .a(new GuessBestMoveBfs(14))
                                        .a(new MoveToTarget())
                                )
                                .a(new Selector()
                                        .a(new MoveToHighestPotentialPill(true, AIProps.DEPTH_HIGHEST_POTENTIAL_PILL))
                                        .a(new MoveToClosestPill(true))
                                )
                        )
                        .a(new Move())
        );
    }

}

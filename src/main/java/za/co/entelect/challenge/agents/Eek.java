package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.AIProps;
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
                                        .a(new GuessBestMoveBfs(12))
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

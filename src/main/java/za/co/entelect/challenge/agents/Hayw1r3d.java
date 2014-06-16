package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.AIProps;
import za.co.entelect.challenge.ai.decision.behavior.*;

public class Hayw1r3d extends BehaviorTreeAgent {

    public Hayw1r3d() {
        this(AIProps.DEPTH_HIGHEST_POTENTIAL_PILL);
    }

    public Hayw1r3d(int numMatches) {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                .a(new CanEatOpponent())
                                                .a(new ShouldEatOpponent())
                                                .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                .a(new MoveToHighestPotentialPill(true, numMatches))
                                                .a(new MoveToClosestPill())
                                        )
                        )
                        .a(new Move())
        );
    }

}

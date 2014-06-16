package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.AIProps;
import za.co.entelect.challenge.ai.decision.behavior.*;

public class Mandy extends BehaviorTreeAgent {

    public Mandy() {
        this(AIProps.DEPTH_HIGHEST_POTENTIAL_PILL);
    }

    public Mandy(int numMatches) {
        super(
                new Sequence()
                        .a(new Selector()
                                .a(new Sequence()
                                        .a(new CanEatOpponent())
                                        .a(new ShouldEatOpponent())
                                        .a(new MoveToTarget())
                                )
                                .a(new Selector()
                                        .a(new MoveToClosestInfluenceBonusPill())
                                        .a(new MoveToClosestBonusPill())
                                        .a(new MoveToHighestPotentialPill(false, numMatches))
                                        .a(new MoveToClosestPill())
                                )
                        )
                        .a(new Move())
        );
    }
}

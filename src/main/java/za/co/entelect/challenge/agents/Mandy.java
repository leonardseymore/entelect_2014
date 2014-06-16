package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Mandy extends BehaviorTreeAgent {

    public Mandy() {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                        .a(new CanEatOpponent())
                                                        .a(new ShouldEatOpponent())
                                                        .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                        .a(new MoveToBiggestClusterYourInfluence())
                                                        .a(new MoveToClosestPill())
                                        )
                        )
                        .a(new Move())
        );
    }

}

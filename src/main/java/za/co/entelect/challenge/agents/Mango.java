package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.decision.behavior.*;

public class Mango extends BehaviorTreeAgent {

    public Mango() {
        super(
                new Sequence()
                        .a(new Selector()
                                        .a(new Sequence()
                                                .a(new CanEatOpponent())
                                                .a(new ShouldEatOpponent())
                                                .a(new MoveToTarget())
                                        )
                                        .a(new Selector()
                                                .a(new GuessBestMoveMst(8))
                                                .a(new MoveToClosestPill(true))
                                        )
                        )
                        .a(new Move())
        );
    }

}

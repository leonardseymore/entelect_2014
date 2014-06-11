package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Naratu extends BehaviorTreeAgent {

    public Naratu() {
        super(
                new Sequence()
                        .a(new GuessBestMoveMinimax(10))
                        .a(new MoveToTarget())
                        .a(new Move())
        );
    }

}

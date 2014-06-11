package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Greedie extends BehaviorTreeAgent {

    public Greedie() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToClosestBonusPill())
                                .a(new MoveToClosestPill())
                )
                .a(new Move())
        );
    }
}

package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.ai.decision.behavior.*;

public class Rover extends BehaviorTreeAgent {

    public Rover() {
        super(
                new Sequence()
                .a(new Selector()
                                .a(new MoveToHighestPathPotential())
                )
                .a(new Move())
        );
    }
}

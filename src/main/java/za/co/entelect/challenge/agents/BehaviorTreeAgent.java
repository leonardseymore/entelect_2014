package za.co.entelect.challenge.agents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.ai.decision.behavior.Task;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.swing.Draw;

import java.awt.*;
import java.util.Stack;

public class BehaviorTreeAgent extends PacmanAgent {

    private static final Logger logger = LoggerFactory.getLogger(BehaviorTreeAgent.class);

    private Stack<SearchNode> lastPath;
    private Task behaviorTree;

    public BehaviorTreeAgent(Task behaviorTree) {
        this.behaviorTree = behaviorTree;
        logger.debug("Behavior Tree DOT:\n{}", behaviorTree.toDot(getName()));
    }

    protected XY getMove(GameState gameState, XY pos) throws NoMoveFoundException {
        behaviorTree.run(this, gameState);
        XY moveTo = blackboard.moveTo;
        lastPath = blackboard.searchPath;
        if (moveTo == null) {
            throw new NoMoveFoundException("Could not find a valid path to any pills");
        }
        return moveTo;
    }

    public void drawDebugInfo(Graphics2D g, GameState gameState, XY pos) {
        Draw.drawPath(g, lastPath);
    }
}
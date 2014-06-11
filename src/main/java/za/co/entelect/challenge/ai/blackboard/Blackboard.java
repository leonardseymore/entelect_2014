package za.co.entelect.challenge.ai.blackboard;

import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.XY;

import java.util.Stack;

public class Blackboard {

    public Stack<SearchNode> searchPath;
    public XY nextMove;
    public XY moveTo;
    public XY target;

    public Blackboard() {
        reset();
    }

    public void reset() {
        nextMove = null;
        moveTo = null;
        searchPath = null;
        target = null;
    }
}

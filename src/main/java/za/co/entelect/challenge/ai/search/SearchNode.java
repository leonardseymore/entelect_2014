package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.XY;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SearchNode implements Comparable<SearchNode> {
    public XY pos;
    public float goalCost;
    public float runningCost;

    public SearchNode parent;

    public SearchNode(XY pos) {
        this.pos = pos;
    }

    @Override
    public int compareTo(SearchNode o) {
        return runningCost > o.runningCost ? +1 : runningCost < o.runningCost ? -1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SearchNode)) return false;

        SearchNode node = (SearchNode) o;

        if (!pos.equals(node.pos)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    public static Stack<SearchNode> pathToNode(SearchNode currentNode) {
        Stack<SearchNode> path = new Stack<>();
        do {
            path.add(currentNode);
            currentNode = currentNode.parent;
        } while (currentNode != null);
        return path;
    }
}

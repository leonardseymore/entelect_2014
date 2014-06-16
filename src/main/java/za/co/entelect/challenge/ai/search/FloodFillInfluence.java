package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class FloodFillInfluence {

    public static Collection<SearchNode> getInfluence(GameState gameState, XY pos) {
        Queue<SearchNode> open = new PriorityQueue<>();

        SearchNode initialNode = new SearchNode(pos);
        open.add(initialNode);
        Collection<SearchNode> closed = new HashSet<>();
        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();
            for (SearchNode node : Search.getAvailableNeighbors(gameState, currentNode)) {
                float estRunningCost = currentNode.runningCost + node.runningCost;
                if (closed.contains(node)) {
                    assert node.runningCost <= estRunningCost;
                    continue;
                } else if (open.contains(node)) {
                    if (node.runningCost <= estRunningCost) {
                        continue;
                    }
                } else {
                    node.runningCost = estRunningCost;
                    if (!open.contains(node)) {
                        open.add(node);
                    }
                }
            }
            open.remove(currentNode);
            closed.add(currentNode);
        }
        return closed;
    }
}

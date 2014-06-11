package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

public class FloodFillPotential {

    public static final String POTENTIAL = "POTENTIAL";

    public static TestNeighborPot TEST_NEIGHBOR = new TestNeighborPot();

    private static class TestNeighborPot extends Search.TestNeighbor {
        public void testNeighbor(GameState gameState, SearchNode node, XY moveTo, Collection<SearchNode> neighbors) {
            if (!Util.isInBounds(moveTo) || Util.isWall(moveTo)) {
                return;
            }

            if (gameState.getCell(moveTo) == Constants.PILL || gameState.getCell(moveTo) == Constants.BONUS_PILL) {
                return;
            }

            SearchNode toNode = new SearchNode(moveTo);
            toNode.parent = node;
            toNode.runningCost = 1;
            neighbors.add(toNode);
        }
    }

    public static Collection<SearchNode> getPotential(GameState gameState) {
        Queue<SearchNode> open = new PriorityQueue<>();

        for (XY xy : gameState.getPills()) {
            SearchNode node = new SearchNode(xy);
            node.setProp(POTENTIAL, (float) Constants.PILL_SCORE);
            open.add(node);
        }

        for (XY xy : gameState.getBonusPills()) {
            SearchNode node = new SearchNode(xy);
            node.setProp(POTENTIAL, (float) Constants.BONUS_PILL_SCORE);
            open.add(node);
        }

        Collection<SearchNode> closed = new HashSet<>();
        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();
            for (SearchNode node : Search.getAvailableNeighbors(gameState, currentNode, TEST_NEIGHBOR)) {
                float estRunningCost = currentNode.runningCost + node.runningCost;
                float potential = (float) currentNode.getProp(POTENTIAL);
                float estPotential = potential / estRunningCost;
                node.setProp(POTENTIAL, estPotential);
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

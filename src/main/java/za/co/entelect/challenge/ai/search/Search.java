package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

public abstract class Search {

    public abstract Stack<SearchNode> search(GameState gameState, XY pos, SearchCriteria criteria);

    private static final SearchMazeDistance searchMazeDistance = new SearchMazeDistance();
    public static Stack<SearchNode> mazeDist(XY pos, XY target) {
        return searchMazeDistance.search(pos, target);
    }

    private static final SearchBfs searchBfs = new SearchBfs();
    public static Stack<SearchNode> bfs(GameState gameState, XY pos, SearchCriteria criteria) {
        return searchBfs.search(gameState, pos, criteria);
    }

    public static TestNeighbor DEFAULT_TEST_NEIGHBOR = new TestNeighbor();

    public static class TestNeighbor {
        public void testNeighbor(GameState gameState, SearchNode node, XY moveTo, Collection<SearchNode> neighbors) {
            if (!Util.isInBounds(moveTo) || !Util.canMoveTo(gameState, moveTo, node.pos)) {
                return;
            }

            SearchNode toNode = new SearchNode(moveTo);
            toNode.parent = node;
            toNode.runningCost = 1;
            neighbors.add(toNode);
        }
    }

    public static Collection<SearchNode> getAvailableNeighbors(GameState gameState, SearchNode node) {
        return getAvailableNeighbors(gameState, node, DEFAULT_TEST_NEIGHBOR);
    }

    public static Collection<SearchNode> getAvailableNeighbors(GameState gameState, SearchNode node, TestNeighbor testNeighbor) {
        Collection<SearchNode> neighbors = new ArrayList<>();

        XY moveTo = node.pos;
        if (gameState.isPoisonPill(moveTo)) {
            moveTo = Constants.PORTAL;
        }

        if (Util.isWarp(moveTo)) {
            for (XY xy : Util.getWarpNeighbors(moveTo)) {
                testNeighbor.testNeighbor(gameState, node, xy, neighbors);
            }
        } else {
            testNeighbor.testNeighbor(gameState, node, new XY(moveTo.x, moveTo.y - 1), neighbors);
            testNeighbor.testNeighbor(gameState, node, new XY(moveTo.x + 1, moveTo.y), neighbors);
            testNeighbor.testNeighbor(gameState, node, new XY(moveTo.x, moveTo.y + 1), neighbors);
            testNeighbor.testNeighbor(gameState, node, new XY(moveTo.x - 1, moveTo.y), neighbors);
        }

        return neighbors;
    }
}

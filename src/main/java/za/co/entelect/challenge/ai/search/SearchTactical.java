package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.AIProps;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class SearchTactical {

    public Stack<SearchNode> search(GameState gameState, XY pos, XY target) {
        Queue<SearchNode> open = new PriorityQueue<>();
        Collection<SearchNode> closed = new HashSet<>();

        SearchNode start = new SearchNode(pos);
        start.goalCost = heuristic(gameState, start, target);
        open.add(start);

        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();

            if (currentNode.pos.equals(target)) {
                return SearchNode.pathToNode(currentNode);
            }

            open.remove(currentNode);
            closed.add(currentNode);
            for (SearchNode toNode : getAvailableNeighbors(gameState, currentNode)) {
                toNode.goalCost = heuristic(gameState, toNode, target);
                float goalCost = currentNode.goalCost;
                float estGoalCost = goalCost + toNode.goalCost;
                assert estGoalCost >= 0 : "Goal cost " + estGoalCost + " must be >= 0";
                if (closed.contains(toNode)) {
                    if (toNode.runningCost > estGoalCost) {
                        closed.remove(toNode);
                        toNode.parent = currentNode;
                        toNode.runningCost = estGoalCost;
                    } else {
                        continue;
                    }
                }

                if (!open.contains(toNode)) {
                    toNode.runningCost = estGoalCost;
                    open.add(toNode);
                } else {
                    if (toNode.runningCost < estGoalCost) {
                        toNode.runningCost = estGoalCost;
                        toNode.parent = currentNode;
                    }
                }
            }
        }

        return null;
    }

    public float heuristic(GameState gameState, SearchNode node, XY target)  {
        int dist = Util.mazeDistance(node.pos, target);
        if (Util.isWarp(node.pos)) {
            XY warp = Util.getWarp(node.pos);
            if (warp.equals(target)) {
                dist = 1;
            }
        }

        float val = dist;
        char moveVal = gameState.getCell(node.pos);
        if (moveVal == Constants.PILL) {
            val += AIProps.PILL_COST;
        } else if (moveVal == Constants.BONUS_PILL) {
            val += AIProps.BONUS_PILL_COST;
        } else {
            val += AIProps.EMPTY_COST;
        }

        /*
        InfluenceMap influenceMap = gameState.getInfluenceMap();
        int influence = (int) influenceMap.getInfluenceOMap()[node.pos.x][node.pos.y] * AIProps.INFLUENCE_COST;
        if (val + influence >= 0) {
            val += influence;
        } else {
            val = Integer.MAX_VALUE;
        }
        */

        InfluenceMap influenceMap = gameState.getInfluenceMap();
        float potential = influenceMap.getPotentialOMap()[node.pos.x][node.pos.y] * AIProps.INFLUENCE_COST;
        if (potential > 0) {
            val += potential;
        }

        return val;
    }

    protected Collection<SearchNode> getAvailableNeighbors(GameState gameState, SearchNode node) {
        Collection<SearchNode> neighbors = new ArrayList<>();

        XY moveTo = node.pos;
        if (Util.isWarp(moveTo)) {
            for (XY xy : Util.getWarpNeighbors(moveTo)) {
                testNeighbor(gameState, node, xy, neighbors);
            }
        } else {
            testNeighbor(gameState, node, new XY(moveTo.x, moveTo.y - 1), neighbors);
            testNeighbor(gameState, node, new XY(moveTo.x + 1, moveTo.y), neighbors);
            testNeighbor(gameState, node, new XY(moveTo.x, moveTo.y + 1), neighbors);
            testNeighbor(gameState, node, new XY(moveTo.x - 1, moveTo.y), neighbors);
        }

        return neighbors;
    }

    protected void testNeighbor(GameState gameState, SearchNode node, XY moveTo, Collection<SearchNode> neighbors) {
        if (!Util.isValidMove(gameState, moveTo, node.pos)) {
            return;
        }

        SearchNode toNode = new SearchNode(moveTo);
        toNode.parent = node;
        neighbors.add(toNode);
    }
}

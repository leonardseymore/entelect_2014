package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class SearchMazeDistance {

    public Stack<SearchNode> search(XY pos, XY target) {
        Queue<SearchNode> open = new PriorityQueue<>();
        Collection<SearchNode> closed = new HashSet<>();

        SearchNode start = new SearchNode(pos);
        start.goalCost = distHeuristic(start, target);
        open.add(start);

        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();

            if (currentNode.pos.equals(target)) {
                return SearchNode.pathToNode(currentNode);
            }

            open.remove(currentNode);
            closed.add(currentNode);
            for (SearchNode toNode : getAvailableNeighbors(currentNode)) {
                toNode.goalCost = distHeuristic(toNode, target);
                int goalCost = currentNode.goalCost;
                float estGoalCost = goalCost + toNode.goalCost;
                assert estGoalCost >= 0;
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

    public static int distHeuristic(SearchNode node, XY target) {
        if (Util.isWarp(node.pos)) {
            XY warp = Util.getWarp(node.pos);
            if (warp.equals(target)) {
                return 1;
            }
        }

        int dist = Util.manhattanDist(node.pos, target);
        return dist;
    }

    protected Collection<SearchNode> getAvailableNeighbors(SearchNode node) {
        Collection<SearchNode> neighbors = new ArrayList<>();

        XY moveTo = node.pos;
        if (Util.isWarp(moveTo)) {
            for (XY xy : Util.getWarpNeighbors(moveTo)) {
                testNeighbor(node, xy, neighbors);
            }
        } else {
            testNeighbor(node, new XY(moveTo.x, moveTo.y - 1), neighbors);
            testNeighbor(node, new XY(moveTo.x + 1, moveTo.y), neighbors);
            testNeighbor(node, new XY(moveTo.x, moveTo.y + 1), neighbors);
            testNeighbor(node, new XY(moveTo.x - 1, moveTo.y), neighbors);
        }

        return neighbors;
    }

    protected void testNeighbor(SearchNode node, XY moveTo, Collection<SearchNode> neighbors) {
        if (!Util.isInBounds(moveTo) || !Util.isWall(moveTo)) {
            return;
        }

        SearchNode toNode = new SearchNode(moveTo);
        toNode.parent = node;
        neighbors.add(toNode);
    }
}

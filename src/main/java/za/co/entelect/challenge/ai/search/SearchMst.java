package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.AIProps;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.mcts.UCTGameState;
import za.co.entelect.challenge.domain.MST;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class SearchMst {

    public Stack<SearchNode> search(UCTGameState gameState, XY pos) {
        Queue<SearchNodeMst> open = new PriorityQueue<>();
        Collection<SearchNodeMst> closed = new HashSet<>();

        SearchNodeMst start = new SearchNodeMst(pos);
        start.gameState = gameState;
        start.goalCost = heuristic(gameState);
        open.add(start);

        while (!open.isEmpty()) {
            SearchNodeMst currentNode = open.poll();

            if (currentNode.gameState.isGameOver()) {
                return SearchNode.pathToNode(currentNode);
            }

            open.remove(currentNode);
            closed.add(currentNode);
            for (SearchNodeMst toNode : getAvailableNeighbors(currentNode.gameState, currentNode)) {
                toNode.goalCost = heuristic(currentNode.gameState);
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

    public float heuristic(UCTGameState gameState)  {
        MST mst = MST.fromGameState(gameState, gameState.getCurrentPosition());
        return mst.getWeight();
    }

    protected Collection<SearchNodeMst> getAvailableNeighbors(UCTGameState gameState, SearchNodeMst node) {

        Collection<SearchNodeMst> neighbors = new ArrayList<>();
        List<XY> moves = gameState.getMoves();
        for (XY move : moves) {
            UCTGameState clone = gameState.clone();
            clone.doMove(move, false, 0);
            SearchNodeMst toNode = new SearchNodeMst(move);
            toNode.parent = node;
            toNode.gameState = clone;
            neighbors.add(toNode);
        }
        return neighbors;
    }
    
    private class SearchNodeMst extends SearchNode {
        UCTGameState gameState;


        public SearchNodeMst(XY pos) {
            super(pos);
        }
    }
}

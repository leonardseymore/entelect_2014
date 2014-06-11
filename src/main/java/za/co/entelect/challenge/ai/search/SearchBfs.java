package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class SearchBfs extends Search {
    public Stack<SearchNode> search(GameState gameState, XY pos, SearchCriteria criteria) {
        Queue<SearchNode> open = new LinkedList<>();
        Set<SearchNode> visitedNodes = new HashSet<>();
        SearchNode start = new SearchNode(pos);
        open.add(start);
        visitedNodes.add(start);

        SearchNode target = null;
        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();
            if (criteria.matches(gameState, currentNode)) {
                target = currentNode;
                break;
            }

            for (SearchNode toNode : getAvailableNeighbors(gameState, currentNode)) {
                if (!visitedNodes.contains(toNode)) {
                    open.add(toNode);
                    visitedNodes.add(toNode);
                }
            }
        }

        if (target != null) {
            return SearchNode.pathToNode(target);
        }

        return null;
    }
}

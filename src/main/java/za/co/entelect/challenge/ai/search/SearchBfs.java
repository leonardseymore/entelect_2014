package za.co.entelect.challenge.ai.search;

import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class SearchBfs extends Search {

    public Stack<SearchNode> search(GameState gameState, XY pos, SearchCriteria criteria) {
        Stack<SearchNode> matches = search(gameState, pos, criteria, 1);
        if (matches.size() > 0) {
            return SearchNode.pathToNode(matches.pop());
        }
        return null;
    }

    public Stack<SearchNode> search(GameState gameState, XY pos, SearchCriteria criteria, int numMatches) {
        Queue<SearchNode> open = new LinkedList<>();
        Set<SearchNode> visitedNodes = new HashSet<>();
        SearchNode start = new SearchNode(pos);
        open.add(start);
        visitedNodes.add(start);

        Stack<SearchNode> matches = new Stack<>();
        while (!open.isEmpty()) {
            SearchNode currentNode = open.poll();
            if (criteria.matches(gameState, currentNode)) {
                if (matches.size() < numMatches) {
                    matches.push(currentNode);
                    visitedNodes.add(currentNode);
                } else {
                    break;
                }
            }

            for (SearchNode toNode : getAvailableNeighbors(gameState, currentNode)) {
                if (!visitedNodes.contains(toNode)) {
                    open.add(toNode);
                    visitedNodes.add(toNode);
                }
            }
        }
        return matches;
    }
}

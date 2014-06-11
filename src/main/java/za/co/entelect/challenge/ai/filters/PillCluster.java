package za.co.entelect.challenge.ai.filters;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class PillCluster {

    // Run cellular automata to find pills that have neighbors on all sides
    public static Map<XY, Integer> getClusteredPills(GameState gameState) {
        Set<XY> allPills = Util.getAllPills(gameState);
        Set<XY> pills = Util.clone(allPills);
        Set<XY> clusteredPills = new HashSet<>();

        List<XY> seen = new ArrayList<>();
        while (pills.size() > 0){
            clusteredPills.clear();
            pills:
            for (XY pill : pills) {
                for (SearchNode searchNode : Search.getAvailableNeighbors(gameState, new SearchNode(pill))) {
                    if (!pills.contains(searchNode.pos) || !Util.isPillAt(gameState, searchNode.pos)) {
                        continue pills;
                    }
                }
                clusteredPills.add(pill);
                seen.add(pill);
            }
            pills = Util.clone(clusteredPills);
        }

        Map<XY, Integer> clusters = new HashMap<>();
        for (XY pos : allPills) {
            clusters.put(pos, Collections.frequency(seen, pos));
        }

        return clusters;
    }

    public static Set<XY> getClusteredPills(GameState gameState, int iterations) {
        Set<XY> pills = Util.getAllPills(gameState);
        Set<XY> clusteredPills = new HashSet<>();

        for (int i = 0; i < iterations; i++) {
            clusteredPills.clear();
            pills:
            for (XY pill : pills) {
                for (SearchNode searchNode : Search.getAvailableNeighbors(gameState, new SearchNode(pill))) {
                    if (!pills.contains(searchNode.pos) || !Util.isPillAt(gameState, searchNode.pos)) {
                        continue pills;
                    }
                }
                clusteredPills.add(pill);
            }
            pills = Util.clone(clusteredPills);
        }

        return clusteredPills;
    }
}

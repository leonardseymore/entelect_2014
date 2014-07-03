package za.co.entelect.challenge.ai.filters;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.FloodFillInfluence;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.*;

public class PillCluster {

    public static float[][] getCellPotential(GameState gameState) {
        Map<XY, Integer> pills = getClusteredPills(gameState);
        if (pills.size() == 0) {
            return new float[Constants.WIDTH][Constants.HEIGHT];
        }
        Map<XY, Integer> spaces = getClusteredSpaces(gameState);

        int maxPillValue = 0;
        for (Integer val : pills.values()) {
            if (val > maxPillValue) {
                maxPillValue = val;
            }
        }

        int maxSpaceValue = 0;
        for (Integer val : spaces.values()) {
            if (val > maxSpaceValue) {
                maxSpaceValue = val;
            }
        }

        Set<XY> bonusPills = gameState.getBonusPills();
        int numBonusPills = bonusPills.size();
        float[][] bonusPillInfluence = new float[Constants.WIDTH][Constants.HEIGHT];
        for (XY bp : bonusPills) {
            Collection<SearchNode> bpInfluences = FloodFillInfluence.getInfluence(gameState, bp);
            for (SearchNode node : bpInfluences) {
                float val = falloff(node.runningCost);
                bonusPillInfluence[node.pos.x][node.pos.y] += val;
            }
        }


        float[][] cellPotentials = new float[Constants.WIDTH][Constants.HEIGHT];
        float maxPotential = 0;
        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                if (numBonusPills > 0) {
                    bonusPillInfluence[x][y] /= numBonusPills;
                }
                XY pos = new XY(x, y);
                float cellPotential = 0;
                if (pills.containsKey(pos)) {
                    cellPotential = pills.get(pos);
                    cellPotential += maxSpaceValue;
                } else if (spaces.containsKey(pos)) {
                    cellPotential = maxSpaceValue - spaces.get(pos);
                }
                cellPotential += bonusPillInfluence[x][y];
                cellPotential /= (float) (maxPillValue + maxSpaceValue);
                //cellPotential = Math.min(1, cellPotential);

                //assert cellPotential >= 0 && cellPotential <= 1.0 : "Potential " + cellPotential + " must be clamped between 0 and 1";

                if (cellPotential > maxPotential) {
                    maxPotential = cellPotential;
                }
                cellPotentials[x][y] = cellPotential;
            }
        }

        for (int x = 0; x < Constants.WIDTH; x++) {
            for (int y = 0; y < Constants.HEIGHT; y++) {
                cellPotentials[x][y] /= maxPotential;
            }
        }

        return cellPotentials;
    }

    private static float falloff(float dist) {
        //return 1 / (float) Math.pow(dist == 0 ? 1f : dist, 0.5);
        //return Math.min(1, 1 - dist / (Constants.MAX_MAZE_DIST));
        return Math.max(0, 1 - dist / (float) 6);
    }

    // Run cellular automata to find pills that have neighbors on all sides
    public static Map<XY, Integer> getClusteredPills(GameState gameState) {
        Set<XY> allCells =  Util.getAllPills(gameState);
        Set<XY> cells = Util.clone(allCells);
        Set<XY> clusteredCells = new HashSet<>();

        List<XY> seen = new ArrayList<>();
        while (cells.size() > 0){
            clusteredCells.clear();
            cells:
            for (XY cell : cells) {
                for (SearchNode searchNode : Search.getAvailableNeighbors(gameState, new SearchNode(cell))) {
                    if (!cells.contains(searchNode.pos) || !Util.isPillAt(gameState, cell)) {
                        continue cells;
                    }
                }
                clusteredCells.add(cell);
                seen.add(cell);
            }
            cells = Util.clone(clusteredCells);
        }

        Map<XY, Integer> clusters = new HashMap<>();
        for (XY pos : allCells) {
            clusters.put(pos, Collections.frequency(seen, pos));
        }

        return clusters;
    }

    public static Map<XY, Integer> getClusteredSpaces(GameState gameState) {
        Set<XY> allCells =  Util.getAllNonPillSpaces(gameState);
        Set<XY> cells = Util.clone(allCells);
        Set<XY> clusteredCells = new HashSet<>();

        List<XY> seen = new ArrayList<>();
        while (cells.size() > 0){
            clusteredCells.clear();
            cells:
            for (XY cell : cells) {
                if (gameState.isPoisonPill(cell)) {
                    continue cells;
                }
                for (SearchNode searchNode : Search.getAvailableNeighbors(gameState, new SearchNode(cell))) {
                    char c = gameState.getCell(cell);
                    if (!cells.contains(searchNode.pos) || (c != Constants.SPACE && c != Constants.PLAYER_A && c != Constants.PLAYER_B)) {
                        continue cells;
                    }
                }
                clusteredCells.add(cell);
                seen.add(cell);
            }
            cells = Util.clone(clusteredCells);
        }

        Map<XY, Integer> clusters = new HashMap<>();
        for (XY pos : allCells) {
            clusters.put(pos, Collections.frequency(seen, pos));
        }

        return clusters;
    }

    private static PillCluster.TestNeighbor testNeighbor = new TestNeighbor();
    private static class TestNeighbor extends Search.TestNeighbor {
        @Override
        public void testNeighbor(GameState gameState, SearchNode node, XY moveTo, Collection<SearchNode> neighbors) {
            char c = gameState.getCell(moveTo);
            if (c != Constants.PILL && c != Constants.BONUS_PILL) {
                return;
            }
            super.testNeighbor(gameState, node, moveTo, neighbors);
        }
    }

    public static List<Collection<SearchNode>> getClusters(GameState gameState) {
        Set<XY> allPills = Util.getAllPills(gameState);

        List<Collection<SearchNode>> clusters = new ArrayList<>();
        while (!allPills.isEmpty()) {
            XY start = allPills.iterator().next();
            Queue<SearchNode> open = new PriorityQueue<>();
            SearchNode initialNode = new SearchNode(start);
            open.add(initialNode);
            Collection<SearchNode> closed = new HashSet<>();
            while (!open.isEmpty()) {
                SearchNode currentNode = open.poll();
                for (SearchNode node : Search.getAvailableNeighbors(gameState, currentNode, testNeighbor)) {
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
                allPills.remove(currentNode.pos);
            }
            clusters.add(closed);
        }
        Collections.sort(clusters, new Comparator<Collection<SearchNode>>() {
            @Override
            public int compare(Collection<SearchNode> o1, Collection<SearchNode> o2) {
                return o1.size() > o2.size() ? -1 : o1.size() < o2.size() ? 1 : 0;
            }
        });
        return clusters;
    }

    public static int[][] getClusterSize(GameState gameState) {
        int[][] result = new int[Constants.WIDTH][Constants.HEIGHT];
        List<Collection<SearchNode>> clusters = getClusters(gameState);
        for (Collection<SearchNode> cluster : clusters) {
            for (SearchNode node : cluster) {
                result[node.pos.x][node.pos.y] = cluster.size();
            }
        }
        return result;
    }
}

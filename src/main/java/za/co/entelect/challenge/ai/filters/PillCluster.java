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

    public static Map<XY, Float> getCellPotential(GameState gameState) {
        Map<XY, Integer> pills = getClusteredPills(gameState);
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
                float val = 1 / (float) Math.sqrt(node.runningCost == 0 ? 1f : node.runningCost);
                bonusPillInfluence[node.pos.x][node.pos.y] += val;
            }
        }

        Map<XY, Float> potential = new HashMap<>();
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
                cellPotential /= (float) (maxPillValue + maxSpaceValue);
                //cellPotential += bonusPillInfluence[x][y];

                assert cellPotential >= 0 && cellPotential <= 1.0 : "Potential " + cellPotential + " must be clamped between 0 and 1";

                potential.put(pos, cellPotential);
            }
        }

        return potential;
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
                for (SearchNode searchNode : Search.getAvailableNeighbors(gameState, new SearchNode(cell))) {
                    char c = gameState.getCell(cell);
                    if (!cells.contains(searchNode.pos) || c != Constants.SPACE) {
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
}

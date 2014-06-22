package za.co.entelect.challenge.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.mcts.UCTGameState;

import java.util.*;

public class MST {

    private static final Logger logger = LoggerFactory.getLogger(MST.class);

    private int weight;
    private Map<Integer, XY> vertices;
    private List<Edge> edges;

    public MST(int weight, Map<Integer, XY> vertices, List<Edge> edges) {
        this.weight = weight;
        this.vertices = vertices;
        this.edges = edges;
    }

    public int getWeight() {
        return weight;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public static MST fromGameState(GameState gameState, XY startingPos) {
        Set<XY> nodes = new HashSet<>();
        nodes.add(startingPos);
        nodes.addAll(gameState.getPills());
        nodes.addAll(gameState.getBonusPills());
        return fromPoints(new ArrayList<>(nodes));
    }

    public static MST fromGameState(UCTGameState gameState, XY startingPos) {
        Set<XY> nodes = new HashSet<>();
        nodes.add(startingPos);

        byte[] board = gameState.getBoard();
        for (int i = 0; i < Constants.WIDTH; i++) {
            for (int j = 0; j < Constants.HEIGHT; j++) {
                byte c = board[i * Constants.HEIGHT + j];
                if (c == UCTGameState.P || c == UCTGameState.BP) {
                    nodes.add(new XY(i, j));
                }
            }
        }
        return fromPoints(new ArrayList<>(nodes));
    }

    // TODO: expensive algo, try binary heap for storage
    public static MST fromPoints(List<XY> nodes) {
        int numNodes = nodes.size();
        int weights[][] = new int[numNodes][numNodes];
        int visited[] = new int[numNodes];

        Map<Integer, XY> vertices = new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            XY ni = nodes.get(i);
            vertices.put(i, ni);
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) {
                    continue;
                }

                XY ji = nodes.get(j);

                int w = Util.mazeDistance(ni, ji);
                weights[i][j] = weights[j][i] = w;
            }
        }

        int start = 0;
        int current = start;
        int total = 1;
        visited[current] = 1;
        int[] minWeights = new int[numNodes];
        Arrays.fill(minWeights, Integer.MAX_VALUE);
        minWeights[current] = 0;

        List<Edge> edges = new ArrayList<>();
        while (total < numNodes) {
            for (int i = 0; i < numNodes; i++) {
                if (i == current) {
                    continue;
                }

                if (visited[i] == 0) {
                    if (minWeights[i] > weights[current][i]) {
                        minWeights[i] = weights[current][i];
                    }
                }
            }

            Edge edge = new Edge();
            edges.add(edge);
            edge.v = vertices.get(current);
            int minWeight = Integer.MAX_VALUE;
            for (int i = 0; i < numNodes; i++) {
                if (visited[i] == 0) {
                    if (minWeights[i] < minWeight) {
                        minWeight = minWeights[i];
                        current = i;
                        edge.w = vertices.get(i);
                        edge.weight = minWeight;
                    }
                }
            }

            visited[current] = 1;
            total++;
        }

        int weight = 0;
        for (int i = 0; i < numNodes; i++) {
            weight = weight + minWeights[i];
        }

        return new MST(weight, vertices, edges);
    }

    public static class Edge implements Comparable<Edge> {
        public int weight;
        public XY v;
        public XY w;

        public Edge() {
        }

        public Edge(XY v, XY w, int weight) {
            this.v = v;
            this.w = w;
            this.weight = weight;
        }

        public XY getEither() {
            return v;
        }

        public XY getOther(XY x) {
            if (x.equals(v)) {
                return w;
            }
            return w;
        }

        @Override
        public int compareTo(Edge o) {
            return weight > o.weight ? +1 : weight < o.weight ? -1 : 0;
        }
    }
}

package za.co.entelect.challenge.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.search.InfluenceMap;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;

import java.util.*;

public class GameStateGraph implements Cloneable {

    public static final Logger logger = LoggerFactory.getLogger(GameStateGraph.class);

    private List<Edge> edges;
    private Set<Vertex> cells;
    private Set<Vertex> intersects;

    public GameStateGraph() {
        edges = new ArrayList<>();
        cells = new HashSet<>();
        intersects = new HashSet<>();
    }

    public void addCell(Vertex cell) {
        cells.add(cell);
    }

    public void addIntersect(Vertex cell) {
        intersects.add(cell);
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public Set<Vertex> getCells() {
        return cells;
    }

    public Set<Vertex> getIntersects() {
        return intersects;
    }

    public Vertex getCell(XY pos) {
        for (Vertex v : cells) {
            if (v.x == pos.x && v.y == pos.y) {
                return v;
            }
        }
        return null;
    }

    public static GameStateGraph fromGameState(GameState gameState) {
        GameStateGraph graph = new GameStateGraph();

        Set<XY> visitedNodes = new HashSet<>();
        Vertex startCell = new Vertex(1, 4);
        graph.addCell(startCell);
        build(gameState, graph, startCell, null, visitedNodes);

        return graph;
    }

    private static void build(GameState gameState, GameStateGraph graph, Vertex v, Edge e, Set<XY> visited) {
        visited.add(v);
        Collection<SearchNode> neighbors = Search.getAvailableNeighbors(gameState, new SearchNode(v));
        if (neighbors.size() > 2) {
            graph.addIntersect(v);

            if (e != null) {
                e.setEnd(v);
            }

            for (SearchNode neighbor : neighbors) {
                if (!visited.contains(neighbor.pos)) {
                    Edge ne = new Edge(v);
                    graph.addEdge(ne);
                    Vertex nv = new Vertex(neighbor.pos.x, neighbor.pos.y);
                    graph.addCell(nv);
                    ne.addCell(nv);
                    build(gameState, graph, nv, ne, visited);
                }
            }
        } else {
            for (SearchNode neighbor : neighbors) {
                if (!visited.contains(neighbor.pos)) {
                    Vertex nv = new Vertex(neighbor.pos.x, neighbor.pos.y);
                    e.addCell(nv);
                    graph.addCell(nv);
                    build(gameState, graph, nv, e, visited);
                }
            }
        }
    }

    public static class Vertex extends XY {

        private Set<Edge> edges = new HashSet<>();

        public Vertex(int x, int y) {
            super(x, y);
        }

        public void addEdge(Edge e) {
            edges.add(e);
        }

        public Set<Edge> getEdges() {
            return edges;
        }
    }

    public static class Edge {
        private Vertex start;
        private Vertex end;
        private Set<Vertex> cells = new HashSet<>();

        private Edge(Vertex start) {
            this.start = start;
            addCell(start);
        }

        public void addCell(Vertex v) {
            cells.add(v);
            v.addEdge(this);
        }

        public Set<Vertex> getCells() {
            return cells;
        }

        public Vertex getStart() {
            return start;
        }

        public void setStart(Vertex start) {
            this.start = start;
        }

        public Vertex getEnd() {
            return end;
        }

        public void setEnd(Vertex end) {
            this.end = end;
        }
    }
}

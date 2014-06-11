package za.co.entelect.challenge.ai.gametree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.ai.search.Search;
import za.co.entelect.challenge.ai.search.SearchNode;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class GameTreeUctOld extends GameTree {

    private static final Logger logger = LoggerFactory.getLogger(GameTreeUctOld.class);

    public static GameNode uct(GameState gameState, int maxDepth) {
        return uct(new GameNode(gameState), maxDepth);
    }

    public static GameNode uct(GameNode gameNode, int maxDepth) {
        for (int i = 0; i < maxDepth; i++){
            gameNode.selectAction();
        }
        return bestMove(gameNode);
    }

    public static GameNode bestMove(GameNode gameNode) {
        GameNode bestMove = null;
        for (GameNode child : gameNode.children) {
            if (bestMove == null) {
                bestMove = child;
            } else {
                if (bestMove.totValue < child.totValue) {
                    bestMove = child;
                }
            }
        }

        return bestMove;
    }

    public static class GameNode {
        static Random r = new Random();
        static double epsilon = 1e-6;

        public XY move;
        public List<GameNode> children;
        public double nVisits, totValue;
        String id;
        public int scoreAdjust;

        private GameState gameState;

        private static AtomicInteger ID_GEN = new AtomicInteger();

        public GameNode(GameState gameState) {
            this(gameState, null, 0);
        }

        public GameNode(GameState gameState, XY move, int scoreAdjust) {
            this.gameState = gameState;
            this.move = move;
            this.scoreAdjust = scoreAdjust;
            id = String.valueOf(ID_GEN.incrementAndGet());
        }

        public void selectAction() {
            List<GameNode> visited = new LinkedList<>();
            GameNode cur = this;
            visited.add(this);
            while (!cur.isLeaf()) {
                cur = cur.select();
                // System.out.println("Adding: " + cur);
                visited.add(cur);
            }
            cur.expand();
            GameNode newNode = cur.select();
            visited.add(newNode);
            double value = rollOut(newNode);
            for (GameNode node : visited) {
                // would need extra logic for n-player game
                // System.out.println(node);
                node.updateStats(value);
            }
        }

        public void expand() {
            children = new ArrayList<>();
            XY pos = gameState.getCurrentPosition();
            for (SearchNode neighbor : Search.getAvailableNeighbors(gameState, new SearchNode(pos))) {
                GameState gameStateAfterMove = gameState.clone();
                int scoreAdjust = gameStateAfterMove.makeMove(pos, neighbor.pos);
                children.add(new GameNode(gameStateAfterMove, neighbor.pos, scoreAdjust));
            }
        }

        private GameNode select() {
            GameNode selected = null;
            double bestValue = Double.MIN_VALUE;
            for (GameNode c : children) {
                double uctValue =
                        c.totValue / (c.nVisits + epsilon) +
                                Math.sqrt(Math.log(nVisits+1) / (c.nVisits + epsilon)) +
                                r.nextDouble() * epsilon;
                // small random number to break ties randomly in unexpanded nodes
                // System.out.println("UCT value = " + uctValue);
                if (uctValue > bestValue) {
                    selected = c;
                    bestValue = uctValue;
                }
            }
            // System.out.println("Returning: " + selected);
            return selected;
        }

        public boolean isLeaf() {
            return children == null;
        }

        public double rollOut(GameNode tn) {
            // ultimately a roll out will end in some value
            // assume for now that it ends in a win or a loss
            // and just return this at random
            return 0;//r.nextInt(2);
            //if (tn.gameState.getCurrentPlayer() == Constants.PLAYER_A) {
//                return tn.gameState.getPlayerAScore();
//            } else {
//                return -tn.gameState.getPlayerBScore();
//            }
            //return tn.gameState.getPlayerAScore();

            //return tn.gameState.getCurrentPlayer() == Constants.PLAYER_B ? tn.scoreAdjust : -tn.scoreAdjust;



//            GameState g = tn.gameState;
//            if (g.isGameOver()) {
//                if (g.getPlayerAScore() > g.getPlayerBScore()) {
//                    // You win, big bonus
//                    return 10000;
//                } else if (g.getPlayerAScore() < g.getPlayerBScore()) {
//                    // You lost, big booobooo
//                    return -10000;
//                } else {
//                    return 0;
//                }
//            }
//
//            if (tn.scoreAdjust > 0) {
//                if (g.getCurrentPlayer() == Constants.PLAYER_B) {
//                    return tn.scoreAdjust;
//                }   else {
//                    return -tn.scoreAdjust;
//                }
//            }
//
//            if (g.getCurrentPlayer() == Constants.PLAYER_B) {
//                return g.getPlayerAScore();
//            }   else {
//                return -g.getPlayerBScore();
//            }
        }

        public void updateStats(double value) {
            nVisits++;
            totValue += value;
        }

        public int arity() {
            return children == null ? 0 : children.size();
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("{");
            sb.append("nVisits=").append(nVisits);
            sb.append(", totValue=").append(totValue);
            sb.append(", move=").append(move);
            sb.append(", id='").append(id).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String toDot() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("digraph UCT {\n");
            toDot(this, buffer);
            buffer.append("}");
            return buffer.toString();
        }

        private String dotLabel(GameNode node) {
            return node.move.x + "," + node.move.y + " " + (int) node.totValue + "/" + (int) node.nVisits;
        }

        private String dotColor(GameNode node) {
            return node.gameState.getCurrentPlayer() == Constants.PLAYER_A ? "red" : "blue";
        }

        private void toDot(GameNode parent, StringBuilder buffer) {
            if (parent.arity() == 0) {
                return;
            }

            String parentId = parent.id;
            for (GameNode child : parent.children) {
                String childId = child.id;
                buffer.append("  ")
                        .append(parentId)
                        .append(" -> ")
                        .append(childId)
                        .append("\n")
                        .append("  ")
                        .append(parentId)
                        .append(" [color="+ dotColor(parent) + ",label=\""  + dotLabel(parent) + "\"]\n")
                        .append("  ")
                        .append(childId)
                        .append(" [color="+ dotColor(parent) + ",label=\"" + dotLabel(child) + "\"]\n");
                toDot(child, buffer);
            }
        }
    }
}

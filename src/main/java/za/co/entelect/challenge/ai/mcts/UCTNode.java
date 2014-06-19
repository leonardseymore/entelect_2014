package za.co.entelect.challenge.ai.mcts;

import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.domain.XY;

import java.util.ArrayList;
import java.util.List;
import static za.co.entelect.challenge.Util.R;

public class UCTNode {

    float heuristic;
    XY move;
    UCTNode parentNode;
    List<UCTNode> children;
    List<XY> untriedMoves;
    byte currentPlayer;
    float score;
    short visits;
    float mean;
    float sumOfSquares;

    public UCTNode(UCTGameState state, float heuristic) {
        this(null, null, state, heuristic);
    }

    public UCTNode(XY move, UCTNode parent, UCTGameState state, float heuristic) {
        this.move = move;
        this.parentNode = parent;
        this.children = new ArrayList<>();
        this.score = 0;
        this.visits = 1;
        this.untriedMoves = state.getMoves();
        this.currentPlayer = state.currentPlayer;
        this.heuristic = heuristic;
    }

    public UCTNode uctSelectChild(int[] numPlayouts, int[] wonPlayouts) {
        UCTNode selected = null;
        double bestValue = Double.NEGATIVE_INFINITY;
        for (UCTNode c : children) {
            XY move = c.move;
            double uctValue = ucb(this, c, numPlayouts[move.x * Constants.HEIGHT + move.y], wonPlayouts[move.x * Constants.HEIGHT + move.y]);

            if (uctValue > bestValue) {
                selected = c;
                bestValue = uctValue;
            }
        }
        return selected;
    }

    //Rapid Action Value Estimation
    public double ucb(UCTNode parent, UCTNode child, int numPlayouts, int wonPlayouts) {
        double beta = numPlayouts / (child.visits + numPlayouts + 4 * Math.pow(heuristic, 2) * child.visits * numPlayouts);
        return (1 - beta) * (wonPlayouts / child.visits)
                + beta * (wonPlayouts / numPlayouts)
                + Math.sqrt(2 * Math.log(parent.visits) / child.visits);
    }


    public boolean hasUntriedMoves() {
        return untriedMoves.size() > 0;
    }

    public XY getRandomUntriedMove() {
        return untriedMoves.get(R.nextInt(untriedMoves.size()));
    }

    public boolean hasChildren() {
        return children.size() > 0;
    }

    public UCTNode addChild(XY move, UCTGameState state) {
        UCTNode n = new UCTNode(move, this, state, heuristic);
        untriedMoves.remove(move);
        children.add(n);
        return n;
    }

    public void update(float value) {
        visits += 1;
        score += value;

        if (visits == 1) {
            mean = score;
            sumOfSquares = 0;
        } else {
            float lastMean = mean;
            mean += (score - lastMean) / visits;
            sumOfSquares += (score - lastMean) * (score - mean);
        }
    }

    public float getVariance() {
        if (visits > 1) {
            return sumOfSquares / (visits - 1);
        } else {
            return 0;
        }
    }

    public String toString() {
        return "[M:" + move + " W/V:" + score + "/" + visits + " U:" + untriedMoves + "]";
    }

    public String treeToString(int indent) {
        String s = indentString(indent) + this;
        for (UCTNode c : children) {
            s += c.treeToString(indent + 1);
        }
        return s;
    }

    public String indentString(int indent) {
        String s = "\n";
            for (int i = 0; i < indent; i++) {
                s += "| ";
            }
            return s;
    }

    public String childrenToString() {
        String s = "";
        for (UCTNode c : children) {
            s += c + "\n";
        }
        return s;
    }
}
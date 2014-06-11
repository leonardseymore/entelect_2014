package za.co.entelect.challenge.ai.mcts;

import java.util.Comparator;

public class UCTNodeComparatorVisits implements Comparator<UCTNode> {
    public int compare(UCTNode o1, UCTNode o2) {
        return o1.visits > o2.visits ? -1 : o1.visits < o2.visits ? 1 : 0;
    }
}
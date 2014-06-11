package za.co.entelect.challenge.ai.gametree;

import za.co.entelect.challenge.domain.XY;

class TranspositionEntry {
    long hash;
    int depth;
    int minScore;
    int maxScore;
    XY bestMove;
}

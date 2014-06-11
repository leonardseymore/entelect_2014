package za.co.entelect.challenge.ai.gametree;

import za.co.entelect.challenge.domain.XY;

/**
* Created by leonardseymore on 2014/05/01.
*/
class GameTreeResult {
    XY move;
    int score;

    GameTreeResult(int score, XY move) {
        this.score = score;
        this.move = move;
    }
}

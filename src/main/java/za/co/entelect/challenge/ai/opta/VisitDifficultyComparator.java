package za.co.entelect.challenge.ai.opta;

import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.XY;

import java.util.Comparator;

public class VisitDifficultyComparator implements Comparator<Visit> {
    @Override
    public int compare(Visit a, Visit b) {
        return Util.mazeDistance(a.getCell(), b.getCell());
    }
}

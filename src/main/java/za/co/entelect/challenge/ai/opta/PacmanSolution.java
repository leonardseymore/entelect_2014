package za.co.entelect.challenge.ai.opta;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.value.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.solution.Solution;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@PlanningSolution
public class PacmanSolution implements Solution<SimpleScore> {

    private SimpleScore score;

    private List<Domicile> domicileList;
    private List<XY> pills;
    private List<Visit> visitList;
    private GameState gameState;

    public List<XY> getPills() {
        return pills;
    }

    public void setPills(List<XY> pills) {
        this.pills = pills;
    }

    @ValueRangeProvider(id = "domicileRange")
    public List<Domicile> getDomicileList() {
        return domicileList;
    }

    public void setDomicileList(List<Domicile> domicileList) {
        this.domicileList = domicileList;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public SimpleScore getScore() {
        return score;
    }

    @Override
    public void setScore(SimpleScore score) {
        this.score = score;
    }

    @Override
    public Collection<?> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(pills);
        facts.addAll(domicileList);
        return facts;
    }

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visitRange")
    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PacmanSolution that = (PacmanSolution) o;

        if (!visitList.equals(that.visitList)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return visitList.hashCode();
    }
}
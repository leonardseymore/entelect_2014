package za.co.entelect.challenge.ai.opta;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.XY;

@PlanningEntity(difficultyComparatorClass = VisitDifficultyComparator.class)
public class Visit implements Standstill {

    private XY cell;
    private Standstill previousStandstill;

    public Visit() {
    }

    public Visit(XY cell) {
        this.cell = cell;
    }

    public XY getCell() {
        return cell;
    }

    public void setCell(XY cell) {
        this.cell = cell;
    }

    @PlanningVariable(chained = true, valueRangeProviderRefs = {"domicileRange", "visitRange"})
    public Standstill getPreviousStandstill() {
        return previousStandstill;
    }

    public void setPreviousStandstill(Standstill previousStandstill) {
        this.previousStandstill = previousStandstill;
    }

    public int getDistanceToPreviousStandstill() {
        if (previousStandstill == null) {
            return 0;
        }
        return getDistanceTo(previousStandstill);
    }

    public int getDistanceTo(Standstill standstill) {
        return Util.mazeDistance(cell, standstill.getCell());
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionHashCode()
     */
    public boolean solutionEquals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Visit) {
            Visit other = (Visit) o;
            return new EqualsBuilder()
                    .append(cell, other.cell)
                    .append(previousStandstill, other.previousStandstill) // TODO performance leak: not needed?
                    .isEquals();
        } else {
            return false;
        }
    }

    /**
     * The normal methods {@link #equals(Object)} and {@link #hashCode()} cannot be used because the rule engine already
     * requires them (for performance in their original state).
     * @see #solutionEquals(Object)
     */
    public int solutionHashCode() {
        return new HashCodeBuilder()
                .append(cell) // TODO performance leak: not needed?
                .append(previousStandstill) // TODO performance leak: not needed?
                .toHashCode();
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Visit visit = (Visit) o;
//
//        if (!cell.equals(visit.cell)) return false;
//        if (previousCell != null ? !previousCell.equals(visit.previousCell) : visit.previousCell != null) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = cell.hashCode();
//        result = 31 * result + (previousCell != null ? previousCell.hashCode() : 0);
//        return result;
//    }

    @Override
    public String toString() {
        return "Visit{" +
                "cell=" + cell +
                ", previousCell=" + previousStandstill +
                '}';
    }
}

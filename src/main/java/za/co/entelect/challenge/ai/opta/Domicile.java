package za.co.entelect.challenge.ai.opta;

import za.co.entelect.challenge.domain.XY;

public class Domicile implements Standstill {

    private XY cell;

    public Domicile(XY cell) {
        this.cell = cell;
    }

    public XY getCell() {
        return cell;
    }

    public void setCell(XY cell) {
        this.cell = cell;
    }


}

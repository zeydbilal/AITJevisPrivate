package org.jevis.jeconfig.tool;

import javax.measure.unit.Unit;

public class UnitData {

    private final String _altSymbol;
    private final Unit _unit;

    public UnitData(Unit unit, String altSymbol) {
        _altSymbol = altSymbol;
        _unit = unit;
    }

    public String getAlternativSymbol() {
        return _altSymbol;
    }

    public Unit getUnit() {
        return _unit;
    }

}

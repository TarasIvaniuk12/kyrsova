package com.company.applianceManager.Conditional;

import com.company.applianceManager.Appliances.Appliance;

public class IsNeedsRepair implements Condition {

    @Override
    public boolean condition(Appliance appliance) {
        return appliance.isNeedsRepair();
    }

    @Override
    public String toSql() {
        return "power <= (basicPower * " + Appliance.REPAIR_PERCENT + " / 100)";
    }
}

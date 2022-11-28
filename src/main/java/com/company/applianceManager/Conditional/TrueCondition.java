package com.company.applianceManager.Conditional;

import com.company.applianceManager.Appliances.Appliance;

public class TrueCondition implements Condition {
    @Override
    public boolean condition(Appliance appliance) {
        return true;
    }

    @Override
    public String toSql() {
        return "applianceID is not null";
    }
}

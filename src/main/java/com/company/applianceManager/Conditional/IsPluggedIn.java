package com.company.applianceManager.Conditional;

import com.company.applianceManager.Appliances.Appliance;

public class IsPluggedIn implements Condition {
    @Override
    public boolean condition(Appliance appliance) {
        return appliance.isPluggedIn();
    }

    @Override
    public String toSql() {
        return "isPlugged = 1";
    }
}

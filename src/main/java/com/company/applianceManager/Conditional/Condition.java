package com.company.applianceManager.Conditional;

import com.company.applianceManager.Appliances.Appliance;

public interface Condition {

    boolean condition(Appliance appliance);
    String toSql();
}

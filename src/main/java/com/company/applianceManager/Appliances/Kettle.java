package com.company.applianceManager.Appliances;

public class Kettle extends Appliance {

    public Kettle() {
        this(0, 2000, 2000, false);
    }

    public Kettle(int id, int basicPower, int power, boolean isPlugged) {
        super(id, basicPower, power, isPlugged);
        type = "Чайник";
    }

    @Override
    public String repair() {
        power = basicPower;
        isNeedsToRepair = false;
        return "Чайник №" + id + " відремонтовано";
    }

    @Override
    public String plugIn() {
        isPlugged = true;
        power -= 10;
        setIsNeedsToRepair();
        return "Чайник №" + id + " почав кип'ятити воду";
    }

    @Override
    public String unplug() {
        isPlugged = false;
        return "Чайник №" + id + " закипів";
    }

}

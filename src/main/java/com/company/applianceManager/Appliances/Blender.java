package com.company.applianceManager.Appliances;

public class Blender extends Appliance{

    public Blender() {
        this(0, 700, 700, false);
    }

    public Blender(int id, int basicPower, int power, boolean isPlugged) {
        super(id, basicPower, power, isPlugged);
        type = "Блендер";
    }

    @Override
    public String repair() {
        power = basicPower;
        isNeedsToRepair = false;
        return "Блендер №" + id + " відремонтовано";
    }

    @Override
    public String plugIn() {
        isPlugged = true;
        power -= 10;
        setIsNeedsToRepair();
        return "Блендер №" + id + " почав працювати";
    }

    @Override
    public String unplug() {
        isPlugged = false;
        return "Блендер №" + id + " вимкнули";
    }

}

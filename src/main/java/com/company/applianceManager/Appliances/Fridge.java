package com.company.applianceManager.Appliances;

public class Fridge extends Appliance {

    public Fridge() {
        this(0, 300, 300, false);
    }

    public Fridge(int id, int basicPower, int power, boolean isPlugged) {
        super(id, basicPower, power, isPlugged);
        type = "Холодильник";
    }

    @Override
    public String repair() {
        power = basicPower;
        isNeedsToRepair = false;
        return "Холодильник №" + id + " відремонтовано";
    }

    @Override
    public String plugIn() {
        isPlugged = true;
        power -= 10;
        setIsNeedsToRepair();
        return "Холодильник №" + id + " почав охолоджувати продукти";
    }

    @Override
    public String unplug() {
        isPlugged = false;
        return "Холодильник №" + id + " вимкнули";
    }

}

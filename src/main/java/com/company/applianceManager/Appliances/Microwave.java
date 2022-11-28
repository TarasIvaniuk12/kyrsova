package com.company.applianceManager.Appliances;

public class Microwave extends Appliance {
    public Microwave() {
        this(0, 1200, 1200, false);
    }

    public Microwave(int id, int basicPower, int power, boolean isPlugged) {
        super(id, basicPower, power, isPlugged);
        type = "Мікрохвильовка";
    }

    @Override
    public String repair() {
        power = basicPower;
        isNeedsToRepair = false;
        return "Мікрохвильову піч №" + id + " відремонтовано";
    }

    @Override
    public String plugIn() {
        isPlugged = true;
        power -= 10;
        setIsNeedsToRepair();
        return "Мікрохвильова піч №" + id + " почала працювати";
    }

    @Override
    public String unplug() {
        isPlugged = false;
        return "Мікрохвильова піч №" + id + " завершила нагрівати";
    }

}

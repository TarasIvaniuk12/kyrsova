package com.company.applianceManager.Appliances;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public abstract class Appliance {
    public static final int REPAIR_PERCENT = 70;

    protected String type;
    protected int id;
    protected int basicPower;
    protected int power;
    protected boolean isPlugged;
    protected boolean isNeedsToRepair;

    public Appliance() {
        this(0, 0, 0, false);
    }

    public Appliance(int id, int basicPower, int power, boolean isPlugged) {
        this.type = "Електроприлад";
        this.id = id;
        this.basicPower = basicPower;
        this.power = power;
        this.isPlugged = isPlugged;
        setIsNeedsToRepair();
    }

    public int getId() {
        return id;
    }

    public boolean getIsPlugged() {
        return isPlugged;
    }

    public boolean getIsNeedsToRepair() {
        return isNeedsToRepair;
    }

    protected void setIsNeedsToRepair() {
        isNeedsToRepair = (REPAIR_PERCENT * basicPower / 100 > power);
    }

    public  String getType() {
        return type;
    }
    public int getPower() {
        return power;
    }

    public boolean isNeedsRepair() {
        return isNeedsToRepair;
    }

    public boolean isPowerWithinLimits(int min, int max) {
        return (power >= min) && (power <= max);
    }

    public boolean isPluggedIn() {
        return isPlugged;
    }

    abstract public String repair();
    public abstract String plugIn();
    public abstract String unplug();

    @Override
    public String toString() {
        return "Appliance{" +
                "type='" + type + '\'' +
                ", id=" + id +
                ", basicPower=" + basicPower +
                ", power=" + power +
                ", isPlugged=" + isPlugged +
                ", isNeedsToRepair=" + isNeedsToRepair +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appliance appliance = (Appliance) o;
        return id == appliance.id
                && basicPower == appliance.basicPower
                && power == appliance.power
                && isPlugged == appliance.isPlugged
                && isNeedsToRepair == appliance.isNeedsToRepair
                && Objects.equals(type, appliance.type);
    }

}

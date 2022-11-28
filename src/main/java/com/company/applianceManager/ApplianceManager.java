package com.company.applianceManager;

import com.company.applianceManager.Appliances.*;
import com.company.applianceManager.Conditional.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ApplianceManager {
    private static final Logger log = LogManager.getLogger(ApplianceManager.class);

    public static int userID;

    public static String addAppliance(Appliance appliance) {
        log.trace("Виклик функції додавання електроприладу");
        if (appliance == null) return null;
        try (Statement statement = App.connection.createStatement()) {
            String query1 = "insert into appliances (type, basicPower, power) values ('"
                    + appliance.getType() + "', "
                    + appliance.getPower() + ", "
                    + appliance.getPower() + ");";
            int rowcount = statement.executeUpdate(query1);
            if (rowcount == 1) {
                ResultSet resultSet = statement.executeQuery("select LAST_INSERT_ID() as last_id;");
                if (resultSet.next()) {
                    int appID = resultSet.getInt("last_id");
                    String query2 = "insert into houses (userID, applianceID) values (" + userID + ", " + appID + ")";
                    if (statement.executeUpdate(query2) == 1) {
                        return "Електроприлад успішно додано!";
                    }
                }
            }
            log.error("Не вдалося додати користувача");
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    public static String calculateTotalPower() {
        log.trace("Виклик функції підрахунку загальної потужності");
        String res;
        try (Statement statement = App.connection.createStatement()){
            String query = "select sum(power) as sum from appliances where applianceID in (select applianceID from houses where userID = " + userID + ");";
            int totalPower;
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                totalPower = resultSet.getInt("sum");
                res = "Загальна потужність = " + totalPower + " Вт";
            } else {
                log.error("Не вдалося порахувати загальну потужність");
                res = "Не вдалося порахувати загальну потужність!";
            }
            return res;
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    public static ObservableList<Appliance> findAppliances(Condition condition) {
        log.trace("Виклик функції пошуку");
        if (condition == null) return null;
        ObservableList<Appliance> founded = FXCollections.observableArrayList();
        String query = "select * from appliances where " + condition.toSql() + " and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        try (Statement statement = App.connection.createStatement()) {
            ResultSet set = statement.executeQuery(query);
            Appliance appliance;
            String type;
            int id, basicPower, power;
            boolean isPlugged;
            while (set.next()) {
                type = set.getString("type");
                id = set.getInt("applianceID");
                basicPower = set.getInt("basicPower");
                power = set.getInt("power");
                isPlugged = set.getBoolean("isPlugged");
                appliance = createApplianceFromTable(type, id, basicPower, power, isPlugged);
                founded.add(appliance);
            }
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return founded;
    }


    public static String removeAppliance(int applianceID) {
        log.trace("Виклик функції видалення електроприладу");
        String query1 = "delete from houses where userID = " + userID + " and applianceID = " + applianceID + ";";
        String query2 = "delete from appliances where applianceID = " + applianceID + ";";
        try (Statement statement = App.connection.createStatement()){
            String res;
            if (statement.executeUpdate(query1) > 0 && statement.executeUpdate(query2) > 0) {
                res = "Електроприлад успішло видалено!";
            } else {
                res = "Не існує електроприладу з таким ID!";
            }
            return res;
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    public static String repairAppliance(int id) {
        log.trace("Виклик функції ремонтування електроприладу");
        String query1 = "select * from appliances where power <= basicPower * 3 / 4 and applianceID = " + id + " and applianceID in (select applianceID from houses where userID = " + userID + ");";
        try (Statement statement = App.connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query1);
            String res;
            if (resultSet.next()) {
                String type;
                int appID, basicPower, power;
                boolean isPlugged;
                type = resultSet.getString("type");
                appID = resultSet.getInt("applianceID");
                basicPower = resultSet.getInt("basicPower");
                power = resultSet.getInt("power");
                isPlugged = resultSet.getBoolean("isPlugged");
                Appliance appliance = createApplianceFromTable(type, appID, basicPower, power, isPlugged);
                String query2 = "update appliances set power = " + basicPower + " where applianceID = " + id + ";";
                if (statement.executeUpdate(query2) == 1) {
                    res = appliance.repair();
                } else {
                    res = "Немає електроприладу з таким ID для ремонту!";
                }
            } else {
                res = "Немає електроприладу з таким ID для ремонту!";
            }
            return res;
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    public static String plugInAppliance(int id) {
        log.trace("Виклик функції ввімкнення електроприладу");
        String query1 = "select * from appliances where isPlugged = false and applianceID = " + id + " and applianceID in (select applianceID from houses where userID = " + userID + ");";
        String query2 = "update appliances set isPlugged = true, power = power - 10 where applianceID = " + id + ";";
        try (Statement statement = App.connection.createStatement()) {
            String res;
            ResultSet resultSet = statement.executeQuery(query1);
            if (resultSet.next()) {
                String type;
                int appID, basicPower, power;
                boolean isPlugged;
                type = resultSet.getString("type");
                appID = resultSet.getInt("applianceID");
                basicPower = resultSet.getInt("basicPower");
                power = resultSet.getInt("power");
                isPlugged = resultSet.getBoolean("isPlugged");
                Appliance appliance = createApplianceFromTable(type, appID, basicPower, power, isPlugged);
                if (statement.executeUpdate(query2) == 1) {
                    res = appliance.plugIn();
                } else {
                    res = "Немає електроприладу з таким ID для ввімкнення!";
                }
            } else {
                res = "Немає електроприладу з таким ID для ввімкнення!";
            }
            return res;
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return null;
    }

    public static String unplugAppliance(int id) {
        log.trace("Виклик функції ввімкнення електроприладу");
        String res = null;
        String query1 = "select * from appliances where isPlugged = true and applianceID = " + id + " and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        String query2 = "update appliances set isPlugged = false where applianceID = " + id + ";";
        try (Statement statement = App.connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(query1);
            if (resultSet.next()) {
                String type;
                int appID, basicPower, power;
                boolean isPlugged;
                type = resultSet.getString("type");
                appID = resultSet.getInt("applianceID");
                basicPower = resultSet.getInt("basicPower");
                power = resultSet.getInt("power");
                isPlugged = resultSet.getBoolean("isPlugged");
                Appliance appliance = createApplianceFromTable(type, appID, basicPower, power, isPlugged);
                if (statement.executeUpdate(query2) == 1) {
                    res = appliance.unplug();
                } else {
                    res = "Немає електроприладу з таким ID для вимкнення!";
                }
            } else {
                res = "Немає електроприладу з таким ID для вимкнення!";
            }
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
        }
        return res;
    }

    public static Appliance createApplianceFromTable(String type, int id, int basicPower, int power, boolean isPlugged) {
        switch (type) {
            case "Блендер":
                return new Blender(id, basicPower, power, isPlugged);
            case "Холодильник":
                return new Fridge(id, basicPower, power, isPlugged);
            case "Чайник":
                return new Kettle(id, basicPower, power, isPlugged);
            case "Мікрохвильовка":
                return new Microwave(id, basicPower, power, isPlugged);
            default:
                throw new IllegalArgumentException("Невадомий тип електроприладу!");
        }
    }
}


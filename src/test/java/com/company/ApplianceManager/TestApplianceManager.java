package com.company.ApplianceManager;

import com.company.applianceManager.App;
import com.company.applianceManager.ApplianceManager;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import com.company.applianceManager.Appliances.*;
import com.company.applianceManager.Conditional.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestApplianceManager {
    private static final String url = "jdbc:mysql://localhost:3306/pp_lab_4_8";
    private static final String username = "root";
    private static final String password = "admin";

    private static Connection connection;
    private static Statement statement;
    private static int userID;

    @BeforeClass
    public static void set() throws SQLException {
        connection = DriverManager.getConnection(url, username, password);
        App.connection = connection;
        statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("select userID from users where userName = 'junit';");
        if (resultSet.next()) {
            userID = resultSet.getInt("userID");
        }
        else {
            if (statement.executeUpdate("insert into users (userName, password) values ('junit', 'junit');") == 1) {
                resultSet = statement.executeQuery("select LAST_INSERT_ID() as last_id;");
                if (resultSet.next()) {
                    userID = resultSet.getInt("last_id");
                } else {
                    throw new UnknownError();
                }
            } else {
                throw new UnknownError();
            }
        }
        clean();
        ApplianceManager.userID = userID;
    }

    @AfterClass
    public static void reset() throws SQLException {
        clean();
        statement.close();
        connection.close();
    }

    @Test
    public void addFridgeTest() {
        assertEquals(ApplianceManager.addAppliance(new Fridge()), "Електроприлад успішно додано!");
    }
    @Test
    public void addKettleTest() {
        assertEquals(ApplianceManager.addAppliance(new Kettle()), "Електроприлад успішно додано!");
    }
    @Test
    public void addBlenderTest() {
        assertEquals(ApplianceManager.addAppliance(new Blender()), "Електроприлад успішно додано!");
    }
    @Test
    public void addMicrowaveTest() {
        assertEquals(ApplianceManager.addAppliance(new Microwave()), "Електроприлад успішно додано!");
    }

    @Test
    public void addNullTest() {
        assertNull(ApplianceManager.addAppliance(null));
    }

    @Test
    public void calculateTotalPowerTest() throws SQLException {
        String query = "select sum(power) as sum from appliances where applianceID in (select applianceID from houses where userID = " + userID + ");";
        ResultSet resultSet = statement.executeQuery(query);
        resultSet.next();
        int expectedTotalPower = resultSet.getInt("sum");
        String expected = "Загальна потужність = " + expectedTotalPower + " Вт";
        String actual = ApplianceManager.calculateTotalPower();
        assertEquals(expected, actual);
    }

    @Test
    public void findAllTest() throws SQLException {
        Condition condition = new TrueCondition();
        String query = "select * from appliances where applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void findNullConditionTest() {
        List<Appliance> actual = ApplianceManager.findAppliances(null);
        assertNull(actual);
    }

    @Test
    public void plugTest() throws SQLException {
        String query1 = "select applianceID from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        ApplianceManager.plugInAppliance(appId);
        resultSet = statement.executeQuery("select isPlugged from appliances where applianceID = " + appId + ";");
        resultSet.next();
        boolean isPlugged = resultSet.getBoolean("isPlugged");
        assertTrue(isPlugged);
    }

    @Test
    public void plugAnotherApplianceTest() throws SQLException {
        String query1 = "select applianceID from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        ApplianceManager.plugInAppliance(appId);
        resultSet = statement.executeQuery("select isPlugged from appliances where applianceID = " + appId + ";");
        resultSet.next();
        boolean isPlugged = resultSet.getBoolean("isPlugged");
        assertTrue(isPlugged);
    }

    @Test
    public void plugInvalidIDTest() throws SQLException {
        String query1 = "select applianceID from appliances where isPlugged = true and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        String actual = ApplianceManager.plugInAppliance(appId);
        String expected = "Немає електроприладу з таким ID для ввімкнення!";
        assertEquals(expected, actual);
    }

    @Test
    public void findPluggedTest() throws SQLException {
        Condition condition = new IsPluggedIn();
        String query = "select * from appliances where isPlugged = true and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void findUnpluggedTest() throws SQLException {
        Condition condition = new IsUnplugged();
        String query = "select * from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void unplugTest() throws SQLException {
        String query1 = "select applianceID from appliances where isPlugged = true and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        ApplianceManager.unplugAppliance(appId);
        resultSet = statement.executeQuery("select isPlugged from appliances where applianceID = " + appId + ";");
        resultSet.next();
        boolean isPlugged = resultSet.getBoolean("isPlugged");
        assertFalse(isPlugged);
    }

    @Test
    public void unplugInvalidIDTest() throws SQLException {
        String query1 = "select applianceID from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        String actual = ApplianceManager.unplugAppliance(appId);
        String expected = "Немає електроприладу з таким ID для вимкнення!";
        assertEquals(expected, actual);
    }

    @Test
    public void findUnpluggedAfterUnpluggingTest() throws SQLException {
        Condition condition = new IsUnplugged();
        String query = "select * from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }


    @Test
    public void findRepairTest() throws SQLException {
        Condition condition = new IsNeedsRepair();
        String query1 = "select * from appliances where isPlugged = false and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appID = resultSet.getInt("applianceID");
        int basicPower = resultSet.getInt("basicPower");
        int power = resultSet.getInt("power");
        if (resultSet.getBoolean("isPlugged")) {
            ApplianceManager.unplugAppliance(appID);
        }
        while (basicPower * Appliance.REPAIR_PERCENT <= power * 100) {
            ApplianceManager.plugInAppliance(appID);
            ApplianceManager.unplugAppliance(appID);
            power -= 10;
        }
        String query2 = "select * from appliances where power <= (basicPower * " + Appliance.REPAIR_PERCENT + " / 100) and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query2);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void repairTest() throws SQLException {
        String query1 = "select applianceID from appliances where power <= (basicPower * 3 / 4) and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        ApplianceManager.repairAppliance(appId);
        resultSet = statement.executeQuery("select basicPower, power from appliances where applianceID = " + appId + ";");
        resultSet.next();
        boolean isRepaired = resultSet.getInt("power") == resultSet.getInt("basicPower");
        assertTrue(isRepaired);
    }

    @Test
    public void repairInvalidIDTest() throws SQLException {
        String query1 = "select applianceID from appliances where power > (basicPower * 3 / 4) and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        String actual = ApplianceManager.repairAppliance(appId);
        String expected = "Немає електроприладу з таким ID для ремонту!";
        assertEquals(expected, actual);
    }

    @Test
    public void findPowerInLimitsTest() throws SQLException {
        Condition condition = new IsPowerWithinLimits(500, 1500);
        String query = "select * from appliances where power between 500 and 1500 and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void findPowerInLimitsMinGreaterMaxTest() throws SQLException {
        Condition condition = new IsPowerWithinLimits(2100, 1000);
        String query = "select * from appliances where power between 1000 and 2100 and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void findPowerInLimitsEmptyListTest() throws SQLException {
        Condition condition = new IsPowerWithinLimits(5000, 15000);
        String query = "select * from appliances where power between 5000 and 15000 and applianceID in (select applianceID from houses where userID = " + userID + ") order by power desc;";
        List<Appliance> expected = selectToList(query);
        List<Appliance> actual = ApplianceManager.findAppliances(condition);
        assertEquals(expected, actual);
    }

    @Test
    public void removeTest() throws SQLException {
        String query1 = "select applianceID from houses where userID = " + userID + ";";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        String actual = ApplianceManager.removeAppliance(appId);
        String expected = "Електроприлад успішло видалено!";
        assertEquals(expected, actual);
    }

    @Test
    public void removeInvalidIDTest() throws SQLException {
        String query1 = "select applianceID from houses where userID <> " + userID + ";";
        ResultSet resultSet = statement.executeQuery(query1);
        resultSet.next();
        int appId = resultSet.getInt("applianceID");
        String actual = ApplianceManager.removeAppliance(appId);
        String expected = "Не існує електроприладу з таким ID!";
        assertEquals(expected, actual);
    }


    private List<Appliance> selectToList(String selectQuery) throws SQLException {
        List<Appliance> expectedList = new ArrayList<>();
        ResultSet set = statement.executeQuery(selectQuery);
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
            appliance = ApplianceManager.createApplianceFromTable(type, id, basicPower, power, isPlugged);
            expectedList.add(appliance);
        }
        return expectedList;
    }

    private static void clean() throws SQLException {
        String query1 = "delete from appliances where applianceID in (select applianceID from houses where userID = " + userID + ");";
        String query2 = "delete from houses where userID = " + userID + ";";
        statement.executeUpdate(query1);
        statement.executeUpdate(query2);
    }
}


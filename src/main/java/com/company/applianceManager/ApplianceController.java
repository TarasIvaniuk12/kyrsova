package com.company.applianceManager;

import com.company.applianceManager.Appliances.*;
import com.company.applianceManager.Conditional.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ApplianceController implements Initializable {

    ObservableList<Appliance> data;
    @FXML public ToggleGroup toShowGroup;
    @FXML public TableView<Appliance> table;
    @FXML public TableColumn<Appliance, Integer> idColumn;
    @FXML public TableColumn<Appliance, String> typeColumn;
    @FXML public TableColumn<Appliance, Integer> powerColumn;
    @FXML public TableColumn<Appliance, Boolean> isPluggedColumn;
    @FXML public TableColumn<Appliance, Boolean> isNeedsToRepairColumn;

    @FXML public ChoiceBox<String> applianceType;
    private final String[] typeNames = {"Блендер", "Чайник", "Холодильник", "Мікрохвильовка"};

    @FXML public RadioButton allRadioB;
    @FXML public RadioButton repairRadioB;
    @FXML public RadioButton pluginRadioB;
    @FXML public RadioButton unpluggedRadioB;
    @FXML public RadioButton limitRadioB;
    @FXML public TextField limitMin;
    @FXML public TextField limitMax;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applianceType.getItems().addAll(typeNames);
        applianceType.setValue("Холодильник");

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        powerColumn.setCellValueFactory(new PropertyValueFactory<>("power"));
        isPluggedColumn.setCellValueFactory(new PropertyValueFactory<>("isPlugged"));
        isNeedsToRepairColumn.setCellValueFactory(new PropertyValueFactory<>("isNeedsToRepair"));

        data = ApplianceManager.findAppliances(new TrueCondition());
        table.setItems(data);
    }

    @FXML
    public void show() throws IllegalArgumentException {
        List<Appliance> appliances;
        if (allRadioB.isSelected()) {
            appliances = ApplianceManager.findAppliances(new TrueCondition());
        } else if (repairRadioB.isSelected()) {
            appliances = ApplianceManager.findAppliances(new IsNeedsRepair());
        } else if (pluginRadioB.isSelected()) {
            appliances = ApplianceManager.findAppliances(new IsPluggedIn());
        } else if (unpluggedRadioB.isSelected()) {
            appliances = ApplianceManager.findAppliances(new IsUnplugged());
        } else if (limitRadioB.isSelected()) {
            int min, max;
            try {
                min = Integer.parseInt(limitMin.getText());
                if (min < 0) {
                    throw new NumberFormatException();
                }
                max = Integer.parseInt(limitMax.getText());
                if (max < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                alertWarning("Неправильно вказаний діапазон", "Будь-ласка, введіть додатні числа в поля для меж діапазону.");
                allRadioB.setSelected(true);
                appliances = ApplianceManager.findAppliances(new TrueCondition());
                table.getItems().clear();
                table.getItems().addAll(appliances);
                return;
            }
            appliances = ApplianceManager.findAppliances(new IsPowerWithinLimits(min, max));
        } else {
            throw new IllegalArgumentException();
        }
        table.getItems().clear();
        table.getItems().addAll(appliances);
    }

    @FXML
    public void add() {
        String type = applianceType.getValue();
        String res;
        switch (type) {
            case "Блендер":
                res = ApplianceManager.addAppliance(new Blender());
                break;
            case "Холодильник":
                res = ApplianceManager.addAppliance(new Fridge());
                break;
            case "Чайник":
                res = ApplianceManager.addAppliance(new Kettle());
                break;
            case "Мікрохвильовка":
                res = ApplianceManager.addAppliance(new Microwave());
                break;
            default:
                throw new IllegalArgumentException("Невідомий тип електроприладу!");
        }
        alertInfo("Додавання електроприладу", res);
        show();
    }

    @FXML
    public void remove() {
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        if (selectedCells.size() != 1) {
            alertWarning("Видалення електроприладу", "Виберіть в таблиці електроприлад для видалення.");
            return;
        }
        String text = ApplianceManager.removeAppliance(data.get(selectedCells.get(0).getRow()).getId());
        alertInfo("Видалення електроприладу", text);
        show();
    }

    @FXML
    public void repair() {
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        if (selectedCells.size() != 1) {
            alertWarning("Ремонт електроприладу", "Виберіть в таблиці електроприлад для ремонту.");
            return;
        }
        String text = ApplianceManager.repairAppliance(data.get(selectedCells.get(0).getRow()).getId());
        alertInfo("Ремонт електроприладу", text);
        show();
    }

    @FXML
    public void plugIn() {
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        if (selectedCells.size() != 1) {
            alertWarning("Ввімкнення електроприладу", "Виберіть в таблиці електроприлад для ввімкнення.");
            return;
        }
        String text = ApplianceManager.plugInAppliance(data.get(selectedCells.get(0).getRow()).getId());
        alertInfo("Ввімкнення електроприладу", text);
        show();
    }

    @FXML
    public void unplug() {
        ObservableList<TablePosition> selectedCells = table.getSelectionModel().getSelectedCells();
        if (selectedCells.size() != 1) {
            alertWarning("Вимкнення електроприладу", "Виберіть в таблиці електроприлад для вимкнення.");
            return;
        }
        String text = ApplianceManager.unplugAppliance(data.get(selectedCells.get(0).getRow()).getId());
        alertInfo("Вимкнення електроприладу", text);
        show();
    }

    @FXML
    public void calculatePower() {
        String text = ApplianceManager.calculateTotalPower();
        alertInfo("Порахувати загальну потужність", text);
    }

    @FXML
    public void exit() throws SQLException {
        App.connection.close();
        System.exit(0);
    }

    @FXML
    public void changeUser(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sing-in-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private static void alertInfo(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private static void alertWarning(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}

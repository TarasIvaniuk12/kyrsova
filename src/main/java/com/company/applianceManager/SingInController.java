package com.company.applianceManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class SingInController {


    @FXML
    private TextField login;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField confirmPassword;

    @FXML
    public void singIn(ActionEvent event) {
        String query = "select userID, password from users where userName = '" + login.getText() + "';";
        try (Statement statement = App.connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                if (password.getText().equals(resultSet.getString("password"))) {
                    //log.info("Успішна спроба увійти");
                    ApplianceManager.userID = resultSet.getInt("userID");
                    changeSceneToUserMenu(event);
                } else {
                    alertWarning("Вхід", "Неправильний логін або пароль! Спробуйте увійти ще раз.");
                }
            } else {
                alertWarning("Вхід", "Немає такого користувача! Введіть логін ще раз або зареєструйтеся як новий користувач.");
            }
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML
    public void singUp(ActionEvent event) {
        String query1 = "select userID, password from users where userName = '" + login.getText() + "';";
        try (Statement statement = App.connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query1);
            if (!resultSet.next()) {
                if (!password.getText().equals(confirmPassword.getText())) {
                    alertWarning("Реєстрація", "Паролі не збігаються! Введіть пароль ще раз!");
                    changeSceneToSingUp(event);
                }
                else if (password.getText().length() < 8 || password.getText().length() > 32) {
                    alertWarning("Реєстрація", "Пароль має мати довжину від 8 до 32 символів! Введіть пароль ще раз");
                    changeSceneToSingUp(event);
                } else {
                    String query2 = "insert into users (userName, password) values ('" + login.getText() + "', '" + password.getText() + "');";
                    if (statement.executeUpdate(query2) == 1) {
                        resultSet = statement.executeQuery("select LAST_INSERT_ID() as last_id;");
                        if (resultSet.next()) {
                            ApplianceManager.userID = resultSet.getInt("last_id");
                            changeSceneToUserMenu(event);
                            //log.info("Успішна спроба зареєструватися");
                        } else {
                            alertWarning("Реєстрація", "Упс, щось пішло не так. Попробуйте зареєструватися ще раз.");
                            changeSceneToSingUp(event);
                        }
                    } else {
                        alertWarning("Реєстрація", "Не вдалось зареєструвати користувача. Попробуйте зареєструватися ще раз.");
                        changeSceneToSingUp(event);
                    }
                }

            } else {
                alertWarning("Реєстрація", "Такий користувач уже існує! Введіть інше ім'я");
                changeSceneToSingUp(event);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        //log.info("Невдала спроба зареєструватися");
    }

    @FXML
    public void changeSceneToSingUp(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sing-up-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void changeSceneToSingIn(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("sing-in-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void changeSceneToUserMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("appliance-manager-view.fxml")));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void exit() throws SQLException {
        App.connection.close();
        System.exit(0);
    }

    private static void alertWarning(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }
}

package com.company.applianceManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class App extends Application {
    private static final Logger log = LogManager.getLogger(App.class);

    private static final String url = "jdbc:mysql://localhost:3306/electrical_appliances";
    private static final String username = "root";
    private static final String password = "root";
    public static Connection connection;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("sing-in-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setResizable(false);
        stage.setTitle("Appliance Manager");
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon.png")));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        log.info("start");
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException throwable) {
            log.fatal("Не вдалося під'єднатися до бази даних", throwable);
            throwable.printStackTrace();
            return;
        }
        launch();
        log.info("end");
    }
}


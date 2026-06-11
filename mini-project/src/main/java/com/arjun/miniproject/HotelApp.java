package com.arjun.miniproject;

import java.io.IOException;
import java.net.URL;

import com.arjun.miniproject.controller.MainViewController;
import com.arjun.miniproject.service.HotelService;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HotelApp extends Application {

    private static final HotelService HOTEL_SERVICE = new HotelService();

    @Override
    public void start(Stage stage) throws IOException {
        // HotelService loads all data on construction
        MainViewController mainViewController = new MainViewController();
        Scene scene = new Scene(mainViewController.getView(), 1200, 700);
        URL darkTheme = HotelApp.class.getResource("/styles/dark-theme.css");
        if (darkTheme != null) {
            scene.getStylesheets().add(darkTheme.toExternalForm());
        }
        stage.setTitle("Hotel Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static HotelService getHotelService() {
        return HOTEL_SERVICE;
    }

    public static void main(String[] args) {
        launch(args);
    }
}


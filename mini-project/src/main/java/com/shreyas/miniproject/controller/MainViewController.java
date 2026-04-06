package com.shreyas.miniproject.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.shreyas.miniproject.HotelApp;
import com.shreyas.miniproject.util.AlertHelper;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.concurrent.Task;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * MainViewController: Handles main window events (menu bar, status bar updates).
 */
public class MainViewController {

    private Label statusLabel, dateTimeLabel;
    private MenuItem refreshItem;
    private RoomsController roomsIncludeController;
    private GuestsController guestsIncludeController;
    private BookingsController bookingsIncludeController;
    private BillingController billingIncludeController;
    private BorderPane view;

    public MainViewController() {
        view = buildUI();
        initialize();
    }

    private BorderPane buildUI() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        // Top: Menu Bar
        root.setTop(buildMenuBar());

        // Center: TabPane
        root.setCenter(buildTabPane());

        // Bottom: Status Bar
        root.setBottom(buildStatusBar());

        return root;
    }

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        refreshItem = new MenuItem("Refresh Data");
        refreshItem.setOnAction(e -> handleRefresh());
        fileMenu.getItems().add(refreshItem);
        fileMenu.getItems().add(new SeparatorMenuItem());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> handleExit());
        fileMenu.getItems().add(exitItem);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> handleAbout());
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        return menuBar;
    }

    private TabPane buildTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("main-tabs");

        roomsIncludeController = new RoomsController();
        Tab roomsTab = new Tab("Rooms", roomsIncludeController.getView());
        roomsTab.setClosable(false);

        guestsIncludeController = new GuestsController();
        Tab guestsTab = new Tab("Guests", guestsIncludeController.getView());
        guestsTab.setClosable(false);

        bookingsIncludeController = new BookingsController();
        Tab bookingsTab = new Tab("Bookings", bookingsIncludeController.getView());
        bookingsTab.setClosable(false);

        billingIncludeController = new BillingController();
        Tab billingTab = new Tab("Billing", billingIncludeController.getView());
        billingTab.setClosable(false);

        tabPane.getTabs().addAll(roomsTab, guestsTab, bookingsTab, billingTab);
        return tabPane;
    }

    private HBox buildStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(8));
        statusBar.getStyleClass().add("status-bar");

        statusLabel = new Label("Ready");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        dateTimeLabel = new Label("Loading...");

        statusBar.getChildren().addAll(statusLabel, spacer, dateTimeLabel);
        return statusBar;
    }

    public BorderPane getView() {
        return view;
    }

    private void initialize() {
        updateDateTime();
    }

    private void handleRefresh() {
        refreshItem.setDisable(true);
        statusLabel.setText("Refreshing data...");

        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() {
                HotelApp.getHotelService().loadAllData();
                return null;
            }
        };

        refreshTask.setOnSucceeded(event -> {
            if (roomsIncludeController != null) {
                roomsIncludeController.refreshView();
            }
            if (guestsIncludeController != null) {
                guestsIncludeController.refreshView();
            }
            if (bookingsIncludeController != null) {
                bookingsIncludeController.refreshView();
            }
            if (billingIncludeController != null) {
                billingIncludeController.refreshView();
            }
            statusLabel.setText("Data refreshed");
            refreshItem.setDisable(false);
            AlertHelper.showSuccess("Success", "All data reloaded from storage.");
        });

        refreshTask.setOnFailed(event -> {
            statusLabel.setText("Refresh failed");
            refreshItem.setDisable(false);
            Throwable ex = refreshTask.getException();
            String message = (ex != null && ex.getMessage() != null)
                ? ex.getMessage()
                : "Unable to refresh data.";
            AlertHelper.showError("Error", message);
        });

        Thread refreshThread = new Thread(refreshTask, "hotel-refresh-thread");
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    private void handleExit() {
        System.exit(0);
    }

    private void handleAbout() {
        AlertHelper.showInfo("About", 
            "Hotel Management System v1.0\n\n" +
            "A comprehensive JavaFX-based hotel management application\n" +
            "with room management, guest registration, bookings, and billing.\n\n" +
            "© 2026 Shreyas Hegde");
    }

    private void updateDateTime() {
        dateTimeLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss")));
    }
}


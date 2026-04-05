package com.oswin.miniproject.controller;

import java.util.Arrays;

import com.oswin.miniproject.HotelApp;
import com.oswin.miniproject.models.Guest;
import com.oswin.miniproject.service.HotelService;
import com.oswin.miniproject.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * GuestsController: register and manage guests.
 */
public class GuestsController {

    private TextField nameField, contactField, emailField;
    private Button registerButton, deleteButton, clearButton;

    private TableView<Guest> guestsTable;
    private TableColumn<Guest, Integer> idCol;
    private TableColumn<Guest, String> nameCol, contactCol, emailCol, roomCol, checkInCol, statusCol;

    private final HotelService hotelService = HotelApp.getHotelService();
    private final ObservableList<Guest> guestItems = FXCollections.observableArrayList();
    private Guest selectedGuest = null;
    private VBox view;

    public GuestsController() {
        view = buildUI();
        initialize();
    }

    private VBox buildUI() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Form Section
        TitledPane formPane = buildFormPane();
        root.getChildren().add(formPane);

        // Guests Table
        guestsTable = buildGuestsTable();
        VBox.setVgrow(guestsTable, Priority.ALWAYS);
        root.getChildren().add(guestsTable);

        return root;
    }

    private TitledPane buildFormPane() {
        TitledPane pane = new TitledPane();
        pane.setText("Register Guest");
        pane.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        ColumnConstraints col1 = new ColumnConstraints(100);
        ColumnConstraints col2 = new ColumnConstraints(200);
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(100);
        ColumnConstraints col4 = new ColumnConstraints(200);
        col4.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        grid.add(new Label("Name:"), 0, 0);
        nameField = new TextField();
        grid.add(nameField, 1, 0);

        grid.add(new Label("Email:"), 2, 0);
        emailField = new TextField();
        grid.add(emailField, 3, 0);

        grid.add(new Label("Contact:"), 0, 1);
        contactField = new TextField();
        grid.add(contactField, 1, 1);

        HBox buttonBox = new HBox(10);
        registerButton = new Button("Register");
        registerButton.setStyle("-fx-padding: 8px 20px;");
        registerButton.setOnAction(e -> register());
        deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteGuest());
        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-padding: 8px 20px;");
        clearButton.setOnAction(e -> clear());
        buttonBox.getChildren().addAll(registerButton, deleteButton, clearButton);
        GridPane.setColumnSpan(buttonBox, 4);
        grid.add(buttonBox, 0, 2);

        pane.setContent(grid);
        return pane;
    }

    private TableView<Guest> buildGuestsTable() {
        TableView<Guest> table = new TableView<>();

        idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("guestId"));

        nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        contactCol = new TableColumn<>("Contact");
        contactCol.setPrefWidth(100);
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        emailCol = new TableColumn<>("Email");
        emailCol.setPrefWidth(150);
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        roomCol = new TableColumn<>("Room");
        roomCol.setPrefWidth(60);
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomDisplay"));

        checkInCol = new TableColumn<>("Check-In");
        checkInCol.setPrefWidth(100);
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDisplay"));

        statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(80);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(Arrays.asList(idCol, nameCol, contactCol, emailCol, roomCol, checkInCol, statusCol));

        table.setOnMouseClicked(e -> {
            selectedGuest = table.getSelectionModel().getSelectedItem();
        });

        return table;
    }

    public VBox getView() {
        return view;
    }

    private void initialize() {
        setupValidation();
        refresh();
    }

    private void setupValidation() {
        contactField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean valid = newVal.matches("\\d*") && newVal.length() <= 10;
            contactField.setStyle(valid ? "" : "-fx-text-fill: #ff7b72;");
        });

        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            String email = newVal == null ? "" : newVal.trim();
            boolean valid = email.isEmpty() || isValidEmail(email);
            emailField.setStyle(valid ? "" : "-fx-text-fill: #ff7b72;");
        });
    }

    private void register() {
        try {
            String name = requireField(nameField);
            String contact = requireField(contactField);
            String email = requireField(emailField);

            if (!contact.matches("\\d{10}")) {
                throw new IllegalArgumentException("Contact must be exactly 10 digits.");
            }
            if (!isValidEmail(email)) {
                throw new IllegalArgumentException("Please enter a valid email address.");
            }

            int guestId = hotelService.addGuest(name, contact, email);
            AlertHelper.showSuccess("Success", "Guest registered. ID: " + guestId);
            clear();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void deleteGuest() {
        if (selectedGuest == null) {
            AlertHelper.showWarning("Warning", "Select a guest to delete.");
            return;
        }
        try {
            hotelService.deleteGuest(selectedGuest.getGuestId());
            AlertHelper.showSuccess("Success", "Guest deleted.");
            clear();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void clear() {
        nameField.clear();
        contactField.clear();
        emailField.clear();
        selectedGuest = null;
        guestsTable.getSelectionModel().clearSelection();
    }

    private void refresh() {
        guestItems.setAll(hotelService.getAllGuests());
        guestsTable.setItems(guestItems);
    }

    public void refreshView() {
        refresh();
    }

    private String requireField(TextField field) {
        String val = field.getText();
        if (val == null || val.trim().isEmpty()) {
            throw new IllegalArgumentException("This field is required.");
        }
        return val.trim();
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9._-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}

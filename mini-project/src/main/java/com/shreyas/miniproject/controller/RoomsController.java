package com.shreyas.miniproject.controller;

import java.util.Arrays;

import com.shreyas.miniproject.HotelApp;
import com.shreyas.miniproject.models.DeluxeRoom;
import com.shreyas.miniproject.models.Room;
import com.shreyas.miniproject.models.StandardRoom;
import com.shreyas.miniproject.models.SuiteRoom;
import com.shreyas.miniproject.service.HotelService;
import com.shreyas.miniproject.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
 * RoomsController: manage rooms (add, delete, view).
 */
public class RoomsController {

    private TextField roomNoField, customPriceField;
    private ComboBox<String> roomTypeCombo;
    private ComboBox<String> filterCombo;
    private Button addButton, deleteButton, clearButton;

    private TableView<Room> roomsTable;
    private TableColumn<Room, Integer> roomNoCol;
    private TableColumn<Room, String> typeCol, classCol, wifiCol, acCol, breakfastCol, statusCol;
    private TableColumn<Room, Double> priceCol;

    private final HotelService hotelService = HotelApp.getHotelService();
    private final ObservableList<Room> roomItems = FXCollections.observableArrayList();
    private Room selectedRoom = null;
    private VBox view;

    public RoomsController() {
        view = buildUI();
        initialize();
    }

    private VBox buildUI() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Form Section
        TitledPane formPane = buildFormPane();
        root.getChildren().add(formPane);

        // Filter Section
        HBox filterBox = new HBox(10);
        filterBox.setStyle("-fx-alignment: CENTER_LEFT;");
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        filterCombo = new ComboBox<>();
        filterCombo.setPrefWidth(150);
        filterCombo.setItems(FXCollections.observableArrayList("All", "Available", "Occupied"));
        filterCombo.getSelectionModel().selectFirst();
        filterCombo.setOnAction(e -> refresh());
        filterBox.getChildren().addAll(filterLabel, filterCombo);
        root.getChildren().add(filterBox);

        // Rooms Table
        roomsTable = buildRoomsTable();
        VBox.setVgrow(roomsTable, Priority.ALWAYS);
        root.getChildren().add(roomsTable);

        return root;
    }

    private TitledPane buildFormPane() {
        TitledPane pane = new TitledPane();
        pane.setText("Add Room");
        pane.setCollapsible(false);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));

        // Column constraints
        ColumnConstraints col1 = new ColumnConstraints(100);
        ColumnConstraints col2 = new ColumnConstraints(150);
        col2.setHgrow(Priority.ALWAYS);
        ColumnConstraints col3 = new ColumnConstraints(100);
        ColumnConstraints col4 = new ColumnConstraints(150);
        col4.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Row 0
        grid.add(new Label("Room No:"), 0, 0);
        roomNoField = new TextField();
        grid.add(roomNoField, 1, 0);

        grid.add(new Label("Room Type:"), 2, 0);
        roomTypeCombo = new ComboBox<>();
        roomTypeCombo.setItems(FXCollections.observableArrayList("STANDARD", "DELUXE", "SUITE"));
        grid.add(roomTypeCombo, 3, 0);

        // Row 1
        grid.add(new Label("Custom Price:"), 0, 1);
        customPriceField = new TextField();
        grid.add(customPriceField, 1, 1);

        // Row 2: Buttons
        HBox buttonBox = new HBox(10);
        addButton = new Button("Add Room");
        addButton.setStyle("-fx-padding: 8px 20px;");
        addButton.setOnAction(e -> addRoom());
        deleteButton = new Button("Delete Selected");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteRoom());
        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-padding: 8px 20px;");
        clearButton.setOnAction(e -> clear());
        buttonBox.getChildren().addAll(addButton, deleteButton, clearButton);
        GridPane.setColumnSpan(buttonBox, 4);
        grid.add(buttonBox, 0, 2);

        pane.setContent(grid);
        return pane;
    }

    private TableView<Room> buildRoomsTable() {
        TableView<Room> table = new TableView<>();

        roomNoCol = new TableColumn<>("Room No");
        roomNoCol.setPrefWidth(80);
        roomNoCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(100);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        classCol = new TableColumn<>("Class");
        classCol.setPrefWidth(100);
        classCol.setCellValueFactory(new PropertyValueFactory<>("roomClass"));

        priceCol = new TableColumn<>("Price (₹)");
        priceCol.setPrefWidth(90);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));

        wifiCol = new TableColumn<>("WiFi");
        wifiCol.setPrefWidth(60);
        wifiCol.setCellValueFactory(new PropertyValueFactory<>("wifi"));

        acCol = new TableColumn<>("AC");
        acCol.setPrefWidth(60);
        acCol.setCellValueFactory(new PropertyValueFactory<>("ac"));

        breakfastCol = new TableColumn<>("Breakfast");
        breakfastCol.setPrefWidth(80);
        breakfastCol.setCellValueFactory(new PropertyValueFactory<>("breakfast"));

        statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(80);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(Arrays.asList(roomNoCol, typeCol, classCol, priceCol, wifiCol, acCol, breakfastCol, statusCol));

        table.setOnMouseClicked(e -> {
            selectedRoom = table.getSelectionModel().getSelectedItem();
            if (selectedRoom != null) {
                populateFormFromRoom();
            }
        });

        return table;
    }

    public VBox getView() {
        return view;
    }

    private void initialize() {
        refresh();
    }

    private void addRoom() {
        try {
            int roomNo = Integer.parseInt(requireField(roomNoField));
            String roomType = requireCombo(roomTypeCombo);
            double price = customPriceField.getText().isEmpty() ? 0 : Double.parseDouble(customPriceField.getText());

            Room room = null;
            switch (roomType) {
                case "STANDARD":
                    room = price > 0 ? new StandardRoom(roomNo, price) : new StandardRoom(roomNo);
                    break;
                case "DELUXE":
                    room = price > 0 ? new DeluxeRoom(roomNo, price) : new DeluxeRoom(roomNo);
                    break;
                case "SUITE":
                    room = price > 0 ? new SuiteRoom(roomNo, price) : new SuiteRoom(roomNo);
                    break;
            }
            hotelService.addRoom(room);
            AlertHelper.showSuccess("Success", "Room added successfully.");
            clear();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void deleteRoom() {
        if (selectedRoom == null) {
            AlertHelper.showWarning("Warning", "Select a room to delete.");
            return;
        }
        try {
            hotelService.deleteRoom(selectedRoom.getRoomNumber());
            AlertHelper.showSuccess("Success", "Room deleted.");
            clear();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void clear() {
        roomNoField.clear();
        customPriceField.clear();
        roomTypeCombo.getSelectionModel().clearSelection();
        selectedRoom = null;
        roomsTable.getSelectionModel().clearSelection();
    }

    private void refresh() {
        String filter = filterCombo.getValue();
        ObservableList<Room> filtered = FXCollections.observableArrayList();

        for (Room room : hotelService.getAllRooms()) {
            if (filter.equals("All") ||
                (filter.equals("Available") && room.isAvailable()) ||
                (filter.equals("Occupied") && !room.isAvailable())) {
                filtered.add(room);
            }
        }
        roomItems.setAll(filtered);
        roomsTable.setItems(roomItems);
    }

    private void populateFormFromRoom() {
        roomNoField.setText(String.valueOf(selectedRoom.getRoomNumber()));
        roomTypeCombo.setValue(selectedRoom.getRoomType().name());
        customPriceField.setText(String.format("%.2f", selectedRoom.getPricePerNight()));
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

    private String requireCombo(ComboBox<String> combo) {
        Object val = combo.getValue();
        if (val == null) {
            throw new IllegalArgumentException("Please select an option.");
        }
        return val.toString();
    }
}


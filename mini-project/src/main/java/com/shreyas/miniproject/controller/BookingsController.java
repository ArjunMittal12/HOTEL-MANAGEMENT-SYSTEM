package com.oswin.miniproject.controller;

import java.time.LocalDate;
import java.util.Arrays;

import com.oswin.miniproject.HotelApp;
import com.oswin.miniproject.models.Booking;
import com.oswin.miniproject.models.Guest;
import com.oswin.miniproject.models.Room;
import com.oswin.miniproject.service.HotelService;
import com.oswin.miniproject.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
 * BookingsController: manage room bookings.
 */
public class BookingsController {

    private ComboBox<String> guestCombo, roomCombo;
    private TextField nightsField;
    private DatePicker checkInDatePicker;
    private Button bookButton, checkoutButton, clearButton;

    private TableView<Booking> bookingsTable;
    private TableColumn<Booking, Integer> bookingIdCol, roomCol, nightsCol;
    private TableColumn<Booking, String> guestCol, checkInCol, checkOutCol, statusCol;
    private TableColumn<Booking, Double> roomChargesCol, grandTotalCol;

    private final HotelService hotelService = HotelApp.getHotelService();
    private final ObservableList<Booking> bookingItems = FXCollections.observableArrayList();
    private Booking selectedBooking = null;
    private VBox view;

    public BookingsController() {
        view = buildUI();
        initialize();
    }

    private VBox buildUI() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Form Section
        TitledPane formPane = buildFormPane();
        root.getChildren().add(formPane);

        // Bookings Table
        Label tableLabel = new Label("All Bookings");
        tableLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        root.getChildren().add(tableLabel);

        bookingsTable = buildBookingsTable();
        VBox.setVgrow(bookingsTable, Priority.ALWAYS);
        root.getChildren().add(bookingsTable);

        return root;
    }

    private TitledPane buildFormPane() {
        TitledPane pane = new TitledPane();
        pane.setText("Create Booking");
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

        grid.add(new Label("Guest:"), 0, 0);
        guestCombo = new ComboBox<>();
        grid.add(guestCombo, 1, 0);

        grid.add(new Label("Room:"), 2, 0);
        roomCombo = new ComboBox<>();
        grid.add(roomCombo, 3, 0);

        grid.add(new Label("Nights:"), 0, 1);
        nightsField = new TextField();
        grid.add(nightsField, 1, 1);

        grid.add(new Label("Check-In:"), 2, 1);
        checkInDatePicker = new DatePicker();
        grid.add(checkInDatePicker, 3, 1);

        HBox buttonBox = new HBox(10);
        bookButton = new Button("Book Room");
        bookButton.setStyle("-fx-padding: 8px 20px;");
        bookButton.setOnAction(e -> bookRoom());
        checkoutButton = new Button("Checkout");
        checkoutButton.setOnAction(e -> checkout());
        clearButton = new Button("Clear");
        clearButton.setStyle("-fx-padding: 8px 20px;");
        clearButton.setOnAction(e -> clear());
        buttonBox.getChildren().addAll(bookButton, checkoutButton, clearButton);
        GridPane.setColumnSpan(buttonBox, 4);
        grid.add(buttonBox, 0, 2);

        pane.setContent(grid);
        return pane;
    }

    private TableView<Booking> buildBookingsTable() {
        TableView<Booking> table = new TableView<>();

        bookingIdCol = new TableColumn<>("Booking ID");
        bookingIdCol.setPrefWidth(80);
        bookingIdCol.setCellValueFactory(new PropertyValueFactory<>("bookingId"));

        guestCol = new TableColumn<>("Guest ID");
        guestCol.setPrefWidth(100);
        guestCol.setCellValueFactory(new PropertyValueFactory<>("guestId"));

        roomCol = new TableColumn<>("Room");
        roomCol.setPrefWidth(60);
        roomCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        nightsCol = new TableColumn<>("Nights");
        nightsCol.setPrefWidth(60);
        nightsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfNights"));

        checkInCol = new TableColumn<>("Check-In");
        checkInCol.setPrefWidth(100);
        checkInCol.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        checkOutCol = new TableColumn<>("Check-Out");
        checkOutCol.setPrefWidth(100);
        checkOutCol.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        roomChargesCol = new TableColumn<>("Charges (₹)");
        roomChargesCol.setPrefWidth(100);
        roomChargesCol.setCellValueFactory(new PropertyValueFactory<>("roomCharges"));

        grandTotalCol = new TableColumn<>("Total (₹)");
        grandTotalCol.setPrefWidth(100);
        grandTotalCol.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));

        statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(80);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(Arrays.asList(bookingIdCol, guestCol, roomCol, nightsCol, checkInCol, checkOutCol, roomChargesCol, grandTotalCol, statusCol));

        table.setOnMouseClicked(e -> {
            selectedBooking = table.getSelectionModel().getSelectedItem();
        });

        return table;
    }

    public VBox getView() {
        return view;
    }

    private void initialize() {
        setupComboBoxes();
        checkInDatePicker.setValue(LocalDate.now());
        refresh();
    }

    private void setupComboBoxes() {
        refreshGuestCombo();
        refreshRoomCombo();
        guestCombo.setOnAction(e -> refreshRoomCombo());
        guestCombo.setOnShowing(e -> refreshGuestCombo());
        roomCombo.setOnShowing(e -> refreshRoomCombo());

        bookingsTable.setOnMouseClicked(e -> {
            selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        });
    }

    private void refreshGuestCombo() {
        ObservableList<String> guestList = FXCollections.observableArrayList();
        for (Guest g : hotelService.getAllGuests()) {
            guestList.add(g.getGuestId() + " — " + g.getName());
        }
        guestCombo.setItems(guestList);
    }

    private void refreshRoomCombo() {
        ObservableList<String> roomList = FXCollections.observableArrayList();
        for (Room r : hotelService.getAvailableRooms()) {
            roomList.add(r.getRoomNumber() + " (" + r.getRoomType().name() + " - ₹" +
                         String.format("%.0f", r.getPricePerNight()) + ")");
        }
        roomCombo.setItems(roomList);
    }

    private void bookRoom() {
        try {
            String guestStr = requireCombo(guestCombo);
            String roomStr = requireCombo(roomCombo);
            int nights = Integer.parseInt(requireField(nightsField));

            int guestId = Integer.parseInt(guestStr.split(" — ")[0]);
            int roomNo = Integer.parseInt(roomStr.split(" ")[0]);
            LocalDate checkIn = checkInDatePicker.getValue();
            LocalDate checkOut = checkIn.plusDays(nights);

            hotelService.bookRoom(guestId, roomNo, checkIn.toString(), checkOut.toString(), nights);
            AlertHelper.showSuccess("Success", "Room booked successfully.");
            clear();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void checkout() {
        if (selectedBooking == null) {
            AlertHelper.showWarning("Warning", "Select a booking to checkout.");
            return;
        }
        if (!selectedBooking.isActive()) {
            AlertHelper.showWarning("Warning", "This booking is already closed.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        AlertHelper.applyTheme(confirmAlert);
        confirmAlert.setTitle("Confirm Checkout");
        confirmAlert.setHeaderText("Checkout Confirmation");
        confirmAlert.setContentText(String.format(
            "Guest: %s\nRoom: %d\nGrand Total: ₹%.2f\n\nProceed with checkout?",
            hotelService.findGuestById(selectedBooking.getGuestId()).getName(),
            selectedBooking.getRoomNumber(),
            selectedBooking.getGrandTotal()
        ));

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                hotelService.checkout(selectedBooking.getBookingId());
                AlertHelper.showSuccess("Success", "Checkout completed.");
                clear();
                refresh();
            } catch (RuntimeException ex) {
                AlertHelper.showError("Error", ex.getMessage());
            }
        }
    }

    private void clear() {
        guestCombo.getSelectionModel().clearSelection();
        roomCombo.getSelectionModel().clearSelection();
        nightsField.clear();
        checkInDatePicker.setValue(LocalDate.now());
        selectedBooking = null;
        bookingsTable.getSelectionModel().clearSelection();
    }

    private void refresh() {
        refreshGuestCombo();
        refreshRoomCombo();
        bookingItems.setAll(hotelService.getAllBookings());
        bookingsTable.setItems(bookingItems);
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

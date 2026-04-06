package com.shreyas.miniproject.controller;

import java.util.Arrays;

import com.shreyas.miniproject.HotelApp;
import com.shreyas.miniproject.models.BillEntry;
import com.shreyas.miniproject.models.Booking;
import com.shreyas.miniproject.models.Guest;
import com.shreyas.miniproject.models.ServiceItem;
import com.shreyas.miniproject.service.HotelService;
import com.shreyas.miniproject.util.AlertHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * BillingController: manage itemised billing for bookings.
 */
public class BillingController {

    private ComboBox<String> bookingCombo, serviceCombo;
    private TextField quantityField;
    private Button addServiceButton, generateBillButton;

    private TableView<BillEntry> servicesTable;
    private TableColumn<BillEntry, String> serviceCol, dateCol;
    private TableColumn<BillEntry, Integer> qtyCol;
    private TableColumn<BillEntry, Double> costCol;

    private TextArea billSummaryArea;
    private Label guestNameLabel, roomLabel, checkInLabel, nightsLabel, roomChargesLabel, serviceChargesLabel, grandTotalLabel;

    private final HotelService hotelService = HotelApp.getHotelService();
    private final ObservableList<BillEntry> serviceItems = FXCollections.observableArrayList();
    private Booking selectedBooking = null;
    private VBox view;

    public BillingController() {
        view = buildUI();
        initialize();
    }

    private VBox buildUI() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Booking Selection
        HBox bookingBox = new HBox(15);
        bookingBox.setStyle("-fx-alignment: CENTER_LEFT;");
        Label bookingLabel = new Label("Select Booking:");
        bookingLabel.setStyle("-fx-font-weight: bold;");
        bookingCombo = new ComboBox<>();
        bookingCombo.setPrefWidth(300);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        bookingBox.getChildren().addAll(bookingLabel, bookingCombo, spacer);
        root.getChildren().add(bookingBox);

        // Main Content: Two Column Layout
        HBox mainBox = new HBox(15);
        VBox.setVgrow(mainBox, Priority.ALWAYS);

        // Left Side: Add Services
        VBox leftPane = buildLeftPane();
        mainBox.getChildren().add(leftPane);

        // Right Side: Bill Summary
        VBox rightPane = buildRightPane();
        mainBox.getChildren().add(rightPane);

        root.getChildren().add(mainBox);
        return root;
    }

    private VBox buildLeftPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(500);
        pane.setStyle("-fx-border-color: #2f3746; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");

        Label title = new Label("Add Service to Booking");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        pane.getChildren().add(title);

        HBox serviceBox = new HBox(10);
        serviceBox.getChildren().addAll(new Label("Service:"), buildServiceCombo());
        pane.getChildren().add(serviceBox);

        HBox qtyBox = new HBox(10);
        quantityField = new TextField();
        quantityField.setPrefWidth(80);
        addServiceButton = new Button("Add");
        addServiceButton.setStyle("-fx-padding: 8px 20px;");
        addServiceButton.setOnAction(e -> addService());
        qtyBox.getChildren().addAll(new Label("Quantity:"), quantityField, addServiceButton);
        pane.getChildren().add(qtyBox);

        Label servicesLabel = new Label("Services Added");
        servicesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        pane.getChildren().add(servicesLabel);

        servicesTable = buildServicesTable();
        VBox.setVgrow(servicesTable, Priority.ALWAYS);
        pane.getChildren().add(servicesTable);

        return pane;
    }

    private ComboBox<String> buildServiceCombo() {
        serviceCombo = new ComboBox<>();
        return serviceCombo;
    }

    private TableView<BillEntry> buildServicesTable() {
        TableView<BillEntry> table = new TableView<>();

        serviceCol = new TableColumn<>("Service");
        serviceCol.setPrefWidth(200);
        serviceCol.setCellValueFactory(new PropertyValueFactory<>("serviceName"));

        qtyCol = new TableColumn<>("Qty");
        qtyCol.setPrefWidth(50);
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        costCol = new TableColumn<>("Cost (₹)");
        costCol.setPrefWidth(80);
        costCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("addedOn"));

        table.getColumns().addAll(Arrays.asList(serviceCol, qtyCol, costCol, dateCol));
        return table;
    }

    private VBox buildRightPane() {
        VBox pane = new VBox(10);
        pane.setPrefWidth(500);
        pane.setStyle("-fx-border-color: #2f3746; -fx-border-width: 1; -fx-padding: 15; -fx-border-radius: 5;");

        Label title = new Label("Bill Summary");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12;");
        pane.getChildren().add(title);

        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(10);
        summaryGrid.setVgap(8);

        ColumnConstraints col1 = new ColumnConstraints(120);
        ColumnConstraints col2 = new ColumnConstraints(200);
        col2.setHgrow(Priority.ALWAYS);
        summaryGrid.getColumnConstraints().addAll(col1, col2);

        Label guestLabel = new Label("Guest:");
        guestLabel.setStyle("-fx-font-weight: bold;");
        guestNameLabel = new Label("-");
        summaryGrid.add(guestLabel, 0, 0);
        summaryGrid.add(guestNameLabel, 1, 0);

        Label roomLblTitle = new Label("Room:");
        roomLblTitle.setStyle("-fx-font-weight: bold;");
        roomLabel = new Label("-");
        summaryGrid.add(roomLblTitle, 0, 1);
        summaryGrid.add(roomLabel, 1, 1);

        Label checkInTitle = new Label("Check-In:");
        checkInTitle.setStyle("-fx-font-weight: bold;");
        checkInLabel = new Label("-");
        summaryGrid.add(checkInTitle, 0, 2);
        summaryGrid.add(checkInLabel, 1, 2);

        Label nightsTitle = new Label("Nights:");
        nightsTitle.setStyle("-fx-font-weight: bold;");
        nightsLabel = new Label("-");
        summaryGrid.add(nightsTitle, 0, 3);
        summaryGrid.add(nightsLabel, 1, 3);

        Separator sep1 = new Separator();
        GridPane.setColumnSpan(sep1, 2);
        summaryGrid.add(sep1, 0, 4);

        Label roomChargesTitle = new Label("Room Charges:");
        roomChargesTitle.setStyle("-fx-font-weight: bold;");
        roomChargesLabel = new Label("₹0.00");
        roomChargesLabel.setStyle("-fx-text-alignment: right;");
        summaryGrid.add(roomChargesTitle, 0, 5);
        summaryGrid.add(roomChargesLabel, 1, 5);

        Label serviceChargesTitle = new Label("Service Charges:");
        serviceChargesTitle.setStyle("-fx-font-weight: bold;");
        serviceChargesLabel = new Label("₹0.00");
        serviceChargesLabel.setStyle("-fx-text-alignment: right;");
        summaryGrid.add(serviceChargesTitle, 0, 6);
        summaryGrid.add(serviceChargesLabel, 1, 6);

        Separator sep2 = new Separator();
        GridPane.setColumnSpan(sep2, 2);
        summaryGrid.add(sep2, 0, 7);

        Label grandTotalTitle = new Label("Grand Total:");
        grandTotalTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
        grandTotalLabel = new Label("₹0.00");
        grandTotalLabel.setStyle("-fx-text-alignment: right; -fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #7cc5ff;");
        summaryGrid.add(grandTotalTitle, 0, 8);
        summaryGrid.add(grandTotalLabel, 1, 8);

        pane.getChildren().add(summaryGrid);

        Label billLabel = new Label("Full Bill");
        billLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11;");
        pane.getChildren().add(billLabel);

        billSummaryArea = new TextArea();
        billSummaryArea.setEditable(false);
        billSummaryArea.setWrapText(true);
        VBox.setVgrow(billSummaryArea, Priority.ALWAYS);
        pane.getChildren().add(billSummaryArea);

        HBox billButtonBox = new HBox(10);
        generateBillButton = new Button("Generate Bill");
        generateBillButton.setStyle("-fx-padding: 8px 20px;");
        generateBillButton.setOnAction(e -> generateBill());
        billButtonBox.getChildren().add(generateBillButton);
        pane.getChildren().add(billButtonBox);

        return pane;
    }

    public VBox getView() {
        return view;
    }

    private void initialize() {
        setupComboBoxes();
        billSummaryArea.setEditable(false);
        refresh();
    }

    private void setupComboBoxes() {
        refreshBookingCombo();
        refreshServiceCombo();
        bookingCombo.setOnShowing(e -> refreshBookingCombo());
        serviceCombo.setOnShowing(e -> refreshServiceCombo());

        bookingCombo.setOnAction(e -> {
            String selected = bookingCombo.getValue();
            if (selected != null) {
                int bookingId = Integer.parseInt(selected.split(" — ")[0]);
                for (Booking b : hotelService.getAllBookings()) {
                    if (b.getBookingId() == bookingId && b.isActive()) {
                        selectedBooking = b;
                        updateBillSummary();
                        updateServicesTable();
                        break;
                    }
                }
            }
        });
    }

    private void refreshBookingCombo() {
        ObservableList<String> bookingList = FXCollections.observableArrayList();
        for (Booking b : hotelService.getActiveBookings()) {
            Guest g = hotelService.findGuestById(b.getGuestId());
            bookingList.add(b.getBookingId() + " — " + (g != null ? g.getName() : "Unknown") +
                           " — Room " + b.getRoomNumber());
        }
        bookingCombo.setItems(bookingList);
    }

    private void refreshServiceCombo() {
        ObservableList<String> serviceList = FXCollections.observableArrayList();
        for (ServiceItem item : ServiceItem.values()) {
            serviceList.add(item.getDisplayName() + " (₹" + String.format("%.0f", item.getUnitPrice()) + ")");
        }
        serviceCombo.setItems(serviceList);
    }

    private void addService() {
        if (selectedBooking == null) {
            AlertHelper.showWarning("Warning", "Select a booking first.");
            return;
        }
        try {
            String serviceStr = requireCombo(serviceCombo);
            int qty = Integer.parseInt(requireField(quantityField));

            // Extract service name from combo display (e.g., "Breakfast (₹200)" -> "Breakfast")
            String displayName = serviceStr.substring(0, serviceStr.indexOf(" ("));
            
            // Find matching ServiceItem by display name
            ServiceItem service = null;
            for (ServiceItem item : ServiceItem.values()) {
                if (item.getDisplayName().equals(displayName)) {
                    service = item;
                    break;
                }
            }
            
            if (service == null) {
                throw new IllegalArgumentException("Service not found.");
            }

            hotelService.addServiceToBooking(selectedBooking.getBookingId(), service, qty);
            AlertHelper.showSuccess("Success", "Service added.");
            quantityField.clear();
            updateBillSummary();
            updateServicesTable();
            refresh();
        } catch (RuntimeException ex) {
            AlertHelper.showError("Error", ex.getMessage());
        }
    }

    private void generateBill() {
        if (selectedBooking == null) {
            AlertHelper.showWarning("Warning", "Select a booking first.");
            return;
        }
        String billText = billSummaryArea.getText();
        Alert billAlert = new Alert(Alert.AlertType.INFORMATION);
        billAlert.setTitle("Bill");
        billAlert.setHeaderText("Bill for Booking " + selectedBooking.getBookingId());
        billAlert.getDialogPane().setPrefWidth(500);
        TextArea textArea = new TextArea(billText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        billAlert.getDialogPane().setContent(textArea);
        billAlert.showAndWait();
    }

    private void updateServicesTable() {
        if (selectedBooking == null) {
            serviceItems.clear();
        } else {
            serviceItems.setAll(selectedBooking.getServiceEntries());
        }
        servicesTable.setItems(serviceItems);
    }

    private void updateBillSummary() {
        if (selectedBooking == null) {
            billSummaryArea.clear();
            return;
        }

        Guest guest = hotelService.findGuestById(selectedBooking.getGuestId());

        guestNameLabel.setText(guest != null ? guest.getName() : "Unknown");
        roomLabel.setText(String.valueOf(selectedBooking.getRoomNumber()));
        checkInLabel.setText(selectedBooking.getCheckInDate());
        nightsLabel.setText(String.valueOf(selectedBooking.getNumberOfNights()));
        roomChargesLabel.setText(String.format("₹%.2f", selectedBooking.getRoomCharges()));

        double serviceCharges = selectedBooking.getTotalServiceCharges();
        serviceChargesLabel.setText(String.format("₹%.2f", serviceCharges));
        grandTotalLabel.setText(String.format("₹%.2f", selectedBooking.getGrandTotal()));

        StringBuilder bill = new StringBuilder();
        bill.append("========== BILL ==========\n");
        bill.append("Guest: ").append(guest != null ? guest.getName() : "Unknown").append("\n");
        bill.append("Room: ").append(selectedBooking.getRoomNumber()).append("\n");
        bill.append("Check-In: ").append(selectedBooking.getCheckInDate()).append("\n");
        bill.append("Nights: ").append(selectedBooking.getNumberOfNights()).append("\n\n");
        bill.append("Room Charges: ₹").append(String.format("%.2f", selectedBooking.getRoomCharges())).append("\n\n");
        bill.append("--- Services ---\n");

        for (BillEntry entry : selectedBooking.getServiceEntries()) {
            bill.append(entry.getService().getDisplayName()).append(" x ").append(entry.getQuantity())
                .append(" = ₹").append(String.format("%.2f", entry.getTotalCost())).append("\n");
        }

        bill.append("\n--- Summary ---\n");
        bill.append("Service Charges: ₹").append(String.format("%.2f", serviceCharges)).append("\n");
        bill.append("Grand Total: ₹").append(String.format("%.2f", selectedBooking.getGrandTotal())).append("\n");
        bill.append("========================\n");

        billSummaryArea.setText(bill.toString());
    }

    private void refresh() {
        refreshBookingCombo();
        refreshServiceCombo();
        updateBillSummary();
        updateServicesTable();
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


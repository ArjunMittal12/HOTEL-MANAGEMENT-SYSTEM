# Hotel Management System - Architecture and Code Guide

## 1. Application Architecture Overview

This project is a desktop JavaFX hotel management system organized in layered MVC style:

1. Presentation Layer (JavaFX UI)
- `controller` package
- Classes: `MainViewController`, `RoomsController`, `GuestsController`, `BookingsController`, `BillingController`
- Role: Build UI screens, handle user events, validate input, and call service methods.

2. Service Layer (Business Logic)
- `service` package
- Class: `HotelService`
- Role: Central domain logic for rooms, guests, bookings, billing, checkout, and in-memory state management.

3. Domain/Model Layer
- `models` package
- Classes/enums/interfaces: `Room`, `StandardRoom`, `DeluxeRoom`, `SuiteRoom`, `RoomType`, `Guest`, `Booking`, `BillEntry`, `Payment`, `PaymentMethod`, `ServiceItem`, `Amenities`
- Role: Define entities and business data structures.

4. Persistence Layer
- `storage` package
- Class: `FileStorageManager`
- Role: Read/write all application state from/to text files in `data/`.

5. Utility Layer
- `util` package
- Class: `AlertHelper`
- Role: Centralized alert/dialog creation and theme application.

6. Entry + Module Boundary
- `HotelApp` (entry point)
- `module-info.java` (JPMS module definition and exports)

---

## 2. Runtime Flow (How the app works)

1. `HotelApp.main()` launches JavaFX.
2. `HotelApp.start()` creates `MainViewController` and scene.
3. `HotelService` singleton in `HotelApp` loads data on construction via `loadAllData()`.
4. Each tab controller reads/writes through `HotelService`.
5. On refresh/menu actions, `MainViewController` runs data reload in a JavaFX background `Task` (`hotel-refresh-thread`) so file I/O does not block the UI thread.
6. `HotelService.saveAllData()` persists data through `FileStorageManager`.

---

## 3. Java Concepts Used

- OOP fundamentals: encapsulation, inheritance, abstraction, polymorphism.
- Interface usage: `Amenities`.
- Abstract class usage: `Room`.
- Enum usage: `RoomType`, `ServiceItem`, `PaymentMethod`.
- JavaFX MVC-ish separation: UI in controllers, business logic in service.
- Collections framework with generics.
- Hash-based lookup for fast access (`Map<Integer, Guest>`).
- Defensive copying (`new ArrayList<>(...)`) to avoid exposing mutable internals.
- Thread-safety on mutating service/storage methods via `synchronized`.
- JavaFX multithreading with `Task` for asynchronous refresh and UI-safe callbacks (`setOnSucceeded` / `setOnFailed`).
- File serialization/deserialization with `toFileString()` / `fromFileString()` patterns.

---

## 4. Java Generics and Collection Types Used

## 4.1 Core Collections
- `List<Room>`, `List<Guest>`, `List<Booking>`, `List<Payment>`
- `List<BillEntry>`
- `ArrayList<...>` concrete implementation for ordered dynamic lists.
- `Map<Integer, Guest>` using `HashMap<Integer, Guest>` for room-to-guest mapping.
- `Map<Integer, List<BillEntry>>` using `HashMap<Integer, List<BillEntry>>` for booking-to-services mapping.
- `Map.Entry<Integer, List<BillEntry>>` while persisting services.

## 4.2 JavaFX Generic Types
- `ObservableList<Guest|Room|Booking|BillEntry|String>` for table/combo reactive data.
- `TableView<T>` typed tables (`TableView<Room>`, `TableView<Guest>`, etc.).
- `TableColumn<T, U>` typed columns (for example `TableColumn<Booking, Integer>`).
- `ComboBox<String>` for selection controls.
- `Dialog<?>` in utility helper.

---

## 5. Class-by-Class Function Purpose Map

## 5.1 `HotelApp`

- `HotelApp()`
  - Default constructor by framework.
- `start(Stage stage)`
  - Creates main view, applies stylesheet, sets stage title/scene, shows app window.
- `getHotelService()`
  - Provides shared singleton `HotelService` to all controllers.
- `main(String[] args)`
  - Java entry point; delegates to JavaFX `launch(args)`.

How it helps: boots the app and exposes a single service instance so all screens share same state.

---

## 5.2 `MainViewController`

- `MainViewController()`
  - Constructs root layout and initializes behavior.
- `buildUI()`
  - Builds top-level container (`BorderPane`) combining menu, tabs, and status bar.
- `buildMenuBar()`
  - Creates File/Help menu actions (refresh, exit, about).
- `buildTabPane()`
  - Creates all feature tabs and embeds child controllers.
- `buildStatusBar()`
  - Builds footer/status section with date-time.
- `getView()`
  - Returns root view for scene attachment.
- `initialize()`
  - Wires startup state and date-time updates.
- `handleRefresh()`
  - Starts a background JavaFX `Task` to run `HotelService.loadAllData()` on a worker thread, then refreshes tabs on success.
- `handleExit()`
  - Terminates app.
- `handleAbout()`
  - Displays app info dialog.
- `updateDateTime()`
  - Updates status clock text.

How it helps: central shell that coordinates all feature modules.

---

## 5.3 `RoomsController`

- `RoomsController()`
  - Creates room management view and initializes data.
- `buildUI()`
  - Assembles form, filter controls, and table.
- `buildFormPane()`
  - Builds room creation/deletion input controls.
- `buildRoomsTable()`
  - Defines typed room table columns and row selection behavior.
- `getView()`
  - Returns this module's root UI.
- `initialize()`
  - Initial data load.
- `addRoom()`
  - Validates form, creates room subclass, calls service add.
- `deleteRoom()`
  - Deletes selected room through service.
- `clear()`
  - Resets room form and selection.
- `refresh()`
  - Reloads/filters room list by availability.
- `populateFormFromRoom()`
  - Fills form when table row selected.
- `refreshView()`
  - Public refresh for parent controller.
- `requireField(TextField field)`
  - Utility validation for required text.
- `requireCombo(ComboBox<String> combo)`
  - Utility validation for required selection.

How it helps: manages room inventory lifecycle and availability visibility.

---

## 5.4 `GuestsController`

- `GuestsController()`
  - Creates guest management view and initializes data.
- `buildUI()`
  - Builds form and guest table sections.
- `buildFormPane()`
  - Creates guest registration inputs/buttons.
- `buildGuestsTable()`
  - Defines guest table columns and selection behavior.
- `getView()`
  - Returns root UI node.
- `initialize()`
  - Startup binding and table refresh.
- `setupValidation()`
  - Wires field-level restrictions/validation behavior.
- `register()`
  - Validates and creates guest through service.
- `deleteGuest()`
  - Removes selected guest (with service constraints).
- `clear()`
  - Clears form and selected table row.
- `refresh()`
  - Reloads guests list from service.
- `refreshView()`
  - Public refresh endpoint.
- `requireField(TextField field)`
  - Required text validation helper.
- `isValidEmail(String email)`
  - Regex-based email format validation.

How it helps: controls guest lifecycle and enforces input quality.

---

## 5.5 `BookingsController`

- `BookingsController()`
  - Builds booking view and initializes data.
- `buildUI()`
  - Builds booking form and bookings table.
- `buildFormPane()`
  - Creates controls for guest-room-date-nights input.
- `buildBookingsTable()`
  - Defines booking table and row selection logic.
- `getView()`
  - Returns root UI node.
- `initialize()`
  - Sets defaults (check-in date) and data.
- `setupComboBoxes()`
  - Initializes combo data and selection listeners.
- `refreshGuestCombo()`
  - Populates guest dropdown.
- `refreshRoomCombo()`
  - Populates only available rooms dropdown.
- `bookRoom()`
  - Creates booking via service after validation.
- `checkout()`
  - Confirms checkout and delegates close/payment to service.
- `clear()`
  - Clears booking form and table selection.
- `refresh()`
  - Reloads bookings and dependent combos.
- `refreshView()`
  - Public refresh endpoint.
- `requireField(TextField field)`
  - Required text validator.
- `requireCombo(ComboBox<String> combo)`
  - Required selection validator.

How it helps: handles occupancy transactions from reservation to checkout.

---

## 5.6 `BillingController`

- `BillingController()`
  - Constructs billing UI and initializes state.
- `buildUI()`
  - Creates booking selector + two-pane billing layout.
- `buildLeftPane()`
  - Creates add-service workflow and services table.
- `buildServiceCombo()`
  - Initializes service selection combo.
- `buildServicesTable()`
  - Defines bill-entry table columns.
- `buildRightPane()`
  - Creates bill summary + full bill text area.
- `getView()`
  - Returns root UI.
- `initialize()`
  - Initial setup and refresh.
- `setupComboBoxes()`
  - Listener setup for booking/service selections.
- `refreshBookingCombo()`
  - Loads active booking choices.
- `refreshServiceCombo()`
  - Loads service item choices.
- `addService()`
  - Adds service line item to selected booking via service.
- `generateBill()`
  - Opens dialog view of generated bill text.
- `updateServicesTable()`
  - Syncs UI table with selected booking entries.
- `updateBillSummary()`
  - Computes and renders bill totals/details.
- `refresh()`
  - Reloads all billing UI data.
- `refreshView()`
  - Public refresh endpoint.
- `requireField(TextField field)`
  - Required text validation helper.
- `requireCombo(ComboBox<String> combo)`
  - Required selection validation helper.

How it helps: handles post-booking monetization and detailed invoice generation.

---

## 5.7 `HotelService` (Core business orchestration)

- `HotelService()`
  - Initializes service and loads persisted data.
- `loadAllData()`
  - Loads rooms/guests/bookings/services/payments/counters from storage.
- `saveAllData()`
  - Persists all in-memory collections and counters to disk.
- `addRoom(Room room)`
  - Adds room with uniqueness checks.
- `getAllRooms()`
  - Returns sorted defensive copy of rooms.
- `getAvailableRooms()`
  - Returns only currently available rooms.
- `findRoomByNumber(int roomNumber)`
  - Finds room by identity.
- `deleteRoom(int roomNumber)`
  - Deletes room with occupancy/business validations.
- `addGuest(String name, String contact, String email)`
  - Creates guest with incrementing id.
- `getAllGuests()`
  - Returns sorted defensive copy of guests.
- `findGuestById(int guestId)`
  - Resolves guest by id.
- `deleteGuest(int guestId)`
  - Deletes guest if not actively tied to booking/room.
- `bookRoom(int guestId, int roomNumber, String checkInDate, String checkOutDate, int numberOfNights)`
  - Validates availability, allocates room, creates booking, updates guest status.
- `addServiceToBooking(int bookingId, ServiceItem service, int quantity)`
  - Adds bill entry to active booking and indexing map.
- `checkout(int bookingId)`
  - Closes booking, frees room, updates guest checkout, records payment, persists.
- `getActiveBookings()`
  - Returns all active bookings only.
- `getAllBookings()`
  - Returns defensive copy of all bookings.

How it helps: single source of truth for domain invariants and cross-entity updates.

---

## 5.8 `FileStorageManager`

- `saveRooms(List<Room> rooms)`
  - Writes room records.
- `loadRooms()`
  - Reads room records.
- `parseRoom(String className, String data)`
  - Converts persisted room type/data into specific room subclass object.
- `saveGuests(List<Guest> guests)`
  - Writes guest records.
- `loadGuests()`
  - Reads guest records.
- `saveBookings(List<Booking> bookings)`
  - Writes booking records.
- `loadBookings()`
  - Reads booking records.
- `saveServices(Map<Integer, List<BillEntry>> servicesMap)`
  - Writes booking-to-services entries.
- `loadServices()`
  - Reads booking service entries map.
- `savePayments(List<Payment> payments)`
  - Writes payment records.
- `loadPayments()`
  - Reads payment records.
- `saveCounters(int nextGuestId, int nextBookingId, int nextPaymentId)`
  - Persists id counters.
- `loadCounters()`
  - Restores id counters.

How it helps: stable persistence boundary separating file I/O from business logic.

---

## 5.9 `AlertHelper`

- `showError(String title, String message)`
  - Convenience wrapper for error alerts.
- `showInfo(String title, String message)`
  - Convenience wrapper for info alerts.
- `showWarning(String title, String message)`
  - Convenience wrapper for warning alerts.
- `showSuccess(String title, String message)`
  - Convenience wrapper for success-style info alerts.
- `showAlert(AlertType type, String title, String message)`
  - Internal common alert builder.
- `applyTheme(Dialog<?> dialog)`
  - Applies app stylesheet/theme classes to dialog pane.

How it helps: avoids duplicated dialog boilerplate and keeps look-and-feel consistent.

---

## 5.10 Domain Model Classes

### `Room` (abstract)
- `Room(int roomNumber, RoomType roomType)`
- `Room(int roomNumber, RoomType roomType, double customPrice)`
- `calculateTariff(int nights)` (abstract)
- `toFileString()`
- `getRoomNumber()`
- `getRoomType()`
- `getType()`
- `getRoomClass()`
- `getPricePerNight()`
- `setPricePerNight(double price)`
- `isAvailable()`
- `getWifi()`
- `getAc()`
- `getBreakfast()`
- `getStatus()`
- `setAvailable(boolean available)`

Purpose: common room identity/state + polymorphic pricing and amenities.

### `StandardRoom`
- `StandardRoom(int roomNumber)`
- `StandardRoom(int roomNumber, double customPrice)`
- `calculateTariff(int nights)`
- `hasWifi()`
- `hasAC()`
- `hasBreakfast()`

Purpose: standard room rules (no premium amenities).

### `DeluxeRoom`
- `DeluxeRoom(int roomNumber)`
- `DeluxeRoom(int roomNumber, double customPrice)`
- `calculateTariff(int nights)`
- `hasWifi()`
- `hasAC()`
- `hasBreakfast()`

Purpose: deluxe pricing and amenity profile.

### `SuiteRoom`
- `SuiteRoom(int roomNumber)`
- `SuiteRoom(int roomNumber, double customPrice)`
- `calculateTariff(int nights)`
- `hasWifi()`
- `hasAC()`
- `hasBreakfast()`

Purpose: suite pricing and full amenity profile.

### `Guest`
- `Guest(int guestId, String name, String contactNumber, String email)`
- `getGuestId()`
- `getName()`
- `getContactNumber()`
- `getEmail()`
- `getAllocatedRoomNumber()`
- `getRoomDisplay()`
- `getCheckInDate()`
- `getCheckInDisplay()`
- `getCheckOutDate()`
- `getStatus()`
- `setAllocatedRoomNumber(int roomNumber)`
- `setCheckInDate(String date)`
- `setCheckOutDate(String date)`
- `toFileString()`
- `fromFileString(String line)`

Purpose: tracks guest profile and stay status fields.

### `Booking`
- `Booking(int bookingId, int guestId, int roomNumber, String checkInDate, String checkOutDate, int numberOfNights, double roomCharges)`
- `getBookingId()`
- `getGuestId()`
- `getRoomNumber()`
- `getCheckInDate()`
- `getCheckOutDate()`
- `getNumberOfNights()`
- `getRoomCharges()`
- `isActive()`
- `getStatus()`
- `setActive(boolean active)`
- `addService(BillEntry entry)`
- `getServiceEntries()`
- `getTotalServiceCharges()`
- `getGrandTotal()`
- `toFileString()`
- `fromFileString(String line)`

Purpose: booking lifecycle + bill aggregation point.

### `BillEntry`
- `BillEntry(ServiceItem service, Integer quantity, String addedOn)`
- `getService()`
- `getServiceName()`
- `getQuantity()`
- `getTotalCost()`
- `getAddedOn()`
- `toFileString()`
- `fromFileString(String line)`

Purpose: item-level billing record attached to a booking.

### `Payment`
- `Payment(int paymentId, int bookingId, Double amount, PaymentMethod paymentMethod, String paymentDate)`
- `getPaymentId()`
- `getBookingId()`
- `getAmount()`
- `getPaymentMethod()`
- `getPaymentDate()`
- `isPaid()`
- `setPaid(boolean paid)`
- `toFileString()`
- `fromFileString(String line)`

Purpose: settlement record created at checkout.

### `RoomType` enum
- `getBasePrice()`
- `calculateCost(int nights)`

Purpose: canonical base tariff configuration.

### `ServiceItem` enum
- `getDisplayName()`
- `getUnitPrice()`
- `calculateCost(int quantity)`
- `toString()`

Purpose: predefined chargeable service catalog.

### `PaymentMethod` enum
- Enum values only (`UPI`, `CASH`, `CARD`).

Purpose: allowed payment channels.

### `Amenities` interface
- `hasWifi()`
- `hasAC()`
- `hasBreakfast()`
- `getAmenitiesSummary()` (default)

Purpose: shared amenity contract implemented by rooms.

---

## 6. Why this design works

- Controllers stay focused on UI behavior.
- `HotelService` centralizes all domain rules, reducing inconsistent updates.
- Models are simple, serializable, and UI-friendly.
- File storage remains isolated, so migration to DB later is straightforward.
- Use of generics in collections and JavaFX controls improves type safety.

---

## 7. Suggested Next Improvements

1. Add DTO/mapper layer if persistence format evolves.
2. Add unit tests for `HotelService` business rules.
3. Replace text file persistence with SQLite or H2.
4. Split very large controllers (`BillingController`) into smaller presenter/service classes.
5. Add validation utility class to unify field checks across controllers.

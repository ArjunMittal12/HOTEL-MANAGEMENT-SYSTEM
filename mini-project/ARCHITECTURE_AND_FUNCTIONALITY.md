# Hotel Management Mini-Project — Architecture & Functionality

This document describes the architecture, data model, module layout, runtime behaviour, and how to run the application in `mini-project/`.

## High-level overview

The application is a small JavaFX-based Hotel Management System. It provides features to:
- Manage rooms (Standard/Deluxe/Suite)
- Register guests
- Create bookings (check-in/check-out)
- Add services to bookings and generate a final bill
- Make and persist payments

The project is intentionally kept lightweight and uses simple file-based persistence under `mini-project/data/`.

## Project layout

```
mini-project/
  pom.xml                       # Maven build + JavaFX plugin
  src/main/java/
    com/arjun/miniproject/      # application module (module-info.java)
      HotelApp.java              # Main JavaFX Application
      controller/                # UI controllers (MainViewController, Bookings, Rooms, Guests, Billing)
      models/                    # Domain model classes (Room, Guest, Booking, Payment, etc.)
      service/                   # Business logic (HotelService)
      storage/                   # FileStorageManager (persistence)
      util/                      # helpers (AlertHelper, Dev utilities)
  src/main/resources/
    styles/dark-theme.css        # Application CSS theme
  data/                          # File-based persistence (rooms.txt, guests.txt, bookings.txt, services.txt, payments.txt, counters.txt)
```

## Module & Packages

The code uses the module `com.arjun.miniproject` (defined in `module-info.java`). Main packages:
- `com.arjun.miniproject` — application entry point
- `com.arjun.miniproject.controller` — JavaFX controllers and UI composition
- `com.arjun.miniproject.models` — POJOs with file serialization helpers
- `com.arjun.miniproject.service` — `HotelService` which contains business logic and offers thread-safe methods to operate on the domain
- `com.arjun.miniproject.storage` — `FileStorageManager` responsible for reading/writing file-based storage
- `com.arjun.miniproject.util` — small utilities (alerts, dialogs, developer helpers)

## Data storage format

All persistent data lives in the `data/` directory as text files. Files use simple delimited formats and are parsed/written by `FileStorageManager`.

- `rooms.txt` — each line: `ClassName|<room-data>` (example: `StandardRoom|101,2,500.00,AVAILABLE`)
- `guests.txt` — each line: serialized guest via `Guest.toFileString()` (comma separated)
- `bookings.txt` — each line: serialized booking via `Booking.toFileString()` (comma separated)
- `services.txt` — each line: `<bookingId>|<BillEntry.toFileString()>` — links service items to bookings
- `payments.txt` — empty or lines of `Payment.toFileString()`
- `counters.txt` — single CSV line: `1,nextGuestId,nextBookingId,nextPaymentId`

Notes:
- `FileStorageManager` creates the `data/` directory if missing.
- The app reads all files on startup (HotelService constructor calls loadAllData()).

## Core components

- HotelApp (Main)
  - Bootstraps JavaFX, loads the CSS theme from `src/main/resources/styles/dark-theme.css`, constructs `MainViewController` and shows the primary stage.

- MainViewController
  - Composes the main UI: a Menu bar, a TabPane (Rooms, Guests, Bookings, Billing) and a status bar.
  - Handles refresh and update actions.

- Controllers (RoomsController, GuestsController, BookingsController, BillingController)
  - Each controller is responsible for building its own UI region and interacting with `HotelService` for operations.
  - BookingsController contains logic to create bookings, perform checkout and refresh the bookings table.

- HotelService
  - In-memory collections for rooms, guests, bookings, payments, and maps linking rooms/bookings to guests/services.
  - Thread-safe public methods (synchronized) to perform operations: addRoom, addGuest, bookRoom, checkout, addServiceToBooking, saveAllData, loadAllData.
  - Persists changes by delegating to `FileStorageManager` after every mutating operation.

- FileStorageManager
  - Reads/writes the text files.
  - Provides: loadRooms, saveRooms, loadGuests, saveGuests, loadBookings, saveBookings, loadServices, saveServices, loadPayments, savePayments, loadCounters, saveCounters.

## Typical flows

1) Application startup
   - `HotelApp` creates `HotelService`.
   - `HotelService` calls `FileStorageManager.load*` to populate in-memory state.
   - The UI is created and bound to the `HotelService` via controller calls to `getAllRooms()`, `getAllGuests()`, `getAllBookings()`.

2) Creating a booking (BookingsController -> HotelService.bookRoom)
   - User selects a guest and an available room, enters nights & check-in date.
   - `bookRoom()` validates inputs, computes room charges, creates a `Booking` object, updates in-memory lists and maps, marks the room as unavailable, sets guest's allocated room number, increments booking counter, calls `saveAllData()`.

3) Checkout
   - User selects an active booking and clicks "Checkout".
   - `HotelService.checkout(bookingId)` marks the booking inactive, frees the room (available), sets guest check-out date, creates a `Payment` marked paid, updates maps and calls `saveAllData()`.

4) Adding services
   - `HotelService.addServiceToBooking(...)` creates a `BillEntry` and stores it in `bookingServices` map and persists via `saveAllData()`.

## Concurrency & Safety

- `HotelService` marks mutating methods `synchronized` to avoid concurrent modifications from UI threads. This is a simple strategy for a single-user desktop app.

## How to run

Prerequisites:
- JDK 17 (or newer, configured for the project)
- Maven

Run locally (macOS X64/ARM example):

```bash
cd mini-project
# if you are on Apple silicon (arm64):
mvn -Djavafx.platform=mac-aarch64 clean javafx:run

# on Intel mac (x86_64):
# mvn -Djavafx.platform=mac clean javafx:run
```

The project's `pom.xml` includes `org.openjfx:javafx-maven-plugin` and uses platform classifiers so Maven downloads the correct native JavaFX binaries. If you see a runtime warning about native access, add the following JVM arg in the `javafx-maven-plugin` configuration: `--enable-native-access=javafx.graphics`.

## Where to change the theme

- CSS is in `src/main/resources/styles/dark-theme.css`. The theme was updated in the current workspace to a deep-teal + warm accent palette.

## Developer notes and next steps

- The app uses file-based persistence to keep the sample project simple. For production replace `FileStorageManager` with a database-backed implementation.
- Consider adding unit tests for `HotelService` and `FileStorageManager` (read/write round trips) and a small migration script for data format changes.
- To support concurrent multi-user access, move persistence to a central server or database, and convert `HotelService` into a stateless service layer.

---

If you'd like, I can also:
- Add a diagram (ASCII or Mermaid) showing component interactions.
- Expand the doc with examples of file formats and sample lines.
- Generate a short README with exact run commands and troubleshooting tips.

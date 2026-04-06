# Hotel Management System - Diagram First Architecture

## 1. High-level Architecture Diagram

```mermaid
flowchart LR
    A[HotelApp\nJavaFX Entry Point] --> B[MainViewController\nShell + Tabs]
  B --> T[Refresh Task\nBackground Thread]
  T --> S

    B --> C1[RoomsController]
    B --> C2[GuestsController]
    B --> C3[BookingsController]
    B --> C4[BillingController]

    C1 --> S[HotelService\nBusiness Logic + Orchestration]
    C2 --> S
    C3 --> S
    C4 --> S

    S --> M1[Room/RoomType + Room subclasses]
    S --> M2[Guest]
    S --> M3[Booking + BillEntry]
    S --> M4[Payment + PaymentMethod + ServiceItem]

    S --> F[FileStorageManager\nPersistence Adapter]

    F --> D1[data/rooms.txt]
    F --> D2[data/guests.txt]
    F --> D3[data/bookings.txt]
    F --> D4[data/services.txt]
    F --> D5[data/payments.txt]
    F --> D6[data/counters.txt]

    C1 --> U[AlertHelper]
    C2 --> U
    C3 --> U
    C4 --> U
    B --> U
```

Note: `MainViewController.handleRefresh()` runs `HotelService.loadAllData()` in a JavaFX `Task` on a daemon worker thread (`hotel-refresh-thread`), then updates UI in `setOnSucceeded` / `setOnFailed`.

## 2. Sequence Diagram - Booking to Checkout

```mermaid
  sequenceDiagram
      participant UI as BookingsController
      participant S as HotelService
      participant FS as FileStorageManager

      UI->>S: bookRoom(guestId, roomNo, checkIn, checkOut, nights)
      S->>S: validate guest + room availability
      S->>S: create Booking, mark Room occupied, update Guest
      S->>FS: saveAllData() via save* methods
      FS-->>S: persisted
      S-->>UI: Booking created

      participant B as BillingController
      B->>S: addServiceToBooking(bookingId, service, qty)
      S->>S: add BillEntry to booking
      S-->>B: updated totals

      UI->>S: checkout(bookingId)
      S->>S: close booking, free room, create payment
      S->>FS: saveAllData()
      FS-->>S: persisted
      S-->>UI: checkout success

      participant MV as MainViewController
      participant BG as JavaFX Task (worker thread)
      MV->>BG: handleRefresh() starts Task
      BG->>S: loadAllData()
      S->>FS: load*() from files
      FS-->>S: data loaded
      BG-->>MV: onSucceeded callback (JavaFX thread)
      MV->>UI: refresh all tabs + status update
```

## 3. Component responsibilities (quick view)
- `HotelApp`: Starts JavaFX and creates the main view.
- `MainViewController`: Tab shell and global actions (refresh/about/exit).
- `RoomsController`: Room CRUD + availability filtering.
- `GuestsController`: Guest registration/deletion + display.
- `BookingsController`: Reservation + checkout workflow.
- `BillingController`: Service additions + bill generation.
- `HotelService`: Domain rules and state consistency across entities.
- `FileStorageManager`: Reads/writes all text file records.
- `AlertHelper`: Centralized alert/dialog handling and theming.

## 4. Data structures and generics diagram

```mermaid
classDiagram
    class HotelService {
      -List~Room~ rooms
      -List~Guest~ guests
      -List~Booking~ bookings
      -List~Payment~ payments
      -Map~Integer, Guest~ roomToGuestMap
      -Map~Integer, List~BillEntry~~ bookingServices
    }

    class FileStorageManager {
      +saveRooms(List~Room~)
      +loadRooms() List~Room~
      +saveGuests(List~Guest~)
      +loadGuests() List~Guest~
      +saveBookings(List~Booking~)
      +loadBookings() List~Booking~
      +saveServices(Map~Integer, List~BillEntry~~)
      +loadServices() Map~Integer, List~BillEntry~~
      +savePayments(List~Payment~)
      +loadPayments() List~Payment~
    }

    HotelService --> FileStorageManager
```

## 5. Concepts used map
- OOP: abstraction (`Room`), inheritance (room subclasses), polymorphism (`calculateTariff`), encapsulation (model fields), interface (`Amenities`).
- Java Collections + Generics: `List<T>`, `ArrayList<T>`, `Map<K,V>`, `HashMap<K,V>`.
- JavaFX typed UI controls: `ObservableList<T>`, `TableView<T>`, `TableColumn<T,U>`, `ComboBox<String>`.
- Persistence pattern: model-level `toFileString()/fromFileString()` plus storage adapter.
- Thread safety: synchronized mutators in service/storage.
- Multithreading: JavaFX background `Task` for menu refresh to avoid UI blocking during file I/O.

## 6. Function-purpose map pointer
For complete function-by-function purpose explanations, refer to:
- `APPLICATION_ARCHITECTURE.md`
- `APPLICATION_ARCHITECTURE_VIVA_NOTES.md`

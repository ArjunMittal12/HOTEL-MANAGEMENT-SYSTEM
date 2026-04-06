# Hotel Management System - Viva Notes (Quick Revision)

## 1. One-line summary
A JavaFX desktop app that manages rooms, guests, bookings, billing, and checkout using MVC-style controllers, a central service layer, and text-file persistence.

## 2. Layered architecture
- UI Layer: `controller` package
- Business Layer: `service/HotelService`
- Model Layer: `models` package
- Persistence Layer: `storage/FileStorageManager`
- Utility Layer: `util/AlertHelper`
- Entry Point: `HotelApp`
- Module config: `module-info.java`

## 3. Core flow (exam answer)
1. `HotelApp.main()` calls JavaFX `launch()`.
2. `HotelApp.start()` creates `MainViewController` and scene.
3. `HotelService` singleton loads data from files.
4. Controllers call `HotelService` for all operations.
5. `HotelService` updates in-memory collections and persists via `FileStorageManager`.

## 4. OOP concepts used
- Encapsulation: private fields + getters/setters in models.
- Inheritance: `StandardRoom`, `DeluxeRoom`, `SuiteRoom` extend `Room`.
- Abstraction: `Room` is abstract with abstract `calculateTariff(int nights)`.
- Interface: `Amenities` implemented by room hierarchy.
- Polymorphism: room-specific `calculateTariff()` overrides.
- Enum: `RoomType`, `ServiceItem`, `PaymentMethod`.

## 5. Generics and collections (very important)
- `ArrayList` through `List<T>`:
  - `List<Room>`, `List<Guest>`, `List<Booking>`, `List<Payment>`, `List<BillEntry>`
- `HashMap` through `Map<K, V>`:
  - `Map<Integer, Guest> roomToGuestMap`
  - `Map<Integer, List<BillEntry>> bookingServices`
- JavaFX generics:
  - `ObservableList<T>` for reactive table/combo data
  - `TableView<T>` and `TableColumn<T, U>`
  - `ComboBox<String>`

## 6. Why `HotelService` is central
`HotelService` is the single source of truth for business rules.
- Prevents invalid operations (booking unavailable room, deleting active guest/room)
- Updates multiple entities consistently (room, guest, booking, payment)
- Provides thread-safe mutations with `synchronized`

## 7. Key functions to remember (high-value for viva)
- `HotelService.loadAllData()` / `saveAllData()`
  - Bootstraps and persists full app state.
- `HotelService.bookRoom(...)`
  - Validates guest and room, creates booking, marks room occupied.
- `HotelService.addServiceToBooking(...)`
  - Adds bill entries to active booking.
- `HotelService.checkout(...)`
  - Closes booking, frees room, creates payment, saves data.
- `FileStorageManager.load*/save*()` methods
  - Text-file persistence boundary.
- `BillingController.updateBillSummary()`
  - Builds live invoice with room + services + grand total.

## 8. File persistence model
The app stores data in plain text files under `data/`:
- `rooms.txt`, `guests.txt`, `bookings.txt`, `services.txt`, `payments.txt`, `counters.txt`

Each model has serialization helpers:
- `toFileString()`
- `fromFileString(String line)`

## 9. Common viva questions and short answers
- Why MVC style?
  - Keeps UI, logic, and data concerns separated for maintainability.

- Why generics?
  - Compile-time type safety and cleaner APIs (`List<Booking>`, `TableView<Guest>`).

- Why `HashMap` + `ArrayList` together?
  - `ArrayList` for ordered traversal; `HashMap` for fast key-based lookup.

- Why defensive copies (`new ArrayList<>(...)`)?
  - Prevents external code from mutating internal service state directly.

- Why `synchronized` methods?
  - Prevents race conditions during mutations/persistence.

## 10. Improvement points (good closing answer)
- Add unit tests for `HotelService`.
- Introduce repository abstraction for future DB migration.
- Move validation logic to shared validator utilities.
- Split large controllers into smaller UI + action handler classes.

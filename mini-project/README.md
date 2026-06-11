# Mini Project — Hotel Management System

This is a compact JavaFX desktop application that demonstrates a small, modular Java application with a service layer and file-based persistence. The project is intentionally simple so learners can easily explore and contribute.

Prerequisites
- JDK 17 or later
- Maven
- (macOS) If using JavaFX native artifacts, pass `-Djavafx.platform=mac` or `mac-aarch64` depending on your CPU.

Run locally

1. Build and run (macOS example for Apple Silicon):

```bash
cd mini-project
mvn -Djavafx.platform=mac-aarch64 clean javafx:run
```

2. Compile only:

```bash
mvn -Djavafx.platform=mac-aarch64 clean compile
```

Project layout
- `src/main/java/com/arjun/miniproject` — application code
- `src/main/resources/styles/dark-theme.css` — CSS theme
- `data/` — application data files (rooms, guests, bookings, services, payments, counters)

How to contribute
- Pick a small issue: add a unit test for `HotelService`, harden file I/O with atomic saves, or tweak the UI theme.
- Fork -> create a branch -> implement -> run locally -> open a PR.

Suggested issues to open (good first issues)
- Add unit tests for `HotelService.bookRoom()` and `checkout()`.
- Replace `System.out` error messages with structured logging (SLF4J).
- Make `FileStorageManager` write files atomically and keep a backup.
- Add a simple GitHub Action that runs `mvn -Djavafx.platform=mac-aarch64 -q test` on PRs.

Contact / Maintainer
- Open an issue or pull request on GitHub: https://github.com/ArjunMittal12/HOTEL-MANAGEMENT-SYSTEM

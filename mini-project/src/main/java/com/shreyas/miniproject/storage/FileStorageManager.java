package com.oswin.miniproject.storage;

import com.oswin.miniproject.models.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * FileStorageManager handles all file I/O operations.
 * Uses try-with-resources for file I/O.
 */
public class FileStorageManager {

    private static final String DATA_DIR = "data";
    private static final String ROOMS_FILE = DATA_DIR + "/rooms.txt";
    private static final String GUESTS_FILE = DATA_DIR + "/guests.txt";
    private static final String BOOKINGS_FILE = DATA_DIR + "/bookings.txt";
    private static final String SERVICES_FILE = DATA_DIR + "/services.txt";
    private static final String PAYMENTS_FILE = DATA_DIR + "/payments.txt";
    private static final String COUNTERS_FILE = DATA_DIR + "/counters.txt";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
    }

    // --- ROOMS ---
    public synchronized void saveRooms(List<Room> rooms) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ROOMS_FILE))) {
            for (Room room : rooms) {
                writer.write(room.getClass().getSimpleName() + "|" + room.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    public List<Room> loadRooms() {
        List<Room> rooms = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ROOMS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                String className = parts[0];
                String data = parts[1];
                Room room = parseRoom(className, data);
                if (room != null) rooms.add(room);
            }
        } catch (IOException e) {
            System.out.println("Rooms file not found. Starting fresh.");
        }
        return rooms;
    }

    private Room parseRoom(String className, String data) {
        String[] f = data.split(",");
        int roomNo = Integer.parseInt(f[0]);
        double price = Double.parseDouble(f[2]);

        Room room = null;
        switch (className) {
            case "StandardRoom":
                room = new StandardRoom(roomNo, price);
                break;
            case "DeluxeRoom":
                room = new DeluxeRoom(roomNo, price);
                break;
            case "SuiteRoom":
                room = new SuiteRoom(roomNo, price);
                break;
        }
        if (room != null) {
            room.setAvailable(f[3].equals("AVAILABLE"));
        }
        return room;
    }

    // --- GUESTS ---
    public synchronized void saveGuests(List<Guest> guests) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(GUESTS_FILE))) {
            for (Guest guest : guests) {
                writer.write(guest.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving guests: " + e.getMessage());
        }
    }

    public List<Guest> loadGuests() {
        List<Guest> guests = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GUESTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                guests.add(Guest.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Guests file not found. Starting fresh.");
        }
        return guests;
    }

    // --- BOOKINGS ---
    public synchronized void saveBookings(List<Booking> bookings) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BOOKINGS_FILE))) {
            for (Booking booking : bookings) {
                writer.write(booking.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    public List<Booking> loadBookings() {
        List<Booking> bookings = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(BOOKINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                bookings.add(Booking.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Bookings file not found. Starting fresh.");
        }
        return bookings;
    }

    // --- SERVICES (linked to bookings) ---
    public synchronized void saveServices(Map<Integer, List<BillEntry>> servicesMap) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SERVICES_FILE))) {
            for (Map.Entry<Integer, List<BillEntry>> entry : servicesMap.entrySet()) {
                int bookingId = entry.getKey();
                for (BillEntry billEntry : entry.getValue()) {
                    writer.write(bookingId + "|" + billEntry.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving services: " + e.getMessage());
        }
    }

    public Map<Integer, List<BillEntry>> loadServices() {
        Map<Integer, List<BillEntry>> servicesMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SERVICES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split("\\|");
                int bookingId = Integer.parseInt(parts[0]);
                BillEntry entry = BillEntry.fromFileString(parts[1]);
                servicesMap.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(entry);
            }
        } catch (IOException e) {
            System.out.println("Services file not found. Starting fresh.");
        }
        return servicesMap;
    }

    // --- PAYMENTS ---
    public synchronized void savePayments(List<Payment> payments) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PAYMENTS_FILE))) {
            for (Payment payment : payments) {
                writer.write(payment.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving payments: " + e.getMessage());
        }
    }

    public List<Payment> loadPayments() {
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PAYMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                payments.add(Payment.fromFileString(line));
            }
        } catch (IOException e) {
            System.out.println("Payments file not found. Starting fresh.");
        }
        return payments;
    }

    // --- COUNTERS ---
    public synchronized void saveCounters(int nextGuestId, int nextBookingId, int nextPaymentId) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(COUNTERS_FILE))) {
            writer.write("1," + nextGuestId + "," + nextBookingId + "," + nextPaymentId);
        } catch (IOException e) {
            System.err.println("Error saving counters: " + e.getMessage());
        }
    }

    public int[] loadCounters() {
        int[] counters = {1, 1, 1, 1};
        try (BufferedReader reader = new BufferedReader(new FileReader(COUNTERS_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                counters[0] = Integer.parseInt(parts[0]);
                counters[1] = Integer.parseInt(parts[1]);
                counters[2] = Integer.parseInt(parts[2]);
                counters[3] = Integer.parseInt(parts[3]);
            }
        } catch (IOException e) {
            System.out.println("Counters file not found. Starting with defaults: 1,1,1,1");
        }
        return counters;
    }
}

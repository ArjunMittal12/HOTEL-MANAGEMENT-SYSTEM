package com.arjun.miniproject.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Booking represents an active or historical booking.
 * Demonstrates Week 8 ArrayList and Iterator usage.
 */
public class Booking {

    private final int bookingId;
    private final int guestId;
    private final int roomNumber;
    private final String checkInDate;
    private final String checkOutDate;
    private final int numberOfNights;
    private final double roomCharges;
    private final List<BillEntry> serviceEntries; // Week 8 ArrayList
    private boolean active;

    public Booking(int bookingId, int guestId, int roomNumber,
                   String checkInDate, String checkOutDate, int numberOfNights, double roomCharges) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.roomCharges = roomCharges;
        this.serviceEntries = new ArrayList<>();
        this.active = true;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public boolean isActive() {
        return active;
    }

    public String getStatus() {
        return active ? "Active" : "Closed";
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // Service entry management
    public void addService(BillEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("BillEntry cannot be null.");
        }
        serviceEntries.add(entry);
    }

    public List<BillEntry> getServiceEntries() {
        return new ArrayList<>(serviceEntries);
    }

    /**
     * Calculate total service charges using Iterator (Week 8).
     */
    public double getTotalServiceCharges() {
        double total = 0.0;
        Iterator<BillEntry> iterator = serviceEntries.iterator();
        while (iterator.hasNext()) {
            BillEntry entry = iterator.next();
            total += entry.getTotalCost();
        }
        return total;
    }

    /**
     * Get grand total: room charges + service charges.
     */
    public double getGrandTotal() {
        return roomCharges + getTotalServiceCharges();
    }

    public String toFileString() {
        return String.format("%d,%d,%d,%s,%s,%d,%.2f,%s",
            bookingId,
            guestId,
            roomNumber,
            checkInDate,
            checkOutDate,
            numberOfNights,
            roomCharges,
            active ? "ACTIVE" : "CLOSED"
        );
    }

    public static Booking fromFileString(String line) {
        String[] parts = line.split(",");
        Booking booking = new Booking(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Integer.parseInt(parts[2]),
            parts[3],
            parts[4],
            Integer.parseInt(parts[5]),
            Double.parseDouble(parts[6])
        );
        booking.setActive(parts[7].equals("ACTIVE"));
        return booking;
    }
}


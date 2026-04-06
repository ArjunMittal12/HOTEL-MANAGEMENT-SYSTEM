package com.shreyas.miniproject.models;

/**
 * Abstract base class for hotel rooms.
 * Demonstrates Week 1 abstraction, encapsulation, and constructor overloading.
 */
public abstract class Room implements Amenities {

    private final int roomNumber;
    private final RoomType roomType;
    private double pricePerNight;
    private boolean available;

    // Constructor 1: with room number and type (uses default price)
    public Room(int roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = roomType.getBasePrice();
        this.available = true;
    }

    // Constructor 2: with custom price (demonstrating overloading)
    public Room(int roomNumber, RoomType roomType, double customPrice) {
        if (customPrice < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = customPrice;
        this.available = true;
    }

    /**
     * Abstract method for calculating room tariff.
     * Subclasses implement their own surcharge logic.
     */
    public abstract double calculateTariff(int nights);

    /**
     * Format room for file storage.
     */
    public String toFileString() {
        return String.format("%d,%s,%.2f,%s",
            roomNumber,
            roomType.name(),
            pricePerNight,
            available ? "AVAILABLE" : "OCCUPIED"
        );
    }

    // Getters and Setters with validation
    public int getRoomNumber() {
        return roomNumber;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public String getType() {
        return roomType.name();
    }

    public String getRoomClass() {
        return getClass().getSimpleName();
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        this.pricePerNight = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getWifi() {
        return hasWifi() ? "Yes" : "No";
    }

    public String getAc() {
        return hasAC() ? "Yes" : "No";
    }

    public String getBreakfast() {
        return hasBreakfast() ? "Yes" : "No";
    }

    public String getStatus() {
        return available ? "Available" : "Occupied";
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}


package com.shreyas.miniproject.models;

/**
 * Deluxe room: base price + 10% surcharge for WiFi and AC.
 * Demonstrates inheritance and polymorphism.
 */
public class DeluxeRoom extends Room {

    private static final double SURCHARGE_MULTIPLIER = 1.10;

    public DeluxeRoom(int roomNumber) {
        super(roomNumber, RoomType.DELUXE);
    }

    public DeluxeRoom(int roomNumber, double customPrice) {
        super(roomNumber, RoomType.DELUXE, customPrice);
    }

    @Override
    public double calculateTariff(int nights) {
        double baseTariff = getPricePerNight() * nights;
        return baseTariff * SURCHARGE_MULTIPLIER;
    }

    @Override
    public boolean hasWifi() {
        return true;
    }

    @Override
    public boolean hasAC() {
        return true;
    }

    @Override
    public boolean hasBreakfast() {
        return false;
    }
}


package com.oswin.miniproject.models;

/**
 * Standard room: simple price × nights, no amenities.
 * Demonstrates Week 1 single inheritance and method overriding.
 */
public class StandardRoom extends Room {

    public StandardRoom(int roomNumber) {
        super(roomNumber, RoomType.STANDARD);
    }

    public StandardRoom(int roomNumber, double customPrice) {
        super(roomNumber, RoomType.STANDARD, customPrice);
    }

    @Override
    public double calculateTariff(int nights) {
        return getPricePerNight() * nights;
    }

    @Override
    public boolean hasWifi() {
        return false;
    }

    @Override
    public boolean hasAC() {
        return false;
    }

    @Override
    public boolean hasBreakfast() {
        return false;
    }
}

package com.oswin.miniproject.models;

/**
 * Suite room: base price + 20% surcharge for full amenities.
 * Demonstrates the full inheritance hierarchy.
 */
public class SuiteRoom extends Room {

    private static final double SURCHARGE_MULTIPLIER = 1.20;

    public SuiteRoom(int roomNumber) {
        super(roomNumber, RoomType.SUITE);
    }

    public SuiteRoom(int roomNumber, double customPrice) {
        super(roomNumber, RoomType.SUITE, customPrice);
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
        return true;
    }
}

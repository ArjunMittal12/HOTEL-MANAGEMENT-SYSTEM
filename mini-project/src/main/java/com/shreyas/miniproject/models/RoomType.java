package com.oswin.miniproject.models;

public enum RoomType {
    STANDARD(1500),
    DELUXE(3200),
    SUITE(5200);

    private final double basePrice;

    RoomType(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double calculateCost(int nights) {
        if (nights <= 0) {
            throw new IllegalArgumentException("Nights must be positive.");
        }
        return basePrice * nights;
    }
}

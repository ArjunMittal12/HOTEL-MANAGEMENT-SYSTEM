package com.shreyas.miniproject.models;

/**
 * ServiceItem enum for itemised billing.
 * Demonstrates Week 2 enum with constructor and methods.
 */
public enum ServiceItem {
    BREAKFAST("Breakfast", 200.0),
    LUNCH("Lunch", 300.0),
    DINNER("Dinner", 400.0),
    ROOM_SERVICE("Room Service", 150.0),
    LAUNDRY("Laundry", 100.0),
    SPA("Spa", 500.0),
    PARKING("Parking", 200.0),
    MINIBAR("Minibar Drinks", 250.0),
    EXTRA_BED("Extra Bed", 500.0),
    AIRPORT_TRANSFER("Airport Transfer", 600.0);

    private final String displayName;
    private final double unitPrice;

    ServiceItem(String displayName, double unitPrice) {
        this.displayName = displayName;
        this.unitPrice = unitPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double calculateCost(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        return unitPrice * quantity;
    }

    @Override
    public String toString() {
        return String.format("%s (₹%.2f)", displayName, unitPrice);
    }
}


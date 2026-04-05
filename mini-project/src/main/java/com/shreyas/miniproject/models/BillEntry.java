package com.oswin.miniproject.models;

/**
 * BillEntry represents a line item in a booking's bill.
 * Demonstrates Week 2 wrapper classes and autoboxing/unboxing.
 */
public class BillEntry {

    private final ServiceItem service;
    private final Integer quantity;      // Wrapper class
    private final Double totalCost;      // Wrapper class
    private final String addedOn;

    public BillEntry(ServiceItem service, Integer quantity, String addedOn) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null.");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive.");
        }
        this.service = service;
        this.quantity = quantity;
        // Autoboxing: unbox quantity and rebox result
        this.totalCost = service.calculateCost(quantity);
        this.addedOn = addedOn;
    }

    public ServiceItem getService() {
        return service;
    }

    public String getServiceName() {
        return service.getDisplayName();
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public String getAddedOn() {
        return addedOn;
    }

    public String toFileString() {
        return String.format("%s,%d,%.2f,%s",
            service.name(),
            quantity,
            totalCost,
            addedOn
        );
    }

    public static BillEntry fromFileString(String line) {
        String[] parts = line.split(",");
        ServiceItem service = ServiceItem.valueOf(parts[0]);
        Integer quantity = Integer.valueOf(parts[1]);
        String addedOn = parts[3];
        return new BillEntry(service, quantity, addedOn);
    }
}

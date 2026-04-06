package com.shreyas.miniproject.models;

/**
 * Payment record for a booking.
 * Demonstrates Week 2 wrapper classes (Double).
 */
public class Payment {

    private final int paymentId;
    private final int bookingId;
    private final Double amount;              // Wrapper class
    private final PaymentMethod paymentMethod;
    private final String paymentDate;
    private boolean paid;

    public Payment(int paymentId, int bookingId, Double amount,
                   PaymentMethod paymentMethod, String paymentDate) {
        if (amount == null || amount < 0) {
            throw new IllegalArgumentException("Amount must be non-negative.");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("Payment method is required.");
        }
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.paid = false;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String toFileString() {
        return String.format("%d,%d,%.2f,%s,%s,%s",
            paymentId,
            bookingId,
            amount,
            paymentMethod.name(),
            paymentDate,
            paid ? "PAID" : "PENDING"
        );
    }

    public static Payment fromFileString(String line) {
        String[] parts = line.split(",");
        Payment payment = new Payment(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1]),
            Double.parseDouble(parts[2]),
            PaymentMethod.valueOf(parts[3]),
            parts[4]
        );
        payment.setPaid(parts[5].equals("PAID"));
        return payment;
    }
}


package com.oswin.miniproject.models;

/**
 * Guest represents a hotel guest with contact information.
 * Demonstrates Week 1 encapsulation and validation.
 */
public class Guest {

    private final int guestId;
    private final String name;
    private final String contactNumber;
    private final String email;
    private int allocatedRoomNumber;  // -1 if not allocated
    private String checkInDate;
    private String checkOutDate;

    public Guest(int guestId, String name, String contactNumber, String email) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest name is required.");
        }
        if (contactNumber == null || !contactNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Contact must be exactly 10 digits.");
        }
        this.guestId = guestId;
        this.name = name.trim();
        this.contactNumber = contactNumber;
        this.email = email;
        this.allocatedRoomNumber = -1;
        this.checkInDate = null;
        this.checkOutDate = null;
    }

    // Getters
    public int getGuestId() {
        return guestId;
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public int getAllocatedRoomNumber() {
        return allocatedRoomNumber;
    }

    public String getRoomDisplay() {
        return allocatedRoomNumber == -1 ? "-" : String.valueOf(allocatedRoomNumber);
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public String getCheckInDisplay() {
        return checkInDate == null || checkInDate.isEmpty() ? "-" : checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public String getStatus() {
        return allocatedRoomNumber == -1 ? "Checked Out" : "Checked In";
    }

    // Setters with validation
    public void setAllocatedRoomNumber(int roomNumber) {
        this.allocatedRoomNumber = roomNumber;
    }

    public void setCheckInDate(String date) {
        this.checkInDate = date;
    }

    public void setCheckOutDate(String date) {
        this.checkOutDate = date;
    }

    public String toFileString() {
        return String.format("%d,%s,%s,%s,%d,%s,%s",
            guestId,
            name,
            contactNumber,
            email,
            allocatedRoomNumber,
            checkInDate == null ? "" : checkInDate,
            checkOutDate == null ? "" : checkOutDate
        );
    }

    public static Guest fromFileString(String line) {
        // Keep trailing empty values so rows like "..., -1,," are parsed as 7 fields.
        String[] parts = line.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid guest record: " + line);
        }

        Guest guest = new Guest(
            Integer.parseInt(parts[0]),
            parts[1],
            parts[2],
            parts[3]
        );
        if (parts.length > 4 && !parts[4].isEmpty()) {
            guest.setAllocatedRoomNumber(Integer.parseInt(parts[4]));
        }
        if (parts.length > 5 && !parts[5].isEmpty()) {
            guest.setCheckInDate(parts[5]);
        }
        if (parts.length > 6 && !parts[6].isEmpty()) {
            guest.setCheckOutDate(parts[6]);
        }
        return guest;
    }
}


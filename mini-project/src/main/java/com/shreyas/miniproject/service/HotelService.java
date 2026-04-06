package com.shreyas.miniproject.service;

import com.shreyas.miniproject.models.*;
import com.shreyas.miniproject.storage.FileStorageManager;
import java.time.LocalDate;
import java.util.*;


public class HotelService {

    private final FileStorageManager fileStorage = new FileStorageManager();
    
    private final List<Room> rooms = new ArrayList<>();
    private final List<Guest> guests = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private final List<Payment> payments = new ArrayList<>();
    private final Map<Integer, Guest> roomToGuestMap = new HashMap<>();  // Week 8 HashMap
    private final Map<Integer, List<BillEntry>> bookingServices = new HashMap<>();
    
    // ID counters
    private int nextGuestId = 1;
    private int nextBookingId = 1;
    private int nextPaymentId = 1;

    public HotelService() {
        loadAllData();
    }

    public synchronized void loadAllData() {
        rooms.clear();
        guests.clear();
        bookings.clear();
        payments.clear();
        roomToGuestMap.clear();
        bookingServices.clear();

        rooms.addAll(fileStorage.loadRooms());
        guests.addAll(fileStorage.loadGuests());
        bookings.addAll(fileStorage.loadBookings());
        payments.addAll(fileStorage.loadPayments());
        
        Map<Integer, List<BillEntry>> servicesMap = fileStorage.loadServices();
        bookingServices.putAll(servicesMap);
        
        int[] counters = fileStorage.loadCounters();
        nextGuestId = counters[1];
        nextBookingId = counters[2];
        nextPaymentId = counters[3];
        
        for (Booking booking : bookings) {
            if (booking.isActive()) {
                Guest guest = findGuestById(booking.getGuestId());
                if (guest != null) {
                    roomToGuestMap.put(booking.getRoomNumber(), guest);
                }
            }
        }
    }

    public synchronized void saveAllData() {
        fileStorage.saveRooms(rooms);
        fileStorage.saveGuests(guests);
        fileStorage.saveBookings(bookings);
        fileStorage.savePayments(payments);
        fileStorage.saveServices(bookingServices);
        fileStorage.saveCounters(nextGuestId, nextBookingId, nextPaymentId);
    }

    // Room operations

    public synchronized void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        // Check for duplicate room number
        for (Room r : rooms) {
            if (r.getRoomNumber() == room.getRoomNumber()) {
                throw new IllegalArgumentException("Room " + room.getRoomNumber() + " already exists.");
            }
        }
        rooms.add(room);
        saveAllData();
    }

    public List<Room> getAllRooms() {
        List<Room> copy = new ArrayList<>(rooms);
        Collections.sort(copy, (r1, r2) -> Integer.compare(r1.getRoomNumber(), r2.getRoomNumber()));
        return copy;
    }

    public List<Room> getAvailableRooms() {
        List<Room> available = new ArrayList<>();
        Iterator<Room> iterator = rooms.iterator();  
        while (iterator.hasNext()) {
            Room room = iterator.next();
            if (room.isAvailable()) {
                available.add(room);
            }
        }
        Collections.sort(available, (r1, r2) -> Double.compare(r1.getPricePerNight(), r2.getPricePerNight()));
        return available;
    }

    public Room findRoomByNumber(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }

    public synchronized void deleteRoom(int roomNumber) {
        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }
        if (!room.isAvailable()) {
            throw new IllegalStateException("Cannot delete an occupied room.");
        }
        rooms.remove(room);
        saveAllData();
    }

    // Guest operations

    public synchronized int addGuest(String name, String contact, String email) {
        int guestId = nextGuestId++;
        Guest guest = new Guest(guestId, name, contact, email);
        guests.add(guest);
        saveAllData();
        return guestId;
    }

    public List<Guest> getAllGuests() {
        List<Guest> copy = new ArrayList<>(guests);
        Collections.sort(copy, (g1, g2) -> Integer.compare(g1.getGuestId(), g2.getGuestId()));
        return copy;
    }

    public Guest findGuestById(int guestId) {
        for (Guest guest : guests) {
            if (guest.getGuestId() == guestId) {
                return guest;
            }
        }
        return null;
    }

    public synchronized void deleteGuest(int guestId) {
        Guest guest = findGuestById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }
        if (guest.getAllocatedRoomNumber() != -1) {
            throw new IllegalStateException("Cannot delete a checked-in guest.");
        }
        guests.remove(guest);
        saveAllData();
    }

    // Booking operations

    
    public synchronized Booking bookRoom(int guestId, int roomNumber, String checkInDate,String checkOutDate, int numberOfNights) {
        Guest guest = findGuestById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest not found.");
        }

        Room room = findRoomByNumber(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room not found.");
        }

        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is not available.");
        }

        double roomCharges = room.calculateTariff(numberOfNights);

        int bookingId = nextBookingId++;
        Booking booking = new Booking(bookingId, guestId, roomNumber,
                                      checkInDate, checkOutDate, numberOfNights, roomCharges);
        bookings.add(booking);
        room.setAvailable(false);
        guest.setAllocatedRoomNumber(roomNumber);
        guest.setCheckInDate(checkInDate);
        roomToGuestMap.put(roomNumber, guest);
        
        saveAllData();
        return booking;
    }

    public synchronized void addServiceToBooking(int bookingId, ServiceItem service, int quantity) {
        Booking booking = null;
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId && b.isActive()) {
                booking = b;
                break;
            }
        }
        if (booking == null) {
            throw new IllegalArgumentException("Active booking not found.");
        }

        BillEntry entry = new BillEntry(service, quantity, LocalDate.now().toString());
        booking.addService(entry);
        bookingServices.computeIfAbsent(bookingId, k -> new ArrayList<>()).add(entry);
        
        saveAllData();
    }

    public synchronized Booking checkout(int bookingId) {
        Booking booking = null;
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId && b.isActive()) {
                booking = b;
                break;
            }
        }
        if (booking == null) {
            throw new IllegalArgumentException("Active booking not found.");
        }

        Room room = findRoomByNumber(booking.getRoomNumber());
        if (room != null) {
            room.setAvailable(true);
        }

        Guest guest = findGuestById(booking.getGuestId());
        if (guest != null) {
            guest.setCheckOutDate(LocalDate.now().toString());
            guest.setAllocatedRoomNumber(-1);
        }

        booking.setActive(false);

        int paymentId = nextPaymentId++;
        Payment payment = new Payment(paymentId, bookingId, booking.getGrandTotal(), PaymentMethod.CARD, LocalDate.now().toString());
        payment.setPaid(true);
        payments.add(payment);

        roomToGuestMap.remove(booking.getRoomNumber());
        
        saveAllData();
        return booking;
    }

    // Operations to get details

    public List<Booking> getActiveBookings() {
        List<Booking> active = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.isActive()) {
                active.add(booking);
            }
        }
        return active;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }
}


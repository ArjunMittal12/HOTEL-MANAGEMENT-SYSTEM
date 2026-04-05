package com.oswin.miniproject.models;

/**
 * Interface defining amenities available in different room types.
 * Demonstrates Week 1 interface-based design.
 */
public interface Amenities {
    boolean hasWifi();
    boolean hasAC();
    boolean hasBreakfast();

    default String getAmenitiesSummary() {
        StringBuilder sb = new StringBuilder();
        if (hasWifi()) sb.append("WiFi, ");
        if (hasAC()) sb.append("AC, ");
        if (hasBreakfast()) sb.append("Breakfast, ");
        String result = sb.toString();
        return result.isEmpty() ? "None" : result.substring(0, result.length() - 2);
    }
}

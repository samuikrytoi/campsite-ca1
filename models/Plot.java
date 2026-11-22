package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a Plot/Pitch within a Camping Area.
 * Can be an empty pitch (for tents/caravans) or a static dwelling (bungalow, mobile home, etc.).
 * Implements Serializable for saving/loading the entire system.
 */
public class Plot implements Serializable {
    private static final long serialVersionUID = 1L;

    private String plotId;                 // Unique identifier for the plot (e.g. "P-01")
    private String type;                   // "empty pitch", "bungalow", "static mobile home", "apartment"
    private boolean hasPower;              // Always true for static; optional for empty pitches
    private int bedrooms;                  // Number of bedrooms (0 if empty pitch)[cite: 1]
    private int beds;                      // Number of beds (0 if empty pitch)[cite: 1]
    private double sizeSquareMetres;       // Area size in m²[cite: 1]
    private double costPerNight;           // Price per night in €[cite: 1]
    private String photoUrl;               // Image URL for GUI display[cite: 1]

    // Custom Linked Lists instead of built-in Java collections
    private MyList<String> facilities;     // e.g. "Air Conditioning", "BBQ Grill"[cite: 1]
    private MyList<Booking> bookings;      // All bookings assigned to this plot[cite: 1]

    /**
     * Full Constructor for Plot.
     */
    public Plot(String plotId, String type, boolean hasPower, int bedrooms, int beds,
                double sizeSquareMetres, double costPerNight, String photoUrl) {
        this.plotId = plotId;
        this.type = type;
        this.hasPower = hasPower;
        this.bedrooms = bedrooms;
        this.beds = beds;
        this.sizeSquareMetres = sizeSquareMetres;
        this.costPerNight = costPerNight;
        this.photoUrl = photoUrl;

        // Initialize our custom data structures
        this.facilities = new MyList<>();
        this.bookings = new MyList<>();
    }

    /**
     * Checks if this plot is available for the given dates.
     * Iterates through all existing bookings using our custom MyList.
     * 
     * @param start Requested start date
     * @param end Requested end date
     * @return true if plot is free, false if occupied[cite: 1]
     */
    public boolean isAvailable(LocalDate start, LocalDate end) {
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            if (b.overlapsWith(start, end)) {
                return false; // Found an overlapping booking
            }
        }
        return true; // No clashes found
    }

    /**
     * Adds a booking to this plot.
     * 
     * @param booking The booking to add
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    /**
     * Removes a booking from this plot by its booking ID.
     * 
     * @param bookingId The ID of the booking to remove[cite: 2]
     * @return true if removed, false otherwise
     */
    public boolean removeBooking(String bookingId) {
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            if (b.getBookingId().equalsIgnoreCase(bookingId)) {
                return bookings.remove(b);
            }
        }
        return false;
    }

    /**
     * Adds an extra facility string to the plot (e.g. "WiFi", "Air Conditioning").
     */
    public void addFacility(String facility) {
        facilities.add(facility);
    }

    // --- Getters and Setters ---

    public String getPlotId() { return plotId; }
    public void setPlotId(String plotId) { this.plotId = plotId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isHasPower() { return hasPower; }
    public void setHasPower(boolean hasPower) { this.hasPower = hasPower; }

    public int getBedrooms() { return bedrooms; }
    public void setBedrooms(int bedrooms) { this.bedrooms = bedrooms; }

    public int getBeds() { return beds; }
    public void setBeds(int beds) { this.beds = beds; }

    public double getSizeSquareMetres() { return sizeSquareMetres; }
    public void setSizeSquareMetres(double sizeSquareMetres) { this.sizeSquareMetres = sizeSquareMetres; }

    public double getCostPerNight() { return costPerNight; }
    public void setCostPerNight(double costPerNight) { this.costPerNight = costPerNight; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public MyList<String> getFacilities() { return facilities; }
    public MyList<Booking> getBookings() { return bookings; }

    @Override
    public String toString() {
        return "Plot " + plotId + " [" + type + "] - €" + costPerNight + "/night";
    }
}
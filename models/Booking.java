package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a Booking in the system.
 * Links a Guest to a specific Plot/Pitch for a specific date range.
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookingId;      // Unique identifier for the booking (e.g., "B-101")
    private int guestId;           // Which guest made the booking
    private String campingAreaId;  // The area where the plot is located
    private String plotId;         // The specific assigned plot/pitch
    private LocalDate startDate;   // Start date of the vacation
    private LocalDate endDate;     // End date of the vacation
    private double totalCost;      // Total calculated cost for the stay

    /**
     * Constructor to create a new Booking.
     */
    public Booking(String bookingId, int guestId, String campingAreaId, 
                   String plotId, LocalDate startDate, LocalDate endDate, double totalCost) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.campingAreaId = campingAreaId;
        this.plotId = plotId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    // --- Getters and Setters ---

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getCampingAreaId() { return campingAreaId; }
    public void setCampingAreaId(String campingAreaId) { this.campingAreaId = campingAreaId; }

    public String getPlotId() { return plotId; }
    public void setPlotId(String plotId) { this.plotId = plotId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    /**
     * Helper method to check if a requested date range overlaps with this booking.
     * Useful for the "Smart Find" and booking logic.
     * 
     * @param checkStart The start date to check
     * @param checkEnd The end date to check
     * @return true if dates overlap, false otherwise
     */
    public boolean overlapsWith(LocalDate checkStart, LocalDate checkEnd) {
        // An overlap occurs if the requested start is BEFORE this booking ends
        // AND the requested end is AFTER this booking starts.
        return checkStart.isBefore(this.endDate) && checkEnd.isAfter(this.startDate);
    }

    @Override
    public String toString() {
        return "Booking " + bookingId + ": Plot " + plotId + " from " + startDate + " to " + endDate;
    }
}
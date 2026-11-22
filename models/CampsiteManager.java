package models;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Core management class for the entire Campsite Management System.
 * Coordinates Campsites, Areas, Plots, Guests, and Bookings.
 * Completely avoids built-in Java Collections and raw arrays.
 */
public class CampsiteManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private MyList<Campsite> campsites; // Custom list of all registered campsites
    private MyList<Guest> guests;       // Custom list of all registered guests
    private int nextBookingIdCounter;   // Auto-increment counter for unique booking IDs

    /**
     * Initializes an empty campsite management system.
     */
    public CampsiteManager() {
        this.campsites = new MyList<>();
        this.guests = new MyList<>();
        this.nextBookingIdCounter = 1001;
    }

    // =========================================================================
    // 1. Campsite & Area Management
    // =========================================================================

    public boolean addCampsite(Campsite campsite) {
        if (campsite == null || findCampsite(campsite.getName()) != null) {
            return false; // Duplicate campsite name
        }
        campsites.add(campsite);
        return true;
    }

    public Campsite findCampsite(String name) {
        if (name == null) return null;
        for (int i = 0; i < campsites.size(); i++) {
            Campsite c = campsites.get(i);
            if (c.getName().equalsIgnoreCase(name.trim())) {
                return c;
            }
        }
        return null;
    }

    public boolean addCampingArea(String campsiteName, CampingArea area) {
        Campsite campsite = findCampsite(campsiteName);
        if (campsite != null) {
            return campsite.addCampingArea(area); // Enforces unique area identifier within campsite
        }
        return false;
    }

    public boolean addPlot(String campsiteName, String areaId, Plot plot) {
        Campsite campsite = findCampsite(campsiteName);
        if (campsite != null) {
            CampingArea area = campsite.findCampingArea(areaId);
            if (area != null) {
                return area.addPlot(plot);
            }
        }
        return false;
    }

    // =========================================================================
    // 2. Guest Management
    // =========================================================================

    public boolean addGuest(Guest guest) {
        if (guest == null || findGuest(guest.getGuestId()) != null) {
            return false; // Duplicate guest ID
        }
        guests.add(guest);
        return true;
    }

    public Guest findGuest(int guestId) {
        for (int i = 0; i < guests.size(); i++) {
            Guest g = guests.get(i);
            if (g.getGuestId() == guestId) {
                return g;
            }
        }
        return null;
    }

    // =========================================================================
    // 3. Booking Logic
    // =========================================================================

    /**
     * Adds a booking to a specific camping area after matching all guest criteria.
     * Searches all plots in the area to find one that is available and matches preferences.
     */
    public Booking addBooking(String campsiteName, String areaId, int guestId,
                              LocalDate start, LocalDate end, String preferredType,
                              boolean needPower, double minSize, int minBedrooms, double budget) {

        Campsite campsite = findCampsite(campsiteName);
        if (campsite == null) return null;

        CampingArea area = campsite.findCampingArea(areaId);
        if (area == null) return null;

        Guest guest = findGuest(guestId);
        if (guest == null) return null;

        long nights = ChronoUnit.DAYS.between(start, end);
        if (nights <= 0) return null; // Invalid date range

        // Search for an available plot meeting all requirements
        Plot selectedPlot = null;
        for (int i = 0; i < area.getPlots().size(); i++) {
            Plot plot = area.getPlots().get(i);

            // 1. Date availability check
            if (!plot.isAvailable(start, end)) continue;

            // 2. Type preference check (case-insensitive substring or match)
            if (preferredType != null && !preferredType.isEmpty()) {
                if (!plot.getType().equalsIgnoreCase(preferredType)) continue;
            }

            // 3. Power requirement
            if (needPower && !plot.isHasPower()) continue;

            // 4. Size & bedroom requirements
            if (plot.getSizeSquareMetres() < minSize) continue;
            if (plot.getBedrooms() < minBedrooms) continue;

            // 5. Budget constraint
            double totalCost = nights * plot.getCostPerNight();
            if (budget > 0 && totalCost > budget) continue;

            selectedPlot = plot;
            break; // Found suitable plot
        }

        if (selectedPlot == null) {
            return null; // No matching available plot found
        }

        // Create and register booking
        String bookingId = "BK-" + (nextBookingIdCounter++);
        double cost = nights * selectedPlot.getCostPerNight();
        Booking newBooking = new Booking(bookingId, guestId, areaId, selectedPlot.getPlotId(), start, end, cost);
        selectedPlot.addBooking(newBooking);

        return newBooking;
    }

    /**
     * Cancels an existing booking across the system.
     */
    public boolean cancelBooking(String bookingId) {
        if (bookingId == null) return false;

        for (int i = 0; i < campsites.size(); i++) {
            Campsite c = campsites.get(i);
            for (int j = 0; j < c.getAreas().size(); j++) {
                CampingArea area = c.getAreas().get(j);
                for (int k = 0; k < area.getPlots().size(); k++) {
                    Plot p = area.getPlots().get(k);
                    if (p.removeBooking(bookingId)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // =========================================================================
    // 4. Smart Find Facility
    // =========================================================================

    /**
     * Smart find: Finds a suitable campsite, area, and plot matching vacation requirements.
     * Returns a string summary of the best recommendation, or null if nothing fits.
     */
    public String smartFind(int minStars, double maxBudget, LocalDate start, LocalDate end,
                            String requiredFacility, String preferredType) {
        long nights = ChronoUnit.DAYS.between(start, end);
        if (nights <= 0) return null;

        for (int i = 0; i < campsites.size(); i++) {
            Campsite c = campsites.get(i);
            if (c.getStars() < minStars) continue;
            if (requiredFacility != null && !requiredFacility.isEmpty() && !c.hasFacility(requiredFacility)) {
                continue;
            }

            for (int j = 0; j < c.getAreas().size(); j++) {
                CampingArea area = c.getAreas().get(j);
                for (int k = 0; k < area.getPlots().size(); k++) {
                    Plot plot = area.getPlots().get(k);

                    if (preferredType != null && !preferredType.isEmpty()) {
                        if (!plot.getType().equalsIgnoreCase(preferredType)) continue;
                    }

                    double totalCost = nights * plot.getCostPerNight();
                    if (maxBudget > 0 && totalCost > maxBudget) continue;

                    if (plot.isAvailable(start, end)) {
                        return "Match Found!\nCampsite: " + c.getName() + " (" + c.getStars() + " Stars)\n"
                                + "Area: " + area.getAreaId() + "\n"
                                + "Plot: " + plot.getPlotId() + " (" + plot.getType() + ")\n"
                                + "Total Cost: €" + totalCost + " for " + nights + " nights";
                    }
                }
            }
        }
        return null; // No match found
    }

    // =========================================================================
    // 5. Remove Plot with Auto-Reassignment
    // =========================================================================

    /**
     * Removes a plot. Any pending bookings are moved/reassigned to another
     * suitable plot within any camping area of the SAME campsite.
     */
    public boolean removePlotWithReassignment(String campsiteName, String areaId, String plotId) {
        Campsite campsite = findCampsite(campsiteName);
        if (campsite == null) return false;

        CampingArea sourceArea = campsite.findCampingArea(areaId);
        if (sourceArea == null) return false;

        Plot targetPlot = sourceArea.findPlot(plotId);
        if (targetPlot == null) return false;

        MyList<Booking> bookingsToMove = targetPlot.getBookings();

        // Try to reassign each booking to another plot in the same campsite
        for (int i = 0; i < bookingsToMove.size(); i++) {
            Booking b = bookingsToMove.get(i);
            boolean reassigned = false;

            for (int j = 0; j < campsite.getAreas().size() && !reassigned; j++) {
                CampingArea area = campsite.getAreas().get(j);
                for (int k = 0; k < area.getPlots().size(); k++) {
                    Plot altPlot = area.getPlots().get(k);

                    // Don't reassign to the plot being deleted
                    if (altPlot.getPlotId().equalsIgnoreCase(plotId)) continue;

                    // Must match type and date availability
                    if (altPlot.getType().equalsIgnoreCase(targetPlot.getType())
                            && altPlot.isAvailable(b.getStartDate(), b.getEndDate())) {

                        b.setPlotId(altPlot.getPlotId());
                        b.setCampingAreaId(area.getAreaId());
                        altPlot.addBooking(b);
                        reassigned = true;
                        break;
                    }
                }
            }

            if (!reassigned) {
                // If any booking cannot be reassigned, operation aborted for data integrity
                return false;
            }
        }

        // Successfully reassigned all bookings; now safely remove plot
        return sourceArea.removePlot(plotId);
    }

    // =========================================================================
    // 6. Persistence & Reset
    // =========================================================================

    /**
     * Clears all system data (Reset facility).
     */
    public void reset() {
        campsites.clear();
        guests.clear();
        nextBookingIdCounter = 1001;
    }

    /**
     * Saves system state to a binary file using Java Object Serialization.
     */
    public void saveToFile(String filepath) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filepath))) {
            oos.writeObject(this);
        }
    }

    /**
     * Loads system state from a binary file.
     */
    public static CampsiteManager loadFromFile(String filepath) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
            return (CampsiteManager) ois.readObject();
        }
    }

    // =========================================================================
    // 7. Getters for GUI Integration
    // =========================================================================

    public MyList<Campsite> getCampsites() { return campsites; }
    public MyList<Guest> getGuests() { return guests; }
}
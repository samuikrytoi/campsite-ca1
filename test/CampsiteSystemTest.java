package test;

import models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.time.LocalDate;

/**
 * Unit test suite demonstrating test-driven validation of custom data structures
 * and core business logic for the Campsite Management System.
 */
public class CampsiteSystemTest {

    private CampsiteManager manager;

    @BeforeEach
    public void setUp() {
        manager = new CampsiteManager();

        // Set up base campsite, area, plot, and guest
        Campsite site = new Campsite("Sunny Valley", "Waterford Road", 4, "contact@sunny.ie");
        site.addFacility("Swimming Pool");
        site.addFacility("WiFi");
        manager.addCampsite(site);

        CampingArea area = new CampingArea("Area-A", "Quiet lakeside zone");
        manager.addCampingArea("Sunny Valley", area);

        Plot plot1 = new Plot("P1", "bungalow", true, 2, 4, 45.0, 80.0, "http://photo.com/1");
        manager.addPlot("Sunny Valley", "Area-A", plot1);

        Guest guest = new Guest(1, "John Doe", "1995-05-12", "Dublin", "0871234567", "john@mail.com", "VIP");
        manager.addGuest(guest);
    }

    /**
     * Test 1: Verifies that our custom generic Singly Linked List (MyList) behaves correctly
     * without relying on Java's built-in collections.
     */
    @Test
    public void testCustomMyListOperations() {
        MyList<String> list = new MyList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());

        list.add("First");
        list.add("Second");
        list.add("Third");

        assertEquals(3, list.size());
        assertEquals("First", list.get(0));
        assertEquals("Second", list.get(1));
        assertTrue(list.contains("Third"));

        boolean removed = list.remove("Second");
        assertTrue(removed);
        assertEquals(2, list.size());
        assertEquals("Third", list.get(1));
    }

    /**
     * Test 2: Ensures that duplicate camping area identifiers within the same campsite
     * are strictly rejected as required by the specification.
     */
    @Test
    public void testCampingAreaIdentifierUniqueness() {
        CampingArea duplicateArea = new CampingArea("Area-A", "Duplicate area id");
        boolean added = manager.addCampingArea("Sunny Valley", duplicateArea);

        assertFalse(added, "System must reject adding an area with an existing ID in the same campsite");
    }

    /**
     * Test 3: Verifies date conflict detection so two bookings cannot occupy
     * the same plot during overlapping dates.
     */
    @Test
    public void testPlotAvailabilityAndOverlap() {
        Campsite site = manager.findCampsite("Sunny Valley");
        CampingArea area = site.findCampingArea("Area-A");
        Plot plot = area.findPlot("P1");

        LocalDate start1 = LocalDate.of(2026, 6, 1);
        LocalDate end1 = LocalDate.of(2026, 6, 10);
        Booking b1 = new Booking("BK-1", 1, "Area-A", "P1", start1, end1, 720.0);
        plot.addBooking(b1);

        // Clashing date range: June 5 to June 15 overlaps with June 1 to June 10
        LocalDate clashStart = LocalDate.of(2026, 6, 5);
        LocalDate clashEnd = LocalDate.of(2026, 6, 15);
        assertFalse(plot.isAvailable(clashStart, clashEnd));

        // Non-clashing date range: June 11 to June 20
        LocalDate freeStart = LocalDate.of(2026, 6, 11);
        LocalDate freeEnd = LocalDate.of(2026, 6, 20);
        assertTrue(plot.isAvailable(freeStart, freeEnd));
    }

    /**
     * Test 4: Verifies the creation of a booking matching specific criteria (budget, type, power).
     */
    @Test
    public void testSuccessfulBookingCreation() {
        LocalDate start = LocalDate.of(2026, 7, 1);
        LocalDate end = LocalDate.of(2026, 7, 5); // 4 nights * 80 = 320

        Booking booking = manager.addBooking(
                "Sunny Valley", "Area-A", 1, start, end,
                "bungalow", true, 40.0, 1, 500.0
        );

        assertNotNull(booking, "Booking should be created when all constraints match");
        assertEquals("P1", booking.getPlotId());
        assertEquals(320.0, booking.getTotalCost(), 0.01);
    }

    /**
     * Test 5: Verifies that cancelling a booking properly frees up the plot.
     */
    @Test
    public void testCancelBooking() {
        LocalDate start = LocalDate.of(2026, 8, 1);
        LocalDate end = LocalDate.of(2026, 8, 5);

        Booking booking = manager.addBooking(
                "Sunny Valley", "Area-A", 1, start, end,
                "bungalow", true, 40.0, 1, 400.0
        );
        assertNotNull(booking);

        boolean cancelled = manager.cancelBooking(booking.getBookingId());
        assertTrue(cancelled, "Pending booking should be successfully removed");

        // The plot should now be available again for those dates
        Campsite site = manager.findCampsite("Sunny Valley");
        Plot plot = site.findCampingArea("Area-A").findPlot("P1");
        assertTrue(plot.isAvailable(start, end));
    }

    /**
     * Test 6: Verifies plot removal and automatic reassignment of pending bookings
     * to another suitable plot within the same campsite.
     */
    @Test
    public void testRemovePlotWithReassignment() {
        // Add a second plot of the same type that can take reassigned bookings
        Plot plot2 = new Plot("P2", "bungalow", true, 2, 4, 50.0, 80.0, "http://photo.com/2");
        manager.addPlot("Sunny Valley", "Area-A", plot2);

        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 7);

        // Make a booking on P1
        Booking b = manager.addBooking("Sunny Valley", "Area-A", 1, start, end, "bungalow", true, 40.0, 1, 600.0);
        assertEquals("P1", b.getPlotId());

        // Remove P1 -> Booking must be automatically reassigned to P2
        boolean removed = manager.removePlotWithReassignment("Sunny Valley", "Area-A", "P1");
        assertTrue(removed, "Plot should be removed and bookings relocated to P2");

        assertEquals("P2", b.getPlotId(), "Booking target plot must be updated to P2");
        assertNull(manager.findCampsite("Sunny Valley").findCampingArea("Area-A").findPlot("P1"));
    }

    /**
     * Test 7: Verifies system persistence via Save and Load without data corruption.
     */
    @Test
    public void testSaveAndLoadPersistence() throws Exception {
        String testFile = "test_campsite_data.dat";

        try {
            manager.saveToFile(testFile);
            CampsiteManager loadedManager = CampsiteManager.loadFromFile(testFile);

            assertNotNull(loadedManager);
            assertNotNull(loadedManager.findCampsite("Sunny Valley"));
            assertNotNull(loadedManager.findGuest(1));
            assertEquals(1, loadedManager.getCampsites().size());
        } finally {
            File f = new File(testFile);
            if (f.exists()) {
                f.delete(); // Cleanup test artifact
            }
        }
    }
}
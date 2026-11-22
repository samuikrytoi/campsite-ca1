package models;

import java.io.Serializable;

/**
 * Represents an entire Campsite.
 * Stores general campsite details, facilities, and a custom list of Camping Areas.
 */
public class Campsite implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;                   // Name of the campsite
    private String address;                // Physical location address
    private int stars;                     // Rating (e.g. 1 to 5 stars)
    private String contactInfo;            // Phone, email, website
    private MyList<String> facilities;     // Facilities: "pool", "gym", "restaurant", "WiFi", etc.
    private MyList<CampingArea> areas;     // Custom list of camping areas

    /**
     * Constructor to create a Campsite.
     */
    public Campsite(String name, String address, int stars, String contactInfo) {
        this.name = name;
        this.address = address;
        this.stars = stars;
        this.contactInfo = contactInfo;
        this.facilities = new MyList<>();
        this.areas = new MyList<>();
    }

    /**
     * Adds a new camping area to the campsite.
     * Enforces the requirement that camping area identifiers must be unique within the campsite.
     * 
     * @param area The camping area to add
     * @return true if added successfully, false if area ID already exists
     */
    public boolean addCampingArea(CampingArea area) {
        if (findCampingArea(area.getAreaId()) != null) {
            return false; // Duplicate area ID not allowed
        }
        areas.add(area);
        return true;
    }

    /**
     * Finds a camping area by its ID (case-insensitive).
     * 
     * @param areaId Area identifier
     * @return CampingArea if found, null otherwise
     */
    public CampingArea findCampingArea(String areaId) {
        for (int i = 0; i < areas.size(); i++) {
            CampingArea area = areas.get(i);
            if (area.getAreaId().equalsIgnoreCase(areaId)) {
                return area;
            }
        }
        return null;
    }

    /**
     * Adds a facility string (e.g., "Swimming Pool", "Spa", "Gym").
     */
    public void addFacility(String facility) {
        if (!facilities.contains(facility)) {
            facilities.add(facility);
        }
    }

    /**
     * Checks if this campsite provides a specific facility (case-insensitive check).
     */
    public boolean hasFacility(String facility) {
        for (int i = 0; i < facilities.size(); i++) {
            if (facilities.get(i).equalsIgnoreCase(facility)) {
                return true;
            }
        }
        return false;
    }

    // --- Getters and Setters ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public MyList<String> getFacilities() { return facilities; }
    public MyList<CampingArea> getAreas() { return areas; }

    @Override
    public String toString() {
        return name + " (" + stars + "★) - " + address;
    }
}
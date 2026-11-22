package models;

import java.io.Serializable;

/**
 * Represents a Guest in the system.
 * Implements Serializable for Save/Load functionality.
 */
public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int guestId;          // Unique identifier for the guest
    private String name;          // Full name
    private String dateOfBirth;   // DOB (String format like "YYYY-MM-DD" for simplicity)
    private String address;       // Home address
    private String phone;         // Contact phone number
    private String email;         // Contact email
    private String extraInfo;     // Any pertinent ancillary information

    /**
     * Constructor to create a new Guest.
     */
    public Guest(int guestId, String name, String dateOfBirth, String address, 
                 String phone, String email, String extraInfo) {
        this.guestId = guestId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.extraInfo = extraInfo;
    }

    // --- Getters and Setters ---

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }

    /**
     * Text representation of the guest for GUI or console printing.
     */
    @Override
    public String toString() {
        return name + " (ID: " + guestId + ") - " + email;
    }
}
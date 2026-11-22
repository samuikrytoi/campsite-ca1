package models;

import java.io.Serializable;

/**
 * Represents a Camping Area within a Campsite.
 * Contains a collection of Plots/Pitches using custom MyList.
 */
public class CampingArea implements Serializable {
    private static final long serialVersionUID = 1L;

    private String areaId;          // Unique identifier for the area within the campsite (e.g. "Lake View 1")
    private String description;     // Description of the area
    private MyList<Plot> plots;     // Custom list of plots/pitches in this area

    /**
     * Constructor to create a new Camping Area.
     */
    public CampingArea(String areaId, String description) {
        this.areaId = areaId;
        this.description = description;
        this.plots = new MyList<>();
    }

    /**
     * Adds a new plot to this camping area.
     * Checks if a plot with the same ID already exists.
     * 
     * @param plot The plot to add
     * @return true if added, false if duplicate ID exists
     */
    public boolean addPlot(Plot plot) {
        if (findPlot(plot.getPlotId()) != null) {
            return false; // Duplicate plot ID within this area
        }
        plots.add(plot);
        return true;
    }

    /**
     * Finds a plot by its identifier.
     * 
     * @param plotId Plot identifier string
     * @return Plot object if found, null otherwise
     */
    public Plot findPlot(String plotId) {
        for (int i = 0; i < plots.size(); i++) {
            Plot p = plots.get(i);
            if (p.getPlotId().equalsIgnoreCase(plotId)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Removes a plot by its identifier.
     * 
     * @param plotId The ID of the plot to remove
     * @return true if removed, false otherwise
     */
    public boolean removePlot(String plotId) {
        Plot target = findPlot(plotId);
        if (target != null) {
            return plots.remove(target);
        }
        return false;
    }

    // --- Getters and Setters ---

    public String getAreaId() { return areaId; }
    public void setAreaId(String areaId) { this.areaId = areaId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public MyList<Plot> getPlots() { return plots; }

    @Override
    public String toString() {
        return areaId + " (" + plots.size() + " plots) - " + description;
    }
}